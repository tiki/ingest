package com.mytiki.ingest.config;

import com.mytiki.common.exception.ApiExceptionHandlerDefault;
import com.mytiki.common.reply.ApiReplyHandlerDefault;
import com.mytiki.ingest.utilities.UtilitiesConfig;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Import({
        ConfigProperties.class,
        ApiExceptionHandlerDefault.class,
        ApiReplyHandlerDefault.class,
        UtilitiesConfig.class,
        ConfigFeatures.class,
        ConfigSecurity.class
})
@EnableScheduling
public class ConfigIngestApp {
    @PostConstruct
    void starter(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
