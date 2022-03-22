package com.mytiki.ingest.features.latest.breaker;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "breaker")
public class BreakerDO implements Serializable {

    private Long id;
    private byte[] edgeHash;
    private boolean closed;
    private ZonedDateTime modified;
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

    @Column(name = "edge_hash")
    public byte[] getEdgeHash() {
        return edgeHash;
    }

    public void setEdgeHash(byte[] edgeHash) {
        this.edgeHash = edgeHash;
    }

    @Column(name = "closed")
    public boolean getClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Column(name = "created_utc")
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    @Column(name = "modified_utc")
    public ZonedDateTime getModified() {
        return modified;
    }

    public void setModified(ZonedDateTime modified) {
        this.modified = modified;
    }
}
