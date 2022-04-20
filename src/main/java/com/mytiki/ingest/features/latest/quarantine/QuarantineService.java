package com.mytiki.ingest.features.latest.quarantine;

import com.mytiki.ingest.features.latest.breaker.BreakerAOReq;
import com.mytiki.ingest.utilities.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuarantineService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final QuarantineRepository repository;

    public QuarantineService(QuarantineRepository repository) {
        this.repository = repository;
    }

    public Map<Long, List<QuarantineDO>> getByBreakerIds(List<Long> breakerIds){
        List<QuarantineDO> quarantined = repository.findByBreakerIdIn(breakerIds);
        Map<Long, List<QuarantineDO>> rsp = new HashMap<>();
        quarantined.forEach(qdo -> rsp.merge(qdo.getBreakerId(), List.of(qdo), (current, update) -> {
            List<QuarantineDO> list = new ArrayList<>(current);
            list.addAll(update);
            return list;
        }));
        return rsp;
    }

    public void deleteByBreakerIds(List<Long> breakerIds){
        repository.deleteByBreakerIdIn(breakerIds);
        logger.debug("Deleted all quarantined edges for " + breakerIds.size() +  " breakers ");
    }

    public void add(Map<String, Long> req) throws NoSuchAlgorithmException {
        List<QuarantineDO> quarantine = new ArrayList<>(req.size());
        for(Map.Entry<String, Long> entry : req.entrySet()){
            QuarantineDO qdo = new QuarantineDO();
            qdo.setBreakerId(entry.getValue());
            qdo.setFingerprintHash(Hash.sha256(entry.getKey()));
            qdo.setCreated(ZonedDateTime.now());
            quarantine.add(qdo);
        }
        repository.saveAll(quarantine);
        logger.debug("Added new " + quarantine.size() +"edges to quarantine");
    }
}
