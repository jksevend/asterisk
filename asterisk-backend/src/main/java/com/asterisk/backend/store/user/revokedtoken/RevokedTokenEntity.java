package com.asterisk.backend.store.user.revokedtoken;

import com.asterisk.backend.store.Timestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity(name = "tab_revoked_token")
public class RevokedTokenEntity{

    @Id
    @Column(name = "token_digest", nullable = false)
    private String tokenDigest;

    @Column(name = "revocation_date", nullable = false)
    private OffsetDateTime revocationDate;

    public RevokedTokenEntity() {

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
