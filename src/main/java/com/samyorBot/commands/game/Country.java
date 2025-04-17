package com.samyorBot.commands.game;

import com.samyorBot.commands.game.country.CountryList;
import com.samyorBot.commands.game.country.SudoCountrySetup;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class Country extends SlashCommand {

    public Country() {
        setCommandData(Commands.slash("country", "country commands"));
        addSubcommands(new CountryList());
    }

    // by default we execute help if no subcommand is provided
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.reply("hey").queue();
    }
}
