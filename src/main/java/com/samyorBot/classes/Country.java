package com.samyorBot.classes;

import java.util.List;

public class Country {
    public static int GLOBAL_YEAR;
    public static String sheetId = "927499955";

    // Map every country data to a list
    private String[] properties = new String[12];

    public String getProperty(int index) {
        return properties[index];
    }
}
