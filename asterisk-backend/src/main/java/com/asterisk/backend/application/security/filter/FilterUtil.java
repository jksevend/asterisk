package com.asterisk.backend.application.security.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class FilterUtil {

    private static final String BEARER_PREFIX = "Bearer ";


    /**
     * Parses the access jwt from the authorization header
     *
     * @param request Incoming http request
     * @return Value extracted from header or null if no token was found
     */
    public static String parseBearer(final HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
