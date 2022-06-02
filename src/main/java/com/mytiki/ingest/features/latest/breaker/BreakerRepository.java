package com.mytiki.ingest.features.latest.breaker;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BreakerRepository extends JpaRepository<BreakerDO, Long> {
    Optional<BreakerDO> findByEdgeHash(byte[] edgeHash);
    Set<BreakerDO> findByEdgeHashIn(Collection<byte[]> edgeHash);
}
