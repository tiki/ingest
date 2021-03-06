package com.mytiki.ingest.features.latest.breaker;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOBuilder;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = BreakerController.PATH_CONTROLLER)
public class BreakerController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "write";

    private final BreakerService breakerService;

    public BreakerController(BreakerService breakerService) {
        this.breakerService = breakerService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ApiReplyAO<List<BreakerAORsp>> post(@RequestBody List<BreakerAOReq> body){
        return ApiReplyAOFactory.ok(breakerService.write(body));
    }
}
