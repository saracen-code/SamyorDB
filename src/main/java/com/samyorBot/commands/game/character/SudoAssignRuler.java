package com.samyorBot.commands.game.character;

import com.samyorBot.classes.characters.Ruler;
import com.samyorBot.database.RulerDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class SudoAssignRuler extends SlashCommand.Subcommand {
    public SudoAssignRuler() {
        setCommandData(new SubcommandData("assign_ruler", "Assign King to a user")
                .addOption(OptionType.USER,   "user",    "Discord user to make King", true)
                .addOption(OptionType.STRING, "country", "Country name",          true)
        );
        // Optionally restrict to mods in your command registrar
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        // permission check
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ You don't have permission to assign rulers.")
                    .setEphemeral(true).queue();
            return;
        }

        var target = event.getOption("user").getAsUser();
        long targetId = target.getIdLong();
        String country = event.getOption("country").getAsString();

        // Roll 1–10 for each stat
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int military    = rnd.nextInt(1, 11);
        int stewardship = rnd.nextInt(1, 11);
        int diplomacy   = rnd.nextInt(1, 11);
        int wisdom      = rnd.nextInt(1, 11);

        Ruler r = new Ruler(targetId, country, military, stewardship, diplomacy, wisdom);
        boolean ok = RulerDAO.saveRuler(r);

        if (!ok) {
            event.reply("❌ Failed to assign ruler.").setEphemeral(true).queue();
            return;
        }

        // Optional: give them a “King” Discord role
        // var role = event.getGuild().getRolesByName("King", true).get(0);
        // event.getGuild().addRoleToMember(targetId, role).queue();

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("👑 King Assigned")
                .setDescription(target.getAsMention() + " is now King of **" + country + "**")
                .setColor(Color.YELLOW)
                .addField("Military",    String.valueOf(military),    true)
                .addField("Stewardship", String.valueOf(stewardship), true)
                .addField("Diplomacy",   String.valueOf(diplomacy),   true)
                .addField("Wisdom",      String.valueOf(wisdom),      true);

        event.replyEmbeds(eb.build()).queue();
    }
}
