package com.asterisk.backend.store.user.confirmation;

import com.asterisk.backend.store.Timestamp;
import com.asterisk.backend.store.user.UserEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity(name = "tab_register_confirmation_token")
public class RegisterConfirmationTokenEntity extends Timestamp {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    /// XXX-XXX-XXX
    @Column(name = "code", length = 11, nullable = false)
    private String code;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    public RegisterConfirmationTokenEntity() {

    }

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public OffsetDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public void setExpiresAt(final OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public void setUser(final UserEntity user) {
        this.user = user;
    }
}
