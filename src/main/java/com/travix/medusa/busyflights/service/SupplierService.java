package com.travix.medusa.busyflights.service;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.enums.Supplier;
import com.travix.medusa.busyflights.properties.FindFlightsProperties;
import com.travix.medusa.busyflights.properties.SupplierProperties;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

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

    @Cacheable(value = "flights", key = "{ #request, #supplier }", unless = "#result==null or #result.isEmpty()")
    public List<BusyFlightsResponse> findFromCache(BusyFlightsRequest request, Supplier supplier) {
        return Collections.emptyList();
    }

    @CachePut(value = "flights", key = "{ #request, #supplier }", unless = "#result==null or #result.isEmpty()")
    public List<BusyFlightsResponse> updateCache(List<BusyFlightsResponse> responses, BusyFlightsRequest request, Supplier supplier) {
        return responses;
    }

    public Supplier getSupplier() {
        return supplier;
    }
}
