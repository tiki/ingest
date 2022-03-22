package com.mytiki.ingest.features.latest.edge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class EdgeConfig {
    @Value("${com.mytiki.kgraph.api_key.header:}")
    private String kgraphApiKeyHeader;

    @Value("${com.mytiki.kgraph.api_key.value:}")
    private String kgraphApiKeyValue;

    @Value("${com.mytiki.kgraph.url:}")
    private String kgraphUrl;

    @Bean
    public RestTemplate kgraphClient(RestTemplateBuilder builder){
        return builder
                .rootUri(kgraphUrl)
                .defaultHeader(kgraphApiKeyHeader, kgraphApiKeyValue)
                .build();
    }

    @Bean
    public EdgeService edgeService(@Autowired @Qualifier("kgraphClient") RestTemplate kgraphClient){
        return new EdgeService(kgraphClient);
    }
}
