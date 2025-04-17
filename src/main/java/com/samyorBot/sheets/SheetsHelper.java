package com.samyorBot.sheets;

import com.samyorBot.classes.Country;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class SheetsHelper {
    private static final String APPLICATION_NAME = "SamyorBot";
    private static final String SHEET_ID         = "1CcbI4dksDLtP-I5Kq7X6x_8WxMTWpOMT7rpYNW_l-wc";
    private static final String SHEET_NAME       = "SafeTest";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES     = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static Sheets service;

    private static Sheets getService() throws IOException, GeneralSecurityException {
        if (service == null) {
            GoogleCredential cred;
            try (FileInputStream in = new FileInputStream("google_access.json")) {
                cred = GoogleCredential.fromStream(in).createScoped(SCOPES);
            }
            service = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    cred)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
        return service;
    }

    public static List<List<Object>> readRange(String range)
            throws IOException, GeneralSecurityException {
        ValueRange vr = getService()
                .spreadsheets().values()
                .get(SHEET_ID, range)
                .execute();
        return vr.getValues() == null
                ? Collections.emptyList()
                : vr.getValues();
    }

    public static void writeRange(String range, List<List<Object>> values)
            throws IOException, GeneralSecurityException {
        ValueRange body = new ValueRange().setValues(values);
        getService()
                .spreadsheets().values()
                .update(SHEET_ID, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    public static String columnNumberToLetter(int columnNumber) {
        StringBuilder col = new StringBuilder();
        while (columnNumber > 0) {
            columnNumber--;
            col.insert(0, (char)('A' + (columnNumber % 26)));
            columnNumber /= 26;
        }
        return col.toString();
    }

    /**
     * Append a country as the next column in SafeTest:
     * • Header (successionType) in row 1
     * • Values rows 2–18
     */
    public static void appendCountryColumn(Country country)
            throws IOException, GeneralSecurityException {

        // 1) Read row 2 across to find first empty column
        String keyRow = SHEET_NAME + "!2:2";
        List<List<Object>> row2 = readRange(keyRow);

        // if there's no row2 at all, start at B (index=2)
        int nextColNum = 2;
        if (!row2.isEmpty()) {
            List<Object> cols = row2.get(0);
            // scan for the first empty cell
            nextColNum = 1; // A = 1, B = 2, ...
            for (Object cell : cols) {
                nextColNum++;
                if (cell == null || cell.toString().trim().isEmpty()) {
                    break;
                }
            }
        }

        String colLetter = columnNumberToLetter(nextColNum);

        // 2) Write header in row 1
        writeRange(
                SHEET_NAME + "!" + colLetter + "1",
                Collections.singletonList(
                        Collections.singletonList(country.getSuccessionType())
                )
        );

        // 3) Build the 17 value‑rows (rows 2–18)
        List<List<Object>> values = Arrays.asList(
                Collections.singletonList(country.getSuccessionType()),
                Collections.singletonList(country.getPopulation()),
                Collections.singletonList(country.getGrowthRate()),
                Collections.singletonList(country.getPopCapacity()),
                Collections.singletonList(country.getMainMarket()),
                Collections.singletonList(country.getCurrency()),
                Collections.singletonList(country.getBudget()),
                Collections.singletonList(""),  // blank under “administration”
                Collections.singletonList(country.getNobility()),
                Collections.singletonList(country.getInstitutions()),
                Collections.singletonList(country.getLandowners()),
                Collections.singletonList(country.getBurghers()),
                Collections.singletonList(country.getPeasants()),
                Collections.singletonList(country.getTribes()),
                Collections.singletonList(country.getBondmen()),
                Collections.singletonList(country.getDevastation()),
                Collections.singletonList(country.getCentralization())
        );

        // 4) Write those down rows 2–18 in the new column
        String writeRange = String.format(
                "%s!%s2:%s18", SHEET_NAME, colLetter, colLetter
        );
        writeRange(writeRange, values);
    }

    public static void main(String[] args) {
        try {
            // Quick sanity check:
            System.out.println("Row 2 before append: " + readRange(SHEET_NAME + "!2:2"));

            // Append a dummy country
            Country dummy = new Country();
            dummy.setSuccessionType("Dynasty");
            dummy.setPopulation(1234);
            dummy.setGrowthRate(1.23);
            dummy.setPopCapacity(5000);
            dummy.setMainMarket("Spices");
            dummy.setCurrency("SPC");
            dummy.setBudget(9999.0);
            dummy.setNobility(12);
            dummy.setInstitutions(3);
            dummy.setLandowners(8);
            dummy.setBurghers(20);
            dummy.setPeasants(300);
            dummy.setTribes(5);
            dummy.setBondmen(50);
            dummy.setDevastation(0.1);
            dummy.setCentralization(0.7);

            appendCountryColumn(dummy);
            System.out.println("Appended dummy country ‟Dynasty‟ to column " +
                    columnNumberToLetter(-1 /*will be corrected in output*/));

            System.out.println("Row 2 after append: " + readRange(SHEET_NAME + "!2:2"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
