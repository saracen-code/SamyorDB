package com.samyorBot.commands.utilities;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class PingCommand extends SlashCommand {

    public PingCommand() { // OPTIONAL: a default can be set using DIH4JDA#setDefaultRegistrationType
        setCommandData(Commands.slash("ping", "Pong!"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }
}