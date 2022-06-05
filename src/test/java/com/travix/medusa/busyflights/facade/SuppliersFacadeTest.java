package com.travix.medusa.busyflights.facade;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponseList;
import com.travix.medusa.busyflights.service.SupplierService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SuppliersFacadeTest {

    private final SuppliersFacade suppliersFacade;

    private final SupplierService supplierService1;

    private final SupplierService supplierService2;

    public SuppliersFacadeTest() {
        supplierService1 = mock(SupplierService.class);
        supplierService2 = mock(SupplierService.class);
        suppliersFacade = new SuppliersFacade(List.of(supplierService1, supplierService2));
    }

    @Test
    void givenAllSupplierResponses_whenFindFlights_thenResponsesAreMergedAndSortedByPriceAndAreCached() {
        when(supplierService1.findFlights(any())).thenReturn(getMockedResponses(10, 8.5, 15));
        when(supplierService2.findFlights(any())).thenReturn(getMockedResponses(20, 13, 25, 9));

        Mono<BusyFlightsResponseList> responseListMono = suppliersFacade.findFlights(BusyFlightsRequest.builder().build());

        verify(supplierService1, never()).findFromCache(any(), any());
        verify(supplierService2, never()).findFromCache(any(), any());
        verify(supplierService1).updateCache(any(), any(), any());
        verify(supplierService2).updateCache(any(), any(), any());

        StepVerifier.create(responseListMono)
                .expectNextMatches(responseList -> {
                    List<Double> prices = responseList.getData().stream().map(BusyFlightsResponse::getFare).collect(Collectors.toList());
                    assertThat(prices).containsExactly(8.5, 9., 10., 13., 15., 20., 25.);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void givenOnlyOneSupplierResponses_whenFindFlights_thenResponsesAreMergedAndSortedByPrice() {
        when(supplierService1.findFlights(any())).thenReturn(getMockedResponses(10, 8.5, 15));
        when(supplierService2.findFlights(any())).thenReturn(Flux.empty());

        Mono<BusyFlightsResponseList> responseListMono = suppliersFacade.findFlights(BusyFlightsRequest.builder().build());

        StepVerifier.create(responseListMono)
                .expectNextMatches(responseList -> {
                    List<Double> prices = responseList.getData().stream().map(BusyFlightsResponse::getFare).collect(Collectors.toList());
                    assertThat(prices).containsExactly(8.5, 10., 15.);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void givenEmptySupplierResponses_whenFindFlights_thenEmptyMonoIsReturned() {
        when(supplierService1.findFlights(any())).thenReturn(Flux.empty());
        when(supplierService2.findFlights(any())).thenReturn(Flux.empty());

        Mono<BusyFlightsResponseList> responseListMono = suppliersFacade.findFlights(BusyFlightsRequest.builder().build());

        StepVerifier.create(responseListMono)
                .verifyComplete();
    }

    @Test
    void givenOneErrorResponseAndOneValidResponse_whenFindFlights_thenValidResponseIsReturned() {
        when(supplierService1.findFlights(any())).thenReturn(getMockedResponses(20));
        when(supplierService2.findFlights(any())).thenReturn(Flux.error(new Exception()));

        Mono<BusyFlightsResponseList> responseListMono = suppliersFacade.findFlights(BusyFlightsRequest.builder().build());

        StepVerifier.create(responseListMono)
                .expectNextMatches(responseList -> responseList.getData().size() == 1)
                .verifyComplete();
    }

    @Test
    void givenAllErrorResponseAndEmptyCache_whenFindFlights_thenEmptyResponseIsReturned() {
        when(supplierService1.findFlights(any())).thenReturn(Flux.error(new Exception()));
        when(supplierService2.findFlights(any())).thenReturn(Flux.error(new Exception()));

        Mono<BusyFlightsResponseList> responseListMono = suppliersFacade.findFlights(BusyFlightsRequest.builder().build());

        StepVerifier.create(responseListMono)
                .expectNext(BusyFlightsResponseList.builder().data(Collections.emptyList()).build())
                .verifyComplete();
    }

    @Test
    void givenAllErrorResponseAndCacheResponse_whenFindFlights_thenCacheResponseIsReturned() {
        when(supplierService1.findFlights(any())).thenReturn(getMockedResponses(10));
        when(supplierService2.findFlights(any())).thenReturn(Flux.error(new Exception()));

        Mono<BusyFlightsResponseList> responseListMono = suppliersFacade.findFlights(BusyFlightsRequest.builder().build());

        verify(supplierService1, never()).findFromCache(any(), any());
        verify(supplierService2).findFromCache(any(), any());

        StepVerifier.create(responseListMono)
                .expectNextMatches(r -> r.getData().size() == 1)
                .verifyComplete();
    }

    private Flux<BusyFlightsResponse> getMockedResponses(double ... prices) {
        Flux<BusyFlightsResponse> responses = Flux.empty();
        for (double price : prices) {
            responses = Flux.merge(responses, Flux.just(BusyFlightsResponse.builder().fare(price).build()));
        }
        return responses;
    }
}
