package com.mytiki.ingest.features.latest.breaker;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BreakerRepository extends JpaRepository<BreakerDO, Long> {
    Optional<BreakerDO> findByEdgeHash(byte[] edgeHash);
}
