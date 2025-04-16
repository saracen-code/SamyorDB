package com.samyorBot.commands.game.company;

import com.samyorBot.database.CompanyDAO;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.AutoCompletable;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class CompCreate extends SlashCommand.Subcommand {

    public CompCreate() {
        setCommandData(new SubcommandData("create", "Create a new company")
                .addOptions(new OptionData(OptionType.STRING, "name", "Name of your new company", true)));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {

        String userId = event.getUser().getId();
        String name = event.getOption("name").getAsString();

        if (CompanyDAO.isUserInCompany(userId)) {
            event.reply("❌ You are already in a company. Leave or dissolve it first.")
                    .setEphemeral(true).queue();
            return;
        }

        if (CompanyDAO.doesCompanyExist(name)) {
            event.reply("❌ A company with that name already exists.")
                    .setEphemeral(true).queue();
            return;
        }

        int companyId = CompanyDAO.createCompany(name, userId);
        if (companyId == -1) {
            event.reply("⚠️ Something went wrong while creating your company.")
                    .setEphemeral(true).queue();
            return;
        }

        CompanyDAO.addUserToCompany(userId, companyId, "owner");

        event.reply("✅ Company **" + name + "** has been successfully created. You are now the owner.")
                .setEphemeral(false).queue();
    }
}
