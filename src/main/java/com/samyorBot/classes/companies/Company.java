package com.samyorBot.classes.companies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.samyorBot.classes.buildings.Building;
import com.samyorBot.classes.buildings.OwnsBuildings;

import java.util.*;

public class Company implements OwnsBuildings {

    private String id;
    private String ownerId;
    private String name;
    private Set<String> members; // character IDs or usernames
    private List<Building> buildings;
    private double funds; // shared company funds (e.g., gold)

    @JsonCreator
    public Company(
            @JsonProperty("id") String id,
            @JsonProperty("ownerId") String ownerId,
            @JsonProperty("name") String name,
            @JsonProperty("members") Set<String> members,
            @JsonProperty("buildings") List<Building> buildings,
            @JsonProperty("funds") double funds) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.members = (members != null) ? members : new HashSet<>();
        this.buildings = (buildings != null) ? buildings : new ArrayList<>();
        this.funds = funds;
    }

    public Company() {
    }

    // MEMBERSHIP
    public void addMember(String memberId) {
        members.add(memberId);
    }

    public void removeMember(String memberId) {
        members.remove(memberId);
    }

    public boolean isMember(String memberId) {
        return members.contains(memberId);
    }

    public Set<String> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    // BUILDING OWNERSHIP
    @Override
    public List<Building> getBuildings() {
        return Collections.unmodifiableList(buildings);
    }

    @Override
    public void addBuilding(Building building) {
        buildings.add(building);
        building.setOwnerId(this.id); // optional if linking back
    }

    @Override
    public void removeBuilding(Building building) {
        buildings.remove(building);
    }

    @Override
    public boolean ownsBuilding(Building building) {
        return buildings.contains(building);
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

    // FINANCIALS
    public double getFunds() {
        return funds;
    }

    public void setFunds(double funds) {
        this.funds = funds;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.funds += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= funds) {
            funds -= amount;
            return true;
        }
        return false;
    }

    // when dissolving a company
    public void distributeRevenue(double totalRevenue) {
        if (members.isEmpty()) return;

        double share = totalRevenue / members.size();
        funds += totalRevenue;

        System.out.printf("Revenue distributed: %.2f per member\n", share);
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() { return ownerId; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) { this.id = id; }

    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    @Override
    public String toString() {
        return String.format("Company{id='%s', name='%s', funds=%.2f, members=%d, buildings=%d}",
                id, name, funds, members.size(), buildings.size());
    }
}
