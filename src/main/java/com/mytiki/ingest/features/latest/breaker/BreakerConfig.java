package com.mytiki.ingest.features.latest.breaker;

import com.mytiki.ingest.features.latest.quarantine.QuarantineRepository;
import com.mytiki.ingest.features.latest.quarantine.QuarantineService;
import com.mytiki.ingest.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(com.mytiki.ingest.features.latest.breaker.BreakerConfig.PACKAGE_PATH)
@EntityScan(com.mytiki.ingest.features.latest.breaker.BreakerConfig.PACKAGE_PATH)
public class BreakerConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".breaker";

    @Bean
    public BreakerService breakerService(
            @Autowired BreakerRepository breakerRepository,
            @Autowired QuarantineService quarantineService){
        return new BreakerService(breakerRepository, quarantineService);
    }

    @Bean
    public BreakerController breakerController(@Autowired BreakerService breakerService){
        return new BreakerController(breakerService);
    }
}
