package com.mytiki.ingest.features.latest.breaker;

import com.mytiki.common.exception.ApiExceptionFactory;
import com.mytiki.ingest.features.latest.cache.CacheService;
import com.mytiki.ingest.features.latest.quarantine.QuarantineDO;
import com.mytiki.ingest.features.latest.quarantine.QuarantineService;
import com.mytiki.ingest.utilities.Hash;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class BreakerService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int EPSILON = 10;
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
    public BreakerAORsp write(BreakerAOReq req){
        byte[] edgeHash;
        try{
            edgeHash = edgeHash(req.vertex1, req.vertex2);
            Optional<BreakerDO> breakerDOOptional = repository.findByEdgeHash(edgeHash);
            if(breakerDOOptional.isPresent()){
                BreakerDO breaker = breakerDOOptional.get();
                if(breaker.getClosed()){
                    cache.add(req);
                    return new BreakerAORsp();
                }else{
                    List<QuarantineDO> quarantineList = quarantine.getByBreakerId(breaker.getId());
                    if(quarantineList.size() >= EPSILON - 1) {
                        toggle(breaker);
                        cache.add(req);
                        return new BreakerAORsp();
                    }else
                        return addQuarantine(breaker.getId(), req.fingerprint);
                }
            }else
                return newBreaker(req.fingerprint, edgeHash);
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

    private BreakerAORsp newBreaker(String fingerprint, byte[] edgeHash) throws NoSuchAlgorithmException {
        BreakerDO breaker = new BreakerDO();
        breaker.setEdgeHash(edgeHash);
        breaker.setClosed(false);
        ZonedDateTime now = ZonedDateTime.now();
        breaker.setModified(now);
        breaker.setCreated(now);
        breaker = repository.save(breaker);

        quarantine.add(breaker.getId(), fingerprint);

        BreakerAORsp rsp = new BreakerAORsp();
        rsp.setRetryIn(RETRY_IN);
        return rsp;
    }

    private BreakerAORsp addQuarantine(Long breakerId, String fingerprint) throws NoSuchAlgorithmException {
        quarantine.add(breakerId, fingerprint);
        BreakerAORsp rsp = new BreakerAORsp();
        rsp.setRetryIn(RETRY_IN);
        return rsp;
    }

    private void toggle(BreakerDO breaker){
        quarantine.deleteByBreakerId(breaker.getId());
        breaker.setClosed(true);
        breaker.setModified(ZonedDateTime.now());
        repository.save(breaker);
    }
}
