package com.samyorBot.commands.game.character;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class CharHelp extends SlashCommand.Subcommand {

    public CharHelp() {
        setCommandData(new SubcommandData("help", "opens a guide on how to use character commands"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("To be implemented").queue();
    }
}