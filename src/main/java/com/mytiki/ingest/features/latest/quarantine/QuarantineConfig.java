package com.mytiki.ingest.features.latest.quarantine;

import com.mytiki.ingest.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(com.mytiki.ingest.features.latest.quarantine.QuarantineConfig.PACKAGE_PATH)
@EntityScan(com.mytiki.ingest.features.latest.quarantine.QuarantineConfig.PACKAGE_PATH)
public class QuarantineConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".quarantine";

    @Bean
    public QuarantineService quarantineService(@Autowired QuarantineRepository quarantineRepository){
        return new QuarantineService(quarantineRepository);
    }
}
