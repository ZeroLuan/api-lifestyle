package br.com.sysmap.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.security.token")
public record JwtProperties(String secret, long expirationMs) {
}
