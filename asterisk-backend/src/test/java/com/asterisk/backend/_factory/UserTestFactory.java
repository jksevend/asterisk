package com.asterisk.backend._factory;

import com.asterisk.backend.adapter.rest.authentication.model.RegisterRequestDto;
import com.asterisk.backend.adapter.rest.user.model.UserChangeRequestDto;
import com.asterisk.backend.adapter.rest.user.model.UserResponseDto;
import com.asterisk.backend.domain.User;
import com.asterisk.backend.infrastructure.UserRole;
import com.asterisk.backend.store.user.UserEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserTestFactory {

    private UUID id = UUID.randomUUID();
    private String firstName = "Johnathan";
    private String lastName = "Doethen";
    private String username = "johndoe";
    private String email = "john@doe.com";
    private String password = "passwordpassword";

    private UserRole role = UserRole.USER;
    private boolean enabled = true;
    private OffsetDateTime created = OffsetDateTime.now();
    private OffsetDateTime updated = OffsetDateTime.now();

    public User newDomainUser() {
        final User user = new User();
        user.setId(this.id);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setRole(this.role);
        user.setEnabled(this.enabled);

        return user;
    }

    public UserEntity newUserEntity() {
        final UserEntity user = new UserEntity();
        user.setId(this.id);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setRole(this.role);
        user.setEnabled(this.enabled);

        return user;
    }

    public UserResponseDto newUserResponseDto() {
        return new UserResponseDto(this.id, this.firstName, this.lastName, this.username, this.email,
                this.created, this.updated);
    }

    public RegisterRequestDto newRegisterRequestDto() {
        return new RegisterRequestDto(this.firstName, this.lastName, this.email, this.username, this.password);
    }

    public UserChangeRequestDto newUserChangeRequestDto() {
        return new UserChangeRequestDto(this.firstName, this.lastName, this.email, this.username);
    }

    public UserTestFactory setId(final UUID id) {
        this.id = id;
        return this;
    }

    public UserTestFactory setPassword(final String password) {
        this.password = password;
        return this;
    }

    public UserTestFactory setEmail(final String email) {
        this.email = email;
        return this;
    }

    public UserTestFactory setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserTestFactory setUsername(final String username) {
        this.username = username;
        return this;
    }

    public UserTestFactory setFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserTestFactory setLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }
    public UserTestFactory setUserRole(final UserRole role) {
        this.role = role;
        return this;
    }
}
