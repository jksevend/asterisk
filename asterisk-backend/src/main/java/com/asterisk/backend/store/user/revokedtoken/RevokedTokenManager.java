package com.asterisk.backend.store.user.revokedtoken;

import com.asterisk.backend.domain.RevokedToken;

public interface RevokedTokenManager {
    void save(RevokedToken revokedToken);

    boolean existsByTokenDigest(String jwtTokenDigestInHex);
}
