package com.mytiki.ingest.features.latest.breaker;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOBuilder;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = BreakerController.PATH_CONTROLLER)
public class BreakerController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "write";

    private final BreakerService breakerService;

    public BreakerController(BreakerService breakerService) {
        this.breakerService = breakerService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ApiReplyAO<BreakerAORsp> post(@RequestBody BreakerAOReq body){
        BreakerAORsp rsp = breakerService.write(body);
        ApiReplyAOBuilder<BreakerAORsp> builder = new ApiReplyAOBuilder<>();
        if(rsp.getRetryIn() == null)
            return builder.httpStatus(HttpStatus.CREATED).build();
        else
            return builder.httpStatus(HttpStatus.ACCEPTED).data(rsp).build();
    }
}
