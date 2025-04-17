package com.samyorBot.commands.game.dynasty;

import com.samyorBot.classes.characters.Character;
import com.samyorBot.database.CharacterDAO;
import com.samyorBot.database.DynastyRelationDAO;
import com.samyorBot.renders.DynastyTreeUploader;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.util.*;

public class DynVisualize extends SlashCommand.Subcommand {

    public DynVisualize() {
        setCommandData(new SubcommandData("visualize", "Visualize the family tree of a dynasty")
                .addOption(OptionType.INTEGER, "dynasty_id", "ID of the dynasty to visualize", true));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        // 0) ACKNOWLEDGE IMMEDIATELY so Discord knows we're on it
        event.deferReply().queue();

        int dynastyId = event.getOption("dynasty_id").getAsInt();

        // 1) Load your tree
        Map<Long, List<Long>> tree = DynastyRelationDAO.getRelationsByDynasty(dynastyId);
        if (tree.isEmpty()) {
            // use the hook after a defer
            event.getHook()
                    .sendMessage("❌ This dynasty has no family relations yet.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // 2) Build all of your ID sets and maps
        Set<Long> allIds = new HashSet<>(tree.keySet());
        tree.values().forEach(allIds::addAll);

        Map<Long, String> nameMap   = new HashMap<>();
        Map<Long, String> genderMap = new HashMap<>();
        Map<Long, String> birthMap  = new HashMap<>();

        for (Long id : allIds) {
            Character c = CharacterDAO.getCharacterById(id);
            if (c != null) {
                nameMap.put(id,
                        c.getName() != null ? c.getName() : "Character " + id);
                genderMap.put(id, "M");  // or pull from your DB
                birthMap.put(id,
                        c.getBirthdate() != null ? c.getBirthdate() : "");
            }
        }

        // 3) Now do the heavy lifting / HTTP call
        try {
            String url = DynastyTreeUploader.generateFamilyTreeUrl(
                    tree, nameMap, genderMap, birthMap
            );

            if (url != null) {
                event.getHook()
                        .sendMessage("🌳 Here’s your family tree:\n" + url)
                        .queue();
            } else {
                event.getHook()
                        .sendMessage("❌ Failed to generate the family tree URL.")
                        .queue();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            event.getHook()
                    .sendMessage("❌ An error occurred while generating the family tree.")
                    .queue();
        }
    }

}
