package com.samyorBot.commands.game.company;

import com.samyorBot.classes.companies.CompanyManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import com.samyorBot.classes.companies.Company;

public class CompInvite extends SlashCommand.Subcommand{

    public CompInvite() {
        setCommandData(new SubcommandData("invite", "send an invitation to a user to join the company")
                .addOption(OptionType.USER, "user", "User to invite", true));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        // Defer reply to allow time for processing
        event.deferReply(true).queue(); // true = ephemeral

        // Get the target user
        var targetUser = event.getOption("user").getAsUser();
        var inviterId = event.getUser().getId();

        // Load inviter's company from storage (assuming you have a CompanyManager)
        Company company = CompanyManager.getCompanyByMemberId(inviterId);

        if (company == null) {
            event.getHook().sendMessage("❌ You are not in a company.").queue();
            return;
        }

        if (company.isMember(targetUser.getId())) {
            event.getHook().sendMessage("❌ That user is already in your company.").queue();
            return;
        }

        // You could also add a check for whether they already have a pending invite, if you track that

        // Send a DM to the user (optional fallback if blocked)
        targetUser.openPrivateChannel().queue(
                channel -> {
                    channel.sendMessage(String.format("📨 You have been invited to join **%s** by <@%s>. Use `/company accept` to join.",
                            company.getName(), inviterId)).queue();

                    event.getHook().sendMessage("✅ Invitation sent to " + targetUser.getAsMention() + "!").queue();
                },
                failure -> {
                    event.getHook().sendMessage("⚠️ Could not DM the user. They might have DMs disabled.").queue();
                }
        );

        // Optionally, add them to a pending invite list
        CompanyManager.addPendingInvite(company.getId(), targetUser.getId());
    }
}
