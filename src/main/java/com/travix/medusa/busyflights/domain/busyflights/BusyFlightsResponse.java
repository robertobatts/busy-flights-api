package com.travix.medusa.busyflights.domain.busyflights;

import com.travix.medusa.busyflights.enums.Supplier;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusyFlightsResponse {

    private String airline;
    private Supplier supplier;
    private double fare;
    private String departureAirportCode;
    private String destinationAirportCode;
    //TODO convert to LocalDateTime
    private String departureDate;
    private String arrivalDate;

}
