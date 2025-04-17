package com.samyorBot.classes.characters;

import java.time.Instant;

public class Ruler {
    private long userId;
    private String country;
    private int military;
    private int stewardship;
    private int diplomacy;
    private int wisdom;
    private Instant assignedAt;

    public Ruler(long userId, String country,
                 int military, int stewardship,
                 int diplomacy, int wisdom) {
        this.userId      = userId;
        this.country     = country;
        this.military    = military;
        this.stewardship = stewardship;
        this.diplomacy   = diplomacy;
        this.wisdom      = wisdom;
        this.assignedAt  = Instant.now();
    }

    // Getters
    public long getUserId() {
        return userId;
    }

    public String getCountry() {
        return country;
    }

    public int getMilitary() {
        return military;
    }

    public int getStewardship() {
        return stewardship;
    }

    public int getDiplomacy() {
        return diplomacy;
    }

    public int getWisdom() {
        return wisdom;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    // Setters
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setMilitary(int military) {
        this.military = military;
    }

    public void setStewardship(int stewardship) {
        this.stewardship = stewardship;
    }

    public void setDiplomacy(int diplomacy) {
        this.diplomacy = diplomacy;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }
}
