/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.ingest.utilities;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiConstants.HEALTH_ROUTE)
public class HealthCheckController {

    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "health";

    @RequestMapping(method = RequestMethod.GET)
    public ApiReplyAO<?> postRefresh(){
        return ApiReplyAOFactory.ok();
    }
}
