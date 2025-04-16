package com.samyorBot.classes.characters;

public class Ability {
    private int id;
    private String name;
    private String type; // "regular" or "special"
    private int damage;
    private String description;
    private boolean approved;

    public Ability() {}

    public Ability(String name, String type, int damage, String description, boolean approved) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.description = description;
        this.approved = approved;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "Ability{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", damage=" + damage +
                ", description='" + description + '\'' +
                ", approved=" + approved +
                '}';
    }
}
