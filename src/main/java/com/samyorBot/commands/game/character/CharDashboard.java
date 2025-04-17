package com.samyorBot.commands.game.character;

import com.samyorBot.classes.characters.Character;
import com.samyorBot.database.CharacterDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.interactions.components.ButtonHandler;
import xyz.dynxsty.dih4jda.interactions.components.StringSelectMenuHandler;

import java.util.List;
import java.util.stream.Collectors;

public class CharDashboard extends SlashCommand.Subcommand implements StringSelectMenuHandler {
    private static final String SELECT_ID = "char_dashboard_select";

    public CharDashboard() {
        setCommandData(new SubcommandData("dashboard", "View a character dashboard"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long userId = event.getUser().getIdLong();
        List<Character> chars = CharacterDAO.getCharactersByUserId(userId);

        if (chars.isEmpty()) {
            event.reply("You don’t have any characters yet. Create one with `/char create`.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        var options = chars.stream()
                .map(c -> SelectOption.of(c.getName() + " (ID " + c.getId() + ")", String.valueOf(c.getId())))
                .collect(Collectors.toList());

        var menu = StringSelectMenu.create(SELECT_ID)
                .setPlaceholder("Select a character…")
                .addOptions(options)
                .setMinValues(1)
                .setMaxValues(1)
                .build();

        event.reply("Choose which character to view:")
                .addActionRow(menu)
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void handleStringSelectMenu(@NotNull StringSelectInteractionEvent event, @NotNull List<String> list) {
        if (!event.getSelectMenu().getId().equals(SELECT_ID))
            return;

        long charId = Long.parseLong(event.getValues().get(0));
        Character c = CharacterDAO.getCharacterById(charId);
        if (c == null) {
            event.reply("❌ Character not found.").setEphemeral(true).queue();
            return;
        }

        // Use toString() to auto‐generate the body
        String raw = c.toString();
        // strip leading "Character{" and trailing "}"
        String body = raw
                .replaceFirst("^Character\\{", "")
                .replaceFirst("\\}$", "");

        // turn each "key=value" into a bullet point
        String desc = List.of(body.split(", "))
                .stream()
                .map(pair -> "• " + pair)
                .collect(Collectors.joining("\n"));

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(c.getName())
                .setThumbnail(c.getImage())   // or getImageUrl()
                .setDescription(desc)
                .setFooter("Use /char edit to modify this character");

        event.replyEmbeds(eb.build())
                .setEphemeral(true)
                .queue();
    }
}
