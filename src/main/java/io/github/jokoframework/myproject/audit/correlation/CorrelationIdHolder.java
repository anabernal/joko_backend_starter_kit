package io.github.jokoframework.myproject.audit.correlation;

/**
 * Thread-local holder for the correlation ID.
 *
 * @author ana bernal
 */
public class CorrelationIdHolder {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    private CorrelationIdHolder() {
        // Utility class
    }

    public static String get() {
        return CORRELATION_ID.get();
    }

    public static void set(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    public static void clear() {
        CORRELATION_ID.remove();
    }
}