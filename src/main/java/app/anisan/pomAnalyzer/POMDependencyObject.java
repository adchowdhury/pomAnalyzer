package app.anisan.pomAnalyzer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class POMDependencyObject {

    private String groupID = null;
    private String artifactID = null;
    private String currentVersion = null;
    private String latestVersion = null;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> vulnerabilities = new ArrayList<>();
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Vulnerability> vulnerabilitiesObject = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{groupID : ").append(groupID);
        sb.append(", artifactID : ").append(artifactID);
        sb.append(", currentVersion : ").append(currentVersion);
        sb.append(", latestVersion : ").append(latestVersion);
        if (vulnerabilities != null && !vulnerabilities.isEmpty()) {
            sb.append(", vulnerabilities : ").append(Arrays.toString(vulnerabilities.toArray()));
        }
        sb.append("}");
        return sb.toString();
    }
}