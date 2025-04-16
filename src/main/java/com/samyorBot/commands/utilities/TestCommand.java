package com.samyorBot.commands.utilities;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.interactions.components.EntitySelectMenuHandler;
import xyz.dynxsty.dih4jda.interactions.components.StringSelectMenuHandler;

import java.util.List;

public class TestCommand extends SlashCommand implements StringSelectMenuHandler {

    public TestCommand() {
        setCommandData(Commands.slash("test", "test description"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("select")
                .setPlaceholder("Choose a culture")
                .addOptions(
                        SelectOption.of("hi", "hi"),
                        SelectOption.of("ho", "ho"),
                        SelectOption.of("oh", "oh")
                ).build();
        event.reply("well received").addActionRow(menu).queue();
    }

    @Override
    public void handleStringSelectMenu(@NotNull StringSelectInteractionEvent e, @NotNull List<String> list) {
        String id = e.getComponentId();
        if (id.equals("select") || id.equals("1")) {
            e.reply("well received, " + e.getValues()).queue();
        }
    }
}