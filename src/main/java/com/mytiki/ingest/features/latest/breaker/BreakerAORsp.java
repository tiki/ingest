package com.mytiki.ingest.features.latest.breaker;

public class BreakerAORsp {
    Long retryIn;
    String fingerprint;

    public Long getRetryIn() {
        return retryIn;
    }

    public void setRetryIn(Long retryIn) {
        this.retryIn = retryIn;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
