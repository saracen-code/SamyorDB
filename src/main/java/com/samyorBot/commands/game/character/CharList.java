package com.samyorBot.commands.game.character;

import com.samyorBot.classes.characters.Character;
import com.samyorBot.database.CharacterDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import com.samyorBot.classes.characters.*;

import java.awt.*;
import java.util.List;

public class CharList extends SlashCommand.Subcommand {

    public CharList() {
        setCommandData(new SubcommandData("list", "lists all characters that one has"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long userId = event.getUser().getIdLong();

        List<Character> characters = CharacterDAO.getCharactersByUserId(userId);
        if (characters.isEmpty()) {
            event.reply("❌ You don’t have any characters yet. Use `/char create` to start!").setEphemeral(true).queue();
            return;
        }

        StringBuilder desc = new StringBuilder();
        for (Character c : characters) {
            desc.append("• `ID ").append(c.getId()).append("`: ").append(c.getName()).append("\n");
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("📘 Your Characters")
                .setDescription(desc.toString())
                .setColor(Color.CYAN);

        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

}
