package be.howest.ti.mars.web.bridge;

import be.howest.ti.mars.logic.controller.DefaultMarsController;
import be.howest.ti.mars.logic.controller.MarsController;
import be.howest.ti.mars.logic.domain.*;
import be.howest.ti.mars.web.exceptions.MalformedRequestException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BearerAuthHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.openapi.RouterBuilder;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * In the MarsOpenApiBridge class you will create one handler-method per API operation.
 * The job of the "bridge" is to bridge between JSON (request and response) and Java (the controller).
 * <p>
 * For each API operation you should get the required data from the `Request` class.
 * The Request class will turn the HTTP request data into the desired Java types (int, String, Custom class,...)
 * This desired type is then passed to the controller.
 * The return value of the controller is turned to Json or another Web data type in the `Response` class.
 */
public class MarsOpenApiBridge {
    private static final Logger LOGGER = Logger.getLogger(MarsOpenApiBridge.class.getName());
    private final MarsController controller;

    public Router buildRouter(RouterBuilder routerBuilder) {
        LOGGER.log(Level.INFO, "Installing cors handlers");
        routerBuilder.rootHandler(createCorsHandler());

        routerBuilder.securityHandler("userAuth", BearerAuthHandler.create(new TokenManager()));

        LOGGER.log(Level.INFO, "Installing failure handlers for all operations");
        routerBuilder.operations().forEach(op -> op.failureHandler(this::onFailedRequest));

        LOGGER.log(Level.INFO, "Installing handler for: Subscribe");
        routerBuilder.operation("postSubscription").handler(this::postSubscription);

        LOGGER.log(Level.INFO, "Installing handler for: Notification");
        routerBuilder.operation("postNotification").handler(this::postNotification);

        LOGGER.log(Level.INFO, "Installing handler for: getNotifications");
        routerBuilder.operation("getNotifications").handler(this::getNotifications);

        LOGGER.log(Level.INFO, "Installing handler for: getMartians");
        routerBuilder.operation("getMartians").handler(this::getMartians);

        LOGGER.log(Level.INFO, "Installing handler for: getMartian");
        routerBuilder.operation("getMartian").handler(this::getMartian);

        LOGGER.log(Level.INFO, "Installing handler for: getProfiles");
        routerBuilder.operation("getProfiles").handler(this::getProfiles);

        LOGGER.log(Level.INFO, "Installing handler for: getProfilesOfMartian");
        routerBuilder.operation("getProfilesOfMartian").handler(this::getProfilesOfMartian);

        LOGGER.log(Level.INFO, "Installing handler for: getTraitsOfMartian");
        routerBuilder.operation("getTraitsOfMartian").handler(this::getTraitsOfMartian);

        LOGGER.log(Level.INFO, "Installing handler for: buyNewTrait");
        routerBuilder.operation("buyNewTrait").handler(this::buyNewTrait);

        LOGGER.log(Level.INFO, "Installing handler for: getProfileOfMartian");
        routerBuilder.operation("getProfileOfMartian").handler(this::getProfileOfMartian);

        LOGGER.log(Level.INFO, "Installing handler for: getProfilesUsage");
        routerBuilder.operation("getProfilesUsage").handler(this::getProfilesUsage);

        LOGGER.log(Level.INFO, "Installing handler for: activateProfile");
        routerBuilder.operation("activateProfile").handler(this::activateProfile);

        LOGGER.log(Level.INFO, "Installing handler for: unlockProfile");
        routerBuilder.operation("unlockProfile").handler(this::unlockProfile);

        LOGGER.log(Level.INFO, "Installing handler for: getDefaultProfileOfMartian");
        routerBuilder.operation("getDefaultProfileOfMartian").handler(this::getDefaultProfileOfMartian);

        LOGGER.log(Level.INFO, "Installing handler for: getActiveProfileOfMartian");
        routerBuilder.operation("getActiveProfileOfMartian").handler(this::getActiveProfileOfMartian);

        LOGGER.log(Level.INFO, "Installing handler for: getTraits");
        routerBuilder.operation("getTraits").handler(this::getTraits);

        LOGGER.log(Level.INFO, "Installing handler for: getDefaultTraitsOfMartian");
        routerBuilder.operation("getDefaultTraitsOfMartian").handler(this::getDefaultTraitsOfMartian);

        LOGGER.log(Level.INFO, "Installing handler for: getTrait");
        routerBuilder.operation("getTrait").handler(this::getTrait);

        LOGGER.log(Level.INFO, "Installing handler for: getTraitsOfMartiansProfile");
        routerBuilder.operation("getTraitsOfMartiansProfile").handler(this::getTraitsOfMartiansProfile);

        LOGGER.log(Level.INFO, "Installing handler for: addTraitToProfile");
        routerBuilder.operation("addTraitToProfile").handler(this::addTraitToProfile);

        LOGGER.log(Level.INFO, "Installing handler for: removeTraitFromProfile");
        routerBuilder.operation("removeTraitFromProfile").handler(this::removeTraitFromProfile);

        LOGGER.log(Level.INFO, "Installing handler for: setTraitsOfMartians");
        routerBuilder.operation("setTraitsOfMartians").handler(this::setTraitsOfMartians);

        LOGGER.log(Level.INFO, "Installing handler for: getChips");
        routerBuilder.operation("getChips").handler(this::getChips);

        LOGGER.log(Level.INFO, "All handlers are installed, creating router.");
        return routerBuilder.createRouter();
    }

