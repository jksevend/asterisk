package com.asterisk.backend.adapter;

import com.asterisk.backend._integration.WebIntegrationTest;
import com.asterisk.backend._integration.authentication.WithFakeAsteriskUser;
import com.asterisk.backend.adapter.rest.authentication.AuthenticationController;
import com.asterisk.backend.adapter.rest.authentication.model.LoginRequestDto;
import com.asterisk.backend.application.common.UserDetailsImpl;
import com.asterisk.backend.application.common.UserDetailsServiceImpl;
import com.asterisk.backend.application.security.error.ForbiddenErrorHandler;
import com.asterisk.backend.application.security.error.UnauthorizedErrorHandler;
import com.asterisk.backend.application.security.jwt.JwtHelper;
import com.asterisk.backend.service.AuthenticationService;
import com.asterisk.backend.service.RevokedTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

import static com.asterisk.backend.adapter.UserControllerWebIT.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(
        controllers = AuthenticationController.class,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = {JwtHelper.class, UnauthorizedErrorHandler.class, ForbiddenErrorHandler.class,}))
public class AuthenticationControllerWebIT extends WebIntegrationTest {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private RevokedTokenService revokedTokenService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }

    private static Stream<Arguments> loginBodyAndExpectedStatusCodes() {
        // TODO: Input too long
        return Stream.of(
                // Not an email
                Arguments.of(new LoginRequestDto("johndoe", "passwordpassword"), HttpStatus.BAD_REQUEST.value())
        );
    }

    @ParameterizedTest
    @MethodSource("loginBodyAndExpectedStatusCodes")
    public void testLoginUnsuccessful(final LoginRequestDto loginRequestDto, final int status) throws Exception {
        // WHEN
        final MvcResult result = this.mvc.perform(post("/auth/login").with(csrf())
                        .content(this.objectMapper.writeValueAsString(loginRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(status);
    }

    @Test
    public void testLoginSuccessful() throws Exception {
        // GIVEN
        final LoginRequestDto loginRequestDto = new LoginRequestDto("johndoe@mail.com", "passwordpassword");
        final UserDetailsImpl userDetails = new UserDetailsImpl(UUID.randomUUID(), "username", "johndoe@mail.com",
                "passwordpassword", true, Collections.emptySet());
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "passwordpassword",
                userDetails.getAuthorities());

        when(this.authenticationService.authenticate(loginRequestDto)).thenReturn(authentication);

        // WHEN
        final MvcResult result = this.mvc.perform(post("/auth/login").with(csrf())
                        .content(this.objectMapper.writeValueAsString(loginRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getHeader("Set-Cookie")).isNotNull();
    }

    @Test
    @WithFakeAsteriskUser(id = USER_ID)
    public void testLogoutSuccessful() throws Exception {
        // GIVEN
        when(this.revokedTokenService.isTokenRevoked(anyString())).thenReturn(false);

        // WHEN
        final MvcResult result =
                this.mvc.perform(post("/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", "ABC"))
                        .with(csrf()))
                        .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    public void testLogoutUnauthorized() throws Exception {
        // WHEN
        final MvcResult result = this.mvc.perform(post("/auth/logout").with(csrf())).andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(401);
    }
}
