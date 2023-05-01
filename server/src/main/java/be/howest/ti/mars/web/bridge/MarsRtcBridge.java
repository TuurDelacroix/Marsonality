package be.howest.ti.mars.web.bridge;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

/**
 * The RTC bridge is one of the class taught topics.
 * If you do not choose the RTC topic you don't have to do anything with this class.
 * Otherwise, you will need to expand this bridge with the websockets topics shown in the other modules.
 * <p>
 * The client-side starter project does not contain any teacher code about the RTC topic.
 * The rtc bridge is already initialized and configured in the WebServer.java.
 * No need to change the WebServer.java
 * <p>
 * The job of the "bridge" is to bridge between websockets events and Java (the controller).
 * Just like in the openapi bridge, keep business logic isolated in the package logic.
 * <p>
 */
public class MarsRtcBridge {

    private static final String CHNL_TO_SERVER = "events.to.server";
    private SockJSHandler sockJSHandler;

    /**
     * Example function to put a message on the event bus every 10 seconds.
     * The timer logic is only there to simulate a repetitive stream of data as an example.
     * Please remove this timer logic or move it to an appropriate place.
     * Please call the controller to get some business logic data.
     * Afterwords publish the result to the client.
     */

    private void createSockJSHandler() {
        final PermittedOptions permittedOptions = new PermittedOptions().setAddressRegex("events\\..+");
        final SockJSBridgeOptions options = new SockJSBridgeOptions()
                .addInboundPermitted(permittedOptions)
                .addOutboundPermitted(permittedOptions);
        sockJSHandler.bridge(options);
    }


    public SockJSHandler getSockJSHandler(Vertx vertx) {
        sockJSHandler = SockJSHandler.create(vertx);

        EventBus eb = vertx.eventBus();
        createSockJSHandler();

        eb.consumer(CHNL_TO_SERVER, this::handleIncomingMessage);

        return sockJSHandler;
    }

    private void handleIncomingMessage(Message<JsonObject> message) {
        // not yet implemented
    }
}
