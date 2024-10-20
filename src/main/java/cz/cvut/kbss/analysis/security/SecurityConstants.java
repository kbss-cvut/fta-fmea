package cz.cvut.kbss.analysis.security;

public class SecurityConstants {

    private SecurityConstants() {
        throw new AssertionError();
    }

    public static final String SESSION_COOKIE_NAME = "FSM_JSESSIONID";

    public static final String REMEMBER_ME_COOKIE_NAME = "remember-me";

    public static final String CSRF_COOKIE_NAME = "CSRF-TOKEN";

    public static final String USERNAME_PARAM = "username";

    public static final String PASSWORD_PARAM = "password";

    public static final String SECURITY_CHECK_URI = "/auth/signin";

    public static final String LOGOUT_URI = "/auth/logout";

    public static final String COOKIE_URI = "/";

    /**
     * Session timeout in seconds.
     */
    public static final int SESSION_TIMEOUT = 12 * 60 * 60;

    public static final String ROLE_USER = "user";

    public static final String ROLE_ADMIN = "admin";
}