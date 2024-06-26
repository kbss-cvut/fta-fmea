package cz.cvut.kbss.analysis.util;

public enum ConfigParam {

    SECURITY_SAME_SITE("security.sameSite"),

    APP_CONTEXT("appContext"),

    OIDC_ROLE_CLAIM("oidc.RoleClaim"),

    CORS_ALLOWED_ORIGINS("cors.allowedOrigins");

    private final String name;

    ConfigParam(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}