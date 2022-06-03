package com.travix.medusa.busyflights.converter;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.crazyair.CrazyAirResponse;
import com.travix.medusa.busyflights.enums.Supplier;
import com.travix.medusa.busyflights.utils.DateUtils;

import java.time.format.DateTimeFormatter;

public class CrazyAirResponseConverter {

    private CrazyAirResponseConverter() {
    }

    public static BusyFlightsResponse toBusyFlightResponse(CrazyAirResponse response) {
        String departureDate = DateUtils.reformatStringDate(response.getDepartureDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeFormatter.ISO_DATE_TIME);
        String arrivalDate = DateUtils.reformatStringDate(response.getArrivalDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeFormatter.ISO_DATE_TIME);
        return BusyFlightsResponse.builder()
                .airline(response.getAirline())
                .supplier(Supplier.CRAZY_AIR)
                .fare(response.getPrice())
                .departureDate(departureDate)
                .arrivalDate(arrivalDate)
                .departureAirportCode(response.getDepartureAirportCode())
                .destinationAirportCode(response.getDestinationAirportCode())
                .build();
    }

}
