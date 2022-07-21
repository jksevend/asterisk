package com.asterisk.backend.adapter;

import com.asterisk.backend._factory.UserTestFactory;
import com.asterisk.backend._integration.WebIntegrationTest;
import com.asterisk.backend._integration.authentication.WithFakeAsteriskUser;
import com.asterisk.backend.adapter.rest.authentication.model.PasswordChangeRequestDto;
import com.asterisk.backend.adapter.rest.user.UserController;
import com.asterisk.backend.adapter.rest.user.model.UserChangeRequestDto;
import com.asterisk.backend.adapter.rest.user.model.UserResponseDto;
import com.asterisk.backend.application.common.UserDetailsServiceImpl;
import com.asterisk.backend.application.security.domain.UserSecurity;
import com.asterisk.backend.application.security.error.ForbiddenErrorHandler;
import com.asterisk.backend.application.security.error.UnauthorizedErrorHandler;
import com.asterisk.backend.application.security.jwt.JwtHelper;
import com.asterisk.backend.domain.User;
import com.asterisk.backend.infrastructure.UserRole;
import com.asterisk.backend.mapper.UserMapper;
import com.asterisk.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(
        controllers = UserController.class,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = {JwtHelper.class, UnauthorizedErrorHandler.class, ForbiddenErrorHandler.class,
                        UserMapper.class, UserSecurity.class}))
public class UserControllerWebIT extends WebIntegrationTest {

    public static final String USER_ID = "550e8400-e29b-11d4-a716-446655440000";

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testReadUserUnauthorized() throws Exception {
        // GIVEN
        final UUID id = UUID.randomUUID();

        // WHEN
        final MvcResult result = this.mvc.perform(get("/user/" + id))
                .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithFakeAsteriskUser(id = USER_ID)
    public void testReadUserForbidden() throws Exception {
        // GIVEN
        final UUID id = UUID.randomUUID();
        final UserTestFactory userTestFactory = new UserTestFactory().setId(id);
        when(this.userService.readUser(id)).thenReturn(Optional.ofNullable(userTestFactory.newDomainUser()));

        // WHEN
        final MvcResult result = this.mvc.perform(get("/user/" + id))
                .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithFakeAsteriskUser(id = USER_ID, role = UserRole.ADMIN)
    public void testReadUserAsAdmin() throws Exception {
        // GIVEN
        final UUID id = UUID.randomUUID();
        final UserTestFactory userTestFactory = new UserTestFactory().setId(id);
        when(this.userService.readUser(id)).thenReturn(Optional.ofNullable(userTestFactory.newDomainUser()));

        // WHEN
        final MvcResult result = this.mvc.perform(get("/user/" + id))
                .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithFakeAsteriskUser(id = USER_ID)
    public void testReadUserSuccess() throws Exception {
        // GIVEN
        final UUID id = UUID.fromString(USER_ID);
        final UserTestFactory userTestFactory = new UserTestFactory().setId(id);
        when(this.userService.readUser(id)).thenReturn(Optional.ofNullable(userTestFactory.newDomainUser()));

        // WHEN
        final MvcResult result = this.mvc.perform(get("/user/" + USER_ID))
                .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(this.objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class))
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(userTestFactory.newUserResponseDto());
    }

    @Test
    @WithFakeAsteriskUser(id = USER_ID)
    public void testUpdateUserSuccess() throws Exception {
        // GIVEN
        final UUID id = UUID.fromString(USER_ID);
        final UserTestFactory userTestFactory = new UserTestFactory();
        final UserChangeRequestDto changeRequestDto = userTestFactory.newUserChangeRequestDto();

        when(this.userService.updateUser(id, changeRequestDto)).thenReturn(true);

        final User user = userTestFactory.setId(id).newDomainUser();

        when(this.userService.readUser(id)).thenReturn(Optional.of(user));

        // WHEN
        final MvcResult result =
                this.mvc.perform(put("/user/" + USER_ID).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(changeRequestDto)))
                        .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithFakeAsteriskUser(id = USER_ID, role = UserRole.ADMIN)
    public void testUpdateUserAsAdminSuccess() throws Exception {
        // GIVEN
        final UUID id = UUID.randomUUID();
        final UserTestFactory userTestFactory = new UserTestFactory();
        final UserChangeRequestDto changeRequestDto = userTestFactory.newUserChangeRequestDto();

        when(this.userService.updateUser(id, changeRequestDto)).thenReturn(true);

        final User user = userTestFactory.setId(id).newDomainUser();

        when(this.userService.readUser(id)).thenReturn(Optional.of(user));

        // WHEN
        final MvcResult result =
                this.mvc.perform(put("/user/" + id).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(changeRequestDto)))
                        .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithFakeAsteriskUser(id = USER_ID)
    public void testChangePasswordSuccess() throws Exception {
        // GIVEN
        final UUID id = UUID.fromString(USER_ID);
        final UserTestFactory userTestFactory = new UserTestFactory();
        final PasswordChangeRequestDto passwordChangeRequestDto = new PasswordChangeRequestDto("newpassword",
                "newpassword");

        when(this.userService.changePassword(id, passwordChangeRequestDto)).thenReturn(true);

        final User user = userTestFactory.setId(id).newDomainUser();

        when(this.userService.readUser(id)).thenReturn(Optional.of(user));

        // WHEN
        final MvcResult result =
                this.mvc.perform(post("/user/" + USER_ID + "/change-password").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(passwordChangeRequestDto)))
                        .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithFakeAsteriskUser(id = USER_ID, role = UserRole.ADMIN)
    public void testChangePasswordAsAdminSuccess() throws Exception {
        // GIVEN
        final UUID id = UUID.randomUUID();
        final UserTestFactory userTestFactory = new UserTestFactory();
        final PasswordChangeRequestDto passwordChangeRequestDto = new PasswordChangeRequestDto("newpassword",
                "newpassword");

        when(this.userService.changePassword(id, passwordChangeRequestDto)).thenReturn(true);

        final User user = userTestFactory.setId(id).newDomainUser();

        when(this.userService.readUser(id)).thenReturn(Optional.of(user));

        // WHEN
        final MvcResult result =
                this.mvc.perform(post("/user/" + id + "/change-password").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(passwordChangeRequestDto)))
                        .andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
