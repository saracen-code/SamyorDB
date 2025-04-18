package com.samyorBot.classes.cities;

import java.util.Objects;

/** A city node, possibly coastal or riverine (or both). */
public class City {
    private final int id;
    private final String name;
    private final boolean coastal;
    private final boolean riverine;

    public City(int id, String name, boolean coastal, boolean riverine) {
        this.id       = id;
        this.name     = name;
        this.coastal  = coastal;
        this.riverine = riverine;
    }

    public City(String name, boolean coastal, boolean riverine) {
        this.id       = -1;          // or 0, or any dummy
        this.name     = name;
        this.coastal  = coastal;
        this.riverine = riverine;
    }


    public String getName()    { return name; }
    public boolean isCoastal() { return coastal; }
    public boolean isRiverine(){ return riverine; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City c = (City) o;
        return name.equals(c.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        if (coastal)  sb.append(" 🌊");
        if (riverine) sb.append(" 🏞");
        return sb.toString();
    }

    public Integer getId() {
        return id;
    }

}
