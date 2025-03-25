package app.anisan.pomAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import app.anisan.pomAnalyzer.log.Logger;

public class PomValidator {
	
	public static ProjectDetails getProjectDetails(String strPathToPOM) throws Throwable {
		ProjectDetails pd = new ProjectDetails();
		Document doc = Jsoup.parse(new File(strPathToPOM), "UTF-8", "", org.jsoup.parser.Parser.xmlParser());
		
		pd.setGroupId(getElementText(doc, "project > groupId"));
		pd.setArtifactId(getElementText(doc, "project > artifactId"));
		pd.setVersion(getElementText(doc, "project > version"));
		pd.setName(getElementText(doc, "project > name"));
		pd.setDescription(getElementText(doc, "project > description"));
		
		return pd;
	}
	
	private static String getElementText(Document doc, String tagName) {
		Element element = doc.selectFirst(tagName);
		return element != null ? element.text() : "N/A";
	}

    public static boolean validatePom(String strProjectFolder) {
        boolean isValid = true;
        try {

            String mavenPath = CheckMavenInstallation.getMavenPath();

            // Build mvn validate command
            ProcessBuilder builder = new ProcessBuilder(mavenPath, "validate");

            File projectFolder = new File(strProjectFolder);
            if (!projectFolder.exists() || !projectFolder.isDirectory() || !projectFolder.canRead()) {
                return false;
            }

            // Set working directory where pom.xml is located
            builder.directory(projectFolder);

            // Start the process
            Process process = builder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                Logger.log(line, App.verbose);
                if (line.contains("[ERROR]")) {
                    Logger.error(line, App.verbose);
                    isValid = false;
                    break;
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
            	Logger.error("Maven validate exited with code: " + exitCode);
            }

            if (exitCode == 0 && isValid) {
                Logger.log("✅ pom.xml is valid.");
            } else {
                Logger.error("❌ pom.xml is invalid.");
                isValid = false;
            }

        } catch (Exception e) {
            Logger.error("Error validating pom.xml: " + e.getMessage(), e);
            isValid = false;
        }
        return isValid;
    }
}