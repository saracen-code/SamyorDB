package com.samyorBot.commands.game;

import com.samyorBot.commands.game.company.CompCreate;
import com.samyorBot.commands.game.company.CompDissolve;
import com.samyorBot.commands.game.company.CompInvite;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import com.samyorBot.commands.game.company.CompManage;

public class Company extends SlashCommand {

    public Company() {
        setCommandData(Commands.slash("company", "company commands"));
        addSubcommands(new CompCreate(), new CompDissolve());
    }

    // by default we execute help if no subcommand is provided
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.reply("hey").queue();
    }
}
