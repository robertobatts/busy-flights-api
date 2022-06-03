package com.travix.medusa.busyflights.domain.busyflights;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@Builder
public class BusyFlightsRequest {

    @NotBlank
    private String origin;
    @NotBlank
    private String destination;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    @Positive
    private int numberOfPassengers;

}
