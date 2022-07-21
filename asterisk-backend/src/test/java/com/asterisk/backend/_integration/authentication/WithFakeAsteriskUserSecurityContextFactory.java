package com.asterisk.backend._integration.authentication;

import com.asterisk.backend.application.common.UserDetailsImpl;
import com.asterisk.backend.infrastructure.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WithFakeAsteriskUserSecurityContextFactory  implements WithSecurityContextFactory<WithFakeAsteriskUser> {

    @Override
    public SecurityContext createSecurityContext(final WithFakeAsteriskUser customUser) {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();

        final String password = "password";

        final UserDetailsImpl userDetails = new UserDetailsImpl(UUID.fromString(customUser.id()), "username", "email@email.com",
                password, true, List.of(new SimpleGrantedAuthority(customUser.role().getRoleName())));

        final Authentication auth =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
