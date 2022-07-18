package com.asterisk.backend.application.security;

import com.asterisk.backend.application.common.UserDetailsServiceImpl;
import com.asterisk.backend.application.security.error.ForbiddenErrorHandler;
import com.asterisk.backend.application.security.error.UnauthorizedErrorHandler;
import com.asterisk.backend.application.security.filter.AccessTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final String AUTH = "/auth/**";
    private static final String LOGOUT = "/auth/logout";
    private static final String HEALTHCHECK = "/healthcheck";
    private static final String VERSION = "/version";
    private static final String FAVICON = "/favicon.ico";

    private final AccessTokenFilter accessTokenFilter;
    private final UnauthorizedErrorHandler unauthorizedErrorHandler;
    private final ForbiddenErrorHandler forbiddenErrorHandler;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(final AccessTokenFilter accessTokenFilter,
                          final UnauthorizedErrorHandler unauthorizedErrorHandler,
                          final ForbiddenErrorHandler forbiddenErrorHandler,
                          final UserDetailsServiceImpl userDetailsService) {
        this.accessTokenFilter = accessTokenFilter;
        this.unauthorizedErrorHandler = unauthorizedErrorHandler;
        this.forbiddenErrorHandler = forbiddenErrorHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGOUT).authenticated()
                .antMatchers(AUTH, HEALTHCHECK, VERSION, FAVICON).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(this.unauthorizedErrorHandler)
                .accessDeniedHandler(this.forbiddenErrorHandler);

        http.authenticationProvider(this.daoAuthenticationProvider());

        final CommonsRequestLoggingFilter commonsRequestLoggingFilter = new CommonsRequestLoggingFilter();
        commonsRequestLoggingFilter.setIncludeHeaders(true);
        commonsRequestLoggingFilter.setMaxPayloadLength(2048);
        commonsRequestLoggingFilter.setIncludePayload(true);
        http.addFilterBefore(commonsRequestLoggingFilter, WebAsyncManagerIntegrationFilter.class);
        http.addFilterBefore(this.accessTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
        return daoAuthenticationProvider;
    }
}