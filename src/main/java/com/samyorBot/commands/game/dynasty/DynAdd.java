package com.samyorBot.commands.game.dynasty;

import com.samyorBot.database.DynastyDAO;
import com.samyorBot.database.DynastyRelationDAO;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class DynAdd extends SlashCommand.Subcommand {

    public DynAdd() {
        setCommandData(new SubcommandData("add", "Dissolve a dynasty you created")
                .addOption(OptionType.INTEGER, "character_id", "ID of the dynasty to dissolve", true)
                .addOption(OptionType.INTEGER, "parent_id", "ID of the dynasty to dissolve", true));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        int childId = event.getOption("character_id").getAsInt();
        int parentId = event.getOption("parent_id").getAsInt();

        int dynastyId = DynastyDAO.getDynastyIdByCharacter(parentId);
        if (dynastyId == -1) {
            event.reply("❌ The parent is not in any dynasty.").setEphemeral(true).queue();
            return;
        }

        boolean added = DynastyDAO.addCharacterToDynasty(dynastyId, childId);
        if (!added) {
            event.reply("❌ Failed to add the character to the dynasty.").setEphemeral(true).queue();
            return;
        }

        DynastyRelationDAO.addRelation(parentId, childId);

        event.reply("✅ Character `" + childId + "` has been added to the dynasty under parent `" + parentId + "`.").setEphemeral(true).queue();
    }

}
