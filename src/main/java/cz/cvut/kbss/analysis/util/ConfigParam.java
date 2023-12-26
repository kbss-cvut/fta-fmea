package cz.cvut.kbss.analysis.util;

public enum ConfigParam {

    SECURITY_SAME_SITE("security.sameSite"),

    REPOSITORY_URL("repositoryUrl"),
    DRIVER("driver"),
    FORM_GEN_REPOSITORY_URL("formGenRepositoryUrl"),
    FORM_GEN_SERVICE_URL("formGenServiceUrl"),

    APP_CONTEXT("appContext"),

    SMTP_HOST("smtp.host"),
    SMTP_PORT("smtp.port"),
    SMTP_USER("smtp.user"),
    SMTP_PASSWORD("smtp.password"),
    E_DISPLAY_NAME("email.displayName"),
    E_FROM_ADDRESS("email.from"),
    E_CC_ADDRESS_LIST("email.cc"),
    E_BCC_ADDRESS_LIST("email.bcc"),
    E_REPLY_TO_ADDRESS_LIST("email.replyTo"),

    E_PASSWORD_RESET_SUBJECT("email.passwordResetSubject"),
    E_PASSWORD_RESET_CONTENT("email.passwordResetContent"),

    E_INVITATION_SUBJECT("email.invitationSubject"),
    E_INVITATION_CONTENT("email.invitationContent"),

    E_PASSWORD_CHANGE_SUBJECT("email.passwordChangeSubject"),
    E_PASSWORD_CHANGE_CONTENT("email.passwordChangeContent"),

    E_PROFILE_UPDATE_SUBJECT("email.profileUpdateSubject"),
    E_PROFILE_UPDATE_CONTENT("email.profileUpdateContent"),

    OIDC_ROLE_CLAIM("oidc.roleClaim"),

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