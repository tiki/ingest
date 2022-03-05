/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.ingest.config;

import com.mytiki.ingest.features.latest.quarantine.QuarantineConfig;
import org.springframework.context.annotation.Import;

@Import({
        QuarantineConfig.class
})
public class ConfigFeatures {}
