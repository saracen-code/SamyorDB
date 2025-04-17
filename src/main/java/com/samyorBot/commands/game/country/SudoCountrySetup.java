package com.samyorBot.commands.game.country;

import com.samyorBot.classes.Country;
import com.samyorBot.sheets.SheetsHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.interactions.components.ButtonHandler;
import xyz.dynxsty.dih4jda.interactions.components.ModalHandler;
import xyz.dynxsty.dih4jda.interactions.components.StringSelectMenuHandler;

import java.awt.Color;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class SudoCountrySetup extends SlashCommand.Subcommand
        implements ButtonHandler, ModalHandler, StringSelectMenuHandler
{
    private static final String SHEET = "SafeTest";
    private final List<EmbedBuilder> pages = new ArrayList<>();
    private static final Map<Long,Integer> userPages         = new HashMap<>();
    private static final Map<Long,Country> userInProgress    = new HashMap<>();
    private static final Map<Long,Integer> userSelectedCol   = new HashMap<>();

    public SudoCountrySetup() {
        setCommandData(new SubcommandData("country_setup", "Setup or edit a country record"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long uid = event.getUser().getIdLong();
        userPages.put(uid, 0);
        userInProgress.put(uid, new Country());
        userSelectedCol.put(uid, -1);

        // Page 0: Create or Edit
        pages.clear();
        pages.add(new EmbedBuilder()
                .setTitle("Country Setup")
                .setDescription("🔹 Create a New Country\n🔹 Edit an Existing Country")
                .setColor(Color.MAGENTA)
        );

        event.deferReply(true).queue(hook -> {
            hook.editOriginalEmbeds(pages.get(0).build())
                    .setActionRow(
                            Button.success("create_new_country", "🆕 Create New"),
                            Button.secondary("select_existing_country", "📂 Edit Existing")
                    )
                    .queue();
        });
    }

    @Override
    public void handleButton(@NotNull ButtonInteractionEvent event, @NotNull Button button) {
        long uid = event.getUser().getIdLong();
        String id = button.getId();

        // ─── Modal‑opening branches ────────────────────────────────────────────
        switch (id) {
            case "setup_succession":
                event.replyModal(Modal.create("modal_succession", "Succession Type")
                                .addActionRow(
                                        TextInput.create("successionType", "Succession Type", TextInputStyle.SHORT)
                                                .setRequired(true).build()
                                ).build())
                        .queue();
                return;
            case "setup_population":
                event.replyModal(Modal.create("modal_population", "Population")
                                .addActionRow(
                                        TextInput.create("population", "Population", TextInputStyle.SHORT)
                                                .setRequired(true).build()
                                ).build())
                        .queue();
                return;
            case "setup_growth":
                event.replyModal(Modal.create("modal_growth", "Growth Rate")
                                .addActionRow(
                                        TextInput.create("growthRate", "Growth Rate", TextInputStyle.SHORT)
                                                .setRequired(true).build()
                                ).build())
                        .queue();
                return;
            case "setup_capacity":
                event.replyModal(Modal.create("modal_capacity", "Population Capacity")
                                .addActionRow(
                                        TextInput.create("popCapacity", "Capacity", TextInputStyle.SHORT)
                                                .setRequired(true).build()
                                ).build())
                        .queue();
                return;
            case "setup_market":
                event.replyModal(Modal.create("modal_market", "Main Market")
                                .addActionRow(
                                        TextInput.create("mainMarket", "Main Market", TextInputStyle.SHORT)
                                                .setRequired(true).build()
                                ).build())
                        .queue();
                return;
            case "setup_currency":
                event.replyModal(Modal.create("modal_currency", "Currency")
                                .addActionRow(
                                        TextInput.create("currency", "Currency", TextInputStyle.SHORT)
                                                .setRequired(true).build()
                                ).build())
                        .queue();
                return;
            case "setup_budget":
                event.replyModal(Modal.create("modal_budget", "Budget")
                                .addActionRow(
                                        TextInput.create("budget", "Budget", TextInputStyle.SHORT)
                                                .setRequired(true).build()
                                ).build())
                        .queue();
                return;
            case "setup_devastation":
                event.replyModal(Modal.create("modal_devastation", "Devastation Level")
                                .addActionRow(
                                        TextInput.create("devastation", "Devastation", TextInputStyle.SHORT)
                                                .setRequired(true).build()
                                ).build())
                        .queue();
                return;
            case "setup_centralization":
                event.replyModal(Modal.create("modal_centralization", "Centralization")
                                .addActionRow(
                                        TextInput.create("centralization", "Centralization", TextInputStyle.SHORT)
                                                .setRequired(true).build()
                                ).build())
                        .queue();
                return;
            case "confirm_country":
                event.deferReply().queue();
                Country country = userInProgress.remove(uid);
                int colIndex = userSelectedCol.get(uid);
                try {
                    if (colIndex < 0) {
                        SheetsHelper.appendCountryColumn(country);
                    } else {
                        // overwrite existing column: write header + values
                        String col = SheetsHelper.columnNumberToLetter(colIndex+1);
                        // header
                        SheetsHelper.writeRange(
                                SHEET + "!" + col + "1",
                                List.of(List.of(country.getSuccessionType()))
                        );
                        // values rows 2–18
                        SheetsHelper.writeRange(
                                SHEET + "!" + col + "2:" + col + "18",
                                List.of(
                                        List.of(country.getSuccessionType()),
                                        List.of(country.getPopulation()),
                                        List.of(country.getGrowthRate()),
                                        List.of(country.getPopCapacity()),
                                        List.of(country.getMainMarket()),
                                        List.of(country.getCurrency()),
                                        List.of(country.getBudget()),
                                        List.of(""),
                                        List.of(country.getNobility()),
                                        List.of(country.getInstitutions()),
                                        List.of(country.getLandowners()),
                                        List.of(country.getBurghers()),
                                        List.of(country.getPeasants()),
                                        List.of(country.getTribes()),
                                        List.of(country.getBondmen()),
                                        List.of(country.getDevastation()),
                                        List.of(country.getCentralization())
                                )
                        );
                    }
                    event.getHook().editOriginalEmbeds(
                                    new EmbedBuilder()
                                            .setTitle("✅ Country Saved")
                                            .setColor(Color.GREEN)
                                            .build()
                            )
                            .setComponents(Collections.emptyList())
                            .queue();
                } catch (Exception ex) {
                    event.getHook()
                            .editOriginal("❌ Save failed: " + ex.getMessage())
                            .queue();

                }
                return;
        }


        event.deferEdit().queue();
        int current = userPages.getOrDefault(uid, 0);

        if (id.equals("create_new_country")) {
            userSelectedCol.put(uid, -1);
            userPages.put(uid, 0);
            loadPages(uid);
            event.getHook().editOriginalEmbeds(
                            pages.get(0).setFooter("Page 1 / " + pages.size()).build()
                    )
                    .setActionRow(getButtonForPage(0))
                    .queue();
            return;
        }

        if ("select_existing_country".equals(id)) {
            // build selection menu from row 2
            List<List<Object>> row2;
            try {
                row2 = SheetsHelper.readRange(SHEET + "!2:2");
            } catch (Exception e) {
                event.reply("❌ Error reading sheet").setEphemeral(true).queue();
                return;
            }
            List<Object> cols = row2.isEmpty() ? List.of() : row2.get(0);
            if (cols.size() <= 1) {
                event.reply("❌ No countries to edit.").setEphemeral(true).queue();
                return;
            }
            StringSelectMenu.Builder menu = StringSelectMenu.create("select_existing_country")
                    .setPlaceholder("Select country to edit");
            for (int i = 1; i < cols.size(); i++) {
                menu.addOption((i)+". "+cols.get(i), String.valueOf(i));
            }
            event.deferEdit().queue(hook -> {
                hook.editOriginalEmbeds(new EmbedBuilder()
                                .setTitle("Select Country")
                                .setDescription("Choose which country to modify")
                                .build())
                        .setActionRow(menu.build())
                        .queue();
            });
            return;
        }

        // pagination (0→1→2→…)
        if (id.equals("prev_country") && current > 0) {
            current--;
        } else if (id.equals("next_country") && current < pages.size() - 1) {
            current++;
        }
        userPages.put(uid, current);

        // ─── Default: re‑render the current page ───────────────────────────────
        EmbedBuilder eb = pages.get(current)
                .setFooter("Page " + (current + 1) + " / " + pages.size());
        event.getHook().editOriginalEmbeds(eb.build())
                .setActionRow(getButtonForPage(current))
                .queue();
    }

    @Override
    public void handleStringSelectMenu(@NotNull StringSelectInteractionEvent event, @NotNull List<String> values) {
        long uid = event.getUser().getIdLong();
        int idx = Integer.parseInt(values.get(0));
        userSelectedCol.put(uid, idx);
        userPages.put(uid, 1);
        loadPages(uid);

        event.deferEdit().queue(hook -> {
            hook.editOriginalEmbeds(pages.get(1)
                            .setFooter("Page 2 / " + pages.size()).build())
                    .setActionRow(getButtonForPage(1))
                    .queue();
        });
    }

    @Override
    public void handleModal(@NotNull ModalInteractionEvent event, @NotNull List<ModalMapping> mappings) {
        long uid = event.getUser().getIdLong();
        Country c = userInProgress.get(uid);

        switch (event.getModalId()) {
            case "modal_succession":
                c.setSuccessionType(event.getValue("successionType").getAsString());
                break;
            case "modal_population":
                c.setPopulation(Long.parseLong(event.getValue("population").getAsString()));
                break;
            case "modal_growth":
                c.setGrowthRate(Double.parseDouble(event.getValue("growthRate").getAsString()));
                break;
            case "modal_capacity":
                c.setPopCapacity(Long.parseLong(event.getValue("popCapacity").getAsString()));
                break;
            case "modal_market":
                c.setMainMarket(event.getValue("mainMarket").getAsString());
                break;
            case "modal_currency":
                c.setCurrency(event.getValue("currency").getAsString());
                break;
            case "modal_budget":
                c.setBudget(Double.parseDouble(event.getValue("budget").getAsString()));
                break;
            case "modal_devastation":
                c.setDevastation(Double.parseDouble(event.getValue("devastation").getAsString()));
                break;
            case "modal_centralization":
                c.setCentralization(Double.parseDouble(event.getValue("centralization").getAsString()));
                break;
            default:
                return;
        }

        // re-render same page
        loadPages(uid);
        int cur = userPages.get(uid);
        event.deferEdit().queue(hook -> {
            hook.editOriginalEmbeds(pages.get(cur)
                            .setFooter("Page " + (cur+1) + " / " + pages.size()).build())
                    .setActionRow(getButtonForPage(cur))
                    .queue();
        });
    }

    private void loadPages(long uid) {
        Country c = userInProgress.get(uid);
        pages.clear();
        pages.add(new EmbedBuilder()
                .setTitle("Country – Succession Type")
                .setDescription(c.getSuccessionType())
                .setColor(Color.BLUE));
        pages.add(new EmbedBuilder()
                .setTitle("Country – Population")
                .setDescription(String.valueOf(c.getPopulation()))
                .setColor(Color.BLUE));
        pages.add(new EmbedBuilder()
                .setTitle("Country – Growth Rate")
                .setDescription(String.valueOf(c.getGrowthRate()))
                .setColor(Color.BLUE));
        pages.add(new EmbedBuilder()
                .setTitle("Country – Population Capacity")
                .setDescription(String.valueOf(c.getPopCapacity()))
                .setColor(Color.BLUE));
        pages.add(new EmbedBuilder()
                .setTitle("Country – Main Market")
                .setDescription(c.getMainMarket())
                .setColor(Color.BLUE));
        pages.add(new EmbedBuilder()
                .setTitle("Country – Currency")
                .setDescription(c.getCurrency())
                .setColor(Color.BLUE));
        pages.add(new EmbedBuilder()
                .setTitle("Country – Budget")
                .setDescription(String.valueOf(c.getBudget()))
                .setColor(Color.BLUE));
        pages.add(new EmbedBuilder()
                .setTitle("Country – Devastation")
                .setDescription(String.valueOf(c.getDevastation()))
                .setColor(Color.BLUE));
        pages.add(new EmbedBuilder()
                .setTitle("Country – Centralization")
                .setDescription(String.valueOf(c.getCentralization()))
                .setColor(Color.BLUE));
        pages.add(new EmbedBuilder()
                .setTitle("Review")
                .addField("Succession", c.getSuccessionType(), true)
                .addField("Population", String.valueOf(c.getPopulation()), true)
                .addField("Growth", String.valueOf(c.getGrowthRate()), true)
                .addField("Capacity", String.valueOf(c.getPopCapacity()), true)
                .addField("Market", c.getMainMarket(), true)
                .addField("Currency", c.getCurrency(), true)
                .addField("Budget", String.valueOf(c.getBudget()), true)
                .addField("Devastation", String.valueOf(c.getDevastation()), true)
                .addField("Centralization", String.valueOf(c.getCentralization()), true)
                .setColor(Color.GREEN));
    }

    private List<ItemComponent> getButtonForPage(int page) {
        List<ItemComponent> buttons = new ArrayList<>();

        // Prev/Next
        if (page > 0) buttons.add(Button.primary("prev_country", "⬅ Prev"));
        if (page < pages.size() - 1) buttons.add(Button.primary("next_country", "Next ➡"));

        // Field buttons for pages 1–8 (same as before)...
        switch (page) {
            case 0: buttons.add(Button.secondary("setup_succession",     "⚙ Set Succession"));     break;
            case 1: buttons.add(Button.secondary("setup_population",     "⚙ Set Population"));     break;
            case 2: buttons.add(Button.secondary("setup_growth",         "⚙ Set Growth Rate"));    break;
            case 3: buttons.add(Button.secondary("setup_capacity",       "⚙ Set Capacity"));       break;
            case 4: buttons.add(Button.secondary("setup_market",         "⚙ Set Market"));         break;
            case 5: buttons.add(Button.secondary("setup_currency",       "⚙ Set Currency"));       break;
            case 6: buttons.add(Button.secondary("setup_budget",         "⚙ Set Budget"));         break;
            case 7: buttons.add(Button.secondary("setup_devastation",    "⚙ Set Devastation"));    break;
            case 8: buttons.add(Button.secondary("setup_centralization", "⚙ Set Centralization")); break;
            // **Confirm button now on page 9** (the last one)
            default: break;
        }

        // After fields, if we're on the last page (index pages.size()-1), add Confirm:
        if (page == pages.size() - 1) {
            buttons.clear(); // clear Prev/Next if you only want Confirm here
            buttons.add(Button.success("confirm_country", "✅ Confirm"));
        }

        return buttons;
    }

}
