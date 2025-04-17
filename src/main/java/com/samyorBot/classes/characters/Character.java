package com.samyorBot.classes.characters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Character {
    private Long id;
    private long userId = 0;
    private double funds = 0;
    private String culture = "Not set yet";
    private String name = "Not set yet";
    private String location = "Not set yet";
    private String birthdate = "Not set yet";      // ISO yyyy-MM-dd
    private String deathdate = "Not set yet";      // new field
    private String affiliation = "Not set yet";
    private String backstory = "Not set yet";
    private List<String> traits = List.of("Default");
    private Map<String, Integer> statistics;
    private List<String> abilities = List.of("Default");
    private String image = "0";
    private String tier = "1";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public Character() {}

    public Character(Long userId) {
        this.userId = userId;
    }

    /** call this once you’ve set birthdate (in yyyy‑MM‑dd) */
    public void randomizeDeathDate() {
        try {
            LocalDate b = LocalDate.parse(this.birthdate, FMT);
            // pick an age between 40 and 90
            long age = ThreadLocalRandom.current().nextLong(40, 91);
            LocalDate d = b.plusYears(age);
            this.deathdate = d.format(FMT);
        } catch (Exception ex) {
            // if parse fails, leave as default
        }
    }

    // full constructor now takes deathdate as well
    public Character(long userId,
                     double funds,
                     String culture,
                     String name,
                     String location,
                     String birthdate,
                     String deathdate,
                     String affiliation,
                     String backstory,
                     List<String> traits,
                     Map<String, Integer> statistics,
                     List<String> abilities,
                     String image,
                     String tier)
    {
        this.userId      = userId;
        this.funds       = funds;
        this.culture     = culture;
        this.name        = name;
        this.location    = location;
        this.birthdate   = birthdate;
        this.deathdate   = deathdate;
        this.affiliation = affiliation;
        this.backstory   = backstory;
        this.traits      = traits;
        this.statistics  = statistics;
        this.abilities   = abilities;
        this.image       = image;
        this.tier        = tier;
    }
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getFunds() { return funds; }

    public void setFunds(double funds) { this.funds = funds; }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
    public String getDeathdate() {
        return this.deathdate;
    }
    public void setDeathdate(String deathdate) {
        this.deathdate = deathdate;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getBackstory() {
        return backstory;
    }

    public void setBackstory(String backstory) {
        this.backstory = backstory;
    }

    public List<String> getTraits() {
        return traits;
    }

    public void setTraits(List<String> traits) {
        this.traits = traits;
    }

    public Map<String, Integer> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Integer> statistics) {
        this.statistics = statistics;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    @Override
    public String toString() {
        return "Character{" +
                "userId=" + userId + '\'' +
                ", funds='" + funds + '\'' +
                ", culture='" + culture + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", backstory='" + backstory + '\'' +
                ", traits='" + traits + '\'' +
                ", statistics='" + statistics + '\'' +
                ", abilities='" + abilities + '\'' +
                ", image='" + image + '\'' +
                ", tier='" + tier + '\'' +
                '}';
    }

}
