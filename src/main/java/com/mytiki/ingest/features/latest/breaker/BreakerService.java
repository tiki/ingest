package com.mytiki.ingest.features.latest.breaker;

import com.mytiki.common.exception.ApiExceptionFactory;
import com.mytiki.ingest.features.latest.cache.CacheService;
import com.mytiki.ingest.features.latest.quarantine.QuarantineDO;
import com.mytiki.ingest.features.latest.quarantine.QuarantineService;
import com.mytiki.ingest.utilities.Hash;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BreakerService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int EPSILON = 10; //TODO move to prop
    private static final long RETRY_IN = Duration.ofDays(1).getSeconds(); //TODO make dynamic

    private final BreakerRepository repository;
    private final QuarantineService quarantine;
    private final CacheService cache;

    public BreakerService(
            BreakerRepository repository,
            QuarantineService quarantine,
            CacheService cache) {
        this.repository = repository;
        this.quarantine = quarantine;
        this.cache = cache;
    }

    @Transactional
    public List<BreakerAORsp> write(List<BreakerAOReq> req){
        try{
            List<byte[]> edgeHashes = new ArrayList<>();
            Map<String, List<BreakerAOReq>> hashReqs = new HashMap<>();
            req.forEach(r -> {
                try {
                    byte[] hash = edgeHash(r.vertex1, r.vertex2);
                    edgeHashes.add(hash);
                    hashReqs.merge(Hex.encodeHexString(hash), List.of(r), (current, update) -> {
                        List<BreakerAOReq> list = new ArrayList<>(current);
                        list.addAll(update);
                        return list;
                    });
                } catch (NoSuchAlgorithmException e) {
                    logger.error("Hashing failed: " + e.getMessage(), e.getCause());
                }
            });

            Set<BreakerDO> existingDOs = repository.findByEdgeHashIn(edgeHashes);

            List<BreakerAOReq> toCache = new ArrayList<>();
            List<BreakerDO> toToggle  = new ArrayList<>();
            Map<BreakerDO, List<BreakerAOReq>> checkQuarantine = new HashMap<>();
            Map<String, List<BreakerAOReq>> newBreakers = new HashMap<>();
            HashMap<String, BreakerDO> existingBreakers = new HashMap<>();

            existingDOs.forEach(breaker -> {
                        String hex = Hex.encodeHexString(breaker.getEdgeHash());
                        List<BreakerAOReq> reqs = hashReqs.get(hex);
                        existingBreakers.put(hex, breaker);
                        if(reqs !=null && reqs.size() > 0) {
                            if(breaker.getClosed())
                                toCache.addAll(reqs);
                            else if(reqs.size() >= EPSILON) {
                                toToggle.add(breaker);
                                toCache.addAll(reqs);
                            }else
                                checkQuarantine.put(breaker, reqs);
                        }
                    });

            hashReqs.entrySet().stream()
                    .filter(entry -> !existingBreakers.containsKey(entry.getKey()))
                    .forEach(entry -> newBreakers.put(entry.getKey(), entry.getValue()));

            List<BreakerAORsp> rsp = new ArrayList<>(processNewBreakers(newBreakers));

            Map<Long, List<QuarantineDO>> quarantined = quarantine.getByBreakerIds(
                    checkQuarantine.keySet().stream()
                            .map(BreakerDO::getId)
                            .collect(Collectors.toList()));

            Map<String, Long> toQuarantine = new HashMap<>();
            checkQuarantine.forEach((breaker, reqs) -> {
                if(quarantined.get(breaker.getId()).size() + reqs.size() >= EPSILON ) {
                    toToggle.add(breaker);
                    toCache.addAll(reqs);
                }else
                    reqs.forEach(r -> {
                        toQuarantine.put(r.fingerprint, breaker.getId());
                        BreakerAORsp breakerAORsp = new BreakerAORsp();
                        breakerAORsp.setRetryIn(RETRY_IN);
                        breakerAORsp.setFingerprint(r.fingerprint);
                        rsp.add(breakerAORsp);
                    });
            });

            quarantine.add(toQuarantine);
            processToggleBreakers(toToggle);
            cache.add(toCache);
            return rsp;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e.getCause());
            throw ApiExceptionFactory.exception(HttpStatus.EXPECTATION_FAILED, "Hashing failed");
        } catch (DataIntegrityViolationException e){
            logger.error(e.getMessage(), e.getCause());
            throw ApiExceptionFactory.exception(HttpStatus.BAD_REQUEST, "Failed. Check the request body");
        }
    }

    private byte[] edgeHash(
            BreakerAOReqVertex vertex1,
            BreakerAOReqVertex vertex2)
            throws NoSuchAlgorithmException {
        List<String> vertices = new ArrayList<>(2);
        vertices.add(vertex1.type.toLowerCase() + ',' + vertex1.value.toLowerCase());
        vertices.add(vertex2.type.toLowerCase() + ',' + vertex2.value.toLowerCase());
        String res = vertices.stream().sorted().collect(Collectors.joining(","));
        return Hash.sha256(res);
    }

    private List<BreakerAORsp> processNewBreakers(Map<String, List<BreakerAOReq>> newBreakers)
            throws NoSuchAlgorithmException {
        Map<byte[], BreakerDO> breakers = new HashMap<>();
        newBreakers.forEach((hex, reqs) -> {
            try {
                byte[] hash = Hex.decodeHex(hex);
                BreakerDO breaker = new BreakerDO();
                breaker.setEdgeHash(hash);
                breaker.setClosed(reqs.size() >= EPSILON);
                ZonedDateTime now = ZonedDateTime.now();
                breaker.setModified(now);
                breaker.setCreated(now);
                breakers.put(hash, breaker);
            } catch (DecoderException e) {
                throw new RuntimeException(e);
            }
        });

        List<BreakerDO> saved = repository.saveAll(breakers.values());

        Map<String, Long> toQuarantine = new HashMap<>();
        List<BreakerAORsp> rspList = new ArrayList<>();
        saved.stream()
                .filter(breaker -> !breaker.getClosed())
                .forEach(breaker -> newBreakers.get(Hex.encodeHexString(breaker.getEdgeHash()))
                        .forEach(req -> {
                            toQuarantine.put(req.fingerprint, breaker.getId());
                            BreakerAORsp rsp = new BreakerAORsp();
                            rsp.setRetryIn(RETRY_IN);
                            rsp.setFingerprint(req.fingerprint);
                            rspList.add(rsp);
                        }));

        quarantine.add(toQuarantine);
        return rspList;
    }

    private void processToggleBreakers(List<BreakerDO> breakers){
        quarantine.deleteByBreakerIds(breakers.stream()
                .map(BreakerDO::getId)
                .collect(Collectors.toList()));
        breakers.forEach(breakerDO -> {
            breakerDO.setClosed(true);
            breakerDO.setModified(ZonedDateTime.now());
        });
        repository.saveAll(breakers);
    }
}
