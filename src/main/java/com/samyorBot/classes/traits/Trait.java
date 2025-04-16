package com.samyorBot.classes.traits;

public class Trait {
    private final String name;
    private final int value;

    public Trait(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public int getValue() { return value; }

    @Override
    public String toString() {
        return name + " (" + (value > 0 ? "+" : "") + value + ")";
    }
}
