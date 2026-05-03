package org.bloggers.ts_users.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
class MandatoryHeadersFilter extends OncePerRequestFilter {

    private final MandatoryHeadersProperties properties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (shouldSkip(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        List<String> missingHeaders = new ArrayList<>();
        for (String header : properties.getRequired()) {
            String value = request.getHeader(header);
            if (value == null || value.isBlank()) {
                missingHeaders.add(header);
            }
        }

        if (!missingHeaders.isEmpty()) {
            writeBadRequest(response, request, missingHeaders);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkip(String uri) {
        for (String route : properties.getRoutesToSkip()) {
            if (uri.equals(route)) {
                return true;
            }
        }
        for (String prefix : properties.getStartsWith()) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private void writeBadRequest(HttpServletResponse response,
                                 HttpServletRequest request,
                                 List<String> missingHeaders) throws IOException {
        String message = "Missing mandatory headers: " + String.join(", ", missingHeaders);
        log.warn("Request rejected for uri={} – {}", request.getRequestURI(), message);

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String json = """
                {"timestamp":"%s","status":400,"error":"Bad Request","message":"%s","path":"%s"}
                """.formatted(
                Instant.now().toString(),
                escapeJson(message),
                escapeJson(request.getRequestURI())
        );

        response.getWriter().write(json);
        response.getWriter().flush();
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
