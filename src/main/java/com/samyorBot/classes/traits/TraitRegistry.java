package com.samyorBot.classes.traits;

import java.util.*;

/**
 * This class manages the loading and retrieval of traits.
 */
public class TraitRegistry {
    // List to hold all the traits
    private static final List<Trait> traits = new ArrayList<>();
    // Map to allow fast lookup by trait name (key is just the name, not the value)
    private static final Map<String, Trait> traitMap = new HashMap<>();

    /**
     * Initialize the trait registry by loading traits from the specified file.
     * @param filepath The path to the JSON file containing traits.
     */
    public static void init(String filepath) {
        System.out.println("[INIT] Loading traits...");
        List<Trait> loaded = TraitLoader.loadTraits(filepath);
        traits.clear();
        traitMap.clear();

        // Populate the traits list and trait map
        for (Trait t : loaded) {
            traits.add(t);
            traitMap.put(t.getName().toLowerCase(), t); // Lowercase name for case-insensitive lookup
        }

        System.out.println("[INIT] Loaded " + traits.size() + " traits.");
    }

    /**
     * Get all traits.
     * @return An unmodifiable list of all traits.
     */
    public static List<Trait> getAllTraits() {
        return Collections.unmodifiableList(traits);
    }

    /**
     * Get a trait by its name.
     * @param name The name of the trait to retrieve.
     * @return The trait corresponding to the given name, or null if not found.
     */
    public static Trait getTrait(String name) {
        if (name == null) {
            return null; // Return null if name is null
        }
        name = name.replaceFirst("^trait:", "");
        return traitMap.get(name.toLowerCase()); // Perform case-insensitive lookup
    }
}
