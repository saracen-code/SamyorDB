package com.samyorBot.commands.game.dynasty;

import com.samyorBot.classes.characters.Character;
import com.samyorBot.database.CharacterDAO;
import com.samyorBot.database.CompanyDAO;
import com.samyorBot.database.DynastyDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.AutoCompletable;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.awt.*;
import java.util.List;

public class DynNew extends SlashCommand.Subcommand {

    public DynNew() {
        setCommandData(new SubcommandData("new", "Create a new dynasty")
                .addOptions(
                        new OptionData(OptionType.STRING, "name", "Name of your new dynasty", true),
                        new OptionData(OptionType.INTEGER, "founder", "ID of the founding character", true)
                ));
    }


    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long userId = event.getUser().getIdLong();
        String name = event.getOption("name").getAsString().trim();
        int founderId = event.getOption("founder").getAsInt();

        if (name.length() < 3 || name.length() > 50) {
            event.reply("❌ Dynasty name must be between 3 and 50 characters.").setEphemeral(true).queue();
            return;
        }

        // Check if founder character exists and belongs to the user
        Character founder = CharacterDAO.getCharacterById(founderId);
        if (founder == null) {
            event.reply("❌ No character found with ID `" + founderId + "`.").setEphemeral(true).queue();
            return;
        }

        if (founder.getUserId() != userId) {
            event.reply("❌ You do not own character `" + founderId + "`.").setEphemeral(true).queue();
            return;
        }

        // Create the dynasty and get its ID
        int dynastyId = DynastyDAO.createDynastyAndReturnId(name, userId);
        if (dynastyId == -1) {
            event.reply("❌ Failed to create dynasty. Try again later.").setEphemeral(true).queue();
            return;
        }

        DynastyDAO.addCharacterToDynasty(dynastyId, founderId);
        DynastyDAO.DynastyRelationDAO.addRoot(founderId);


        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("🏰 Dynasty Created")
                .setDescription("✅ Dynasty **" + name + "** has been created.\n\n" +
                        "👑 Founder: `" + founder.getName() + "` (ID `" + founderId + "`)")
                .setColor(Color.BLUE);

        event.replyEmbeds(eb.build()).setEphemeral(true).queue();

    }

}