    public MarsOpenApiBridge() {
        this.controller = new DefaultMarsController();
    }

    public MarsOpenApiBridge(MarsController controller) {
        this.controller = controller;
    }

    public void postSubscription(RoutingContext ctx) {
        Subscription sub = Request.from(ctx).getSubscription();
        controller.postSubscription(sub);
        Response.sendEmptyResponse(ctx, 200);
    }

    public void postNotification(RoutingContext ctx) {
        JsonObject notification = Request.from(ctx).getNotification();
        controller.postNotification(notification);
        Response.sendEmptyResponse(ctx, 200);
    }
    public void getMartians(RoutingContext ctx) {
        List<Martian> martians = controller.getMartians();

        if (martians.isEmpty())
            Response.sendFailure(ctx, 404, "No martians in the company");
        else Response.sendJsonResponse(ctx, martians);
    }

    private void getMartian(RoutingContext ctx) {
        Martian martian = controller.getMartian(Request.from(ctx).getMarsId());

        if (martian == null)
            Response.sendFailure(ctx, 404, "This martian is no customer of your company");

        else Response.sendJsonResponse(ctx, martian);
    }

    private void getProfiles(RoutingContext ctx) {
        List<Profile> profiles = controller.getProfiles();

        if (profiles.isEmpty())
            Response.sendFailure(ctx, 404, "There are no profiles.");

        else Response.sendJsonResponse(ctx, profiles);
    }

    private void getProfilesOfMartian(RoutingContext ctx) {
        List<Profile> profiles = controller.getProfilesOfMartian(Request.from(ctx).getMarsId());

        if (profiles.isEmpty())
            Response.sendFailure(ctx, 404, "This Martian has no profiles.");

        else Response.sendJsonResponse(ctx, profiles);
    }

    private void getTraitsOfMartian(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();
        String traitType = Request.from(ctx).getTraitTypeFromQuery();

        List<Trait> traits;

        if (traitType == null) traits = controller.getTraitsOfMartian(marsId);
        else traits = controller.getTraitsOfMartianByTraitType(marsId, traitType);

        Response.sendJsonResponse(ctx, traits);
    }

    private void buyNewTrait(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();
        String traitName = Request.from(ctx).getTraitNameFromBody();

        Trait trait = controller.buyTrait(marsId, traitName);

        Response.sendJsonResponse(ctx, trait);
    }

    private void getProfileOfMartian(RoutingContext ctx) {
        Profile profile = controller.getProfileOfMartian(Request.from(ctx).getMarsId(), Request.from(ctx).getProfileName());

        if (profile == null)
            Response.sendFailure(ctx, 404, "Profile with this name can't be found in the profiles of this martian");

        else Response.sendJsonResponse(ctx, profile);
    }


    private void unlockProfile(RoutingContext ctx) {
        try {
            List<Profile> profilesOfMarsId = controller.getProfilesOfMartian(Request.from(ctx).getMarsId());
            int profileSlot = controller.getNextProfileSlot(profilesOfMarsId);
            String price = controller.calculatePriceOfProfile(profileSlot);

            Profile newProfile = controller.unlockProfile(Request.from(ctx).getMarsId(), Request.from(ctx).getHeaderProfileName(), price);

            controller.storeNewProfile(newProfile);

            JsonObject response = new JsonObject();
            response.put("message", "Profile " + profileSlot + " unlocked!");

            Response.sendJsonResponse(ctx, 201, response);
        }
        catch (Exception ex) {
            Response.sendFailure(ctx, 404, "Error unlocking the profile.");
        }
    }

    private void getDefaultProfileOfMartian(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();

        Profile profile = controller.getDefaultProfileOfMartian(marsId);

        if (profile == null) Response.sendFailure(ctx, 404, "The initial profile of this customer can't be found.");
        else Response.sendJsonResponse(ctx, 201, profile);
    }

    public void getActiveProfileOfMartian(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();

        Profile profile = controller.getActiveProfileOfMartian(marsId);

        if (profile == null) Response.sendFailure(ctx, 404, "This Martian has no active profile");
        else Response.sendJsonResponse(ctx, 201, profile);
    }

