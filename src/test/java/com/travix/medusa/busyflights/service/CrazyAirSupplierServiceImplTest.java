package com.travix.medusa.busyflights.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.crazyair.CrazyAirResponse;
import com.travix.medusa.busyflights.enums.Supplier;
import com.travix.medusa.busyflights.properties.FindFlightsProperties;
import com.travix.medusa.busyflights.utils.TestUtils;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CrazyAirSupplierServiceImplTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CrazyAirSupplierServiceImpl crazyAirSupplierService;

    private static MockWebServer mockWebServer;

    @SneakyThrows
    @BeforeAll
    static void setUp() {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void initialize() {
        FindFlightsProperties properties = TestUtils.getFindFlightsPropertiesForSupplier(Supplier.CRAZY_AIR, mockWebServer.getPort());
        crazyAirSupplierService = new CrazyAirSupplierServiceImpl(WebClient.builder(), properties);
    }

    @SneakyThrows
    @AfterAll
    static void shutDown() {
        mockWebServer.shutdown();
    }

    @Test
    void givenCrazyAirResponses_whenFindFlight_thenReturnBusyFlightResponse() {
        List<CrazyAirResponse> mockedResponses = mockWebResponse();
        BusyFlightsRequest request = BusyFlightsRequest.builder()
                .origin("London")
                .destination("Dublin")
                .departureDate("2022-10-01")
                .returnDate("2022-10-02")
                .numberOfPassengers(1).build();

        Flux<BusyFlightsResponse> responses = crazyAirSupplierService.findFlights(request);

        StepVerifier.create(responses)
                .expectNextMatches(response -> {
                    CrazyAirResponse mockedResponse = mockedResponses.get(0);
                    assertThat(response.getFare()).isEqualTo(mockedResponse.getPrice());
                    assertThat(response.getDepartureAirportCode()).isEqualTo(mockedResponse.getDepartureAirportCode());
                    assertThat(response.getDestinationAirportCode()).isEqualTo(mockedResponse.getDestinationAirportCode());
                    assertThat(response.getSupplier()).isEqualTo(Supplier.CRAZY_AIR);
                    return true;
                })
                .verifyComplete();

    }

    @Test
    void givenCrazyAirEmptyResponse_whenFindFlight_thenReturnEmpty() {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json"));
        BusyFlightsRequest request = BusyFlightsRequest.builder()
                .origin("London")
                .destination("Dublin")
                .departureDate("2022-10-01")
                .returnDate("2022-10-02")
                .numberOfPassengers(1).build();

        Flux<BusyFlightsResponse> responses = crazyAirSupplierService.findFlights(request);

        StepVerifier.create(responses).verifyComplete();
    }

    @Test
    void givenCrazyAirErrorResponse_whenFindFlight_thenReturnError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500)
                .addHeader("Content-Type", "application/json"));
        BusyFlightsRequest request = BusyFlightsRequest.builder()
                .origin("London")
                .destination("Dublin")
                .departureDate("2022-10-01")
                .returnDate("2022-10-02")
                .numberOfPassengers(1).build();

        Flux<BusyFlightsResponse> responses = crazyAirSupplierService.findFlights(request);

        StepVerifier.create(responses).expectError().verify();
    }

    @SneakyThrows
    private List<CrazyAirResponse> mockWebResponse() {
        CrazyAirResponse response = CrazyAirResponse.builder()
                .airline("Ryanair")
                .price(50)
                .cabinclass("Economy")
                .departureDate("2022-12-03T10:15:00")
                .arrivalDate("2022-12-03T11:26:00")
                .departureAirportCode("STN")
                .destinationAirportCode("DUB").build();
        List<CrazyAirResponse> responses = List.of(response);
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(responses))
                .addHeader("Content-Type", "application/json"));
        return responses;
    }
}
