package com.travix.medusa.busyflights.service;

import com.travix.medusa.busyflights.converter.CrazyAirRequestConverter;
import com.travix.medusa.busyflights.converter.CrazyAirResponseConverter;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.crazyair.CrazyAirRequest;
import com.travix.medusa.busyflights.domain.crazyair.CrazyAirResponse;
import com.travix.medusa.busyflights.properties.FindFlightsProperties;
import com.travix.medusa.busyflights.utils.WebRequestUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static com.travix.medusa.busyflights.enums.Supplier.CRAZY_AIR;

@Service
public class CrazyAirSupplierServiceImpl extends SupplierService {

    public CrazyAirSupplierServiceImpl(WebClient.Builder webClientBuilder,
                                       FindFlightsProperties findFlightsProperties) {
        super(webClientBuilder, findFlightsProperties, CRAZY_AIR);
    }

    @Cacheable(value = "CRAZY_AIR")
    public Flux<BusyFlightsResponse> findFlights(BusyFlightsRequest busyFlightsRequest) {
        CrazyAirRequest request = CrazyAirRequestConverter.fromBusyFlightsRequest(busyFlightsRequest);
        return webClient.get()
                .uri(supplierProperties.getUrl(), uriBuilder -> uriBuilder.queryParams(WebRequestUtils.toMultiValueMap(request)).build())
                .retrieve()
                .bodyToFlux(CrazyAirResponse.class)
                .map(CrazyAirResponseConverter::toBusyFlightResponse);
    }

}
