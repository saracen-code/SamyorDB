package com.samyorBot.classes.traits;

import java.util.ArrayList;
import java.util.List;

public class TraitProfile {
    private final List<Trait> traits = new ArrayList<>();

    // Add a trait if under 5 total
    public boolean addTrait(Trait trait) {
        if (traits.size() < 5) {
            traits.add(trait);
            return true;
        }
        return false; // cannot add more than 5 traits
    }

    // Get all traits
    public List<Trait> getTraits() {
        return traits;
    }

    // Compute if the traits are balanced
    public boolean isBalanced() {
        int total = traits.stream().mapToInt(Trait::getValue).sum();
        return total > -3 && total < 3;
    }

    @Override
    public String toString() {
        return "Traits: " + traits + "\nBalanced: " + isBalanced();
    }
}
