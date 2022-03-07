package com.mytiki.ingest.features.latest.cache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface CacheRepository extends JpaRepository<CacheDO, Long> {
}
