package com.travix.medusa.busyflights.domain.busyflights;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BusyFlightsErrorResponse {
    private int status;
    private String message;
    private String incidentId;
}
