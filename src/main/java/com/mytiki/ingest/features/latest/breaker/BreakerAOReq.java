package com.mytiki.ingest.features.latest.breaker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BreakerAOReq {

    BreakerAOReqVertex vertex1;
    BreakerAOReqVertex vertex2;
    String fingerprint;

    @JsonCreator
    public BreakerAOReq(
            @JsonProperty(required = true) BreakerAOReqVertex vertex1,
            @JsonProperty(required = true) BreakerAOReqVertex vertex2,
            @JsonProperty(required = true) String fingerprint) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.fingerprint = fingerprint;
    }

    public BreakerAOReqVertex getVertex1() {
        return vertex1;
    }

    public void setVertex1(BreakerAOReqVertex vertex1) {
        this.vertex1 = vertex1;
    }

    public BreakerAOReqVertex getVertex2() {
        return vertex2;
    }

    public void setVertex2(BreakerAOReqVertex vertex2) {
        this.vertex2 = vertex2;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
