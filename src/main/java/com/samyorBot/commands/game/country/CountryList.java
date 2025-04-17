package com.samyorBot.commands.game.country;

import com.samyorBot.sheets.SheetsHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.awt.Color;
import java.util.List;

public class CountryList extends SlashCommand.Subcommand {
    private static final String SHEET_NAME = "SafeTest";

    public CountryList() {
        setCommandData(new SubcommandData("list", "List all countries"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            // Read row 2 across: A2 is the key label, B2.. are your countries
            List<List<Object>> row2 = SheetsHelper.readRange(SHEET_NAME + "!2:2");

            if (row2.isEmpty() || row2.get(0).size() <= 1) {
                event.reply("❌ No countries found.").setEphemeral(true).queue();
                return;
            }

            List<Object> cols = row2.get(0);
            StringBuilder list = new StringBuilder();
            for (int i = 1; i < cols.size(); i++) {
                list.append("**").append(i).append(".** ")
                        .append(cols.get(i).toString())
                        .append("\n");
            }

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("🌐 Countries")
                    .setDescription(list.toString())
                    .setColor(Color.CYAN);

            event.replyEmbeds(eb.build()).queue();

        } catch (Exception e) {
            event.reply("❌ Failed to fetch countries: " + e.getMessage())
                    .setEphemeral(true)
                    .queue();
            e.printStackTrace();
        }
    }
}
