package com.samyorBot.commands.game;
import com.samyorBot.commands.game.dynasty.*;
import com.samyorBot.commands.game.dynasty.DynNew;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class Dynasty extends SlashCommand {

    public Dynasty() {
        setCommandData(Commands.slash("dynasty", "company commands"));
        addSubcommands(new DynNew(), new DynList(), new DynDissolve(),
                new DynVisualize());
    }

    // by default we execute help if no subcommand is provided
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.reply("hey").queue();
    }
}
