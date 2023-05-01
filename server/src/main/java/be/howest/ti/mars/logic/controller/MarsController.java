package be.howest.ti.mars.logic.controller;

import io.vertx.core.json.JsonObject;
import be.howest.ti.mars.logic.domain.*;

import java.util.List;
import java.util.Map;

public interface MarsController {
    void postSubscription(Subscription sub);

    void postNotification(JsonObject notification);

    List<Martian> getMartians();

    Martian getMartian(int marsId);

    List<Profile> getProfiles();

    List<Profile> getProfilesOfMartian(int marsId);

    Profile getProfileOfMartian(int marsId, String profileName);

    int getNextProfileSlot(List<Profile> profilesOfMarsId);

    String calculatePriceOfProfile(int profileSlot);

    Profile unlockProfile(int marsId, String headerProfileName, String price);

    void storeNewProfile(Profile newProfile);

    Profile getDefaultProfileOfMartian(int marsId);

    Profile getActiveProfileOfMartian(int marsId);

    List<Trait> getTraits();

    List<Trait> getTraits(String traitType);

    List<Trait> getTraitsOfMartiansProfile(int marsId, String profileName);

    Trait addTraitToProfile(int marsId, String profileName, String traitName);

    Trait removeTraitFromProfile(int marsId, String profileName, String traitName);

    void setTraitsOfMartians();

    List<Chip> getChips();

    List<Trait> getTraitsOfMartian(int marsId);

    Trait getTrait(String traitName);

    Trait buyTrait(int marsId, String traitName);

    List<Trait> getTraitsOfMartianByTraitType(int marsId, String traitType);

    List<JsonObject> getNotifications();

    Map<String, Integer> getProfilesUsage(List<Profile> profiles);

    Profile activateProfile(int marsId, String profileName);
}
