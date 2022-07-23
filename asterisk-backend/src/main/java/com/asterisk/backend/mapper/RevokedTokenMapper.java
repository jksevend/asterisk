package com.asterisk.backend.mapper;

import com.asterisk.backend.domain.RevokedToken;
import com.asterisk.backend.store.user.revokedtoken.RevokedTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class RevokedTokenMapper {
    public RevokedTokenEntity toRevokedTokenEntity(final RevokedToken revokedToken) {
        final RevokedTokenEntity revokedTokenEntity = new RevokedTokenEntity();
        revokedTokenEntity.setTokenDigest(revokedToken.getTokenDigest());
        revokedTokenEntity.setRevocationDate(revokedToken.getRevocationDate());
        return revokedTokenEntity;
    }
}
