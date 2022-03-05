/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.ingest.config;

import org.springframework.beans.factory.annotation.Value;

public class ConfigProperties {

    @Value("${spring.profiles.active:}")
    private String springProfilesActive;

    public String getSpringProfilesActive() {
        return springProfilesActive;
    }

    public void setSpringProfilesActive(String springProfilesActive) {
        this.springProfilesActive = springProfilesActive;
    }
}