    private void getProfilesUsage(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();

        List<Profile> profiles = controller.getProfilesOfMartian(marsId);
        Map<String, Integer> profilesUsage = controller.getProfilesUsage(profiles);

        if (profilesUsage.isEmpty())
            Response.sendFailure(ctx, 404, "There are no profiles.");

        else Response.sendJsonResponse(ctx, 201, profilesUsage);
    }

    private void activateProfile(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();
        String profileName = Request.from(ctx).getProfileName();

        Profile profile = controller.activateProfile(marsId, profileName);

        Response.sendJsonResponse(ctx, profile);
    }

    private void getTraits(RoutingContext ctx) {
        String type = Request.from(ctx).getTraitTypeFromQuery();

        List<Trait> traits;
        if (type == null) {
            traits = controller.getTraits();
        }
        else {
            traits = controller.getTraits(type);
        }

        if (traits.isEmpty())
            Response.sendFailure(ctx, 404, "Oopsie woopsie, all the traits are gone \uD83D\uDE2D");

        else Response.sendJsonResponse(ctx, traits);
    }

    private void getDefaultTraitsOfMartian(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();
        List<Trait> traits = controller.getTraitsOfMartiansProfile(marsId, "DEFAULT-" + marsId);

        if (traits.isEmpty())
            Response.sendFailure(ctx, 404, "The person has no traits by default :(");

        else Response.sendJsonResponse(ctx, traits);
    }

    private void getTrait(RoutingContext ctx) {
        String traitName = Request.from(ctx).getTraitName();

        Trait trait = controller.getTrait(traitName);

        Response.sendJsonResponse(ctx, trait);
    }

    private void getTraitsOfMartiansProfile(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();
        String profileName = Request.from(ctx).getProfileName();
        List<Trait> traits = controller.getTraitsOfMartiansProfile(marsId, profileName);

        if (traits.isEmpty())
            Response.sendFailure(ctx, 404, "Couldn't find the traits of the given profile of a Martian");

        else Response.sendJsonResponse(ctx, traits);
    }

    private void addTraitToProfile(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();
        String profileName = Request.from(ctx).getProfileName();
        String traitName = Request.from(ctx).getTraitNameFromBody();

        Trait trait = controller.addTraitToProfile(marsId, profileName, traitName);

        Response.sendJsonResponse(ctx, trait);
    }

    private void removeTraitFromProfile(RoutingContext ctx) {
        int marsId = Request.from(ctx).getMarsId();
        String profileName = Request.from(ctx).getProfileName();
        String traitName = Request.from(ctx).getTraitNameFromBody();

        Trait trait = controller.removeTraitFromProfile(marsId, profileName, traitName);

        Response.sendJsonResponse(ctx, trait);
    }

    private void setTraitsOfMartians(RoutingContext ctx) {
        try {
            controller.setTraitsOfMartians();

            JsonObject response = new JsonObject();
            response.put("message", "Updated the table");

            Response.sendJsonResponse(ctx, 201, response);
        }
        catch (Exception ex) {
            Response.sendFailure(ctx, 404, "Something went wrong.");
        }
    }

    private void getChips(RoutingContext ctx) {
        List<Chip> chips = controller.getChips();

        if (chips.isEmpty())
            Response.sendFailure(ctx, 404, "There were no chips found.");

        else Response.sendJsonResponse(ctx, 201, chips);
    }

    private void getNotifications(RoutingContext ctx) {
        List<JsonObject> notifications = controller.getNotifications();

        if (notifications.isEmpty())
            Response.sendFailure(ctx, 404, "There were no notifications found.");

        else Response.sendJsonResponse(ctx, 201, notifications);
    }

    private void onFailedRequest(RoutingContext ctx) {
        Throwable cause = ctx.failure();
        int code = ctx.statusCode();
        String quote = Objects.isNull(cause) ? "" + code : cause.getMessage();

        // Map custom runtime exceptions to a HTTP status code.
        LOGGER.log(Level.INFO, "Failed request", cause);
        if (cause instanceof IllegalArgumentException) {
            code = 400;
        } else if (cause instanceof MalformedRequestException) {
            code = 400;
        } else if (cause instanceof NoSuchElementException) {
            code = 404;
        } else {
            LOGGER.log(Level.WARNING, "Failed request", cause);
        }

        Response.sendFailure(ctx, code, quote);
    }

    private CorsHandler createCorsHandler() {
        return CorsHandler.create(".*.")
                .allowedHeader("x-requested-with")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowCredentials(true)
                .allowedHeader("origin")
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization")
                .allowedHeader("accept")
                .allowedMethod(HttpMethod.HEAD)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PUT);
    }
}
