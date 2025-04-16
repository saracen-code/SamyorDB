package com.samyorBot.commands.game.company;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class CompManage extends SlashCommand.Subcommand {

    public CompManage() {
        setCommandData(new SubcommandData("manage", "opens a dashboard for managing company affairs"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.reply("hello").queue();

    }
}
