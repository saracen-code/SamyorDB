package com.samyorBot.commands.game.dynasty;

import com.samyorBot.database.DynastyDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.interactions.components.ButtonHandler;

import java.awt.*;
import java.util.List;

public class DynDissolve extends SlashCommand.Subcommand implements ButtonHandler {

    public DynDissolve() {
        setCommandData(new SubcommandData("dissolve", "Dissolve a dynasty you created")
                .addOption(OptionType.INTEGER, "id", "ID of the dynasty to dissolve", true));

    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long userId = event.getUser().getIdLong();
        int dynastyId = event.getOption("id").getAsInt();

        // Confirm ownership
        List<Integer> userDynasties = DynastyDAO.getDynastiesByCreator(userId);
        if (!userDynasties.contains(dynastyId)) {
            event.reply("❌ You don’t own a dynasty with ID `" + dynastyId + "`.")
                    .setEphemeral(true).queue();
            return;
        }

        EmbedBuilder warn = new EmbedBuilder()
                .setTitle("⚠️ Confirm Dynasty Dissolution")
                .setDescription("Are you **really** sure you want to dissolve dynasty `ID " + dynastyId + "`?\n\n" +
                        "This will also **remove all members** from this family tree permanently.")
                .setColor(Color.RED);

        event.replyEmbeds(warn.build())
                .addActionRow(
                        Button.danger("confirm_dissolve:" + dynastyId, "✅ Yes, dissolve it"),
                        Button.secondary("cancel_dissolve", "❌ Cancel")
                )
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void handleButton(@NotNull ButtonInteractionEvent event, @NotNull Button button) {
        String id = button.getId();
        long userId = event.getUser().getIdLong();

        if (id.startsWith("confirm_dissolve:")) {
            int dynastyId = Integer.parseInt(id.split(":")[1]);

            boolean deleted = DynastyDAO.deleteDynastyById(dynastyId, userId);
            if (deleted) {
                event.editMessage("✅ Dynasty `" + dynastyId + "` has been dissolved.").setEmbeds().setComponents().queue();
            } else {
                event.editMessage("❌ Failed to dissolve dynasty.").setEmbeds().setComponents().queue();
            }
            return;
        }

        if (id.equals("cancel_dissolve")) {
            event.editMessage("❎ Action cancelled.").setEmbeds().setComponents().queue();
            return;
        }
    }

}
