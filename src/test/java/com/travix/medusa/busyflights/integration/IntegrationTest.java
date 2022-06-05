package com.travix.medusa.busyflights.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTest {

    @LocalServerPort
    protected int serverPort;

    @Value("${wiremock.server.port}")
    private int wireMockServerPort;

    @Autowired
    protected CacheTestService cacheTestService;

    protected WireMockServer wireMockServer;


    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = serverPort;

        wireMockServer = new WireMockServer(wireMockServerPort);
        wireMockServer.start();
    }

    @AfterEach
    void shutDown() {
        wireMockServer.stop();
        cacheTestService.evictAll();
    }

}
