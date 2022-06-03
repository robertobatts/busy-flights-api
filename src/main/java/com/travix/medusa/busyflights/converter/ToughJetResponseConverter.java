package com.travix.medusa.busyflights.converter;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.toughjet.ToughJetResponse;
import com.travix.medusa.busyflights.enums.Supplier;
import com.travix.medusa.busyflights.utils.DateUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ToughJetResponseConverter {

    private ToughJetResponseConverter() {
    }

    public static BusyFlightsResponse toBusyFlightResponse(ToughJetResponse response) {
        double price = response.getBasePrice() + response.getTax();
        price -= price * response.getDiscount() / 100.;
        String departureDate = LocalDateTime.ofInstant(Instant.parse(response.getOutboundDateTime()), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME);
        String arrivalDate = LocalDateTime.ofInstant(Instant.parse(response.getInboundDateTime()), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME);
        return BusyFlightsResponse.builder()
                .airline(response.getCarrier())
                .supplier(Supplier.TOUGH_JET)
                .fare(price)
                .departureDate(departureDate)
                .arrivalDate(arrivalDate)
                .departureAirportCode(response.getDepartureAirportName())
                .destinationAirportCode(response.getArrivalAirportName())
                .build();
    }

}
