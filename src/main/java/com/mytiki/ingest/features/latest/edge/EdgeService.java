package com.mytiki.ingest.features.latest.edge;

import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.ingest.features.latest.cache.CacheDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

public class EdgeService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RestTemplate kgraphClient;

    private static final String PATH = "/api/latest/edge";

    public EdgeService(RestTemplate kgraphClient) {
        this.kgraphClient = kgraphClient;
    }

    public boolean send(List<CacheDO> cache){
        List<EdgeAO> edges = cache.stream().map( e -> {
            EdgeAO edge = new EdgeAO();
            edge.setFingerprint(e.getFingerprint());

            EdgeAOVertex from = new EdgeAOVertex();
            from.setType(e.getVertex1Type());
            from.setId(e.getVertex1Value());
            edge.setFrom(from);

            EdgeAOVertex to = new EdgeAOVertex();
            to.setType(e.getVertex2Type());
            to.setId(e.getVertex2Value());
            edge.setTo(to);

            return edge;
        }).collect(Collectors.toList());

        ParameterizedTypeReference<ApiReplyAO<List<EdgeAO>>> rspType = new ParameterizedTypeReference<>() {};
        ResponseEntity<ApiReplyAO<List<EdgeAO>>> rsp = kgraphClient.exchange(
                PATH, HttpMethod.POST, new HttpEntity<>(edges), rspType);

        if(!rsp.getStatusCode().is2xxSuccessful()){
            logger.error("POST failed. \ncode: " + rsp.getStatusCodeValue() + "\nbody: " + rsp.getBody());
            return false;
        }else
            return true;
    }
}
