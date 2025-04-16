package com.samyorBot.commands.utilities;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class HelloWorldCommand extends SlashCommand {

    public HelloWorldCommand() {
        setCommandData(Commands.slash("ping", "Pong!"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent e) {
        e.reply("Pong!").queue();
    }
}
