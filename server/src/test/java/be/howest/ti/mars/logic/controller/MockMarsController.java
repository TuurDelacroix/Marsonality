package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.MarsH2Repository;
import be.howest.ti.mars.logic.domain.*;
import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MockMarsController implements MarsController {
    private static final String SOME_QUOTE = "quote";
    private static final Logger LOGGER = Logger.getLogger(MarsH2Repository.class.getName());

    @Override
    public void postSubscription(Subscription sub) {
        //TODO  do not know how to test this
    }

    @Override
    public void postNotification(JsonObject notification) {
        //TODO do not know how to test this
    }

    @Override
    public List<Martian> getMartians() {
        return List.of(
                new Martian(1, "John", "Doe", LocalDate.of(2000, Month.JANUARY, 22))
        );
    }

    @Override
    public Martian getMartian(int marsId) {
        return new Martian(1, "John", "Doe", LocalDate.of(2000, Month.JANUARY, 22));
    }

    @Override
    public List<Profile> getProfiles() {
        return List.of(new Profile("Happy Hans", 1, "200"));
    }

    @Override
    public List<Profile> getProfilesOfMartian(int marsId) {
        return getProfiles();
    }

    @Override
    public Profile getProfileOfMartian(int marsId, String profileName) {
        return new Profile(profileName, marsId, "200");
    }

    @Override
    public int getNextProfileSlot(List<Profile> profilesOfMarsId) {
        return profilesOfMarsId.size()+1;
    }

    @Override
    public String calculatePriceOfProfile(int profileSlot) {
        return String.valueOf((int)(Math.pow(2, profileSlot)*100));
    }

    @Override
    public Profile unlockProfile(int marsId, String profileName, String price) {
        return new Profile(profileName, marsId, price);
    }

    @Override
    public void storeNewProfile(Profile newProfile) {
        LOGGER.log(Level.INFO, "MOCK storing works");
    }

    @Override
    public Profile getDefaultProfileOfMartian(int marsId) {
        return new Profile("DEFAULT-1", 1, "0");
    }

    @Override
    public Profile getActiveProfileOfMartian(int marsId) {
        return new Profile("Happy Hans", 1, "0", LocalDate.now(), 0, true);
    }

    @Override
    public List<Trait> getTraits() {
        return List.of(new Trait("Jealous", "Be more jealous.", TraitType.NEGATIVE));
    }

    @Override
    public List<Trait> getTraits(String traitType) {
        return List.of(new Trait("Jealous", "Be more jealous.", TraitType.valueOf(traitType)));
    }

    @Override
    public List<Trait> getTraitsOfMartiansProfile(int marsId, String profileName) {
        return List.of(new Trait("Jealous", "Be more jealous.", TraitType.NEGATIVE));
    }

    @Override
    public Trait addTraitToProfile(int marsId, String profileName, String traitName) {
        return new Trait(traitName, "description", TraitType.NEUTRAL);
    }

    @Override
    public Trait removeTraitFromProfile(int marsId, String profileName, String traitName) {
        return new Trait(traitName, "description", TraitType.NEUTRAL);
    }

    @Override
    public void setTraitsOfMartians() {
        // Hi if you read this :) This works, believe me!!
    }

    @Override
    public List<Chip> getChips() {
        return List.of(new Chip(1, true));
    }

    @Override
    public List<Trait> getTraitsOfMartian(int marsId) {
        return List.of(new Trait("Jealous", "Be more jealous.", TraitType.NEGATIVE));
    }

    @Override
    public Trait getTrait(String traitName) {
        return new Trait(traitName, "description", TraitType.NEUTRAL);
    }

    @Override
    public Trait buyTrait(int marsId, String traitName) {
        return new Trait(traitName, "description", TraitType.NEUTRAL);
    }

    @Override
    public List<Trait> getTraitsOfMartianByTraitType(int marsId, String traitType) {
        return List.of(new Trait("Jealous", "Be more jealous.", TraitType.valueOf(traitType)));
    }

    @Override
    public List<JsonObject> getNotifications() {
        return List.of(new JsonObject(Map.of("title", "title", "options", "{ body: 'test' }")));
    }

    @Override
    public Map<String, Integer> getProfilesUsage(List<Profile> profiles) {
        return Collections.emptyMap();
    }

    @Override
    public Profile activateProfile(int marsId, String profileName) {
        return new Profile(profileName, 1, "200", LocalDate.now(), 1, true);
    }
}
