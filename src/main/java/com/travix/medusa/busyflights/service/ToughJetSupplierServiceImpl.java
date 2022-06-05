package com.travix.medusa.busyflights.service;

import com.travix.medusa.busyflights.converter.ToughJetRequestConverter;
import com.travix.medusa.busyflights.converter.ToughJetResponseConverter;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.toughjet.ToughJetRequest;
import com.travix.medusa.busyflights.domain.toughjet.ToughJetResponse;
import com.travix.medusa.busyflights.properties.FindFlightsProperties;
import com.travix.medusa.busyflights.utils.WebRequestUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static com.travix.medusa.busyflights.enums.Supplier.TOUGH_JET;

@Service
public class ToughJetSupplierServiceImpl extends SupplierService {

    public ToughJetSupplierServiceImpl(WebClient.Builder webClientBuilder,
                                       FindFlightsProperties findFlightsProperties) {
        super(webClientBuilder, findFlightsProperties, TOUGH_JET);
    }

    @Override
    @Cacheable(value = "TOUGH_JET")
    public Flux<BusyFlightsResponse> findFlights(BusyFlightsRequest busyFlightsRequest) {
        ToughJetRequest request = ToughJetRequestConverter.fromBusyFlightsRequest(busyFlightsRequest);
        return webClient.get()
                .uri(supplierProperties.getUrl(), uriBuilder -> uriBuilder.queryParams(WebRequestUtils.toMultiValueMap(request)).build())
                .retrieve()
                .bodyToFlux(ToughJetResponse.class)
                .map(ToughJetResponseConverter::toBusyFlightResponse);
    }

}
