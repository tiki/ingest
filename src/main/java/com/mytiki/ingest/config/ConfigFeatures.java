/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.ingest.config;

import com.mytiki.ingest.features.latest.breaker.BreakerConfig;
import com.mytiki.ingest.features.latest.quarantine.QuarantineConfig;
import com.mytiki.ingest.utilities.UtilitiesConfig;
import org.springframework.context.annotation.Import;

@Import({
        UtilitiesConfig.class,
        QuarantineConfig.class,
        BreakerConfig.class
})
public class ConfigFeatures {}
