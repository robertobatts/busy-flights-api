package com.travix.medusa.busyflights.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsRequest;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponse;
import com.travix.medusa.busyflights.domain.toughjet.ToughJetResponse;
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

class ToughJetSupplierServiceImplTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ToughJetSupplierServiceImpl toughJetSupplierService;

    private static MockWebServer mockWebServer;

    @SneakyThrows
    @BeforeAll
    static void setUp() {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void initialize() {
        FindFlightsProperties properties = TestUtils.getFindFlightsPropertiesForSupplier(Supplier.TOUGH_JET, mockWebServer.getPort());
        toughJetSupplierService = new ToughJetSupplierServiceImpl(WebClient.builder(), properties);
    }

    @SneakyThrows
    @AfterAll
    static void shutDown() {
        mockWebServer.shutdown();
    }

    @Test
    void givenToughJetResponses_whenFindFlight_thenReturnBusyFlightResponse() {
        List<ToughJetResponse> mockedResponses = mockWebResponse();
        BusyFlightsRequest request = BusyFlightsRequest.builder()
                .origin("London")
                .destination("Dublin")
                .departureDate("2022-10-01")
                .returnDate("2022-10-02")
                .numberOfPassengers(1).build();

        Flux<BusyFlightsResponse> responses = toughJetSupplierService.findFlights(request);

        StepVerifier.create(responses)
                .expectNextMatches(response -> {
                    ToughJetResponse mockedResponse = mockedResponses.get(0);
                    assertThat(response.getFare()).isEqualTo(80);
                    assertThat(response.getDepartureAirportCode()).isEqualTo(mockedResponse.getDepartureAirportName());
                    assertThat(response.getDestinationAirportCode()).isEqualTo(mockedResponse.getArrivalAirportName());
                    assertThat(response.getSupplier()).isEqualTo(Supplier.TOUGH_JET);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void givenToughJetEmptyResponse_whenFindFlight_thenReturnEmpty() {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json"));
        BusyFlightsRequest request = BusyFlightsRequest.builder()
                .origin("London")
                .destination("Dublin")
                .departureDate("2022-10-01")
                .returnDate("2022-10-02")
                .numberOfPassengers(1).build();

        Flux<BusyFlightsResponse> responses = toughJetSupplierService.findFlights(request);

        StepVerifier.create(responses).verifyComplete();
    }

    @Test
    void givenToughJetErrorResponse_whenFindFlight_thenReturnError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500)
                .addHeader("Content-Type", "application/json"));
        BusyFlightsRequest request = BusyFlightsRequest.builder()
                .origin("London")
                .destination("Dublin")
                .departureDate("2022-10-01")
                .returnDate("2022-10-02")
                .numberOfPassengers(1).build();

        Flux<BusyFlightsResponse> responses = toughJetSupplierService.findFlights(request);

        StepVerifier.create(responses).expectError().verify();
    }



    @SneakyThrows
    private List<ToughJetResponse> mockWebResponse() {
        ToughJetResponse response = ToughJetResponse.builder()
                .carrier("Ryanair")
                .basePrice(60)
                .tax(40)
                .discount(20)
                .outboundDateTime("2022-12-03T10:15:00Z")
                .inboundDateTime("2022-12-03T11:26:00Z")
                .departureAirportName("STN")
                .arrivalAirportName("DUB").build();
        List<ToughJetResponse> responses = List.of(response);
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(responses))
                .addHeader("Content-Type", "application/json"));
        return responses;
    }
}
