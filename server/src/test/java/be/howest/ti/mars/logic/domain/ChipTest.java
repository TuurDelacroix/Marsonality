package be.howest.ti.mars.logic.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChipTest {

    Chip c;

    @BeforeEach
    void createChip() {
        c = new Chip(1, true);
    }

    @Test
    void chipsAreEqual() {
        Chip c2 = new Chip(1, true);

        assertEquals(c, c2);
    }

    @Test
    void chipsWithDifferentIdAreNotEqual() {
        Chip c2 = new Chip(2, true);

        assertNotEquals(c, c2);
    }

    @Test
    void chipsWithSameIdButDifferentActivatedAreNotTheSame() {
        Chip c2 = new Chip(1, false);

        assertNotEquals(c, c2);
    }

    @Test
    void chipIsNotEqualToNull() {
        assertNotEquals(null, c);
    }

    @Test
    void isActivated() {
        assertTrue(c.isActivated());
    }

    @Test
    void setActivated() {
        c.setActivated(false);

        assertFalse(c.isActivated());
    }

    @Test
    void toggleActivated() {
        assertTrue(c.isActivated());

        c.toggleActivated();

        assertFalse(c.isActivated());

        c.toggleActivated();

        assertTrue(c.isActivated());
    }

}