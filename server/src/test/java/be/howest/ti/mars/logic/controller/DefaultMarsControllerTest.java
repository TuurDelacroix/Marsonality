package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.domain.*;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultMarsControllerTest {

    private static final String URL = "jdbc:h2:./db-14";

    MarsController sut;

    @BeforeEach
    void setup() {
        sut = new DefaultMarsController();
    }

    @BeforeAll
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url", "jdbc:h2:./db-14",
                "username", "",
                "password", "",
                "webconsole.port", 9000));
        Repositories.configure(dbProperties);
    }

    @BeforeEach
    void setupTest() {
        Repositories.getH2Repo().generateData();
    }

    @Test
    void getMartians() {
        List<Martian> martians = sut.getMartians();

        assertFalse(martians.isEmpty());
        assertNotNull(martians.get(0));
    }

    @Test
    void getMartian() {
        Martian martian = sut.getMartian(1);

        assertNotNull(martian);
        assertEquals(1, martian.getMarsId());
    }

    @Test
    void getProfiles() {
        List<Profile> profiles = sut.getProfiles();

        assertFalse(profiles.isEmpty());
    }

    @Test
    void getProfilesOfMartian() {
        int marsId = 1;
        List<Profile> profiles = sut.getProfilesOfMartian(marsId);

        assertFalse(profiles.isEmpty());
    }

    @Test
    void getProfileOfMartian() {
        int marsId = 2;
        String profileName = "DEFAULT-2";

        Profile profile = sut.getProfileOfMartian(marsId, profileName);

        assertNotNull(profile);
        assertEquals(profileName, profile.getName());
    }

    @Test
    void getNextProfileSlot() {
        List<Profile> profilesOfMarsId = sut.getProfilesOfMartian(1);

        int nextSlot = sut.getNextProfileSlot(profilesOfMarsId);

        assertFalse(profilesOfMarsId.isEmpty());
        assertEquals(profilesOfMarsId.size(), nextSlot);
    }

    @Test
    void calculatePriceOfProfile() {
        List<Profile> profilesOfMarsId = sut.getProfilesOfMartian(1);
        int nextSlot = sut.getNextProfileSlot(profilesOfMarsId);
        int profileSlot = sut.getNextProfileSlot(profilesOfMarsId);

        String price = sut.calculatePriceOfProfile(profileSlot);

        assertEquals(String.valueOf((int) Math.pow(2, nextSlot) * 100), price);
    }

    @Test
    void unlockProfile() {
        int marsId = 2;
        String profileName = "new profile";
        String price = "100";

        Profile profile = new Profile(profileName, marsId, price);

        Profile newProfile = sut.unlockProfile(marsId, profileName, price);

        assertEquals(profile, newProfile);
    }

    @Test
    void storeNewProfile() {
        assertDoesNotThrow(() -> sut.storeNewProfile(new Profile("test", 1, "100")));
    }

    @Test
    void getDefaultProfileOfMartian() {
        Profile defaultProfile = sut.getDefaultProfileOfMartian(1);

        assertTrue(defaultProfile.getName().contains("DEFAULT"));
        assertEquals("DEFAULT-1", defaultProfile.getName());
    }

    @Test
    void getTraits() {
        List<Trait> traits = sut.getTraits();

        assertFalse(traits.isEmpty());
    }

    @Test
    void getTraitsByType() {
        List<Trait> traits = sut.getTraits("POSITIVE");
        List<Trait> allTraits = sut.getTraits();

        assertFalse(traits.isEmpty());
        assertTrue(traits.size() < allTraits.size());
    }

    @Test
    void getTraitsOfMartiansProfile() {
        int marsId = 2;
        String profileName = "Party Mode";

        List<Trait> traitsOfMartian = sut.getTraitsOfMartiansProfile(marsId, profileName);

        assertFalse(traitsOfMartian.isEmpty());
    }

    @Test
    void setTraitsOfMartians() {
        assertDoesNotThrow(() -> sut.setTraitsOfMartians());
    }

    @Test
    void getChips() {
        List<Chip> chips = sut.getChips();

        assertFalse(chips.isEmpty());
    }

    @Test
    void getNotifications() {
        List<JsonObject> notifications = sut.getNotifications();
        assertTrue(notifications.isEmpty());
    }

    @Test
    void postSubscription() {
        assertDoesNotThrow(() -> sut.postSubscription(new Subscription(new JsonObject())));
    }

    @Test
    void postNotification() {
        // TODO => Figure out how to test this
    }

    @Test
    void getActiveProfileOfMartian() {
        Profile profile = sut.getActiveProfileOfMartian(1);

        assertNotNull(profile);
        assertTrue(profile.isInUse());
    }

    @Test
    void getActiveProfileOfMartianThatDoesNotExist() {
        Profile profile = sut.getActiveProfileOfMartian(0);
        assertNull(profile);
    }

    @Test
    void getTrait() {
        Trait trait = sut.getTrait("Kind");

        assertNotNull(trait);
        assertEquals("Kind", trait.getName());
    }

    @Test
    void getTraitThatDoesNotExist() {
        Trait trait = sut.getTrait("test trait");
        assertNull(trait);
    }

    @Test
    void addTraitToProfile() {
        int id = 2;

        // Doing this to have an open slot in this profile
        sut.removeTraitFromProfile(id, "Party Mode", "Courageous");

        sut.buyTrait(id, "Dull");

        Trait trait = sut.addTraitToProfile(id, "Party Mode", "Dull");

        assertNotNull(trait);
        assertEquals("Dull", trait.getName());
        assertTrue(sut.getProfileOfMartian(id, "Party Mode").getTraits().contains(trait));
    }

    @Test
    void removeTraitFromProfile() {
        Trait trait = sut.removeTraitFromProfile(3, "Festive events", "Erratic");

        assertNotNull(trait);
        assertFalse(sut.getTraitsOfMartiansProfile(3, "Festive events").contains(sut.getTrait("Erratic")));
    }

    @Test
    void getTraitsOfMartian() {
        List<Trait> traits = sut.getTraitsOfMartian(1);

        assertNotNull(traits);
    }

    @Test
    void buyTrait() {
        Trait trait = sut.buyTrait(1, "Dull");

        assertNotNull(trait);
        assertTrue(sut.getTraitsOfMartian(1).contains(trait));
    }

    @Test
    void getTraitsOfMartianByTraitType() {
        sut.setTraitsOfMartians();

        List<Trait> traits = sut.getTraitsOfMartianByTraitType(1, "POSITIVE");

        assertNotNull(traits);
        assertEquals(TraitType.POSITIVE, traits.get(0).getType());
    }

    @Test
    void getProfileUsages() {
        Map<String, Integer> profileUsages = sut.getProfilesUsage(List.of(new Profile("test", 1, "200", LocalDate.now())));

        assertNotNull(profileUsages);
    }

    @Test
    void activateProfile() {
        Profile profile = sut.activateProfile(3, "Romance time");

        assertNotNull(profile);
        assertTrue(profile.isInUse());
    }
}
