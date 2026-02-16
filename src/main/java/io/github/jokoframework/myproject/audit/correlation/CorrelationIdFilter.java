package io.github.jokoframework.myproject.audit.correlation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that ensures every request has a correlation ID.
 * If the client sends X-Correlation-Id header, that value is used.
 * Otherwise, a new UUID is generated.
 *
 * @author ana bernal
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String correlationId = request.getHeader(CorrelationIdHolder.CORRELATION_ID_HEADER);

            if (correlationId == null || correlationId.trim().isEmpty()) {
                correlationId = UUID.randomUUID().toString();
                LOGGER.debug("No correlation ID in request, generated: {}", correlationId);
            } else {
                LOGGER.debug("Using correlation ID from request header: {}", correlationId);
            }

            // Store in thread-local and MDC
            CorrelationIdHolder.set(correlationId);
            MDC.put(CorrelationIdHolder.CORRELATION_ID_MDC_KEY, correlationId);

            // Return it in response header for client tracing
            response.setHeader(CorrelationIdHolder.CORRELATION_ID_HEADER, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            // Clean up to prevent thread-local leaks
            CorrelationIdHolder.clear();
            MDC.remove(CorrelationIdHolder.CORRELATION_ID_MDC_KEY);
        }
    }
}