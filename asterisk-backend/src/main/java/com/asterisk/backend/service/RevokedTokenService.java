package com.asterisk.backend.service;

import com.asterisk.backend.domain.RevokedToken;
import com.asterisk.backend.store.user.revokedtoken.RevokedTokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class RevokedTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevokedTokenService.class);
    private static final String ALGORITHM = "SHA-256";

    private final RevokedTokenManager revokedTokenManager;

    @Autowired
    public RevokedTokenService(final RevokedTokenManager revokedTokenManager) {
        this.revokedTokenManager = revokedTokenManager;
    }

    public void revokeToken(final String jwtInHex) {
        if (!jwtInHex.trim().isEmpty()) {
            //Decode the ciphered token
            final byte[] cipheredToken = DatatypeConverter.parseHexBinary(jwtInHex);
            try {
                //Compute a SHA256 of the ciphered token
                final MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
                final byte[] cipheredTokenDigest = digest.digest(cipheredToken);
                final String jwtTokenDigestInHex = DatatypeConverter.printHexBinary(cipheredTokenDigest);

                //Check if the token digest in HEX is already in the DB and add it if it is absent
                if (!this.isTokenRevoked(jwtInHex)) {
                    final RevokedToken revokedToken = new RevokedToken(jwtTokenDigestInHex);
                    this.revokedTokenManager.save(revokedToken);
                }
            } catch (final NoSuchAlgorithmException e) {
                LOGGER.error("Unable to create {} instance: {}", ALGORITHM, e);
            }
        }
    }

    public boolean isTokenRevoked(final String jwtInHex) {
        if (!jwtInHex.trim().isEmpty()) {
            //Decode the ciphered token
            final byte[] cipheredToken = DatatypeConverter.parseHexBinary(jwtInHex);
            try {
                //Compute a SHA256 of the ciphered token
                final MessageDigest digest = MessageDigest.getInstance("SHA-256");
                final byte[] cipheredTokenDigest = digest.digest(cipheredToken);
                final String jwtTokenDigestInHex = DatatypeConverter.printHexBinary(cipheredTokenDigest);

                return this.revokedTokenManager.existsByTokenDigest(jwtTokenDigestInHex);
            } catch (final NoSuchAlgorithmException e) {
                LOGGER.error("Unable to create {} instance: {}", ALGORITHM, e.getMessage());
                return false;
            }
        }

        return false;
    }
}
