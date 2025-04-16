package com.samyorBot.classes.buildings;

import java.util.List;

public interface OwnsBuildings {

    // Get a list of building IDs or objects owned
    List<Building> getBuildings();

    // Add a building to the owner
    void addBuilding(Building building);

    // Remove a building from the owner
    void removeBuilding(Building building);

    // Check if the owner owns a specific building
    boolean ownsBuilding(Building building);
}
