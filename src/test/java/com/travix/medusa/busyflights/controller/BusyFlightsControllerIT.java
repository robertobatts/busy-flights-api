package com.travix.medusa.busyflights.controller;

import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsErrorResponse;
import com.travix.medusa.busyflights.domain.busyflights.BusyFlightsResponseList;
import com.travix.medusa.busyflights.integration.IntegrationTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

class BusyFlightsControllerIT extends IntegrationTest {

    private static final String GET_BUSY_FLIGHTS_ENDPOINT = "/flights";
    private static final String GET_CRAZY_AIR_ENDPOINT = "/test-crazy-air";
    private static final String GET_TOUGH_JET_ENDPOINT = "/test-tough-jet";

    @Test
    void givenValidParams_whenSuppliersReturnResponses_thenReturnResponses() {
        mockCrazyAirOk();
        mockToughJetOk();

        testResponseOkSize(3);
    }

    @Test
    void givenValidParams_whenOneSupplierHasErrorAndOneIsOk_thenReturnResponses() {
        mockCrazyAirOk();
        mockToughJetError();

        testResponseOkSize(1);
    }

    @Test
    void givenValidParams_whenSuppliersErrorAndCacheIsFilled_thenReturnResponses() {
        mockCrazyAirOk();
        mockToughJetOk();
        given()
                .queryParams(getValidQueryParams())
                .when()
                .get(GET_BUSY_FLIGHTS_ENDPOINT);

        wireMockServer.resetAll();
        mockCrazyAirError();
        mockToughJetError();

        testResponseOkSize(3);
    }

    @Test
    void givenNotValidDates_whenFindFlights_thenBadRequest() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("origin", "London");
        queryParams.put("destination", "Dublin");
        queryParams.put("departureDate", "2022-10-30");
        queryParams.put("returnDate", "2022-10-02");
        queryParams.put("numberOfPassengers", "1");

        BusyFlightsErrorResponse errorResponse = given()
                .queryParams(queryParams)
                .when()
                .get(GET_BUSY_FLIGHTS_ENDPOINT)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .extract().response().as(BusyFlightsErrorResponse.class);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getIncidentId()).isNotBlank();
        assertThat(errorResponse.getMessage()).isNotBlank();
    }

    @Test
    void givenValidParams_whenAllSuppliersError_thenNoContent() {
        mockCrazyAirError();
        mockToughJetError();

        given()
                .queryParams(getValidQueryParams())
                .when()
                .get(GET_BUSY_FLIGHTS_ENDPOINT)
                .then()
                .statusCode(SC_NO_CONTENT);
    }


    private void testResponseOkSize(int size) {
        BusyFlightsResponseList response = given()
                .queryParams(getValidQueryParams())
                .when()
                .get(GET_BUSY_FLIGHTS_ENDPOINT)
                .then()
                .statusCode(SC_OK)
                .extract().response().as(BusyFlightsResponseList.class);

        assertThat(response.getData()).hasSize(size);
    }

    private Map<String, Object> getValidQueryParams() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("origin", "London");
        queryParams.put("destination", "Dublin");
        queryParams.put("departureDate", "2022-10-01");
        queryParams.put("returnDate", "2022-10-02");
        queryParams.put("numberOfPassengers", "1");
        return queryParams;
    }

    @SneakyThrows
    private void mockCrazyAirOk() {
        Path path = new ClassPathResource("/json/crazy_air_response.json").getFile().toPath();
        String body = Files.readString(path);
        wireMockServer.stubFor(get(urlPathMatching(GET_CRAZY_AIR_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(body)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(SC_OK)));
    }

    @SneakyThrows
    private void mockToughJetOk() {
        Path path = new ClassPathResource("/json/tough_jet_response.json").getFile().toPath();
        String body = Files.readString(path);
        wireMockServer.stubFor(get(urlPathMatching(GET_TOUGH_JET_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(body)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(SC_OK)));
    }

    private void mockCrazyAirError() {
        wireMockServer.stubFor(get(urlPathMatching(GET_CRAZY_AIR_ENDPOINT))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(SC_INTERNAL_SERVER_ERROR)));
    }

    private void mockToughJetError() {
        wireMockServer.stubFor(get(urlPathMatching(GET_TOUGH_JET_ENDPOINT))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(SC_INTERNAL_SERVER_ERROR)));
    }
}
