package com.samyorBot.classes.companies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyorBot.classes.companies.Company;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CompanyLoader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Company loadCompanyFrom(String resourcePath) throws IOException {
        InputStream is = CompanyLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        return objectMapper.readValue(is, Company.class);
    }

    public static void main(String[] args) {
        try {
            Company loaded = CompanyLoader.loadCompanyFrom("company.json");
            System.out.println(loaded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
