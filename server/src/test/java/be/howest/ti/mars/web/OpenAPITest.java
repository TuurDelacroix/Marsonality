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
    void getMartian(final VertxTestContext testContext) {
        webClient.get(PORT, HOST, "/api/martians/1").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue((response.bodyAsJsonObject().getInteger("marsId") == 1),
                            "You must provide a valid marsId"
                    );
                    testContext.completeNow();
                }));
    }
//
//    @Test
//    void getQuoteWithInvalidID(final VertxTestContext testContext) {
//        webClient.get(PORT, HOST, "/api/quotes/333").send()
//                .onFailure(testContext::failNow)
//                .onSuccess(response -> testContext.verify(() -> {
//                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
//                    assertTrue(
//                            StringUtils.isNotBlank(response.bodyAsJsonObject().getString("value")),
//                            "Empty quotes are not allowed"
//                    );
//                    testContext.completeNow();
//                }));
//    }
//
//    @Test
//    void createQuote(final VertxTestContext testContext) {
//        String testQuote = "some value";
//        webClient.post(PORT, HOST, "/api/quotes").sendJsonObject(createQuote(testQuote))
//                .onFailure(testContext::failNow)
//                .onSuccess(response -> testContext.verify(() -> {
//                    assertEquals(201, response.statusCode(), MSG_201_EXPECTED);
//                    assertEquals(
//                            testQuote,
//                            response.bodyAsJsonObject().getString("value"),
//                            "Quote does not match " + testQuote);
//                    testContext.completeNow();
//                }));
//    }
//
//    @Test
//    void updateQuote(final VertxTestContext testContext) {
//        String testQuote = "some value";
//        webClient.put(PORT, HOST, "/api/quotes/0").sendJsonObject(createQuote(testQuote))
//                .onFailure(testContext::failNow)
//                .onSuccess(response -> testContext.verify(() -> {
//                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
//                    assertEquals(
//                            testQuote,
//                            response.bodyAsJsonObject().getString("value"),
//                            "Quote does not match " + testQuote);
//                    testContext.completeNow();
//                }));
//    }
//
//    @Test
//    void deleteQuote(final VertxTestContext testContext) {
//        webClient.delete(PORT, HOST, "/api/quotes/1").send()
//                .onFailure(testContext::failNow)
//                .onSuccess(response -> testContext.verify(() -> {
//                    assertEquals(204, response.statusCode(), MSG_204_EXPECTED);
//                    testContext.completeNow();
//                }));
//    }
//
//    private JsonObject createQuote(String quote) {
//        return new JsonObject().put("quote", quote);
//    }
}