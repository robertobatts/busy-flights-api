package com.travix.medusa.busyflights.facade;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponseList;
import com.travix.medusa.busyflights.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class SuppliersFacade {

    private final List<SupplierService> supplierServices;

    public SuppliersFacade(List<SupplierService> supplierServices) {
        this.supplierServices = Collections.unmodifiableList(supplierServices);
    }

    public Mono<BusyFlightsResponseList> findFlights(BusyFlightsRequest request) {
        log.debug("Finding flights :: request={}", request);
        Flux<BusyFlightsResponse> mergedResponses = Flux.empty();
        for (SupplierService supplierService : supplierServices) {
            Flux<BusyFlightsResponse> responses = supplierService.findFlights(request);
            mergedResponses = Flux.merge(mergedResponses, responses);
        }
        return mergedResponses
                .collectSortedList(Comparator.comparing(BusyFlightsResponse::getFare))
                .map(responses -> BusyFlightsResponseList.builder().data(responses).build());
    }
}
