package com.samyorBot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyorBot.classes.traits.TraitRegistry;
import com.samyorBot.commands.game.character.CharSetup;
import com.samyorBot.commands.game.country.SudoCountrySetup;
import com.samyorBot.commands.game.dynasty.DynDissolve;
import com.samyorBot.commands.utilities.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDABuilder;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.components.IdMapping;
import xyz.dynxsty.examples.listeners.DIH4JDAListener;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.Temporal;

public class Main {
    public static Temporal startTime = Instant.now();

    public static void main(String[] args) throws DIH4JDAException {


        TraitRegistry.init("src/main/resources/traits.json");

        // Load bot token and webhook URL from config.json
        String token = getBotConfig("config.json", "token");

        if (token == null) {
            System.err.println("[INIT] Failed to load bot configuration. Exiting...");
            return;
        }

        System.out.println("[INIT] Bot token and Webhook URL loaded successfully!");

        // Connect to Discord and set up the bot
        JDA jda = null;
        try {
            System.out.println("[INIT] Connecting to Discord...");
            jda = JDABuilder.createLight(token).build();

            DIH4JDA.setDefaultRegistrationType(RegistrationType.GLOBAL);
            DIH4JDA dih4JDA = DIH4JDABuilder
                    .setJDA(jda)
                    .setCommandPackages("com.samyorBot.commands")
                    .build();

            dih4JDA.addEventListener(new DIH4JDAListener());

            dih4JDA.addButtonMappings(
                    IdMapping.of(new CharSetup(), "prev", "next", "select_culture",
                            "edit_details", "cities_list",
                            "setup_name", "random_name",
                            "set_traits", "setup_sp",
                            "setup_url", "setup_abilities",
                            "view_current_abilities", "choose_existing_ability",
                            "confirm_char", "create_new_char", "select_existing_char"),
                    IdMapping.of(new DynDissolve(), "confirm_dissolve", "cancel_dissolve"),
                    IdMapping.of(new SudoCountrySetup(), "prev_country", "next_country", "setup_succession",
                            "setup_population", "setup_growth", "setup_capacity", "setup_market", "setup_currency",
                            "setup_budget", "setup_devastation", "setup_centralization", "confirm_country",
                            "create_new_country", "select_existing_country")
            );

            dih4JDA.addStringSelectMenuMappings(
                    IdMapping.of(new TestCommand(), "select"),
                    IdMapping.of(new CharSetup(), "select_culture", "setup_trait", "existing_char_select",
                            "select_existing_ability"),
                    IdMapping.of(new SudoCountrySetup(), "select_existing_country")
            );

            dih4JDA.addModalMappings(
                    IdMapping.of(new CharSetup(), "setup_name", "setup_details", "setup_skillpoints",
                            "setup_abilities", "setup_url"),
                    IdMapping.of(new SudoCountrySetup(), "modal_succession",
                            "modal_population", "modal_growth", "modal_capacity", "modal_market", "modal_currency",
                            "modal_budget", "modal_devastation", "modal_centralization")
            );

        } catch (Exception e) {
            System.err.println("[ERROR] Error while starting the bot:");
            e.printStackTrace();
        }
    }

    // Function to read any config value from config.json
    public static String getBotConfig(String filePath, String configKey) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            return jsonNode.get(configKey).asText();
        } catch (IOException e) {
            System.err.println("[ERROR] Error reading " + configKey + " from config.json:");
            e.printStackTrace();
            return null;
        }
    }
}
