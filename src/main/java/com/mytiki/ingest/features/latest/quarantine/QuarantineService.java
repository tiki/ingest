package com.mytiki.ingest.features.latest.quarantine;

import com.mytiki.ingest.utilities.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;

public class QuarantineService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final QuarantineRepository repository;

    public QuarantineService(QuarantineRepository repository) {
        this.repository = repository;
    }

    public List<QuarantineDO> getByBreakerId(Long breakerId){
        return repository.findByBreakerId(breakerId);
    }

    public void deleteByBreakerId(Long breakerId){
        repository.deleteByBreakerId(breakerId);
        logger.debug("Deleted all quarantined edges for breaker " + breakerId);
    }

    public void add(Long breakerId, String fingerprint) throws NoSuchAlgorithmException {
        QuarantineDO quarantineDO = new QuarantineDO();
        quarantineDO.setBreakerId(breakerId);
        quarantineDO.setFingerprintHash(Hash.sha256(fingerprint));
        quarantineDO.setCreated(ZonedDateTime.now());
        repository.save(quarantineDO);
        logger.debug("Added new edge to quarantine for breaker " + breakerId);
    }
}
