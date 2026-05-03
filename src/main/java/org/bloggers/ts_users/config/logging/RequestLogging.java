package org.bloggers.ts_users.config.logging;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bloggers.ts_users.annotations.HideResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
class RequestLogging extends OncePerRequestFilter {

    private final Tracer tracer;

    private static final String PASSWORD_PATTERN = "(\"password\"\\s*:\\s*\")[^\"]*\"";
    private static final String PASSWORD_HASH_PATTERN = "(\"passwordHash\"\\s*:\\s*\")[^\"]*\"";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpRequest,
                                    @NonNull HttpServletResponse httpResponse,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        boolean isMultipart = httpRequest.getContentType() != null
                && httpRequest.getContentType().startsWith("multipart/");
        boolean isDownload = httpResponse.getContentType() != null
                && httpResponse.getContentType().startsWith("application/octet-stream");

        var span = tracer.currentSpan();
        boolean spanStartedByUs = false;
        if (span == null) {
            span = tracer.nextSpan().name(httpRequest.getMethod() + " " + httpRequest.getRequestURI()).start();
            spanStartedByUs = true;
        }

        Tracer.SpanInScope scope = null;
        if (span != null) {
            scope = tracer.withSpan(span);
            httpResponse.setHeader("X-request-id", span.context().traceId());
        }

        try {
            if (isMultipart) {
                logRequest(httpRequest, "<<Multipart content>>");
                filterChain.doFilter(httpRequest, httpResponse);
                logLatency(httpResponse, startTime);
            } else if (isDownload) {
                logRequest(httpRequest, "<<Download content>>");
                filterChain.doFilter(httpRequest, httpResponse);
                logLatency(httpResponse, startTime);
            } else {
                CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                        new CachedBodyHttpServletRequest(httpRequest);
                String body = IOUtils.toString(cachedBodyHttpServletRequest.getInputStream(),
                        cachedBodyHttpServletRequest.getCharacterEncoding());
                logRequest(cachedBodyHttpServletRequest, maskSensitiveData(body));
                ContentCachingResponseWrapper cachedResponse =
                        new ContentCachingResponseWrapper(httpResponse);

                try {
                    filterChain.doFilter(cachedBodyHttpServletRequest, cachedResponse);
                } finally {
                    String responseBody = new String(cachedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
                    boolean hideResponse = shouldHideResponse(cachedBodyHttpServletRequest);

                    log.info("Response: Latency = {} ms, status={}, body={}",
                            (System.currentTimeMillis() - startTime), httpResponse.getStatus(),
                            hideResponse ? "<<hidden>>" : maskSensitiveData(responseBody));
                    cachedResponse.copyBodyToResponse();
                }
            }
        } finally {
            if (scope != null) {
                scope.close();
            }
            if (spanStartedByUs && span != null) {
                span.end();
            }
        }
    }

    private boolean shouldHideResponse(HttpServletRequest request) {
        Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.hasMethodAnnotation(HideResponse.class);
        }
        return false;
    }

    private static String maskSensitiveData(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }
        return body.replaceAll(PASSWORD_PATTERN, "$1***\"")
                   .replaceAll(PASSWORD_HASH_PATTERN, "$1***\"");
    }

    private static void logLatency(@org.jspecify.annotations.NonNull HttpServletResponse httpResponse, long startTime) {
        log.info("Response: Latency = {} ms, status={}",
                (System.currentTimeMillis() - startTime), httpResponse.getStatus());
    }

    private void logRequest(HttpServletRequest cachedBodyHttpServletRequest, String body) {
        log.info("Request: method={}, uri={}, body={}, headers={}, clientIps={}",
                cachedBodyHttpServletRequest.getMethod(),
                cachedBodyHttpServletRequest.getRequestURI(), body,
                getHeaders(cachedBodyHttpServletRequest),
                getClientIpAddress(cachedBodyHttpServletRequest));
    }

    private Map<String, String> getHeaders(HttpServletRequest cachedBodyHttpServletRequest) {
        Map<String, String> headers = new HashMap<>();
        cachedBodyHttpServletRequest.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> headers.put(headerName,
                        cachedBodyHttpServletRequest.getHeader(headerName)));
        return headers;
    }

    private String getClientIpAddress(HttpServletRequest httpServletRequest) {
        String[] ipHeaderCandidates = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };
        for (String header : ipHeaderCandidates) {
            String ipAddress = httpServletRequest.getHeader(header.toLowerCase());
            if (ipAddress != null
                    && !StringUtils.isEmpty(ipAddress)
                    && !ipAddress.equalsIgnoreCase("unknown")) {
                return ipAddress;
            }
        }
        return httpServletRequest.getRemoteAddr();
    }
}
