package com.travix.medusa.busyflights.domain.toughjet;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ToughJetResponse {

    private String carrier;
    private double basePrice;
    private double tax;
    private double discount;
    private String departureAirportName;
    private String arrivalAirportName;
    private String outboundDateTime;
    private String inboundDateTime;

}
