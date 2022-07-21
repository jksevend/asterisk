package com.asterisk.backend._integration.authentication;

import com.asterisk.backend.infrastructure.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Indicates that an authentication object is created without creating the underlying user for it.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithFakeAsteriskUserSecurityContextFactory.class)
public @interface WithFakeAsteriskUser {

    String id() default "";

    UserRole role() default UserRole.USER;
}
