package com.travix.medusa.busyflights.domain.busyflights;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Value
@Builder
public class BusyFlightsRequest {

    @NotBlank
    private String origin;
    @NotBlank
    private String destination;
    @NotNull
    private String departureDate;
    private String returnDate;
    @NotNull
    @Positive
    private Integer numberOfPassengers;

}
