package app.anisan.pomAnalyzer.output;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.anisan.pomAnalyzer.App;
import app.anisan.pomAnalyzer.POMDependencyObject;
import app.anisan.pomAnalyzer.Vulnerability;
import app.anisan.pomAnalyzer.log.Logger;
import app.anisan.pomAnalyzer.output.params.WriterParams;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class HTMLWriter implements Writer {

	public void write(List<POMDependencyObject> dependencies, WriterParams params) {
		try {
			Set<Vulnerability>	allVulnerabilities	= new LinkedHashSet<>();
			int					affectedLIbraries	= 0;
			for (POMDependencyObject pomDependency : dependencies) {

				if (pomDependency.getVulnerabilitiesObject() != null
						&& !pomDependency.getVulnerabilitiesObject().isEmpty()) {
					allVulnerabilities.addAll(pomDependency.getVulnerabilitiesObject());
					affectedLIbraries++;
				}
			}

			List<Vulnerability> sortedList = new ArrayList<>(allVulnerabilities);

			sortedList.sort(Comparator.comparing(Vulnerability::getCvssScore).reversed());

			Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

			cfg.setClassLoaderForTemplateLoading(HTMLWriter.class.getClassLoader(), "templates");

			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

			// Load the template
			Template			template	= cfg.getTemplate("htmlReport.ftl");

			Map<String, Object>	data		= new HashMap<>();
			data.put("project", App.projectDetails);
			data.put("totalLibraries", dependencies.size());
			data.put("uniqueVulnerabilityCount", allVulnerabilities.size());
			data.put("affectedLIbraries", affectedLIbraries);
			data.put("dependencies", dependencies);
			data.put("allVulnerabilities", sortedList);

			StringWriter out = new StringWriter();
			template.process(data, out);

			// Output generated string
			String	result		= out.toString();

			File	outputFile	= writeFile(params, result);
			// Open the HTML file using the default browser
			Logger.log("Opening into the default browser...:" + outputFile.getAbsolutePath(), App.verbose);
			Desktop.getDesktop().browse(outputFile.toURI());
		} catch (Throwable e) {
			Logger.error("Error while writing HTML", e, App.verbose);
		}
	}

	private File writeFile(WriterParams params, String htmlContent) throws IOException {
		// Write HTML content to file in temp folder
		String				tempDir		= System.getProperty("java.io.tmpdir");
		DateTimeFormatter	formatter	= DateTimeFormatter.ofPattern("yyyyMMddHHmm");								// yyyyMMddHHmmss
		LocalDateTime		now			= LocalDateTime.now();
		String				timestamp	= now.format(formatter);
		File				outputFile	= new File(params.getOutputDir() + File.separator
				+ App.projectDetails.getGroupId().replaceAll("\\.", "-") + "-"
				+ App.projectDetails.getArtifactId().replaceAll("\\.", "-") + "_analysis_" + timestamp + ".html");
		FileWriter			fileWriter	= new FileWriter(outputFile);
		fileWriter.write(htmlContent);
		fileWriter.close();
		Logger.log("HTML file created successfully!", App.verbose);
		return outputFile;
	}
}