package com.mytiki.ingest.features.latest.cache;

import com.mytiki.ingest.features.latest.quarantine.QuarantineRepository;
import com.mytiki.ingest.features.latest.quarantine.QuarantineService;
import com.mytiki.ingest.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(com.mytiki.ingest.features.latest.cache.CacheConfig.PACKAGE_PATH)
@EntityScan(com.mytiki.ingest.features.latest.cache.CacheConfig.PACKAGE_PATH)
public class CacheConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".cache";

    @Bean
    public CacheService cacheService(@Autowired CacheRepository cacheRepository){
        return new CacheService(cacheRepository);
    }
}
