package com.mytiki.ingest.config;

import com.mytiki.common.exception.ApiExceptionHandlerDefault;
import com.mytiki.common.reply.ApiReplyHandlerDefault;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Import({
        ApiExceptionHandlerDefault.class,
        ApiReplyHandlerDefault.class,
})
@EnableScheduling
public class ConfigIngestApp {
    @PostConstruct
    void starter(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
