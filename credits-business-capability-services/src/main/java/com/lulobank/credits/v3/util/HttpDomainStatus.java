package com.lulobank.credits.v3.util;

import io.vavr.collection.List;
import io.vavr.control.Option;

public enum HttpDomainStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    LOCKED(423, "Locked"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout");

    private final int value;
    private final String reasonPhrase;

    HttpDomainStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public Series series() {
        return Series.valueOf(this);
    }

    public boolean is4xxClientError() {
        return (series() == Series.CLIENT_ERROR);
    }

    public boolean is5xxServerError() {
        return (series() == Series.SERVER_ERROR);
    }

    public boolean isError() {
        return (is4xxClientError() || is5xxServerError());
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static HttpDomainStatus valueOf(int statusCode) {
        return Option.of(resolve(statusCode))
                .getOrElseThrow(() -> new IllegalArgumentException("No matching constant for [" + statusCode + "]"));
    }

    public static HttpDomainStatus resolve(int statusCode) {
        return List.of(values())
                .find(status -> status.value == statusCode)
                .getOrNull();
    }

    public enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;

        Series(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static Series valueOf(HttpDomainStatus status) {
            return valueOf(status.value);
        }

        public static Series valueOf(int statusCode) {
            return Option.of(resolve(statusCode))
                    .getOrElseThrow(() -> new IllegalArgumentException("No matching constant for [" + statusCode + "]"));
        }

        public static Series resolve(int statusCode) {
            return List.of(values())
                    .find(series -> series.value == (statusCode / 100))
                    .getOrNull();
        }
    }
}
