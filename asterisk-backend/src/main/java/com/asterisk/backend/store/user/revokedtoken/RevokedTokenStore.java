package com.asterisk.backend.store.user.revokedtoken;

import com.asterisk.backend.domain.RevokedToken;
import com.asterisk.backend.mapper.RevokedTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RevokedTokenStore implements RevokedTokenManager {

    private final RevokedTokenRepository revokedTokenRepository;
    private final RevokedTokenMapper revokedTokenMapper;

    @Autowired
    public RevokedTokenStore(final RevokedTokenRepository revokedTokenRepository, final RevokedTokenMapper revokedTokenMapper) {
        this.revokedTokenRepository = revokedTokenRepository;
        this.revokedTokenMapper = revokedTokenMapper;
    }

    @Override
    public void save(final RevokedToken revokedToken) {
        final RevokedTokenEntity revokedTokenEntity = this.revokedTokenMapper.toRevokedTokenEntity(revokedToken);
        this.revokedTokenRepository.save(revokedTokenEntity);
    }

    @Override
    public boolean existsByTokenDigest(final String jwtTokenDigestInHex) {
        return this.revokedTokenRepository.existsById(jwtTokenDigestInHex);
    }
}
