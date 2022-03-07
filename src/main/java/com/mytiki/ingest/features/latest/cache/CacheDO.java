package com.mytiki.ingest.features.latest.cache;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "cache")
public class CacheDO implements Serializable {

    private Long id;
    private String vertex1Type;
    private String vertex1Value;
    private String vertex2Type;
    private String vertex2Value;
    private String fingerprint;
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

    @Column(name = "vertex_1_type")
    public String getVertex1Type() {
        return vertex1Type;
    }

    public void setVertex1Type(String vertex1Type) {
        this.vertex1Type = vertex1Type;
    }

    @Column(name = "vertex_1_value")
    public String getVertex1Value() {
        return vertex1Value;
    }

    public void setVertex1Value(String vertex1Value) {
        this.vertex1Value = vertex1Value;
    }

    @Column(name = "vertex_2_type")
    public String getVertex2Type() {
        return vertex2Type;
    }

    public void setVertex2Type(String vertex2Type) {
        this.vertex2Type = vertex2Type;
    }

    @Column(name = "vertex_2_value")
    public String getVertex2Value() {
        return vertex2Value;
    }

    public void setVertex2Value(String vertex2Value) {
        this.vertex2Value = vertex2Value;
    }

    @Column(name = "fingerprint")
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Column(name = "created_utc")
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}