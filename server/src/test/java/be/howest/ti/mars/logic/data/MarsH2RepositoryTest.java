package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.*;
import be.howest.ti.mars.logic.exceptions.RepositoryException;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MarsH2RepositoryTest {
    private static final String URL = "jdbc:h2:./db-14";

    @BeforeEach
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url",URL,
                "username", "",
                "password", "",
                "webconsole.port", 9000 ));
        Repositories.configure(dbProperties);
    }

    @Test
    void getMartians() {
        List<Martian> martians = Repositories.getH2Repo().getMartians();

        assertNotNull(martians);
    }

    @Test
    void getMartian() {
        int marsId = 1;

        Martian martian = Repositories.getH2Repo().getMartian(marsId);

        assertNotNull(martian);
        assertEquals(1, martian.getMarsId());
    }

    @Test
    void getMartianDoesntExist() {
        int marsId = 99;

        Martian martian = Repositories.getH2Repo().getMartian(marsId);

        assertNull(martian);
    }

    @Test
    void getProfiles() {
        List<Profile> profiles = Repositories.getH2Repo().getProfiles();

        assertNotNull(profiles);
    }

    @Test
    void getProfilesOfMartian() {
        int marsId = 1;

        List<Profile> profiles = Repositories.getH2Repo().getProfilesOfMartian(marsId);

        assertNotNull(profiles);
        assertEquals(0, profiles.get(0).getNumber());
    }

    @Test
    void getProfilesOfMartianMarsIdDoesntExist() {
        int marsId = 99;

        List<Profile> profiles = Repositories.getH2Repo().getProfilesOfMartian(marsId);

        assertEquals(Collections.emptyList(), profiles);
    }

    @Test
    void getProfileOfMartian() {
        int marsId = 1;
        String profileName = "DEFAULT-1";

        Profile profile = Repositories.getH2Repo().getProfileOfMartian(marsId, profileName);

        assertNotNull(profile);
        assertEquals(profileName, profile.getName());
    }

    @Test
    void getTraits() {
        List<Trait> traits = Repositories.getH2Repo().getTraits();

        assertNotNull(traits);
    }

    @Test
    void getTraitsWithType() {
        List<Trait> traits = Repositories.getH2Repo().getTraits("neutral");

        assertNotNull(traits);
    }

    @Test
    void getTraitsWithInvalidType() {
        List<Trait> traits = Repositories.getH2Repo().getTraits("invalid");

        assertTrue(traits.isEmpty());
    }

    @Test
    void getTraitsOfMartians() {
        Repositories.getH2Repo().setTraitsOfMartians();

        List<Trait> traits = Repositories.getH2Repo().getTraitsOfMartian(3);

        assertNotNull(traits);
        assertFalse(traits.isEmpty());
    }

    @Test
    void buyTrait() {
        Trait trait = Repositories.getH2Repo().buyTrait(1, "Tough");

        assertNotNull(trait);
    }

    @Test
    void buyTraitThatMartianAlreadyHasFails() {
        Trait trait = Repositories.getH2Repo().buyTrait(1, "Tough");
        assertNotNull(trait);

        assertThrows(RepositoryException.class, () -> Repositories.getH2Repo().buyTrait(1, "Tough"));
    }

    @Test
    void buyingTraitThatIsInDefaultProfileFails() {
        Repositories.getH2Repo().setTraitsOfMartians();

        assertThrows(RepositoryException.class, () -> Repositories.getH2Repo().buyTrait(1, "Perfectionist"));
    }

    @Test
    void getTraitsOfMartianByTraitType() {
        Repositories.getH2Repo().setTraitsOfMartians();

        List<Trait> traits = Repositories.getH2Repo().getTraitsOfMartianByTraitType(1, "POSITIVE");

        assertNotNull(traits);
        assertFalse(traits.isEmpty());
        assertEquals("POSITIVE", traits.get(0).getType().toString());
    }

    @Test
    void getTraitsOfMartiansProfile() {
        int marsId = 2;
        String profileName = "DEFAULT-2";

        List<Trait> traits = Repositories.getH2Repo().getTraitsOfMartiansProfile(marsId, profileName);

        assertNotNull(traits);
    }

    @Test
    void getTrait() {
        Trait trait = Repositories.getH2Repo().getTrait("Kind");

        assertNotNull(trait);
    }

    @Test
    void getTraitThatDoesNotExist() {
        Trait trait = Repositories.getH2Repo().getTrait("this does not exist");

        assertNull(trait);
    }

    @Test
    void addTraitToProfile() {
        // Doing this to have an open slot in this profile
        Repositories.getH2Repo().removeTraitFromProfile(3, "Party animal", "Chummy");

        Repositories.getH2Repo().buyTrait(3, "Quiet");
        Trait trait = Repositories.getH2Repo().addTraitToProfile(3, "Party animal", "Quiet");

        assertNotNull(trait);
    }

    @Test
    void addTraitThatDoesNotExistToProfile() {
        assertThrows(RepositoryException.class, () -> Repositories.getH2Repo().addTraitToProfile(1, "Happy Vibes", "this trait does not exist"));
    }

    @Test
    void addTraitThatMartianDoesNotHaveToProfile() {
        assertThrows(RepositoryException.class, () -> Repositories.getH2Repo().addTraitToProfile(1, "Happy Vibes", "Quiet"));
    }

    @Test
    void addTraitToProfileThatDoesNotExist() {
        Repositories.getH2Repo().buyTrait(1, "Quiet");
        assertThrows(RepositoryException.class, () -> Repositories.getH2Repo().addTraitToProfile(1, "Sad Vibes", "Quiet"));
    }

    @Test
    void addTraitToProfileThatAlreadyHasThatTrait() {
        assertThrows(RepositoryException.class, () -> Repositories.getH2Repo().addTraitToProfile(1, "Happy Vibes", "Outspoken"));
    }

    @Test
    void removeTraitFromProfile() {
        Trait trait = Repositories.getH2Repo().removeTraitFromProfile(3, "Party animal", "Chummy");

        assertNotNull(trait);
        assertFalse(Repositories.getH2Repo().getTraitsOfMartiansProfile(3, "Party animal").contains(Repositories.getH2Repo().getTrait("Outspoken")));
    }

    @Test
    void removeTraitNotInProfile() {
        assertThrows(RepositoryException.class, () -> Repositories.getH2Repo().removeTraitFromProfile(1, "Happy Vibes", "Quiet"));
    }

    @Test
    void removeTraitFromNonExistingProfile() {
        assertThrows(RepositoryException.class, () -> Repositories.getH2Repo().removeTraitFromProfile(1, "Sad Vibes", "Outspoken"));
    }

    @Test
    void removeTraitFromDefaultProfile() {
        assertThrows(RepositoryException.class, () -> Repositories.getH2Repo().removeTraitFromProfile(1, "DEFAULT-1", "Artful"));
    }

    @Test
    void storeProfileOfMartian() {
        Profile newProfile = new Profile(
                "my new profile",
                3,
                "100",
                LocalDate.now(),
                0,
                false
        );

        assertDoesNotThrow(() ->
            Repositories.getH2Repo().storeProfileOfMartian(newProfile)
        );
    }

    @Test
    void getDefaultProfileOfMartian() {
        int marsId = 1;

        Profile defaultProfile = Repositories.getH2Repo().getDefaultProfileOfMartian(marsId);

        assertNotNull(defaultProfile);
        assertEquals("DEFAULT-1", defaultProfile.getName());
        assertEquals(0, defaultProfile.getNumber());
    }

    @Test
    void getDefaultProfileOfMartianWithIdThatDoesNotExist() {
        int marsId = 99;

        Profile defaultProfile = Repositories.getH2Repo().getDefaultProfileOfMartian(marsId);

        assertNull(defaultProfile);
    }

    @Test
    void getChips() {
        List<Chip> chips = Repositories.getH2Repo().getChips();

        assertFalse(chips.isEmpty());
    }

    @Test
    void setTraitsOfMartians() {
        assertDoesNotThrow(
                () -> Repositories.getH2Repo().setTraitsOfMartians()
        );
    }

    @Test
    void insertNotification() {
        JsonObject notification = new JsonObject();
        notification.put("title", "title");
        notification.put("options", Map.of("body", "testtest"));

        assertDoesNotThrow(() -> Repositories.getH2Repo().insertNotification(notification));
    }

    @Test
    void getNotifications() {
        List<JsonObject> notifications = Repositories.getH2Repo().getNotifications();

        assertNotNull(notifications);
    }

    @Test
    void activateProfile() {
        int id = 3;

        Profile activeProfile = Repositories.getH2Repo().getActiveProfileOfMartian(id);
        assertNotNull(activeProfile);
        assertTrue(activeProfile.isInUse());

        Profile profileToActivate = Repositories.getH2Repo().getProfileOfMartian(id, "DEFAULT-3");
        assertFalse(profileToActivate.isInUse());

        profileToActivate = Repositories.getH2Repo().activateProfile(id, profileToActivate.getName());
        activeProfile = Repositories.getH2Repo().getProfileOfMartian(id, activeProfile.getName());

        assertEquals(profileToActivate, Repositories.getH2Repo().getActiveProfileOfMartian(id));
        assertTrue(profileToActivate.isInUse());
        assertFalse(activeProfile.isInUse());
    }
}
