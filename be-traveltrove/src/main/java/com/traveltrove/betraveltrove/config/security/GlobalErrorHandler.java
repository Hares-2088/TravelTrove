package com.traveltrove.betraveltrove.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.joselion.maybe.Maybe;
import com.traveltrove.betraveltrove.models.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(-2)
@Component
@Slf4j
public record GlobalErrorHandler(ObjectMapper mapper) implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable error) {
        final var response = exchange.getResponse();
        final var bufferFactory = response.bufferFactory();

        log.error("Handling global exception: {}", error.getMessage(), error);

        if (error instanceof ResponseStatusException statusError) {
            final var status = statusError.getStatusCode();
            response.setStatusCode(status);

            log.warn("ResponseStatusException detected. Status: {}, Message: {}", status, error.getMessage());

            if (status.equals(HttpStatus.NOT_FOUND)) {
                log.info("Resource not found: {}", exchange.getRequest().getURI());

                final var body = this.makeBodyBytes("Not Found")
                        .map(bufferFactory::wrap);

                return response.writeWith(body);
            }
        }

        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        log.error("Internal Server Error occurred: {}", error.getMessage());

        return response.writeWith(
                this.makeBodyBytes(error.getMessage())
                        .map(bufferFactory::wrap));
    }

    public Mono<Void> handleAuthenticationError(final ServerWebExchange exchange, final AuthenticationException error) {
        final var response = exchange.getResponse();
        final var message = "Unauthorized. %s".formatted(error.getMessage());

        log.warn("Authentication error: {}, URI: {}", error.getMessage(), exchange.getRequest().getURI());

        final var body = this.makeBodyBytes(message)
                .map(response.bufferFactory()::wrap);

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(body);
    }

    public Mono<Void> handleAccessDenied(final ServerWebExchange exchange, final AccessDeniedException error) {
        final var response = exchange.getResponse();
        final var message = "Permission denied";

        log.warn("Access Denied for URI: {}. Message: {}", exchange.getRequest().getURI(), error.getMessage());

        final var body = this.makeBodyBytes(message)
                .map(response.bufferFactory()::wrap);

        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.writeWith(body);
    }

    private Mono<byte[]> makeBodyBytes(final String message) {
        return Mono.create(sink -> {
            log.debug("Creating response body for message: {}", message);

            Maybe.just(message)
                    .map(ErrorMessage::from)
                    .resolve(mapper::writeValueAsBytes)
                    .doOnSuccess(sink::success)
                    .doOnError(sink::error);
        });
    }
}
