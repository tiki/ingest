/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.ingest.features.latest.edge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EdgeAO {
    private EdgeAOVertex from;
    private EdgeAOVertex to;
    private String fingerprint;

    public EdgeAOVertex getFrom() {
        return from;
    }

    public void setFrom(EdgeAOVertex from) {
        this.from = from;
    }

    public EdgeAOVertex getTo() {
        return to;
    }

    public void setTo(EdgeAOVertex to) {
        this.to = to;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
