// com/samyorBot/commands/game/city/SudoAddCity.java

package com.samyorBot.commands.game.city;

import com.samyorBot.database.CityDAO;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class SudoAddCity extends SlashCommand.Subcommand {
    public SudoAddCity() {
        setCommandData(new SubcommandData("add_city", "Add a new city to the world")
                .addOption(OptionType.STRING,  "name",     "City name",     true)
                .addOption(OptionType.BOOLEAN, "coastal",  "Is coastal?",   true)
                .addOption(OptionType.BOOLEAN, "riverine", "Is riverine?",  true)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String name     = event.getOption("name").getAsString();
        boolean coastal = event.getOption("coastal").getAsBoolean();
        boolean riverine= event.getOption("riverine").getAsBoolean();

        event.deferReply(true).queue(hook -> {
            try {
                int id = CityDAO.insertCity(name, coastal, riverine);
                if (id < 0) {
                    hook.sendMessage("❌ Failed to insert city.").queue();
                } else {
                    hook.sendMessage("✅ City **" + name + "** added with ID " + id).queue();
                }
            } catch (Exception e) {
                hook.sendMessage("❌ Database error: " + e.getMessage())
                        .setEphemeral(true)
                        .queue();
            }
        });
    }
}
