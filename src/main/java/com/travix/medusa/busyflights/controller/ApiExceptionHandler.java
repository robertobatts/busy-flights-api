package com.travix.medusa.busyflights.controller;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * This handler transforms the exceptions into BusyFlightsErrorResponse,
 * it generates a unique incidentId that is logged and returned to the client. The incidentId can be used
 * by developers to quickly find the error message in the logs
 */
@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public BusyFlightsErrorResponse handleUnexpectedExceptions(Throwable ex) {
        String incidentId = generateIncidentId();
        logExceptionWithWebRequestInfo(ex, incidentId);
        return BusyFlightsErrorResponse.builder()
                .status(INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .incidentId(incidentId).build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BusyFlightsErrorResponse> handleUnexpectedExceptions(ResponseStatusException ex) {
        String incidentId = generateIncidentId();
        logExceptionWithWebRequestInfo(ex, incidentId);
        return ResponseEntity.status(ex.getStatus())
                .body(BusyFlightsErrorResponse.builder()
                        .status(ex.getStatus().value())
                        .message(ex.getMessage())
                        .incidentId(incidentId).build());
    }


    private void logExceptionWithWebRequestInfo(Throwable ex, String incidentId) {
        String message = String.format("REST endpoint error :: incidentId=%s", incidentId);
        log.error(message, ex);
    }

    private String generateIncidentId() {
        return UUID.randomUUID().toString();
    }
}
