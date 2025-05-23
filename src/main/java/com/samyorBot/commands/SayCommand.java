package com.samyorBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SayCommand extends SlashCommand {

    public SayCommand() {
        setCommandData(Commands.slash("say", "Sends a message.")
                .addOption(OptionType.STRING, "str", "What you want to send as a message.", true));
        setRequiredPermissions(Permission.ADMINISTRATOR); // Only administrators can execute this command.
    }

    @Override
    public void execute(@Nonnull SlashCommandInteractionEvent event) {
        // Sends the message you specified in the 'str' option
        event.reply(Objects.requireNonNull(event.getOption("str", OptionMapping::getAsString))).setEphemeral(false).queue();
    }
}