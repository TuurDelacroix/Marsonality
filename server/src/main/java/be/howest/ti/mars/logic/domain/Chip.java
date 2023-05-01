package be.howest.ti.mars.logic.domain;

import java.util.Objects;

public class Chip {

    private int id;
    private boolean activated;

    public Chip(int id, boolean activated) {
        this.id = id;
        this.activated = activated;
    }

    public int getId() {
        return id;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void toggleActivated() { activated = !activated; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chip chip = (Chip) o;
        return getId() == chip.getId() && isActivated() == chip.isActivated();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), isActivated());
    }
}
