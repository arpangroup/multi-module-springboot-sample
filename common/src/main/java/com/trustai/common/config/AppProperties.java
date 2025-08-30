package com.trustai.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Auth auth = new Auth();
    private final Jwt jwt = new Jwt();

    public Auth getAuth() { return auth; }
    public Jwt getJwt() { return jwt; }

    public static class Auth {
        private String defaultRole;

        public String getDefaultRole() { return defaultRole; }
        public void setDefaultRole(String defaultRole) { this.defaultRole = defaultRole; }
    }

    public static class Jwt {
        private String secret;
        private long expirationMs;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }

        public long getExpirationMs() { return expirationMs; }
        public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
    }
}