package be.howest.ti.mars.logic.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TraitTest {

    Trait t1;

    @BeforeEach
    void createTrait() {
        t1 = new Trait("Happy", ":)", TraitType.POSITIVE);
    }

    @Test
    void traitsAreEqual() {
        Trait t2 = new Trait("Happy", ":)", TraitType.POSITIVE);

        assertEquals(t1, t2);
    }

    @Test
    void traitsAreNotEqual() {
        Trait t2 = new Trait("Sad", ":(", TraitType.NEGATIVE);

        assertNotEquals(t1, t2);
    }

    @Test
    void getName() {
        assertEquals("Happy", t1.getName());
    }

    @Test
    void getDescription() {
        assertEquals(":)", t1.getDescription());
    }

    @Test
    void getType() {
        assertEquals(TraitType.POSITIVE, t1.getType());
    }

    @Test
    void traitsWithSameNameDescriptionAndTypeAreEqual() {
        Trait t2 = new Trait("Happy", ":)", TraitType.POSITIVE);

        assertEquals(t1, t2);
    }

    @Test
    void traitsWithDifferentNameAreDifferent() {
        Trait t2 = new Trait("Joyful", ":)", TraitType.POSITIVE);

        assertNotEquals(t1, t2);
    }

}