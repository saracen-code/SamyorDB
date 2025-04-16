package com.samyorBot.classes.buildings;

public class Building {
    private final int id;
    private String name;
    private String location;  // e.g., "North District" or coordinates
    private String type;      // e.g., "House", "Barracks", "Library"
    private String description;

    // Optional owner (can be character ID, guild name, etc.)
    private String ownerId;

    public Building() {
        this.id = -1;
        this.name = "";
        this.location = "";
        this.type = "";
        this.description = "";
        this.ownerId = "";
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return String.format("Building{id=%d, name='%s', type='%s', location='%s'}", id, name, type, location);
    }
}
