package app.anisan.pomAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public final class DependencyFinder {

    public static String getDependencyList(String a_strPathToPOM) throws Throwable {
        StringBuilder output = new StringBuilder();
        String strMavenPath = CheckMavenInstallation.getMavenPath();

        ProcessBuilder builder = new ProcessBuilder(strMavenPath, "dependency:list");  // Linux/macOS
        // For Windows, use: new ProcessBuilder("cmd.exe", "/c", "dir");

        // Set the working directory
        builder.directory(new File(a_strPathToPOM).getParentFile());  // Replace with your folder path

        // Start the process
        Process process = builder.start();

        // Output handling (optional)
        //process.getInputStream().transferTo(System.out);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        process.getErrorStream().transferTo(System.err);

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append("ERROR: ").append(line).append("\n");
//            }
//        }

        return output.toString();
    }
}
