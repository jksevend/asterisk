package com.asterisk.backend.service;

import com.asterisk.backend.adapter.rest.authentication.model.*;
import com.asterisk.backend.domain.ForgotPasswordToken;
import com.asterisk.backend.domain.RegisterConfirmationToken;
import com.asterisk.backend.domain.User;
import com.asterisk.backend.infrastructure.ConfirmationCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserService userService;
    private final RegisterConfirmationService registerConfirmationService;
    private final ForgotPasswordService forgotPasswordService;

    @Autowired
    public AuthenticationService(final AuthenticationManager authenticationManager,
                                 final EmailService emailService,
                                 final UserService userService,
                                 final RegisterConfirmationService registerConfirmationService,
                                 final ForgotPasswordService forgotPasswordService) {
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userService = userService;
        this.registerConfirmationService = registerConfirmationService;
        this.forgotPasswordService = forgotPasswordService;
    }

    /**
     * Registers a new user.
     *
     * @param registerRequestDto user information
     * @return the id of the generated confirmation token
     */
    public UUID registerNewUser(final RegisterRequestDto registerRequestDto) {
        // Creating and saving
        final User user = this.userService.createUser(registerRequestDto);

        // Confirmation token expires 10 minutes from now
        final RegisterConfirmationToken registerConfirmationToken = this.registerConfirmationService.createToken(user);

        // Send the email
        this.emailService.sendRegisterConfirmationEmail(user, registerConfirmationToken.getConfirmationCode());
        LOGGER.info("Register confirmation email sent to {}", registerRequestDto.email());

        return registerConfirmationToken.getId();
    }

    /**
     * Attempts to authenticate user credentials
     *
     * @param loginRequestDto email and password
     * @return Populated Authentication object
     */
    public Authentication authenticate(final LoginRequestDto loginRequestDto) {
        final String email = loginRequestDto.email();
        final String password = loginRequestDto.password();
        // Attempt login
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Re-Set to current authentication
        authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication;
    }

    /**
     * Uses the id of the underlying confirmation token and the confirmation code
     * to enable an user account
     *
     * @param confirmationId            confirmation token id
     * @param registerConfirmRequestDto confirmation code
     * @return true if the confirmation succeeds
     */
    public boolean confirmRegistrationOfUser(final UUID confirmationId, final RegisterConfirmRequestDto registerConfirmRequestDto) {
        // Check for token existence
        try {
            final RegisterConfirmationToken registerConfirmationToken =
                    this.registerConfirmationService.findToken(confirmationId);

            // Check if the token is expired
            if (registerConfirmationToken.isExpired()) {
                this.registerConfirmationService.deleteToken(registerConfirmationToken);
                return false;
            }

            // Check for token matches
            if (!registerConfirmationToken.codeMatches(registerConfirmRequestDto.code())) {
                return false;
            }

            // Enable the user
            final User user = registerConfirmationToken.getUser();
            user.setEnabled(true);
            this.userService.saveUser(user);
            this.registerConfirmationService.deleteToken(registerConfirmationToken);
            return true;
        } catch (final EntityNotFoundException e) {
            return false;
        }

    }

    /**
     * Resends a confirmation code to the users email address
     * <p>
     * Uses provided confirmation token id to determine the token's user
     *
     * @param confirmationId confirmation token id
     */
    public boolean resendConfirmationCode(final UUID confirmationId) {
        try {
            // Check for token existence
            RegisterConfirmationToken registerConfirmationToken =
                    this.registerConfirmationService.findToken(confirmationId);

            // Check if the token is expired
            if (registerConfirmationToken.isExpired()) {
                this.registerConfirmationService.deleteToken(registerConfirmationToken);
                return false;
            }

            final User user = registerConfirmationToken.getUser();
            registerConfirmationToken.setConfirmationCode(ConfirmationCodeUtil.generateRegisterConfirmationCode());
            registerConfirmationToken = this.registerConfirmationService.saveToken(registerConfirmationToken);
            this.emailService.sendRegisterConfirmationEmail(user, registerConfirmationToken.getConfirmationCode());
            return true;
        } catch (final EntityNotFoundException e) {
            return false;
        }
    }

    /**
     * @param passwordForgetRequestDto
     * @return
     */
    public Optional<UUID> generateForgotPasswordToken(final PasswordForgetRequestDto passwordForgetRequestDto) {
        final Optional<User> userOptional = this.userService.findByEmail(passwordForgetRequestDto.email());

        if (userOptional.isEmpty()) return Optional.empty();

        final ForgotPasswordToken forgotPasswordToken = this.forgotPasswordService.createToken(userOptional.get());
        this.emailService.sendForgotPasswordEmail(userOptional.get(), forgotPasswordToken.getId());
        return Optional.of(forgotPasswordToken.getId());

    }

    /**
     * @param tokenId
     * @param passwordChangeRequestDto
     * @return
     */
    public boolean resetPassword(final UUID tokenId, final PasswordChangeRequestDto passwordChangeRequestDto) {
        try {
            final ForgotPasswordToken forgotPasswordToken = this.forgotPasswordService.findToken(tokenId);

            // Check expiration
            if (forgotPasswordToken.isExpired()) return false;

            // Perform a password change
            final boolean passwordChangeResult = this.userService.changePassword(forgotPasswordToken.getUser().getId(),
                    passwordChangeRequestDto);
            if (!passwordChangeResult) return false;


            this.forgotPasswordService.deleteToken(forgotPasswordToken);
            return true;
        } catch (final EntityNotFoundException e) {
            return false;
        }

    }
}
