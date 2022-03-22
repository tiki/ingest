package com.mytiki.ingest.features.latest.breaker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BreakerAOReqVertex {
    String type;
    String value;

    @JsonCreator
    public BreakerAOReqVertex(
            @JsonProperty(required = true) String type,
            @JsonProperty(required = true) String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
