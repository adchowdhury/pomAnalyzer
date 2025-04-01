package app.anisan.pomAnalyzer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDetails {

	private String groupId;
	private String artifactId;
	private String version;
	private String name;
	private String description;
	
}
