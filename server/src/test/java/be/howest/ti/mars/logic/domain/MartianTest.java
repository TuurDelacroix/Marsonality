package be.howest.ti.mars.logic.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class MartianTest {

    Martian m1;

    @BeforeEach
    void createMartian() {
        m1 = new Martian(1, "John", "Doe", LocalDate.of(2000, 2, 2));
    }

    @Test
    void martiansCanBeCompared() {
        Martian m2 = new Martian(1, "John", "Doe", LocalDate.of(2000, 2, 2));

        assertEquals(m1, m2);
    }

    @Test
    void martiansWithDifferentIdAreNotTheSame() {
        Martian m2 = new Martian(2, "John", "Doe", LocalDate.of(2000, 2, 2));

        assertNotEquals(m1, m2);
    }

    @Test
    void getAgeReturnsCorrectAge() {
        Martian jasper = new Martian(1, "Jasper", "Desnyder", LocalDate.of(2003, 1, 22));

        int age = jasper.getAge();

        assertEquals(19, age);
    }

    @Test
    void getActiveProfileReturnsCorrectProfile() {
        Profile p1 = new Profile("Jolly John", 1, "200", LocalDate.now(), 0, true);
        Profile p2 = new Profile("Sad John", 1, "300", LocalDate.now(), 0, false);

        m1.addProfile(p1);
        m1.addProfile(p2);

        assertEquals(p1, m1.getActiveProfile());
    }

    @Test
    void switchActiveProfileCorrectlySwitchesProfiles() {
        Profile p1 = new Profile("Jolly John", 1, "200", LocalDate.now(), 0, true);
        Profile p2 = new Profile("Sad John", 1, "300", LocalDate.now(), 0, false);

        m1.addProfile(p1);
        m1.addProfile(p2);

        assertEquals(p1, m1.getActiveProfile());

        m1.setCooldownStartMoment(null);
        m1.switchActiveProfile(p2);

        assertEquals(p2, m1.getActiveProfile());
    }

    @Test
    void getProfileWithNameGivenReturnsCorrectProfile() {
        Profile p1 = new Profile("Jolly John", 1, "200", LocalDate.now(), 0, true);
        Profile p2 = new Profile("Sad John", 1, "300", LocalDate.now(), 0, false);

        m1.addProfile(p1);
        m1.addProfile(p2);

        assertEquals(p1, m1.getProfile("Jolly John"));
    }

    @Test
    void getProfileWithProfileGivenReturnsCorrectProfile() {
        Profile p1 = new Profile("Jolly John", 1, "200", LocalDate.now(), 0, true);
        Profile p2 = new Profile("Sad John", 1, "300", LocalDate.now(), 0, false);

        m1.addProfile(p1);
        m1.addProfile(p2);

        assertEquals(p2, m1.getProfile(p2));
    }

    @Test
    void getProfilesReturnsTheRightAmountOfProfiles() {
        Profile p1 = new Profile("Jolly John", 1, "200", LocalDate.now(), 0, true);
        Profile p2 = new Profile("Sad John", 1, "300", LocalDate.now(), 0, false);

        m1.addProfile(p1);
        m1.addProfile(p2);

        assertEquals(2, m1.getProfiles().size());
    }

    @Test
    void getCoolDownStartedReturnsDateAfterSwitchingProfiles() {
        Profile p1 = new Profile("Jolly John", 1, "200", LocalDate.now(), 0, true);
        Profile p2 = new Profile("Sad John", 1, "300", LocalDate.now(), 0, false);

        m1.addProfile(p1);
        m1.addProfile(p2);

        m1.switchActiveProfile(p2);

        LocalDate date = LocalDate.from(m1.getCooldownStartMoment());
        assertEquals(LocalDate.now(), LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth()));
    }

    @Test
    void switchingProfilesSetsInUseToFalseOfPreviouslyActiveProfile() {
        Profile p1 = new Profile("Jolly John", 1, "200", LocalDate.now(), 0, true);
        Profile p2 = new Profile("Sad John", 1, "300", LocalDate.now(), 0, false);

        m1.addProfile(p1);
        m1.addProfile(p2);

        m1.setCooldownStartMoment(null);
        m1.switchActiveProfile(p2);

        assertFalse(p1.isInUse());
    }

    @Test
    void cantSwitchDuringCooldown() {
        Profile p1 = new Profile("Jolly John", 1, "200", LocalDate.now(), 0, true);
        Profile p2 = new Profile("Sad John", 1, "300", LocalDate.now(), 0, false);

        m1.addProfile(p1);
        m1.addProfile(p2);

        m1.switchActiveProfile(p2);

        assertTrue(p1.isInUse());
    }

    @Test
    void getFullName() {
        assertEquals("John Doe", m1.getFullName());
    }
}