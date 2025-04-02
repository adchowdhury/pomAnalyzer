package app.anisan.pomAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
            Logger.error("Maven is not installed.", true);
            throw new RuntimeException("Maven is not installed.");
        }
        if(App.verbose == false) {
        	System.out.print("\rValidating POM file");
        }
        if (!PomValidator.validatePom(new File(pomFilePath).getParent())) {
            Logger.error("Invalid POM : " + pomFilePath, true);
            throw new RuntimeException("Invalid POM : " + pomFilePath);
        }
        if(App.verbose == false) {
        	System.out.print("\rGenerating Dependency List");
        }
        String mavenDependencyList = DependencyFinder.getDependencyList(pomFilePath);

        Logger.log(mavenDependencyList, App.verbose);
        //Set<Vulnerability> allVulnerabilities = new LinkedHashSet<>();
        if(App.verbose == false) {
        	System.out.print("\rParsing Dependency List");
        }
        List<POMDependencyObject> dependencies = ParseMavenDependencyOutput.getPOMDependencies(mavenDependencyList);
        dependencies.sort(Comparator.comparing(POMDependencyObject::getGroupID).thenComparing(POMDependencyObject::getArtifactID));
        ObjectMapper objectMapper = new ObjectMapper();
        int stepCounter = 0;
        double precentageCompletion = 0.0;
        if(App.verbose == false) {
        	System.out.print("\rStarting scanning, total : " + dependencies.size() + " dependencies");
        }
        for (POMDependencyObject pomDependency : dependencies) {
        	if(App.verbose == false) {
        		System.out.print("\r[" + String.format(" %.2f%% ", ((double) stepCounter / (dependencies.size() * 2) * 100)) +"] Scanning vulnerabilities > " + pomDependency.getGroupID() + "." + pomDependency.getArtifactID()+ " ".repeat(100));
        	}
        	try {
        		VulnerabilityUpdater.updateVulnerabilities(pomDependency);
                
                if (pomDependency.getVulnerabilities() != null && !pomDependency.getVulnerabilities().isEmpty()) {
                    Logger.error(objectMapper.writeValueAsString(pomDependency), App.verbose);
                    //allVulnerabilities.addAll(pomDependency.getVulnerabilitiesObject());
                } else {
                    Logger.log(objectMapper.writeValueAsString(pomDependency), App.verbose);
                }
			} catch (Throwable a_th) {
				Logger.error("VulnerabilityUpdater Error while getting " + pomDependency, a_th, App.verbose);
			}
        	stepCounter++;
        	if(App.verbose == false) {
        		System.out.print("\r[" + String.format(" %.2f%% ", ((double) stepCounter / (dependencies.size() * 2) * 100)) +"] Checking Latest Version > " + pomDependency.getGroupID() + "." + pomDependency.getArtifactID()+ " ".repeat(100));
        	}
        	try {
            	VersionUpdater.updateLatestVersion(pomDependency);
			} catch (Throwable a_th) {
				Logger.error("VersionUpdater Error while getting " + pomDependency, a_th, App.verbose);
			}
        	
        	
        	stepCounter++;
            //added delay so the source server don't block you out.
            Thread.sleep(2000);
        }
        if(App.verbose == false) {
        	System.out.print("\r Scanning completed."+ " ".repeat(100));
        }
        App.projectDetails = PomValidator.getProjectDetails(pomFilePath);
        if(App.verbose == false) {
        	System.out.print("\r Preparing report file."+ " ".repeat(100));
        }
        writeOutput(pomFilePath, objectMapper, dependencies, writerType);
        if(App.verbose == false) {
        	System.out.println("\r Report generated."+ " ".repeat(100));
        }
    }

    private void writeOutput(String pomFilePath, ObjectMapper objectMapper, List<POMDependencyObject> dependencies,
                             WriterFactory.WriterType writerType) throws JsonProcessingException {
        //String json = objectMapper.writeValueAsString(dependencies);
        //Logger.log("\n\n" + json, App.verbose);
        //Logger.log("\n\n" + objectMapper.writeValueAsString(allVulnerabilities), App.verbose);
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
            Logger.error(a_strLine, App.verbose);
            return null;
        }
        pomDependency.setLatestVersion(pomDependency.getCurrentVersion());
        return pomDependency;
    }
}