package com.samyorBot.classes.companies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class CompanySaver {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT); // pretty print

    public static void saveCompanyToFile(Company company, String path) throws IOException {
        objectMapper.writeValue(new File(path), company);
    }
}
