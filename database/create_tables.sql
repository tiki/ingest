/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

-- -----------------------------------------------------------------------
-- BREAKER
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS breaker(
    id BIGSERIAL NOT NULL,
    edge_hash BYTEA NOT NULL UNIQUE,
    closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    modified_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(id)
);

-- -----------------------------------------------------------------------
-- QUARANTINE
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS quarantine(
    id BIGSERIAL NOT NULL,
    breaker_id BIGSERIAL NOT NULL,
    fingerprint_hash BYTEA NOT NULL UNIQUE,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(breaker_id) REFERENCES breaker(id)
);

-- -----------------------------------------------------------------------
-- CACHE
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cache(
    id BIGSERIAL NOT NULL,
    vertex_1_type TEXT NOT NULL,
    vertex_1_value TEXT NOT NULL,
    vertex_2_type TEXT NOT NULL,
    vertex_2_value TEXT NOT NULL,
    fingerprint TEXT NOT NULL UNIQUE,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(id)
);