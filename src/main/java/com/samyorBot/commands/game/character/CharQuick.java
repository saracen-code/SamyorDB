package com.samyorBot.commands.game.character;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyorBot.classes.characters.Character;
import com.samyorBot.classes.characters.RandomCharacterData;
import com.samyorBot.database.AbilityDAO;
import com.samyorBot.database.CharacterDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.awt.Color;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class CharQuick extends SlashCommand.Subcommand {

    private static final RandomCharacterData randomData;
    private static final Random RNG = new Random();

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            randomData = mapper.readValue(
                    new File("src/main/resources/random_characters.json"),
                    RandomCharacterData.class
            );
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load character JSON", e);
        }
    }

    public CharQuick() {
        setCommandData(new SubcommandData("quick", "🎲 Instantly generate a random character."));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long userId = event.getUser().getIdLong();

        // 1) build a fresh Character
        Character character = new Character(userId);
        character.setName(pick(randomData.names));
        character.setCulture(pick(randomData.cultures));
        character.setLocation(pick(randomData.locations));
        character.setAffiliation(pick(randomData.affiliations));
        character.setBackstory(pick(randomData.backstories));
        character.setImage(pick(randomData.images));
        character.setStatistics(generateRandomStats());
        character.setTraits(pickMultiple(randomData.traits, 3));

        // 2) generate a VALID DATE for birth (YYYY-MM-DD)
        int year  = 1700 + RNG.nextInt(50);         // 1700–1749
        int month = RNG.nextInt(12) + 1;            // 1–12
        int day   = RNG.nextInt(28) + 1;            // safe day
        LocalDate birth = LocalDate.of(year, month, day);
        character.setBirthdate(birth.toString());

        // 3) auto-generate death date based on birth
        character.randomizeDeathDate();

        // 4) save to DB (now includes deathdate)
        boolean ok = CharacterDAO.saveCharacter(character);
        if (!ok) {
            event.reply("❌ Something went wrong saving your character. Try again?")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // 5) fetch back to pick up saved deathdate
        Character saved = CharacterDAO.getCharacterById(character.getId());

        // 6) build the embed, now including both dates
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("🎲 Your Randomized Character")
                .setColor(Color.ORANGE)
                .addField("Name",        saved.getName(),       true)
                .addField("Culture",     saved.getCulture(),    true)
                .addField("Location",    saved.getLocation(),   true)
                .addField("Affiliation", saved.getAffiliation(),true)
                .addField("Birthdate",   saved.getBirthdate(),  true)
                .addField("Deathdate",   saved.getDeathdate(),  true)
                .addField("Backstory",   shorten(saved.getBackstory(), 1024), false)
                .addField("Stats",       saved.getStatistics().toString(),  false)
                .addField("Traits",      String.join(", ", saved.getTraits()),   false)
                .addField("Abilities",   String.join(", ", saved.getAbilities()),false)
                .setImage(saved.getImage());

        event.replyEmbeds(eb.build())
                .setEphemeral(true)
                .queue();
    }

    private static String pick(List<String> list) {
        return list.get(RNG.nextInt(list.size()));
    }

    private static List<String> pickMultiple(List<String> list, int count) {
        Collections.shuffle(list, RNG);
        return list.subList(0, Math.min(count, list.size()));
    }

    private static Map<String, Integer> generateRandomStats() {
        var keys = List.of("vit", "int", "str", "aff");
        int total = 12;
        var stats = new HashMap<String,Integer>();
        for (int i = 0; i < keys.size(); i++) {
            int val = (i == keys.size()-1)
                    ? total
                    : RNG.nextInt(total+1);
            stats.put(keys.get(i), val);
            total -= val;
        }
        return stats;
    }

    private static String shorten(String text, int limit) {
        return text.length() > limit
                ? text.substring(0, limit-3) + "..."
                : text;
    }
}
