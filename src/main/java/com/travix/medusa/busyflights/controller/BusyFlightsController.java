package com.travix.medusa.busyflights.controller;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponseList;
import com.travix.medusa.busyflights.facade.SuppliersFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
public class BusyFlightsController {

    private final SuppliersFacade suppliersFacade;

    public BusyFlightsController(SuppliersFacade suppliersFacade) {
        this.suppliersFacade = suppliersFacade;
    }

    @GetMapping("/flights")
    public Mono<ResponseEntity<BusyFlightsResponseList>> findFlights(@Valid BusyFlightsRequest request) {
        validateDates(request);
        return suppliersFacade.findFlights(request)
                .flatMap(response -> Mono.just(ResponseEntity.ok(response)))
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    private void validateDates(BusyFlightsRequest request) {
        try {
            LocalDate departureDate = LocalDate.parse(request.getDepartureDate());
            if (request.getReturnDate() != null) {
                LocalDate returnDate = LocalDate.parse(request.getReturnDate());
                if (returnDate.isBefore(departureDate)) {
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates are not valid");
        }
    }
}
