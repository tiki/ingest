package com.mytiki.ingest;

import com.mytiki.ingest.features.latest.breaker.BreakerAOReq;
import com.mytiki.ingest.features.latest.breaker.BreakerAOReqVertex;
import com.mytiki.ingest.features.latest.breaker.BreakerAORsp;
import com.mytiki.ingest.features.latest.breaker.BreakerService;
import com.mytiki.ingest.main.IngestApp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {IngestApp.class}
)
@ActiveProfiles(profiles = {"test", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BreakerTests {
    @Autowired
    private BreakerService breakerService;

    private final String transaction = UUID.randomUUID().toString();

    @Test
    @Order(1)
    public void Test_Write1_Accepted(){
        List<BreakerAORsp> rsp = breakerService.write(req(1));
        assertEquals(1, rsp.size());
        assertNotNull(rsp.get(0).getRetryIn());
    }

    @Test
    @Order(2)
    public void Test_Write8_Accepted(){
        List<BreakerAOReq> reqList = req(8);
        List<String> fpList = reqList.stream()
                .map(BreakerAOReq::getFingerprint)
                .collect(Collectors.toList());

        List<BreakerAORsp> rspList = breakerService.write(reqList);
        assertEquals(8, rspList.size());
        rspList.forEach(rsp -> assertTrue(fpList.contains(rsp.getFingerprint())));
    }

    @Test
    @Order(3)
    public void Test_WriteToggle_Created(){
        List<BreakerAORsp> rsp = breakerService.write(req(1));
        assertEquals(0, rsp.size());
    }

    @Test
    @Order(4)
    public void Test_Write_Created(){
        List<BreakerAORsp> rsp = breakerService.write(req(1));
        assertEquals(0, rsp.size());
    }


    private List<BreakerAOReq> req(int num){
        List<BreakerAOReq> rsp = new ArrayList<>(num);
        for(int i=0; i<num; i++) {
            BreakerAOReqVertex vertex1 = new BreakerAOReqVertex("who" + transaction, "world");
            BreakerAOReqVertex vertex2 = new BreakerAOReqVertex("message" + transaction, "hello");
            rsp.add(new BreakerAOReq(vertex1, vertex2, UUID.randomUUID().toString()));
        }
        return rsp;
    }
}
