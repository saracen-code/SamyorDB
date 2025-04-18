package com.samyorBot.commands.utilities;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Saskartanize extends SlashCommand {

    public Saskartanize() {
        setCommandData(Commands.slash("saskartanize", "Saskartanizes a word you give")
                .addOption(OptionType.STRING, "word", "The word to evolve", true));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String word = event.getOption("word").getAsString();
        event.deferReply().queue();

        try {
            String evolved = evolveWord(word);
            event.getHook().sendMessage("Evolved form: `" + evolved + "`").queue();
        } catch (Exception e) {
            e.printStackTrace();
            event.getHook().sendMessage("Error: " + e.getMessage()).queue();
        }
    }

    public static String evolveWord(String inputWord) throws Exception {
        File runtimeDir = new File("src/main/java/haedus-runtime");
        if (!runtimeDir.exists()) runtimeDir.mkdirs();

        File inputFile = new File(runtimeDir, "input.txt");
        File outputFile = new File(runtimeDir, "output.txt");
        File ruleFile = new File(runtimeDir, "runtime.rule");
        File script = new File(runtimeDir, "toolbox.sh");

        // 1. Write the input word to input.txt
        Files.writeString(inputFile.toPath(), inputWord + "\n", StandardCharsets.UTF_8);

        // 2. Check that your custom rule file exists
        if (!ruleFile.exists()) {
            throw new FileNotFoundException("Missing: runtime.rule (your custom rule file)");
        }

        // 3. Run Haedus via toolbox.sh
        ProcessBuilder pb = new ProcessBuilder(script.getAbsolutePath(), "runtime.rule");
        pb.directory(runtimeDir);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exit = process.waitFor();
        if (exit != 0) throw new RuntimeException("Haedus execution failed (exit code " + exit + ")");

        // 4. Return first output line
        List<String> lines = Files.readAllLines(outputFile.toPath());
        return lines.isEmpty() ? "(no result)" : lines.get(0);
    }
}
