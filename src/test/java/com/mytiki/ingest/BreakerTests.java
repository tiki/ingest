package com.mytiki.ingest;

import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.ingest.features.latest.breaker.BreakerAOReq;
import com.mytiki.ingest.features.latest.breaker.BreakerAOReqVertex;
import com.mytiki.ingest.features.latest.breaker.BreakerAORsp;
import com.mytiki.ingest.features.latest.breaker.BreakerService;
import com.mytiki.ingest.features.latest.cache.CacheDO;
import com.mytiki.ingest.features.latest.cache.CacheRepository;
import com.mytiki.ingest.features.latest.cache.CacheService;
import com.mytiki.ingest.features.latest.quarantine.QuarantineService;
import com.mytiki.ingest.main.IngestApp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {IngestApp.class}
)
@ActiveProfiles(profiles = {"local", "test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BreakerTests {
    @Autowired
    private BreakerService breakerService;

    private final String transaction = UUID.randomUUID().toString();

    @Test
    @Order(1)
    public void Test_Write1_Accepted(){
        BreakerAORsp rsp = breakerService.write(req());
        assertNotNull(rsp.getRetryIn());
    }

    @Test
    @Order(2)
    public void Test_Write8_Accepted(){
        for(int i=0; i<8; i++) {
            BreakerAORsp rsp = breakerService.write(req());
            assertNotNull(rsp.getRetryIn());
        }
    }

    @Test
    @Order(3)
    public void Test_WriteToggle_Created(){
        BreakerAORsp rsp = breakerService.write(req());
        assertNull(rsp.getRetryIn());
    }

    @Test
    @Order(4)
    public void Test_Write_Created(){
        BreakerAORsp rsp = breakerService.write(req());
        assertNull(rsp.getRetryIn());
    }


    private BreakerAOReq req(){
        BreakerAOReqVertex vertex1 = new BreakerAOReqVertex("who" + transaction, "world");
        BreakerAOReqVertex vertex2 = new BreakerAOReqVertex("message" + transaction, "hello");
        return new BreakerAOReq(vertex1, vertex2, UUID.randomUUID().toString());
    }
}
