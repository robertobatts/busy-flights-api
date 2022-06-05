package com.travix.medusa.busyflights.facade;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponseList;
import com.travix.medusa.busyflights.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

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
            responses = responses
                    .doOnError(t -> log.error("Error Finding flight :: supplier=" + supplierService.getSupplier() + ", request=" + request, t))
                    .onErrorResume(t -> Flux.fromIterable(supplierService.findFromCache(request, supplierService.getSupplier())));
            responses.collectList().subscribe(r -> supplierService.updateCache(r, request, supplierService.getSupplier()));
            mergedResponses = mergedResponses.concatWith(responses);
        }
        return mergedResponses
                .collectSortedList(Comparator.comparing(BusyFlightsResponse::getFare))
                .mapNotNull(responses -> {
                    if (responses.isEmpty()) {
                        return null;
                    }
                    return BusyFlightsResponseList.builder().data(responses).build();
                });
    }
}
