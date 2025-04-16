package com.samyorBot.commands.game.character;

import com.samyorBot.classes.characters.Ability;
import com.samyorBot.classes.traits.Trait;
import com.samyorBot.classes.traits.TraitProfile;
import com.samyorBot.classes.traits.TraitRegistry;
import com.samyorBot.database.CharacterDAO;
import com.samyorBot.classes.characters.Character;
import com.samyorBot.database.AbilityDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
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

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CharSetup extends SlashCommand.Subcommand implements ButtonHandler, StringSelectMenuHandler, ModalHandler {

    private final List<EmbedBuilder> pages = new ArrayList<>();
    private static final Map<Long, Integer> userPages = new HashMap<>();
    private static final Map<Long, Long> userSelectedCharacter = new HashMap<>();


    public CharSetup() {
        setCommandData(new SubcommandData("setup", "enables you to setup/edit a new character"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long userId = event.getUser().getIdLong();
        userPages.put(userId, 0);

        // First page is character selection; no need to load a character yet
        pages.clear();
        pages.add(new EmbedBuilder()
                .setTitle("Character Setup")
                .setDescription("Do you want to **create a new character** or **edit an existing one**?\n\nYou may have multiple characters under your account.")
                .setColor(Color.MAGENTA));

        event.deferReply(true).queue(interactionHook -> {
            interactionHook.editOriginalEmbeds(pages.getFirst().setFooter("Page 1 / " + pages.size()).build())
                    .setActionRow(getButtonForPage(0))
                    .queue();
        });
    }

    @Override
    public void handleButton(@NotNull ButtonInteractionEvent event, @NotNull Button button) {
        String id = event.getComponentId();
        Long userId = event.getUser().getIdLong();
        int currentPage = userPages.getOrDefault(userId, 0);

        /*
         * PAGINATION HANDLING
         */
        if (pages.isEmpty()) {
            loadPages(event.getUser().getIdLong());
        }

        if (id.equals("prev") && currentPage > 0) {
            currentPage--;
        } else if (id.equals("next") && currentPage < pages.size() - 1) {
            if (currentPage == pages.size() - 2) {
                //reload the pages to update information
                pages.clear();
                loadPages(userId);
            }
            currentPage++;
        }

        userPages.put(userId, currentPage);


        /*
         * BUTTON HANDLING
         */

        List<ItemComponent> actions = getButtonForPage(currentPage);


        /*
         * PAGE BUTTONS HANDLING
         */

        if (id.equals("create_new_char")) {
            Character newChar = new Character(userId);
            CharacterDAO.saveCharacter(newChar);
            userSelectedCharacter.put(userId, newChar.getId());

            pages.clear(); // reset pages for this character
            loadPages(userId);
            userPages.put(userId, 1); // go to first actual editing page
            EmbedBuilder embed = pages.get(1).setFooter("Page 2 / " + pages.size());
            event.editMessageEmbeds(embed.build())
                    .setActionRow(getButtonForPage(1))
                    .queue();
            return;
        }

        if (id.equals("select_existing_char")) {
            List<Character> chars = CharacterDAO.getCharactersByUserId(userId);

            if (chars.isEmpty()) {
                event.reply("❌ You don't have any characters yet. Click **Create New Character** to begin.").setEphemeral(true).queue();
                return;
            }

            StringSelectMenu.Builder menu = StringSelectMenu.create("existing_char_select")
                    .setPlaceholder("Choose a character to edit");

            for (Character c : chars) {
                menu.addOption(c.getName() + " (ID " + c.getId() + ")", String.valueOf(c.getId()));
            }

            event.editMessageEmbeds(new EmbedBuilder()
                            .setTitle("Select a character")
                            .setDescription("Choose which character to edit.")
                            .build())
                    .setActionRow(menu.build())
                    .queue();

            return;
        }

        ///  select culture
        if (id.equals("select_culture")) {
            actions = new ArrayList<>();
            StringSelectMenu selectMenu = StringSelectMenu.create("select_culture")
                    .setPlaceholder("Choose an option...")
                    .addOption("khaiyuha (generic)", "khaiyuha")
                    .addOption("daban", "daban")
                    .addOption("kniziyt", "kniziyt")
                    .build();

            // Add the select menu to the actions list
            actions.add(selectMenu);  // Ensure this list isn't null and can handle ItemComponent
        }
        /// name setup
        if (id.equals("setup_name")) {
            Modal modal = Modal.create("setup_name", "Write your name")
                    .addActionRow(
                            TextInput.create("character_name", "Character Name", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g., Yezhira, Darnukh, Savael")
                                    .setRequired(true)
                                    .setMinLength(2)
                                    .setMaxLength(32)
                                    .build()
                    )
                    .build();
            event.replyModal(modal).queue();
            return;
        }

        if (id.equals("random_name")) {
            event.reply("will be added soon");
            return;
        }

        /// details

        if (id.equals("cities_list")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("**CITIES LIST**");
            eb.setDescription("When setting up your location, you must appropriately type it in from among these cities.");
            eb.setUrl("https://docs.google.com/document/d/1ibRTPype9hzVhhcuW7U3iSafU--mEaqaj-mjvxR9p7I/edit?tab=t.0");
            event.editMessageEmbeds(eb.build())
                    .setActionRow(actions)
                    .queue();
            return;
        }

        if (id.equals("edit_details")) {
            Modal modal = Modal.create("setup_details", "Character Background Info")
                    .addActionRow(TextInput.create("birthdate", "Birth Date", TextInputStyle.SHORT)
                            .setPlaceholder("e.g., 1740")
                            .setRequired(true)
                            .setMinLength(3)
                            .setMaxLength(4)
                            .build())
                    .addActionRow(TextInput.create("location", "Birth Date", TextInputStyle.SHORT)
                            .setPlaceholder("Must be correct name. e.g. Ghlaleb, Yiytchiy, Sandibat, ...")
                            .setRequired(true)
                            .setMinLength(2)
                            .setMaxLength(30)
                            .build())
                    .addActionRow(TextInput.create("affiliation", "Affiliation", TextInputStyle.SHORT)
                            .setPlaceholder("e.g., Ironheart Guild, Order of Elaron")
                            .setRequired(true)
                            .setMinLength(3)
                            .setMaxLength(50)
                            .build())
                    .addActionRow(TextInput.create("backstory", "Backstory", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("Who are you? What is your journey?")
                            .setRequired(true)
                            .setMinLength(30)
                            .setMaxLength(1000)
                            .build())
                    .build();
            event.replyModal(modal).queue();
            return;
        }
        if (id.equals("set_traits")) {
            actions = new ArrayList<>();
            List<Trait> allTraits = TraitRegistry.getAllTraits();

            // You can filter or sort if needed
            List<SelectOption> options = allTraits.stream()
                    .map(trait -> SelectOption.of(
                            trait.getName() + " (" + (trait.getValue() > 0 ? "+" : "") + trait.getValue() + ")", // label
                            trait.getName().toLowerCase() // value
                    ))
                    .collect(Collectors.toList());

            actions.add(StringSelectMenu.create("setup_trait")
                    .setPlaceholder("Select up to 5 balanced traits...")
                    .setMinValues(1)
                    .setMaxValues(5)
                    .addOptions(options)
                    .build());
        }

        /// stats
        if (id.equals("setup_sp")) {
            Modal modal = Modal.create("setup_skillpoints", "Tier X: Distribute X Stat Points")
                    .addActionRow(
                            TextInput.create("vit", "Vitality (VIT) – Health", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. 3").setRequired(true).build()
                    )
                    .addActionRow(
                            TextInput.create("intr", "Introspection (INT) – Namshî", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. 0").setRequired(true).build()
                    )
                    .addActionRow(
                            TextInput.create("aff", "Affinity (AFF) – Shepher", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. 0").setRequired(true).build()
                    )
                    .addActionRow(
                            TextInput.create("str", "Strength (STR) – Dlaeb", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. 1").setRequired(true).build()
                    )
                    .build();
            event.replyModal(modal).queue();
            return;
        }

        if (id.equals("choose_existing_ability")) {
            Long characterId = userSelectedCharacter.get(userId);
            Character character = CharacterDAO.getCharacterById(characterId);
            int totalAbilities = character.getAbilities().size();
            int maxAbilities = 4; // You can adjust this based on Tier logic
            int remaining = maxAbilities - totalAbilities;

            if (remaining <= 0) {
                event.reply("❌ You've reached the maximum number of abilities.").setEphemeral(true).queue();
                return;
            }

            List<Ability> approved = AbilityDAO.getApprovedAbilities(); // get from your DAO
            if (approved.isEmpty()) {
                event.reply("⚠ No approved abilities are available at the moment.").setEphemeral(true).queue();
                return;
            }

            StringSelectMenu.Builder menu = StringSelectMenu.create("select_existing_ability")
                    .setPlaceholder("Select ability to add")
                    .setMinValues(1)
                    .setMaxValues(1);

            for (Ability ab : approved) {
                menu.addOption(ab.getName() + " (" + ab.getType() + ")", ab.getName());
            }

            event.reply("📚 You may add **" + remaining + "** more abilities.")
                    .addActionRow(menu.build())
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (id.equals("view_current_abilities")) {
            Long characterId = userSelectedCharacter.get(userId);
            Character character = CharacterDAO.getCharacterById(characterId);
            List<String> abilities = character.getAbilities();

            if (abilities.isEmpty()) {
                event.reply("❌ You haven't selected any abilities yet.").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("📖 Current Abilities")
                    .setDescription(String.join("\n", abilities.stream().map(a -> "• " + a).toList()))
                    .setColor(Color.CYAN);

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }


        if (id.equals("setup_abilities")) {
            Modal modal = Modal.create("setup_abilities", "Create a New Ability")
                    .addActionRow(
                            TextInput.create("ability_name", "Ability Name", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. Serpent's Fang").setRequired(true).build()
                    )
                    .addActionRow(
                            TextInput.create("ability_type", "Type (regular/special)", TextInputStyle.SHORT)
                                    .setPlaceholder("regular or special").setRequired(true).build()
                    )
                    .addActionRow(
                            TextInput.create("ability_damage", "Damage (numeric)", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. 7").setRequired(true).build()
                    )
                    .addActionRow(
                            TextInput.create("ability_description", "Description", TextInputStyle.PARAGRAPH)
                                    .setPlaceholder("Describe the effect...").setRequired(true).build()
                    )
                    .build();

            event.replyModal(modal).queue();
            return;
        }

        if (id.equals("setup_url")) {
            Modal modal = Modal.create("setup_url", "Type the URL for your character portrait")
                    .addActionRow(
                            TextInput.create("setup_url", "Portrait Image URL", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g., Yezhira, Darnukh, Savael")
                                    .setRequired(true)
                                    .build()
                    )
                    .build();
            event.replyModal(modal).queue();
            return;
        }

        // Ensure the embed is being updated properly
        EmbedBuilder embed = pages.get(currentPage).setFooter("Page " + (currentPage + 1) + " / " + pages.size());

        // Properly update the embed and the action row
        event.editMessageEmbeds(embed.build())
                .setActionRow(actions)  // Add action row with buttons and select menu
                .queue();
    }


    @Override
    public void handleStringSelectMenu(@NotNull StringSelectInteractionEvent e, @NotNull List<String> list) {
        String id = e.getComponentId();
        Long userId = e.getUser().getIdLong();
        EmbedBuilder eb = new EmbedBuilder();

        if (id.equals("existing_char_select")) {
            long selectedId = Long.parseLong(list.getFirst());
            Character selected = CharacterDAO.getCharacterById(selectedId);
            userSelectedCharacter.put(userId, selected.getId());


            if (selected == null) {
                e.reply("❌ Character not found.").setEphemeral(true).queue();
                return;
            }

            // Use selected character data
            // Optionally save in session if needed
            pages.clear();
            loadPages(selected.getUserId()); // or store selected.getId() in a separate map if needed
            userPages.put(userId, 1); // skip select page
            EmbedBuilder embed = pages.get(1).setFooter("Page 2 / " + pages.size());
            e.editMessageEmbeds(embed.build())
                    .setActionRow(getButtonForPage(1))
                    .queue();
        }

        if (id.equals("select_culture") || id.equals("1")) {
            String culture = list.getFirst();
            CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "culture", culture);
            eb.setTitle("✅ Culture Set");
            eb.setDescription("You selected the culture: **" + culture + "**");
        }

        if (id.equals("setup_trait")) {
            TraitProfile traitList = new TraitProfile();

            for (String s : list) {
                Trait t = TraitRegistry.getTrait(s);
                traitList.addTrait(t);
            }

            if (!traitList.isBalanced()) {
                e.reply("❌ Your traits are not balanced. The total must be between -3 and +3.").setEphemeral(true).queue();
                return;
            }

            CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "traits", list);
            eb.setTitle("✅ Traits Set");
            eb.setDescription("Traits selected:\n" + traitList.toString());
        }

        if (id.equals("select_existing_ability")) {
            String abilityName = list.getFirst();
            Long characterId = userSelectedCharacter.get(userId);

            Character character = CharacterDAO.getCharacterById(characterId);
            List<String> currentAbilities = new ArrayList<>(character.getAbilities());

            if (currentAbilities.contains(abilityName)) {
                e.reply("❌ Ability already selected.").setEphemeral(true).queue();
                return;
            }

            currentAbilities.add(abilityName);
            CharacterDAO.updateCharacterField(characterId, "abilities", currentAbilities);

            int remaining = 4 - currentAbilities.size(); // Adjust this if needed by tier
            e.reply("✅ Added **" + abilityName + "**. You can select **" + remaining + "** more abilities.")
                    .setEphemeral(true)
                    .queue();
        }


        // Only send embed edit if eb has content
        if (!eb.getFields().isEmpty() || !eb.getDescriptionBuilder().isEmpty()) {
            e.editMessageEmbeds(eb.build())
                    .setActionRow(getButtonForPage(userPages.getOrDefault(userId, 0)))
                    .queue();
        }
    }

    private List<ItemComponent> getButtonForPage(int page) {
        List<ItemComponent> buttonList = new ArrayList<>();

        // Default navigation buttons (Prev/Next)
        buttonList.add(Button.primary("prev", "⬅ Previous"));
        buttonList.add(Button.primary("next", "Next ➡"));


        // Add page-specific buttons
        switch (page) {
            case 0: // Character selection page
                buttonList.add(Button.success("create_new_char", "🆕 Create New Character"));
                buttonList.add(Button.secondary("select_existing_char", "📁 Edit Existing Character"));
                break;

            case 1: // Culture page
                buttonList.add(ActionRow.of(Button.secondary("select_culture", "🔄 Select Culture")).getComponents().getFirst());
                break;

            case 2: // Name page
                buttonList.add(ActionRow.of(Button.secondary("setup_name", "⚙ Manual Name")).getComponents().getFirst());
                buttonList.add(ActionRow.of(Button.secondary("random_name", "⚙ Randomized Name")).getComponents().getFirst());
                break;

            case 3: // Character Details
                buttonList.add(ActionRow.of(Button.secondary("edit_details", "⚙ Setup Details")).getComponents().getFirst());
                buttonList.add(ActionRow.of(Button.secondary("cities_list", "📋 Cities Listing")).getComponents().getFirst());
                break;

            case 4: // Traits page
                buttonList.add(ActionRow.of(Button.secondary("set_traits", "✏️ Set Traits")).getComponents().getFirst());
                break;

            case 5: // Character skillpoints page
                buttonList.add(ActionRow.of(Button.secondary("setup_sp", "🔄 Redistribute Skillpoints")).getComponents().getFirst());
                break;

            case 6: // Abilities page
                buttonList.add(Button.primary("choose_existing_ability", "📚 Choose Existing"));
                buttonList.add(Button.success("setup_abilities", "➕ Add New"));
                buttonList.add(Button.secondary("view_current_abilities", "📖 View Selected"));
                break;


            case 7: // Image page
                buttonList.add(ActionRow.of(Button.secondary("setup_url", "Add URL")).getComponents().getFirst());
                break;

            case 9: // Last page (Finish button)
                buttonList.add(Button.success("confirm_char", "✅ confirm"));
                break;

            default:
                break;
        }

        return buttonList;
    }


    private void loadPages(Long userId) {

        Long selectedId = userSelectedCharacter.get(userId);
        if (selectedId == null) {
            System.err.println("❌ No character selected for userId = " + userId);
            return; // or throw, or reply to the user with a warning
        }

        Character character = CharacterDAO.getCharacterById(selectedId);


        // Page 0 – Character Selection
        pages.add(new EmbedBuilder()
                .setTitle("Character Setup")
                .setDescription("Do you want to **create a new character** or **edit an existing one**?\n\nYou may have multiple characters under your account.")
                .setColor(Color.MAGENTA));

        // Page 1 – Culture
        pages.add(new EmbedBuilder()
                .setTitle("Character Creation – Culture")
                .setDescription("Select your character’s cultural background. This can influence naming conventions, traditions, spiritual inclinations, and even gameplay elements in roleplay.")
                .setColor(Color.ORANGE));

        // Page 2 – Name
        pages.add(new EmbedBuilder()
                .setTitle("Character Creation – Name")
                .setDescription("Choose a name fitting your culture or unique identity.\n\n✸ **Name:**\n_This will be the name used in all character interactions._")
                .setColor(Color.ORANGE));

        // Page 3 – Details
        pages.add(new EmbedBuilder()
                .setTitle("Character Creation – Details")
                .addField("__✸ Start Location__", "Define where your character starts their journey. This can be a real or fictional city, region, or landmark.\nConsider the geography, culture, and lore impact.", false)
                .addField("__✸ Birth Date__", "State your character’s date of birth. This must follow in-universe calendar rules.\nThis will impact your randomized age...", false)
                .addField("__✸ Affiliation__", "Specify the organization, group, or figure your character is affiliated with. (e.g. government, guild, or anything broad enough to define allegiance)", false)
                .addField("__✸ Description__", "**Write a compelling history for your character.**\n\nMust include:\n- Economic status\n- Domestic/foreign struggles\n- Emotional development\n- Personality response to hardship\n\n✸ NOTE: Can be left empty for lore-based secrecy (requires staff approval).", false)
                .setColor(Color.ORANGE));

        // Page 4 – Character Traits
        pages.add(new EmbedBuilder()
                .setTitle("Character Creation – Character Traits")
                .setDescription("**Define your character’s nature.**\n\nInclude:\n- Skills, fighting style, passions\n- Positive and negative traits (one negative per positive)\n- What they hold dear or aim to achieve\n\n_These traits impact your affinity with Cardinal Spirits and persuasion modifiers._")
                .setColor(Color.ORANGE));

        // Page 5 – Statistics
        pages.add(new EmbedBuilder()
                .setTitle("Character Creation – Statistics")
                .setDescription("Each Tier grants 12 skill points to distribute. Start with Tier 1.\n\n✦ **Vitality (VIT):** Health\n✦ **Introspection (INT):** Magic – Namshî\n✦ **Strength (STR):** Weapon/physical – Dlaeb\n✦ **Affinity (AFF):** Magic – Shepher\n\n**Example Spread (Tier 1):**\n- Vitality: +3 (+7)\n- Strength: +1 (+5)\n\n✸ Tier: 1\n✸ Resistance (Tier 2+): _Specify stat resisted._")
                .setColor(Color.ORANGE));

        // Page 6 – Abilities
        pages.add(new EmbedBuilder()
                .setTitle("Character Creation – Abilities")
                .setDescription("Create abilities your character can use in combat.\n\nEach must be labeled: Light / Medium / Heavy.\n\n✸ **Tier 1:** 3 regular, 1 special\n✸ **Tier 2:** 4 regular, 1 special\n✸ **Tier 3:** 5 regular, 1 special\n\n_No magical abilities unless approved for Azhi. Creative non-damage effects are encouraged._")
                .setColor(Color.ORANGE));

        // Page 7 – Image
        pages.add(new EmbedBuilder()
                .setTitle("Character Creation – Character Image")
                .setDescription("Provide a visual representation of your character.\n\n✸ **Image:** _Upload or link. Optional if lore-secret._")
                .setColor(Color.ORANGE));

        // Page 8 – Helper Page
        pages.add(new EmbedBuilder()
                .setTitle("Character Creation – Helper: Stats, Persuasion, Tiers")
                .addField("🎲 Persuasion Modifier (Roll d100)",
                        "1–6: Manipulative (+4)\n7–12: Charismatic (+3)\n13–21: Persuasive (+2)\n22–32: Charming (+1)\n33–54: Indifferent (+0)\n55–65: Callous (-1)\n66–76: Introverted (-1)\n77–85: Impatient (-2)\n86–94: Cruel (-3)\n95–100: Mentally Infirm (-5)", false)
                .addField("📊 Stat Rules",
                        "- Default: -4\n- Unassigned: -3\n- Max +5 at Tier 1\n- INT/AFF unlocked at Tier 2 (if approved)", false)
                .addField("🛡️ Resistances (Tier 2+)",
                        "Choose 1 of STR / INT / AFF to resist. Halves damage taken from that stat.", false)
                .addField("🏆 Tier Progression",
                        "**Tier 1:** 30 HP, 12 SP\n**Tier 2:** 40 HP, +12 SP, unlock Azhi, choose resistance\n**Tier 3:** 50 HP, +12 SP, advanced Azhi branches\n**Tier 4:** 60 HP, +12 SP, [REDACTED]", false)
                .addField("📈 Advancing Tiers",
                        "Requires roleplay, combat, screenshots, and staff review. Dungeon completions help.", false)
                .setColor(Color.YELLOW));

        // Page - 9 Summary
        pages.add(new EmbedBuilder()
                .setTitle("Character Creation – Summary")
                .setDescription("Here is a summary of your character's current selection.")
                .addField("Culture", character.getCulture(), true)
                .addField("Name", character.getName(), true)
                .addField("Start Location",character.getLocation(), true)
                .addField("Birth Date", character.getBirthdate(), true)
                .addField("Affiliation", character.getAffiliation(), true)
                .addField("Backstory", character.getBackstory(), true)
                .addField("Character Traits", character.getTraits().toString(), true)
                .addField("Statistics", character.getStatistics().toString(), true)
                .addField("Abilities", character.getAbilities().toString(), true)
                .addField("Image", character.getImage(), true)
                .addField("Tier Progression", character.getTier(), true)
                .setColor(Color.GREEN));
    }

    @Override
    public void handleModal(@NotNull ModalInteractionEvent event, @NotNull List<ModalMapping> list) {
        String modalId = event.getModalId();
        EmbedBuilder eb = new EmbedBuilder();

        long userId = event.getUser().getIdLong();

        if (modalId.equals("setup_name")) {
            String name = event.getValue("character_name").getAsString();
            CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "name", name);
            eb.setTitle("✅ Name Set");
            eb.setDescription("Your character's name has been set to **" + name + "**.");

        } else if (modalId.equals("setup_details")) {
            String birthdate = event.getValue("birthdate").getAsString();
            String affiliation = event.getValue("affiliation").getAsString();
            String backstory = event.getValue("backstory").getAsString();
            String location = event.getValue("location").getAsString();

            CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "birthdate", birthdate);
            CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "affiliation", affiliation);
            CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "backstory", backstory);
            CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "location", location);

            eb.setTitle("✅ Character Details Updated");
            eb.addField("Birthdate", birthdate, true);
            eb.addField("Affiliation", affiliation, true);
            eb.addField("Backstory", backstory.length() > 500 ? backstory.substring(0, 500) + "..." : backstory, false);

        } else if (modalId.equals("setup_skillpoints")) {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("vit", Integer.parseInt(event.getValue("vit").getAsString()));
            stats.put("int", Integer.parseInt(event.getValue("intr").getAsString()));
            stats.put("aff", Integer.parseInt(event.getValue("aff").getAsString()));
            stats.put("str", Integer.parseInt(event.getValue("str").getAsString()));

            int total = stats.values().stream().mapToInt(Integer::intValue).sum();
            if (total > 12) {
                event.reply("❌ You allocated more than 12 points. Please try again.").setEphemeral(true).queue();
                return;
            }

            CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "statistics", stats);

            eb.setTitle("✅ Stats Updated");
            eb.setDescription(String.format("VIT: **%d**, INT: **%d**, AFF: **%d**, STR: **%d**",
                    stats.get("vit"), stats.get("int"), stats.get("aff"), stats.get("str")));

        } else if (modalId.equals("setup_abilities")) {
            String name = event.getValue("ability_name").getAsString();
            String type = event.getValue("ability_type").getAsString().toLowerCase();
            String damageStr = event.getValue("ability_damage").getAsString();
            String description = event.getValue("ability_description").getAsString();

            if (!type.equals("regular") && !type.equals("special")) {
                event.reply("❌ Ability type must be either `regular` or `special`.").setEphemeral(true).queue();
                return;
            }

            int damage;
            try {
                damage = Integer.parseInt(damageStr);
            } catch (NumberFormatException e) {
                event.reply("❌ Damage must be a number.").setEphemeral(true).queue();
                return;
            }

            Ability ability = new Ability(name, type, damage, description, false); // not approved by default
            boolean saved = AbilityDAO.saveAbility(ability);

            if (saved) {
                CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "abilities", List.of(name));
                eb.setTitle("✅ Ability Saved");
                eb.setDescription("Added ability: **" + name + "**\n\n**Type:** " + type + "\n**Damage:** " + damage + "\n**Description:** " + description);
            } else {
                event.reply("❌ Failed to save ability.").setEphemeral(true).queue();
                return;
            }
        } else if (modalId.equals("setup_url")) {
            String url = event.getValue("setup_url").getAsString();

            // Basic validation
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                event.reply("❌ Invalid URL. Please ensure it starts with `http://` or `https://`.").setEphemeral(true).queue();
                return;
            }

            CharacterDAO.updateCharacterField(userSelectedCharacter.get(userId), "image", url);

            eb.setTitle("✅ Image URL Set");
            eb.setDescription("Your character image has been set to the following URL.");
            eb.setImage(url); // shows preview if support
        }

        event.editMessageEmbeds(eb.build())
                .setActionRow(getButtonForPage(userPages.getOrDefault(userId, 0)))
                .queue();
    }
}