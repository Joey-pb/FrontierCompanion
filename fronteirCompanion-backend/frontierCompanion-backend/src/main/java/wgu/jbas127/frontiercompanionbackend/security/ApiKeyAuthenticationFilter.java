package wgu.jbas127.frontiercompanionbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;

@Component
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final String androidApiKey;

    private static final String API_KEY_HEADER = "X-API-KEY";

    public ApiKeyAuthenticationFilter(@Value( "${android.api.key:#{null}}" ) String androidApiKey) {
        if (androidApiKey == null || androidApiKey.equals("MISSING_ANDROID_KEY")) {
            log.error("CRITICAL: Android API key is missing.");
            this.androidApiKey = "UNCONFIGURED_KEY_SECURE_REJECTION";
        } else {
            log.info("Android API key configured successfully.");
            this.androidApiKey = androidApiKey;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey != null && androidApiKey != null &&
                MessageDigest.isEqual(apiKey.getBytes(StandardCharsets.UTF_8),
                        androidApiKey.getBytes(StandardCharsets.UTF_8))) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(apiKey,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANDROID_APP")));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("Authenticated Android app with API key");
        }

        filterChain.doFilter(request, response);

    }
}
