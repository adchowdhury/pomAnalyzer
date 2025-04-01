package app.anisan.pomAnalyzer;

import app.anisan.pomAnalyzer.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckMavenInstallation {

    public static boolean checkIfMavenIsInstalled() {
        try {

            String mavenPath = getMavenPath();

            if (mavenPath == null) {
                Logger.error("❌ Maven is not installed or not available in PATH.", App.verbose);
                return false;
            }

            ProcessBuilder builder = new ProcessBuilder(mavenPath, "-v");
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean foundMaven = false;

            while ((line = reader.readLine()) != null) {
                Logger.log(line, App.verbose);  // Prints Maven version details
                if (line.toLowerCase().contains("apache maven")) {
                    foundMaven = true;
                    break;
                }
            }

            int exitCode = process.waitFor();

            if (foundMaven && exitCode == 0) {
                Logger.log("✅ Maven is installed!", App.verbose);
                return true;
            } else {
                Logger.error("❌ Maven is not installed or not available in PATH.", App.verbose);
                return false;
            }

        } catch (Exception e) {
            Logger.error("❌ Maven is not installed or not in system PATH.", e, App.verbose);
            return false;
        }
    }

    public static String getMavenPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String command;

        if (os.contains("win")) {
            command = "where mvn";
        } else {
            command = "which mvn";
        }
        String line;
        try {
            ProcessBuilder builder = new ProcessBuilder(command.split(" "));
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            if ((line = reader.readLine()) != null && !line.isEmpty()) {
                Logger.log("Maven executable found at: " + line, App.verbose);
                if (os.contains("win") && !line.endsWith(".cmd")) {
                    line += ".cmd";
                }
                return line;
            } else {
                Logger.error("Maven executable not found in PATH.", App.verbose);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                Logger.error("Command exited with code: " + exitCode, App.verbose);
            }

        } catch (IOException | InterruptedException e) {
            Logger.error("Error while searching for Maven executable: " + e.getMessage(), e, App.verbose);
        }
        return null;
    }
}
