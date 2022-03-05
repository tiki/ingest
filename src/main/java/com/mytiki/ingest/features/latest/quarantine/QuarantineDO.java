package com.mytiki.ingest.features.latest.quarantine;

import com.mytiki.ingest.features.latest.breaker.BreakerDO;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "quarantine")
public class QuarantineDO implements Serializable {

    private Long id;
    private Long breakerId;
    private byte[] fingerprintHash;
    private ZonedDateTime created;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "breakerId")
    public Long getBreakerId() {
        return breakerId;
    }

    public void setBreakerId(Long breakerId) {
        this.breakerId = breakerId;
    }

    @Column(name = "fingerprint_hash")
    public byte[] getFingerprintHash() {
        return fingerprintHash;
    }

    public void setFingerprintHash(byte[] fingerprintHash) {
        this.fingerprintHash = fingerprintHash;
    }

    @Column(name = "created_utc")
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
