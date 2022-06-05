package com.travix.medusa.busyflights.controller;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * This handler transforms the exceptions into BusyFlightsErrorResponse, it logs the exception and request parameters,
 * and it generates a unique incidentId that is logged and returned to the client. The incidentId can be used
 * by developers to quickly find the error message in the logs
 */
@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public BusyFlightsErrorResponse handleUnexpectedExceptions(Throwable ex, ServerHttpRequest request) {
        String incidentId = generateIncidentId();
        logExceptionWithWebRequestInfo(ex, request, incidentId);
        return BusyFlightsErrorResponse.builder()
                .status(INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .incidentId(incidentId).build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BusyFlightsErrorResponse> handleUnexpectedExceptions(ResponseStatusException ex, ServerHttpRequest request) {
        String incidentId = generateIncidentId();
        logExceptionWithWebRequestInfo(ex, request, incidentId);
        return ResponseEntity.status(ex.getStatus())
                .body(BusyFlightsErrorResponse.builder()
                        .status(ex.getStatus().value())
                        .message(ex.getMessage())
                        .incidentId(incidentId).build());
    }


    private void logExceptionWithWebRequestInfo(Throwable ex, ServerHttpRequest request, String incidentId) {
        String message = String.format("REST endpoint error :: incidentId=%s, path=%s, parameters=%s", incidentId, request.getPath(), request.getQueryParams());
        log.error(message, ex);
    }

    private String generateIncidentId() {
        return UUID.randomUUID().toString();
    }
}
