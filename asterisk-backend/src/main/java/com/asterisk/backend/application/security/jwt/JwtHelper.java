package com.asterisk.backend.application.security.jwt;

import com.asterisk.backend.application.common.UserDetailsImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtHelper {
    private final SecureRandom secureRandom = new SecureRandom();

    private final JwtProperties jwtProperties;

    @Autowired
    public JwtHelper(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Generates a new access token for an authenticated user. Takes the hash of the
     * generated fingerprint and encodes it in the token for further validation.
     *
     * @param authentication  the authenticated users info
     * @param fingerprintHash the generated hash of the fingerprint
     * @return a jwt as string
     */
    public String generateAccessJwt(final Authentication authentication, final String fingerprintHash) {
        final UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        final Algorithm algorithm = Algorithm.HMAC256(this.jwtProperties.getAccessSecret());
        final Map<String, ?> payload = Map.of("email", userPrincipal.getEmail(), "username",
                userPrincipal.getUsername(), "roles",
                List.of(userPrincipal.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority().split("_")[1]).collect(Collectors.joining())));
        return JWT.create()
                .withPayload(payload)
                .withClaim("fgp", fingerprintHash)
                .withSubject(userPrincipal.getId().toString())
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + (this.jwtProperties.getAccessExpiration() * 1000)))
                .sign(algorithm);
    }

    /**
     * Attempts to validate whether a given jwt is valid. Checks whether the hash of the fingerprint
     * is in the JWT
     *
     * @param token       jwt token
     * @param fingerprint the non-hashed fingerprint
     * @return true if valid, false if not or if exceptions happen along the way
     */
    public boolean validateAccessJwt(final String token, final String fingerprint) {
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            final byte[] userFingerprintDigest = digest.digest(fingerprint.getBytes(StandardCharsets.UTF_8));
            final String fingerprintHash = DatatypeConverter.printHexBinary(userFingerprintDigest);
            final Algorithm algorithm = Algorithm.HMAC256(this.jwtProperties.getAccessSecret());
            final JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("fgp", fingerprintHash)
                    .build();
            try {
                final DecodedJWT decodedJWT = verifier.verify(token);
                return decodedJWT.getExpiresAt().after(new Date());
            } catch (final JWTVerificationException exception) {
                return false;
            }
        } catch (final NoSuchAlgorithmException exception) {
            return false;
        }
    }

    /**
     * @return 50 Bytes of random hex gibberish
     */
    public String generateFingerprint() {
        final byte[] randomFingerprint = new byte[50];
        this.secureRandom.nextBytes(randomFingerprint);
        return DatatypeConverter.printHexBinary(randomFingerprint);
    }

    /**
     * Hashes a previously generated fingerprint
     *
     * @param fingerprint the fingerprint generated
     * @return a hashed fingerprint in hex
     */
    public String generateFingerprintHash(final String fingerprint) {
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            final byte[] userFingerprintDigest = digest.digest(fingerprint.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(userFingerprintDigest);
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a cookie and puts the fingerprint value in it
     *
     * @param fingerprint the non-hashed fingerprint
     * @return Cookie
     */
    public ResponseCookie generateFingerprintCookie(final String fingerprint) {
        return ResponseCookie
                .from("fgp", fingerprint)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(this.jwtProperties.getAccessExpiration())
                .sameSite("Strict")
                .build();
    }

    /**
     * @param token
     * @return
     */
    public String getEmailFromAccessJwt(final String token) {
        final DecodedJWT jwt = JWT.decode(token);
        final Map<String, Claim> claims = jwt.getClaims();
        final Claim claim = claims.get("email");
        return claim.asString();
    }
}
