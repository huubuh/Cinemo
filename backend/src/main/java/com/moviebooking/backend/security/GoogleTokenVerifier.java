package com.moviebooking.backend.security;

import com.moviebooking.backend.exception.AppException;
import com.moviebooking.backend.exception.ErrorCode;
import com.moviebooking.backend.util.EmailUtils;
import com.nimbusds.jose.RemoteKeySourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleTokenVerifier {
    private final String clientId;
    private final NimbusJwtDecoder decoder;

    @Autowired
    public GoogleTokenVerifier(@Value("${google.client-id:}") String clientId) {
        this(clientId, "https://www.googleapis.com/oauth2/v3/certs");
    }

    // Cho phép dùng JWKS server cục bộ trong kiểm thử chữ ký, cache và xoay khóa.
    GoogleTokenVerifier(String clientId, String jwkSetUri) {
        this.clientId = clientId.trim();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        this.decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .restOperations(new RestTemplate(factory))
                .build();
        var timestamps = JwtValidators.createDefault();
        decoder.setJwtValidator(jwt -> {
            var result = timestamps.validate(jwt);
            if (result.hasErrors()) {
                return result;
            }
            return validClaims(jwt) ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        });
    }

    private boolean validClaims(Jwt jwt) {
        String issuer = jwt.getClaimAsString("iss");
        String subject = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        return ("https://accounts.google.com".equals(issuer) || "accounts.google.com".equals(issuer))
                && jwt.getAudience() != null && jwt.getAudience().contains(clientId)
                && jwt.getExpiresAt() != null
                && subject != null && !subject.isBlank() && subject.length() <= 255
                && email != null && !email.isBlank() && email.length() <= 254 && email.contains("@")
                && Boolean.TRUE.equals(jwt.getClaims().get("email_verified"));
    }

    public GoogleIdentity verify(String idToken) {
        if (clientId.isBlank()) {
            throw new AppException(ErrorCode.GOOGLE_UNAVAILABLE);
        }
        try {
            Jwt jwt = decoder.decode(idToken);
            String email = EmailUtils.normalize(jwt.getClaimAsString("email"));
            String name = jwt.getClaimAsString("name");
            if (name == null || name.isBlank() || name.length() > 255) {
                name = email;
            }
            return new GoogleIdentity(jwt.getSubject(), email, name);
        } catch (JwtException | IllegalArgumentException exception) {
            // Không log exception: có thể chứa credential từ request hoặc Google.
            for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
                if (cause instanceof RemoteKeySourceException) {
                    throw new AppException(ErrorCode.GOOGLE_UNAVAILABLE);
                }
            }
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
        }
    }
}
