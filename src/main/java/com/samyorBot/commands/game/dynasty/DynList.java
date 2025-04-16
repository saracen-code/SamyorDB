package com.samyorBot.commands.game.dynasty;

import com.samyorBot.database.DynastyDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.awt.*;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class DynList extends SlashCommand.Subcommand {

    public DynList() {
        setCommandData(new SubcommandData("list", "View all dynasties you've created"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long userId = event.getUser().getIdLong();
        Map<Integer, String> dynastyMap = DynastyDAO.getDynastyIdNameMapByCreator(userId);

        if (dynastyMap.isEmpty()) {
            event.reply("❌ You haven’t created any dynasties yet.")
                    .setEphemeral(true).queue();
            return;
        }

        StringBuilder desc = new StringBuilder();
        for (Map.Entry<Integer, String> entry : dynastyMap.entrySet()) {
            int dynastyId = entry.getKey();
            String name = entry.getValue();
            List<Long> members = DynastyDAO.getAllCharacterIdsInDynasty(dynastyId);

            desc.append("🛡️ **").append(name).append("** (`ID ").append(dynastyId).append("`)\n");
            desc.append("👥 Members: ")
                    .append(members.size() == 0 ? "_none_" : members.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")))
                    .append("\n\n");
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("📜 Your Dynasties")
                .setDescription(desc.toString())
                .setColor(Color.YELLOW);

        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }


}
