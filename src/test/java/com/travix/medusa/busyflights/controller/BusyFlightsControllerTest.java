package com.travix.medusa.busyflights.controller;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponseList;
import com.travix.medusa.busyflights.facade.SuppliersFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
class BusyFlightsControllerTest {

    private static final String GET_FLIGHTS_URI = "/flights";

    @Autowired
    private WebTestClient client;

    @MockBean
    private SuppliersFacade suppliersFacade;

    @Test
    void givenSuppliersResponses_whenAllRequestFields_thenResponsesAreReturned() {
        BusyFlightsResponseList mockedResponseList = BusyFlightsResponseList.builder()
                .data(List.of(BusyFlightsResponse.builder().fare(50).airline("EasyJet").build())).build();
        when(suppliersFacade.findFlights(any())).thenReturn(Mono.just(mockedResponseList));

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("origin", "London");
        queryParams.add("destination", "Dublin");
        queryParams.add("departureDate", "2022-10-12");
        queryParams.add("returnDate", "2022-10-13");
        queryParams.add("numberOfPassengers", "1");
        var response = client
                .get()
                .uri(uriBuilder -> uriBuilder.path(GET_FLIGHTS_URI).queryParams(queryParams).build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(BusyFlightsResponseList.class)
                .getResponseBody();

        StepVerifier.create(response)
                .expectNext(mockedResponseList)
                .verifyComplete();
    }

    @Test
    void givenEmptySuppliersResponses_whenAllRequestFields_thenNoContentStatus() {
        when(suppliersFacade.findFlights(any())).thenReturn(Mono.empty());

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("origin", "London");
        queryParams.add("destination", "Dublin");
        queryParams.add("departureDate", "2022-10-12");
        queryParams.add("returnDate", "2022-10-13");
        queryParams.add("numberOfPassengers", "1");
        client
                .get()
                .uri(uriBuilder -> uriBuilder.path(GET_FLIGHTS_URI).queryParams(queryParams).build())
                .exchange()
                .expectStatus().isNoContent();

    }

    @Test
    void whenReturnDateIsBeforeDepartureDate_thenBadRequest() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("origin", "London");
        queryParams.add("destination", "Dublin");
        queryParams.add("departureDate", "2022-10-12");
        queryParams.add("returnDate", "2022-09-13");
        queryParams.add("numberOfPassengers", "1");
        client
                .get()
                .uri(uriBuilder -> uriBuilder.path(GET_FLIGHTS_URI).queryParams(queryParams).build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenDatesAreNotValid_thenBadRequest() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("origin", "London");
        queryParams.add("destination", "Dublin");
        queryParams.add("departureDate", "12-10-2022");
        queryParams.add("returnDate", "2022/09/13");
        queryParams.add("numberOfPassengers", "1");
        client
                .get()
                .uri(uriBuilder -> uriBuilder.path(GET_FLIGHTS_URI).queryParams(queryParams).build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenDepartureDateIsMissing_thenBadRequest() {
        testBadRequestWithParamMissing("departureDate");
    }

    @Test
    void whenOriginIsMissing_thenBadRequest() {
        testBadRequestWithParamMissing("origin");
    }

    @Test
    void whenDestinationIsMissing_thenBadRequest() {
        testBadRequestWithParamMissing("destination");
    }

    @Test
    void whenNumberOfPassengersIsMissing_thenBadRequest() {
        testBadRequestWithParamMissing("numberOfPassengers");
    }

    private void testBadRequestWithParamMissing(String missingParameter) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("origin", "London");
        queryParams.add("destination", "Dublin");
        queryParams.add("departureDate", "2022-10-12");
        queryParams.add("returnDate", "2022-10-13");
        queryParams.add("numberOfPassengers", "1");
        queryParams.remove(missingParameter);
        client
                .get()
                .uri(uriBuilder -> uriBuilder.path(GET_FLIGHTS_URI).queryParams(queryParams).build())
                .exchange()
                .expectStatus().isBadRequest();
    }


}
