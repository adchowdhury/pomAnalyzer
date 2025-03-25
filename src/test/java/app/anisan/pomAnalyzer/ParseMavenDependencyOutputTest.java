package app.anisan.pomAnalyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ParseMavenDependencyOutputTest {

    @Test
    void parseValidPomFileWithVerbose() throws Throwable {
        ParseMavenDependencyOutput parser = new ParseMavenDependencyOutput();
        parser.parse("src/test/resources/valid/pom.xml");
        // Add assertions to verify the expected behavior
    }

    @Test
    void parseValidPomFileWithoutVerbose() throws Throwable {
        ParseMavenDependencyOutput parser = new ParseMavenDependencyOutput();
        parser.parse("src/test/resources/valid/pom.xml");
        // Add assertions to verify the expected behavior
    }

    @Test
    void parseInvalidPomFile() {
        ParseMavenDependencyOutput parser = new ParseMavenDependencyOutput();
        assertThrows(Throwable.class, () -> parser.parse("src/test/resources/invalid/pom.xml"));
    }

    @Test
    void getPOMDependenciesWithValidData() throws Throwable {
        String dependencyData = "The following files have been resolved \n[INFO]    com.example:example-artifact:jar:1.0.0:compile";
        List<POMDependencyObject> dependencies = ParseMavenDependencyOutput.getPOMDependencies(dependencyData);
        assertEquals(1, dependencies.size());
        assertEquals("com.example", dependencies.get(0).getGroupID());
        assertEquals("example-artifact", dependencies.get(0).getArtifactID());
        assertEquals("1.0.0", dependencies.get(0).getCurrentVersion());
    }

    @Test
    void getPOMDependenciesWithInvalidData() throws Throwable {
        String dependencyData = "[INFO]    invalid-data";
        ParseMavenDependencyOutput.getPOMDependencies(dependencyData);
    }
}