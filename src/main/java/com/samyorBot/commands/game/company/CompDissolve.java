package com.samyorBot.commands.game.company;

import com.samyorBot.classes.companies.Company;
import com.samyorBot.database.CompanyDAO;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class CompDissolve extends SlashCommand.Subcommand {

    public CompDissolve() {
        setCommandData(new SubcommandData("dissolve", "dissolves the company and all of its members and redistributes funds equallty"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();

        Company company = CompanyDAO.getCompanyByMember(userId);

        if (company == null || !company.getOwnerId().equals(userId)) {
            event.reply("❌ You are not the owner of any company.").setEphemeral(true).queue();
            return;
        }

        boolean success = CompanyDAO.deleteCompanyIfOwner(userId);

        if (success) {
            event.reply("☠️ Company **" + company.getName() + "** has been dissolved and all members removed.")
                    .setEphemeral(false).queue();
        } else {
            event.reply("⚠️ Failed to dissolve the company. Please try again later.")
                    .setEphemeral(true).queue();
        }
    }
}
