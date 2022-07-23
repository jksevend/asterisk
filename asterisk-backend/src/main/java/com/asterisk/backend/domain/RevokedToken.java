package com.asterisk.backend.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RevokedToken {

    private String tokenDigest;
    private OffsetDateTime revocationDate;

    public RevokedToken(final String tokenDigest) {
        this.tokenDigest = tokenDigest;
        this.revocationDate = OffsetDateTime.now();
    }

    public String getTokenDigest() {
        return this.tokenDigest;
    }

    public void setTokenDigest(final String tokenDigest) {
        this.tokenDigest = tokenDigest;
    }

    public OffsetDateTime getRevocationDate() {
        return this.revocationDate;
    }

    public void setRevocationDate(final OffsetDateTime revocationDate) {
        this.revocationDate = revocationDate;
    }
}
