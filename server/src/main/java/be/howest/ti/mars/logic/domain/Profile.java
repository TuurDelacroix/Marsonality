package be.howest.ti.mars.logic.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Profile {

    private String name;
    private int marsId;
    private BigInteger price;
    private LocalDate creationDate;
    private int usages;
    private boolean inUse;
    private int number;
    List<Trait> traits;

    public Profile(String name, int marsId, String price) {
        this(name, marsId, price, LocalDate.now(), 0, false);
    }

    public Profile(String name, int marsId, String price, LocalDate creationDate) {
        this(name, marsId, price, creationDate, 0, false);
    }

    public Profile(String name, int marsId, String price, LocalDate creationDate, int counter, boolean inUse) {
        this.name = name;
        this.marsId = marsId;
        this.price = new BigInteger(price);
        this.creationDate = creationDate;
        this.usages = counter;
        this.inUse = inUse;

        this.number = -1;

        traits = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getMarsId() {
        return marsId;
    }

    public BigInteger getPrice() {
        return price;
    }

    @JsonIgnore
    public LocalDate getCreationDate() {
        return creationDate;
    }

    public int getUsages() {
        return usages;
    }

    public void increaseUsages() {
        this.usages++;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Trait> getTraits() {
        return traits;
    }

    public void addTrait(Trait trait) {
        if (!getTraits().contains(trait)) getTraits().add(trait);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return getMarsId() == profile.getMarsId() && getName().equals(profile.getName()) && getPrice().equals(profile.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getMarsId(), getPrice());
    }
}
