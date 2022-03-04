/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.ingest.main;

import com.mytiki.ingest.config.ConfigIngestApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({
        ConfigIngestApp.class
})
@SpringBootApplication
public class IngestApp {

    public static void main(final String... args) {
        SpringApplication.run(IngestApp.class, args);
    }
}