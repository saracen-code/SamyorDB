package com.samyorBot.renders;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynastyTreeUploader {

    public static String generateFamilyTreeUrl(
            Map<Long, List<Long>> tree,
            Map<Long, String> nameMap,
            Map<Long, String> genderMap,
            Map<Long, String> birthMap
    ) throws IOException {

        // 1) Gather every ID we need
        Set<Long> allIds = new HashSet<>();
        allIds.addAll(nameMap.keySet());
        allIds.addAll(genderMap.keySet());
        allIds.addAll(birthMap.keySet());
        tree.keySet().forEach(allIds::add);
        tree.values().forEach(allIds::addAll);

        // 2) Build a child → [parent‑tags] map
        //    We decide father vs. mother by genderMap ("F" → mother, else father)
        Map<Long, List<String>> childParentTags = new HashMap<>();
        for (Map.Entry<Long, List<Long>> e : tree.entrySet()) {
            Long parentId = e.getKey();
            boolean isMother = "F".equalsIgnoreCase(genderMap.getOrDefault(parentId, "M"));
            String tag = isMother ? "\tm" : "\tf";  // m=mother, f=father
            for (Long childId : e.getValue()) {
                childParentTags
                        .computeIfAbsent(childId, k -> new ArrayList<>())
                        .add(tag + parentId);
            }
        }

        // 3) Build the FamilyScript body
        StringBuilder familyScript = new StringBuilder();
        for (Long id : allIds) {
            String rawName = nameMap.getOrDefault(id, "").trim();
            String[] parts = rawName.isEmpty()
                    ? new String[]{"Unknown", ""}
                    : rawName.split("\\s+", 2);

            String given  = parts[0];
            String surname= parts.length > 1 ? parts[1] : "";
            String gender = genderMap.getOrDefault(id, "M").toLowerCase(); // "m" or "f"
            String birth  = birthMap.getOrDefault(id, "");

            familyScript
                    .append("i").append(id)
                    .append("\tp").append(escape(given))    // given name
                    .append("\tl").append(escape(surname))  // surname now
                    .append("\tg").append(gender);         // gender

            if (!birth.isEmpty()) {
                familyScript.append("\tb").append(birth); // birth date YYYYMMDD
            }

            // append any parent tags
            List<String> pTags = childParentTags.get(id);
            if (pTags != null) {
                for (String pt : pTags) {
                    familyScript.append(pt);
                }
            }

            familyScript.append("\n");
        }

        // 4) URL‑encode and POST to FamilyEcho API
        String postData = "format=json"
                + "&operation=temp_view"
                + "&family=" + URLEncoder.encode(familyScript.toString(), StandardCharsets.UTF_8.name())
                + "&hide_header=0"
                + "&show_photo=1"
                + "&show_detail=life_years";

        URL apiUrl = new URL("https://www.familyecho.com/api/");
        HttpsURLConnection conn = (HttpsURLConnection) apiUrl.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }

        // 5) Read the JSON response
        StringBuilder json = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                json.append(line);
            }
        }

        // 6) Extract the URL via regex
        Pattern p = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(json.toString());
        if (m.find()) {
            String rawUrl   = m.group(1);                  // e.g. "https:\/\/www.familyecho.com\/?..."
            String cleanUrl = rawUrl.replace("\\/", "/");  // turn '\/' into '/'
            return cleanUrl;
        } else {
            System.err.println("❌ Failed to parse URL from response: " + json);
            return null;
        }

    }

    // FamilyScript values must escape backslashes, tabs, newlines
    private static String escape(String s) {
        return s
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n");
    }
}
