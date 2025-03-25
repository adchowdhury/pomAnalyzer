package app.anisan.pomAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.anisan.pomAnalyzer.log.Logger;
import app.anisan.pomAnalyzer.output.WriterFactory;
import app.anisan.pomAnalyzer.output.params.WriterParams;

public final class ParseMavenDependencyOutput {

    public static void main(String[] args) {
        try {
        	App.verbose = false;
            new ParseMavenDependencyOutput().parse("D:\\Softwares\\march03\\demo05\\pom.xml",
                    WriterFactory.WriterType.HTML);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void parse(String pomFilePath) throws Throwable {
        parse(pomFilePath, WriterFactory.WriterType.HTML);
    }

    public void parse(String pomFilePath, WriterFactory.WriterType writerType) throws Throwable {
        //https://codebeautify.org/json-to-html-converter
        if (!CheckMavenInstallation.checkIfMavenIsInstalled()) {
            Logger.error("Maven is not installed.");
            throw new RuntimeException("Maven is not installed.");
        }

        if (!PomValidator.validatePom(new File(pomFilePath).getParent())) {
            Logger.error("Invalid POM : " + pomFilePath);
            throw new RuntimeException("Invalid POM : " + pomFilePath);
        }

        String mavenDependencyList = DependencyFinder.getDependencyList(pomFilePath);

        Logger.log(mavenDependencyList, App.verbose);
        Set<String> allVulnerabilities = new LinkedHashSet<>();
        List<POMDependencyObject> dependencies = ParseMavenDependencyOutput.getPOMDependencies(mavenDependencyList);
        dependencies.sort(Comparator.comparing(POMDependencyObject::getGroupID).thenComparing(POMDependencyObject::getArtifactID));
        
        ObjectMapper objectMapper = new ObjectMapper();

        for (POMDependencyObject pomDependency : dependencies) {
            VulnerabilityUpdater.updateVulnerabilities(pomDependency);
            if (pomDependency.getLatestVersion() == null) {
                //VersionUpdater.updateLatestVersion(pomDependency);
            }
            if (pomDependency.getVulnerabilities() != null && !pomDependency.getVulnerabilities().isEmpty()) {
                Logger.error(objectMapper.writeValueAsString(pomDependency));
                allVulnerabilities.addAll(pomDependency.getVulnerabilities());
            } else {
                Logger.log(objectMapper.writeValueAsString(pomDependency), App.verbose);
            }
            
            //added delay so the source server don't block you out.
            Thread.sleep(5000);
        }
        
        App.projectDetails = PomValidator.getProjectDetails(pomFilePath);

        writeOutput(pomFilePath, objectMapper, dependencies, allVulnerabilities, writerType);
    }

    private void writeOutput(String pomFilePath, ObjectMapper objectMapper, List<POMDependencyObject> dependencies,
                             Set<String> allVulnerabilities, WriterFactory.WriterType writerType) throws JsonProcessingException {
        //String json = objectMapper.writeValueAsString(dependencies);
        //Logger.log("\n\n" + json, App.verbose);
        Logger.log("\n\n" + objectMapper.writeValueAsString(allVulnerabilities), App.verbose);
        WriterParams writerParams = new WriterParams(Path.of(pomFilePath).getParent().toString(),
                "POMAnalyzerReport");
        WriterFactory.getWriter(writerType).write(dependencies, writerParams);
    }

    public static List<POMDependencyObject> getPOMDependencies(String a_strDependency) throws Throwable {
        List<POMDependencyObject> returnObject = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(a_strDependency));
        String line = reader.readLine();
        boolean isHeadFound = false;

        while (line != null) {
            if (line.toLowerCase().contains("The following files have been resolved".toLowerCase())) {
                isHeadFound = true;
                line = reader.readLine();
                continue;
            }
            if (isHeadFound && line.toLowerCase().trim().equalsIgnoreCase("[INFO]".toLowerCase())) {
                break;
            }
            if (isHeadFound) {
                line = line.substring("[INFO]    ".length());
                POMDependencyObject pomDependencyObject = getPOMDependencyObject(line);
                if (pomDependencyObject != null) {
                    returnObject.add(pomDependencyObject);
                }
                Logger.log(line, App.verbose);
            }
            line = reader.readLine();
        }
        reader.close();
        return returnObject;
    }

    private static POMDependencyObject getPOMDependencyObject(String a_strLine) {
        POMDependencyObject pomDependency = new POMDependencyObject();
        String[] strParts = a_strLine.split(":");
        pomDependency.setGroupID(strParts[0]);
        pomDependency.setArtifactID(strParts[1]);

        if (strParts.length == 5) {
            pomDependency.setCurrentVersion(strParts[3]);
        } else if (strParts.length == 6) {
            pomDependency.setCurrentVersion(strParts[4]);
        } else {
            Logger.error(a_strLine);
            return null;
        }
        pomDependency.setLatestVersion(pomDependency.getCurrentVersion());
        return pomDependency;
    }
}