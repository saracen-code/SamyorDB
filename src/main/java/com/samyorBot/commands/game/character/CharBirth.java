package com.samyorBot.commands.game.character;

import com.samyorBot.database.CharacterDAO;
import com.samyorBot.classes.characters.Character;
import com.samyorBot.database.DynastyDAO;
import com.samyorBot.database.DynastyRelationDAO;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class CharBirth extends SlashCommand.Subcommand {

    public CharBirth() {
        setCommandData(new SubcommandData("birth", "create a new character within a dynasty")
                .addOption(OptionType.STRING, "parent_id", "Type the ID of the parent character"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long parentId = event.getOption("parent_id").getAsLong();
        int dynastyId = DynastyDAO.getDynastyIdByCharacter(parentId);

        if (dynastyId == -1) {
            event.reply("❌ The parent is not in any dynasty.").setEphemeral(true).queue();
            return;
        }

        // Create the child character
        long userId = event.getUser().getIdLong();
        Character child = new Character(userId);
        child.setName("Unnamed Child");
        CharacterDAO.saveCharacter(child);

        // Link into dynasty
        DynastyDAO.addCharacterToDynasty(dynastyId, child.getId());
        DynastyRelationDAO.addRelation((int) parentId, (int) child.getId().longValue());

        event.reply("✅ A new character has been born into dynasty `" + dynastyId + "`! ID: `" + child.getId() + "`"
                + "\n\n Please set it up using /char setup.")
                .setEphemeral(true).queue();
    }
}
