/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.ingest.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

public class UtilitiesConfig {
    @Bean
    public AccessDeniedHandler accessDeniedHandler(@Autowired ObjectMapper objectMapper){
        return new AccessDeniedHandlerImpl(objectMapper);
    }

    @Bean(name = "authEntryPointImplException")
    public AuthenticationEntryPoint authEntryPointImplException(@Autowired ObjectMapper objectMapper){
        return new AuthEntryPointImplException(objectMapper);
    }

    @Bean
    public HealthCheckController healthCheckController(){
        return new HealthCheckController();
    }
}
