package com.samyorBot.commands.game.dynasty;

import com.samyorBot.database.DynastyRelationDAO;
import com.samyorBot.renders.DynastyTreeRenderer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class DynVisualize extends SlashCommand.Subcommand {

    public DynVisualize() {
        setCommandData(new SubcommandData("visualize", "Visualize the family tree of a dynasty")
                .addOption(OptionType.INTEGER, "dynasty_id", "ID of the dynasty to visualize", true));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        int dynastyId = event.getOption("dynasty_id").getAsInt();

        Map<Long, List<Long>> tree = DynastyRelationDAO.getRelationsByDynasty(dynastyId);
        if (tree.isEmpty()) {
            event.reply("❌ Dynasty has no family relations yet.").setEphemeral(true).queue();
            return;
        }

        // Find root nodes (characters with no parent)
        Set<Long> allChildren = tree.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        Set<Long> roots = new HashSet<>(tree.keySet());
        roots.removeAll(allChildren);

        try {
            File image = DynastyTreeRenderer.renderTreeAsImage(tree, roots);
            event.reply("🌳 Dynasty Tree:").addFiles(FileUpload.fromData(image)).queue();
        } catch (Exception e) {
            e.printStackTrace();
            event.reply("❌ Failed to generate tree image.").setEphemeral(true).queue();
        }
    }
}
