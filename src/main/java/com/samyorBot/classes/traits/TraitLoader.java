package com.samyorBot.classes.traits;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/*
 * LOADS TRAITS FROM @resources/traits.json
 */

public class TraitLoader {
    public static List<Trait> loadTraits(String filepath) {
        List<Trait> traits = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filepath)));
            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                int value = obj.getInt("value");
                traits.add(new Trait(name, value));
            }
        } catch (IOException e) {
            System.err.println("Failed to load traits: " + e.getMessage());
        }
        return traits;
    }

    public static Map<String, Trait> loadTraitsMap(String filepath) {
        Map<String, Trait> map = new HashMap<>();
        for (Trait t : loadTraits(filepath)) {
            map.put(t.getName().toLowerCase(), t); // lowercase for easy lookup
        }
        return map;
    }
}
