package com.travix.medusa.busyflights.domain.busyflights;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@Jacksonized
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
