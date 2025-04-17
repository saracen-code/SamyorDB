package com.samyorBot.commands.game;

import com.samyorBot.commands.game.character.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class Character extends SlashCommand {

    public Character() {
        setCommandData(Commands.slash("character", "character commands"));
        addSubcommands(new CharHelp(), new CharSetup(), new CharQuick(), new CharBirth(), new CharList(), new CharDashboard());
    }

    // by default we execute help if no subcommand is provided
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        new CharHelp().execute(event);
    }
}
