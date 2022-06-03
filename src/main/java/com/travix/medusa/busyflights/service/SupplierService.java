package com.travix.medusa.busyflights.service;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.enums.Supplier;
import com.travix.medusa.busyflights.properties.FindFlightsProperties;
import com.travix.medusa.busyflights.properties.SupplierProperties;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public abstract class SupplierService {

    protected final SupplierProperties supplierProperties;

    protected final WebClient webClient;

    protected final Supplier supplier;

    protected SupplierService(WebClient.Builder webClientBuilder,
                              FindFlightsProperties findFlightsProperties,
                              Supplier supplier) {
        this.webClient = webClientBuilder.build();
        this.supplier = supplier;
        this.supplierProperties = findFlightsProperties.getSupplierProperties(supplier);
    }

    public abstract Flux<BusyFlightsResponse> findFlights(BusyFlightsRequest busyFlightsRequest);
}
