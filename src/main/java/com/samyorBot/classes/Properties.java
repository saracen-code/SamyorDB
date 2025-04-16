package com.samyorBot.classes;

public class Properties {

    private String[] countryProperties = new String[12];

    public String getCountryProperty(int index) {
        return countryProperties[index];
    }
    public void setCountryProperty(int index, String value) {
        countryProperties[index] = value;
    }
}
