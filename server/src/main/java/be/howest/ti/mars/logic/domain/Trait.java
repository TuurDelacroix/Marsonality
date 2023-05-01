package be.howest.ti.mars.logic.domain;

import java.util.Objects;

public class Trait {

    private String name;
    private String description;
    private TraitType type;

    public Trait(String name, String description, TraitType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TraitType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trait trait = (Trait) o;
        return name.equals(trait.name) && Objects.equals(description, trait.description) && type == trait.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, type);
    }
}
