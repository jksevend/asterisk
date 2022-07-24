package com.asterisk.backend.adapter.rest.authentication;

import com.asterisk.backend.adapter.rest.ResponseDto;
import com.asterisk.backend.adapter.rest.authentication.model.*;
import com.asterisk.backend.application.common.UserDetailsImpl;
import com.asterisk.backend.application.security.filter.FilterUtil;
import com.asterisk.backend.application.security.jwt.JwtHelper;
import com.asterisk.backend.service.AuthenticationService;
import com.asterisk.backend.service.RevokedTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;
    private final RevokedTokenService revokedTokenService;
    private final JwtHelper jwtHelper;

    @Autowired
    public AuthenticationController(final AuthenticationService authenticationService, final RevokedTokenService revokedTokenService, final JwtHelper jwtHelper) {
        this.authenticationService = authenticationService;
        this.revokedTokenService = revokedTokenService;
        this.jwtHelper = jwtHelper;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@Valid @RequestBody final RegisterRequestDto registerRequestDto) {
        final UUID confirmationTokenId = this.authenticationService.registerNewUser(registerRequestDto);
        final ResponseDto responseDto = new ResponseDto("A register confirmation email was sent.", confirmationTokenId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ResponseDto> login(@Valid @RequestBody final LoginRequestDto loginRequestDto) {
        try {
            final Authentication authentication = this.authenticationService.authenticate(loginRequestDto);
            final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            // Fingerprints
            final String fingerprint = this.jwtHelper.generateFingerprint();
            final String hash = this.jwtHelper.generateFingerprintHash(fingerprint);

            // Prepare cookie and jwt
            final ResponseCookie cookie = this.jwtHelper.generateFingerprintCookie(fingerprint);
            final String accessJwt = this.jwtHelper.generateAccessJwt(authentication, hash);

            final HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", cookie.toString());
            final ResponseDto responseDto = new ResponseDto("Login successful", accessJwt);
            LOGGER.info("Successfully authenticated {}", userDetails.getId());
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(responseDto);
        } catch (final BadCredentialsException | UsernameNotFoundException e) {
            LOGGER.info("Authentication request failed: {}", e.getMessage());
            final ResponseDto responseDto = new ResponseDto("Credentials dont match.", null);
            return ResponseEntity.badRequest().body(responseDto);
        }
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(final HttpServletRequest request) {
        final String token = FilterUtil.parseBearer(request);
        if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        this.revokedTokenService.revokeToken(DatatypeConverter.printHexBinary(token.getBytes(StandardCharsets.UTF_8)));

        final ResponseCookie responseCookie = ResponseCookie
                .from("fgp", "")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(0L)
                .sameSite("Strict")
                .build();
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", responseCookie.toString());
        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    @PostMapping(value = "/register/{confirmationId}/confirm")
    public ResponseEntity<?> confirmRegistration(@PathVariable final UUID confirmationId,
                                                 @Valid @RequestBody final RegisterConfirmRequestDto registerConfirmRequestDto) {
        final boolean result = this.authenticationService.confirmRegistrationOfUser(confirmationId,
                registerConfirmRequestDto);
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/register/{confirmationId}/resend-code")
    public ResponseEntity<?> resendConfirmationCode(@PathVariable final UUID confirmationId) {
        final boolean result = this.authenticationService.resendConfirmationCode(confirmationId);
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody final PasswordForgetRequestDto passwordForgetRequestDto) {
        final Optional<UUID> forgotPasswordTokenId = this.authenticationService.generateForgotPasswordToken(passwordForgetRequestDto);
        if (forgotPasswordTokenId.isEmpty()) return ResponseEntity.badRequest().build();

        final ResponseDto responseDto = new ResponseDto("Password reset link sent.", forgotPasswordTokenId.get());
        return ResponseEntity.ok(responseDto);


    }

    @PostMapping(value = "/forgot-password/{tokenId}/reset")
    public ResponseEntity<?> resetForgottenPassword(@PathVariable final UUID tokenId,
                                                    @Valid @RequestBody final PasswordChangeRequestDto passwordChangeRequestDto) {
        final boolean result = this.authenticationService.resetPassword(tokenId, passwordChangeRequestDto);
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

}
