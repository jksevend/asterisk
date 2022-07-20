package com.asterisk.backend.application.common;

import com.asterisk.backend.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UserDetailsImpl implements UserDetails {

    private final UUID id;
    private final String username;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(final UUID id,
                           final String username,
                           final String email,
                           final String password,
                           final boolean enabled,
                           final Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.authorities = Collections.unmodifiableCollection(authorities);
    }

    public static UserDetailsImpl of(final User user) {
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getEmail(),
                user.getPassword(), user.isEnabled(), List.of(new SimpleGrantedAuthority(user.getRole().getRoleName())));
    }



    public UUID getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.copyOf(this.authorities);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}

