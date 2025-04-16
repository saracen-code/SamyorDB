package com.samyorBot.commands.game.character;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyorBot.classes.characters.Ability;
import com.samyorBot.classes.characters.Character;
import com.samyorBot.classes.characters.RandomCharacterData;
import com.samyorBot.database.CharacterDAO;
import com.samyorBot.database.AbilityDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class CharQuick extends SlashCommand.Subcommand {

    private static final RandomCharacterData randomData;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            randomData = mapper.readValue(new File("src/main/resources/random_characters.json"), RandomCharacterData.class);
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

        Character character = new Character(userId);
        character.setName(pick(randomData.names));
        character.setCulture(pick(randomData.cultures));
        character.setLocation(pick(randomData.locations));
        character.setAffiliation(pick(randomData.affiliations));
        character.setBirthdate(String.valueOf(1700 + new Random().nextInt(300)));
        character.setBackstory(pick(randomData.backstories));
        character.setImage(pick(randomData.images));
        character.setStatistics(generateRandomStats());
        character.setTraits(pickMultiple(randomData.traits, 3));

        // Get 4 random approved abilities from DB
        List<Ability> randomAbilities = AbilityDAO.getRandomApprovedAbilities(4);
        character.setAbilities(randomAbilities.stream().map(Ability::getName).toList());

        CharacterDAO.saveCharacter(character);

        // Build embed
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("🎲 Your Randomized Character")
                .addField("Name", character.getName(), true)
                .addField("Culture", character.getCulture(), true)
                .addField("Location", character.getLocation(), true)
                .addField("Affiliation", character.getAffiliation(), true)
                .addField("Backstory", shorten(character.getBackstory(), 1024), false)
                .addField("Stats", character.getStatistics().toString(), false)
                .addField("Traits", String.join(", ", character.getTraits()), false)
                .addField("Abilities", String.join(", ", character.getAbilities()), false)
                .setImage(character.getImage())
                .setColor(Color.ORANGE);

        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    private static String pick(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    private static List<String> pickMultiple(List<String> list, int count) {
        Collections.shuffle(list);
        return list.subList(0, Math.min(count, list.size()));
    }

    private static Map<String, Integer> generateRandomStats() {
        List<String> keys = List.of("vit", "int", "str", "aff");
        int total = 12;
        Map<String, Integer> stats = new HashMap<>();

        for (int i = 0; i < keys.size(); i++) {
            int value = (i == keys.size() - 1) ? total : new Random().nextInt(total + 1);
            stats.put(keys.get(i), value);
            total -= value;
        }

        return stats;
    }

    private static String shorten(String text, int limit) {
        return text.length() > limit ? text.substring(0, limit - 3) + "..." : text;
    }
}

