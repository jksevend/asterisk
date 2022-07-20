package com.asterisk.backend.infrastructure;

public enum UserRole {

    ADMIN("ROLE_ADMIN", "ADMIN"),
    USER("ROLE_USER", "USER");

    private final String roleName;
    private final String value;

    UserRole(final String roleName, final String value) {
        this.roleName = roleName;
        this.value = value;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public String getValue() {
        return this.value;
    }
}
