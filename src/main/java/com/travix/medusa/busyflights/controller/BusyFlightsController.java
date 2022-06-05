package com.travix.medusa.busyflights.controller;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponseList;
import com.travix.medusa.busyflights.domain.crazyair.CrazyAirRequest;
import com.travix.medusa.busyflights.domain.crazyair.CrazyAirResponse;
import com.travix.medusa.busyflights.domain.toughjet.ToughJetRequest;
import com.travix.medusa.busyflights.domain.toughjet.ToughJetResponse;
import com.travix.medusa.busyflights.facade.SuppliersFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BusyFlightsController {

    private final SuppliersFacade suppliersFacade;

    public BusyFlightsController(SuppliersFacade suppliersFacade) {
        this.suppliersFacade = suppliersFacade;
    }

    //TODO: make request immutable and bind it with ControllerAdvice and InitBinder
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

    @GetMapping("/crazy-air")
    public List<CrazyAirResponse> crazyAir(CrazyAirRequest request) {
        List<CrazyAirResponse> responses = new ArrayList<>();
        responses.add(CrazyAirResponse.builder().airline("CXY").price(50).departureDate("2011-12-03T10:15:30").arrivalDate("2011-12-03T10:16:30").build());
        responses.add(CrazyAirResponse.builder().airline("ABC").price(47).departureDate("2011-12-04T10:15:30").arrivalDate("2011-12-05T10:15:30").build());
        return responses;
    }

    @GetMapping("/tough-jet")
    public List<ToughJetResponse> toughJet(ToughJetRequest request) {
        List<ToughJetResponse> responses = new ArrayList<>();
        responses.add(ToughJetResponse.builder().basePrice(80).carrier("RYN").outboundDateTime("2011-12-03T10:15:30Z").inboundDateTime("2011-12-03T10:15:30Z").build());
        responses.add(ToughJetResponse.builder().basePrice(27).carrier("RYN").outboundDateTime("2011-12-05T10:15:30Z").inboundDateTime("2011-12-03T10:15:30Z").build());
        return responses;
    }
}
