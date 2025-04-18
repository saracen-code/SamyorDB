package com.samyorBot.util;

import java.io.*;
import java.util.Objects;

public class RunToolboxScript {
    public static void main(String[] args) {
        try {
            // Get the working directory (target/classes/haedus-sca-0.7.0)
            File sampleFile = new File(Objects.requireNonNull(
                    RunToolboxScript.class.getClassLoader()
                            .getResource("haedus-sca-0.7.0/sample.txt")).getFile());

            File workingDir = sampleFile.getParentFile();

            // Get the script and rule file paths
            File toolboxScript = new File(workingDir, "toolbox.sh");
            File ruleFile = new File(workingDir, "static-evolve.rule");

            if (!toolboxScript.canExecute()) {
                System.out.println("Making toolbox.sh executable...");
                toolboxScript.setExecutable(true);
            }

            ProcessBuilder pb = new ProcessBuilder(
                    toolboxScript.getAbsolutePath(),
                    ruleFile.getAbsolutePath()
            );
            pb.directory(workingDir);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Execution finished with exit code: " + exitCode);

            // Read and show the output
            File output = new File(workingDir, "output.txt");
            if (output.exists()) {
                System.out.println("\n--- Output Lexicon ---");
                BufferedReader outReader = new BufferedReader(new FileReader(output));
                outReader.lines().forEach(System.out::println);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
