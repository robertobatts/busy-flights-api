package com.travix.medusa.busyflights.converter;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.toughjet.ToughJetRequest;

import java.time.format.DateTimeFormatter;

public class ToughJetRequestConverter {

    private ToughJetRequestConverter() {
    }

    public static ToughJetRequest fromBusyFlightsRequest(BusyFlightsRequest busyFlightsRequest) {
        ToughJetRequest.ToughJetRequestBuilder builder = ToughJetRequest.builder()
                .from(busyFlightsRequest.getOrigin())
                .to(busyFlightsRequest.getDestination())
                .outboundDate(busyFlightsRequest.getDepartureDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .numberOfAdults(busyFlightsRequest.getNumberOfPassengers());
        if (busyFlightsRequest.getReturnDate() != null) {
            builder.inboundDate(busyFlightsRequest.getReturnDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        return builder.build();
    }
}
