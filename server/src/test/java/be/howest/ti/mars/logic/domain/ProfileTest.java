package be.howest.ti.mars.logic.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    Profile p1;

    @BeforeEach
    void createProfile() {
        p1 = new Profile("Happy Hans", 1, "200");
    }

    @Test
    void profilesWithDifferentNamesAreNotTheSame() {
        Profile p2 = new Profile("Sad Hans", 1, "300");

        assertNotEquals(p1, p2);
    }

    @Test
    void profilesWithSameNameAndDifferentMarsIdAreNotTheSame() {
        Profile p2 = new Profile("Happy Hans", 2, "300");

        assertNotEquals(p1, p2);
    }

    @Test
    void profilesWithSameNameAndMarsIdButDifferentPriceAreNotTheSame() {
        Profile p2 = new Profile("Sad Hans", 1, "300");

        assertNotEquals(p1, p2);
    }

    @Test
    void profilesWithSameNameAndMarsIdAndPriceAreTheSame() {
        Profile p2 = new Profile("Happy Hans", 1, "200");

        assertEquals(p1, p2);
    }

    @Test
    void addTraits() {
        Trait t1 = new Trait("Happy", ":)", TraitType.POSITIVE);

        p1.addTrait(t1);

        assertEquals(1, p1.getTraits().size());
    }

    @Test
    void onlyUniqueTraitsCanBeAdded() {
        Trait t1 = new Trait("Happy", ":)", TraitType.POSITIVE);
        Trait t2 = new Trait("Happy", ":)", TraitType.POSITIVE);

        p1.addTrait(t1);
        p1.addTrait(t2);

        assertEquals(1, p1.getTraits().size());
    }

    @Test
    void numberCanBeSetCorrectly() {
        assertEquals(-1, p1.getNumber());

        p1.setNumber(1);

        assertEquals(1, p1.getNumber());
    }

    @Test
    void usageIsIncreasedAfterSwitchingToProfile() {
        Martian m1 = new Martian(1, "John", "Doe", LocalDate.EPOCH);
        Profile p2 = new Profile("Jealous John", 1, "300");

        m1.addProfiles(List.of(p1, p2));
        assertEquals(1, p1.getUsages());
        assertEquals(0, p2.getUsages());

        // Doing this to get around the cooldown period (normally a user can't switch for 5 days)
        m1.setCooldownStartMoment(null);
        m1.switchActiveProfile(p2);

        assertEquals(1, p1.getUsages());
        assertEquals(1, p2.getUsages());
    }

}