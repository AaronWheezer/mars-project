package be.howest.ti.mars.web;

import be.howest.ti.mars.logic.controller.MockMarsController;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.web.bridge.MarsOpenApiBridge;
import be.howest.ti.mars.web.bridge.MarsRtcBridge;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert","PMD.AvoidDuplicateLiterals"})
/*
 * PMD.JUnitTestsShouldIncludeAssert: VertxExtension style asserts are marked as false positives.
 * PMD.AvoidDuplicateLiterals: Should all be part of the spec (e.g., urls and names of req/res body properties, ...)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OpenAPITest {

    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    public static final String MSG_200_EXPECTED = "If all goes right, we expect a 200 status";
    public static final String MSG_201_EXPECTED = "If a resource is successfully created.";
    public static final String MSG_204_EXPECTED = "If a resource is successfully deleted";
    private Vertx vertx;
    private WebClient webClient;

    @BeforeAll
    void deploy(final VertxTestContext testContext) {
        Repositories.shutdown();
        vertx = Vertx.vertx();

        WebServer webServer = new WebServer(new MarsOpenApiBridge(new MockMarsController()), new MarsRtcBridge());
        vertx.deployVerticle(
                webServer,
                testContext.succeedingThenComplete()
        );
        webClient = WebClient.create(vertx);
    }

    @AfterAll
    void close(final VertxTestContext testContext) {
        vertx.close(testContext.succeedingThenComplete());
        webClient.close();
        Repositories.shutdown();
    }

    @Test
    void getContainer(final VertxTestContext testContext){
        webClient.get(PORT, HOST, "/api/containers/0").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(String.valueOf(response)),
                            "There should be just one container"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void getContainers(final VertxTestContext testContext){
        webClient.get(PORT, HOST, "/api/containers").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(String.valueOf(response)),
                            "There should be just one container"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void createContainer(final VertxTestContext testContext){
        webClient.post(PORT, HOST, "/api/containers/medium").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(String.valueOf(response)),
                            "There should be just one container created"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void getContainersByOwner(final VertxTestContext testContext){
        webClient.get(PORT, HOST, "/api/business/dusty%20depot/containers").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(String.valueOf(response)),
                            "There should be an output"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void getContainersByLocation(final VertxTestContext testContext){
        webClient.get(PORT, HOST, "/api/location/warehouse%20space%20station/containers").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(String.valueOf(response)),
                            "There should be an output"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void getPalletInfo(final VertxTestContext testContext){
        webClient.get(PORT, HOST, "/api/pallets/0").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(String.valueOf(response)),
                            "There should be an output"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void getPalletsByOwner(final VertxTestContext testContext){
        webClient.get(PORT, HOST, "/api/business/dusty%20depot/pallets").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(String.valueOf(response)),
                            "There should be an output"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void getPalletsByLocation(final VertxTestContext testContext){
        webClient.get(PORT, HOST, "/api/location/warehouse%20space%20station/pallets").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(String.valueOf(response)),
                            "There should be an output"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void changePalletLocation(final VertxTestContext testContext){
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("locationName", "test");
        jsonObject.put("longitude", 3);
        jsonObject.put("latitude", -2);
        webClient.put(PORT, HOST, "/api/location/warehouse%20space%20station/pallets/0")
                .sendJsonObject(jsonObject)
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(String.valueOf(response)),
                            "There should be an output"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void deleteContainer(final VertxTestContext testContext) {
        webClient.delete(PORT, HOST, "/api/containers/0").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    testContext.completeNow();
                }));
    }

    @Test
    void deletePallet(final VertxTestContext testContext) {
        webClient.delete(PORT, HOST, "/api/pallets/0").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    testContext.completeNow();
                }));
    }

    private JsonObject createQuote(String quote) {
        return new JsonObject().put("quote", quote);
    }
}