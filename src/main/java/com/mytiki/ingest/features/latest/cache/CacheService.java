package com.mytiki.ingest.features.latest.cache;

import com.mytiki.ingest.features.latest.breaker.BreakerAOReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Console;
import java.lang.invoke.MethodHandles;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int THRESHOLD = 1000;
    private final CacheRepository repository;

    public CacheService(CacheRepository repository) {
        this.repository = repository;
    }

    public void add(BreakerAOReq req){
        CacheDO cacheDO = new CacheDO();
        cacheDO.setFingerprint(req.getFingerprint());
        cacheDO.setVertex1Type(req.getVertex1().getType());
        cacheDO.setVertex1Value(req.getVertex1().getValue());
        cacheDO.setVertex2Type(req.getVertex2().getType());
        cacheDO.setVertex2Value(req.getVertex2().getValue());
        cacheDO.setCreated(ZonedDateTime.now());
        repository.save(cacheDO);
        long count = repository.count();
        if(count > THRESHOLD) execute();
    }

    @Scheduled(fixedDelay = 1000*60*5) //5 Minutes
    private void loop(){
        long count = repository.count();
        if(count > 0) execute();
    }

    private void execute(){
        int pageNum = 0;
        Page<CacheDO> page = repository.findAll(PageRequest.of(pageNum, THRESHOLD));
        List<CacheDO> items = page.toList();
        send(items);
        List<Long> ids = items.stream().map(CacheDO::getId).collect(Collectors.toList());

        while(page.hasNext()){
            pageNum++;
            page = repository.findAll(PageRequest.of(pageNum, THRESHOLD));
            items = page.toList();
            send(items);
            ids.addAll(items.stream().map(CacheDO::getId).collect(Collectors.toList()));
        }

        repository.deleteAllById(ids);
    }

    private void send(List<CacheDO> list){
        logger.info("sending " + list.size() + " items");
    }

}
