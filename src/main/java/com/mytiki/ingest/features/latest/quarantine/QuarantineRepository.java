package com.mytiki.ingest.features.latest.quarantine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuarantineRepository extends JpaRepository<QuarantineDO, Long> {
    List<QuarantineDO> findByBreakerId(Long id);
    List<QuarantineDO> deleteByBreakerId(Long id);
}
