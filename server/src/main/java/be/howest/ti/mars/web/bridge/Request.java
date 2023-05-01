package be.howest.ti.mars.web.bridge;

import be.howest.ti.mars.logic.domain.Subscription;
import be.howest.ti.mars.web.exceptions.MalformedRequestException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Request class is responsible for translating information that is part of the
 * request into Java.
 * <p>
 * For every piece of information that you need from the request, you should provide a method here.
 * You can find information in:
 * - the request path: params.pathParameter("some-param-name")
 * - the query-string: params.queryParameter("some-param-name")
 * Both return a `RequestParameter`, which can contain a string or an integer in our case.
 * The actual data can be retrieved using `getInteger()` or `getString()`, respectively.
 * You can check if it is an integer (or not) using `isNumber()`.
 * <p>
 * Finally, some requests have a body. If present, the body will always be in the json format.
 * You can acces this body using: `params.body().getJsonObject()`.
 * <p>
 * **TIP:** Make sure that al your methods have a unique name. For instance, there is a request
 * that consists of more than one "player name". You cannot use the method `getPlayerName()` for both,
 * you will need a second one with a different name.
 */
public class Request {
    public static final String SPEC_QUOTE_ID = "quoteId";
    public static final String SPEC_QUOTE = "quote";
    public static final String SPEC_NOTIFICATION = "notification";
    private static final Logger LOGGER = Logger.getLogger(Request.class.getName());
    private static final String UNABLE_TO_DECIPHER_THE_DATA_IN_THE_BODY = "Unable to decipher the data in the body";
    private static final String UNABLE_TO_DECIPHER_THE_DATA_IN_THE_REQUEST_BODY_SEE_LOGS_FOR_DETAILS = "Unable to decipher the data in the request body. See logs for details.";
    private final RequestParameters params;

    private Request(RoutingContext ctx) {
        this.params = ctx.get(ValidationHandler.REQUEST_CONTEXT_KEY);
    }

    public static Request from(RoutingContext ctx) {
        return new Request(ctx);
    }

    public int getQuoteId() {
        return params.pathParameter(SPEC_QUOTE_ID).getInteger();
    }

    public String getQuote() {
        try {
            if (params.body().isJsonObject())
                return params.body().getJsonObject().getString(SPEC_QUOTE);
            return params.body().get().toString();
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, UNABLE_TO_DECIPHER_THE_DATA_IN_THE_BODY, ex);
            throw new MalformedRequestException(UNABLE_TO_DECIPHER_THE_DATA_IN_THE_REQUEST_BODY_SEE_LOGS_FOR_DETAILS);
        }
    }

    public JsonObject getNotification() {
        try {
            return params.body().getJsonObject().getJsonObject(SPEC_NOTIFICATION);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, UNABLE_TO_DECIPHER_THE_DATA_IN_THE_BODY, ex);
            throw new MalformedRequestException(UNABLE_TO_DECIPHER_THE_DATA_IN_THE_REQUEST_BODY_SEE_LOGS_FOR_DETAILS);
        }
    }
    public int getMarsId() {
        return params.pathParameter("marsId").getInteger();
    }

    public String getHeaderProfileName() {
        return params.body().getJsonObject().getString("profileName");
    }

    public String getProfileName() {
        return params.pathParameter("profileName").getString();
    }

    public String getTraitName() {
        return params.pathParameter("traitName").getString();
    }

    public String getTraitTypeFromQuery() {
        try
        {
            return params.queryParameter("traitType").getString();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public Subscription getSubscription() {
        try {
            return new Subscription(params.body().getJsonObject());
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, UNABLE_TO_DECIPHER_THE_DATA_IN_THE_BODY, ex);
            throw new MalformedRequestException(UNABLE_TO_DECIPHER_THE_DATA_IN_THE_REQUEST_BODY_SEE_LOGS_FOR_DETAILS);
        }
    }

    public String getTraitNameFromBody() {
        return params.body().getJsonObject().getString("name");
    }
}

