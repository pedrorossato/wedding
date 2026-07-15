package com.pedrogio.wedding.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wedding")
public class WeddingProperties {

    private Admin admin = new Admin();
    private Jwt jwt = new Jwt();
    private LoginEncryption loginEncryption = new LoginEncryption();
    private Aws aws = new Aws();
    private Stripe stripe = new Stripe();
    private Cors cors = new Cors();

    public Admin getAdmin() { return admin; }
    public Jwt getJwt() { return jwt; }
    public LoginEncryption getLoginEncryption() { return loginEncryption; }
    public Aws getAws() { return aws; }
    public Stripe getStripe() { return stripe; }
    public Cors getCors() { return cors; }

    public static class Admin {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class Jwt {
        private String secret;
        private long expiration = 86400000L;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpiration() { return expiration; }
        public void setExpiration(long expiration) { this.expiration = expiration; }
    }

    public static class LoginEncryption {
        private String key;

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
    }

    public static class Aws {
        private String region;
        private String bucket;
        private String accessKey;
        private String secretKey;

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public String getBucket() { return bucket; }
        public void setBucket(String bucket) { this.bucket = bucket; }
        public String getAccessKey() { return accessKey; }
        public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    }

    public static class Stripe {
        private String secretKey;
        private String webhookSecret;
        private String publishableKey;

        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
        public String getWebhookSecret() { return webhookSecret; }
        public void setWebhookSecret(String webhookSecret) { this.webhookSecret = webhookSecret; }
        public String getPublishableKey() { return publishableKey; }
        public void setPublishableKey(String publishableKey) { this.publishableKey = publishableKey; }
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:4200";

        public String getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(String allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    }
}