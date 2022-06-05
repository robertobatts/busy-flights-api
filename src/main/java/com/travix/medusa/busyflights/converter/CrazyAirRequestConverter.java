package com.travix.medusa.busyflights.converter;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.crazyair.CrazyAirRequest;

public class CrazyAirRequestConverter {

    private CrazyAirRequestConverter() {
    }

    public static CrazyAirRequest fromBusyFlightsRequest(BusyFlightsRequest busyFlightsRequest) {
        CrazyAirRequest.CrazyAirRequestBuilder builder = CrazyAirRequest.builder()
                .origin(busyFlightsRequest.getOrigin())
                .destination(busyFlightsRequest.getDestination())
                .departureDate(busyFlightsRequest.getDepartureDate())
                .passengerCount(busyFlightsRequest.getNumberOfPassengers());
        if (busyFlightsRequest.getReturnDate() != null) {
                builder.returnDate(busyFlightsRequest.getReturnDate());
        }
        return builder.build();
    }
}
