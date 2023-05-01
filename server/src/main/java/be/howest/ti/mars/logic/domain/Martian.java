package be.howest.ti.mars.logic.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Martian {

    private static final int COOLDOWN = 5;

    private final int marsId;
    private final String firstname;
    private final String lastname;
    private final LocalDate birthdate;
    private LocalDateTime cooldownStartMoment;

    private List<Profile> profiles;

    private Chip chip;

    public Martian(int marsId, String firstname, String lastname, LocalDate birthdate) {
        this(marsId, firstname, lastname, birthdate, null, new ArrayList<>());
    }

    public Martian(int marsId, String firstname, String lastname, LocalDate birthdate, LocalDateTime cooldownStartMoment) {
        this(marsId, firstname, lastname, birthdate, cooldownStartMoment, new ArrayList<>());
    }

    public Martian(int marsId, String firstname, String lastname, LocalDate birthdate, LocalDateTime cooldownStartMoment, List<Profile> profiles) {
        this.marsId = marsId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.cooldownStartMoment = cooldownStartMoment;
        this.profiles = profiles;

        chip = new Chip(marsId, true);
    }

    public int getMarsId() {
        return marsId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    @JsonIgnore
    public String getFullName() {
        return getFirstname() + " " + getLastname();
    }

    @JsonIgnore
    public LocalDate getBirthdate() {
        return birthdate;
    }

    public Chip getChip() {
        return chip;
    }

    public int getAge() {
        LocalDate today = LocalDate.now();

        Period p = Period.between(getBirthdate(), today);

        return p.getYears();
    }

    @JsonIgnore
    public LocalDateTime getCooldownStartMoment() {
        return cooldownStartMoment;
    }

    public void setCooldownStartMoment(LocalDateTime cooldownStartMoment) {
        this.cooldownStartMoment = cooldownStartMoment;
    }

    public List<Profile> getProfiles() {
        return Collections.unmodifiableList(profiles);
    }

    public Profile getProfile(String name) {
        return profiles.stream()
                .filter(profile -> profile.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Profile getProfile(Profile profile) {
        return profiles.stream()
                .filter(p -> p.equals(profile))
                .findFirst()
                .orElse(null);
    }

    public void addProfile(Profile profile) {
        if (getProfiles().isEmpty())
            switchActiveProfile(profile);

        profiles.add(profile);
        profile.setNumber(profiles.size() - 1);
    }

    public void addProfiles(List<Profile> profiles) {
        profiles.forEach(this::addProfile);
    }

    public Profile getActiveProfile() {
        return profiles.stream()
                        .filter(Profile::isInUse)
                        .findFirst()
                        .orElse(null);
    }

    public void switchActiveProfile(Profile profile) {
        if (canSwitch()) {
            Profile currentlyActiveProfile = getActiveProfile();

            if (currentlyActiveProfile != null)
                currentlyActiveProfile.setInUse(false);

            profile.setInUse(true);
            profile.increaseUsages();

            setCooldownStartMoment(LocalDateTime.now());
        }
    }

    public boolean canSwitch() {
        if (getCooldownStartMoment() == null) return true;

        long diff = ChronoUnit.DAYS.between(getCooldownStartMoment(), LocalDateTime.now());

        return diff >= COOLDOWN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Martian martian = (Martian) o;
        return getMarsId() == martian.getMarsId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMarsId());
    }
}
