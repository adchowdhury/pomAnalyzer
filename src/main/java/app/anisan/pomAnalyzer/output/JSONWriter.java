
package app.anisan.pomAnalyzer.output;

import app.anisan.pomAnalyzer.App;
import app.anisan.pomAnalyzer.POMDependencyObject;
import app.anisan.pomAnalyzer.log.Logger;
import app.anisan.pomAnalyzer.output.params.WriterParams;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by sadhus on 22-03-2025.
 */
public class JSONWriter implements Writer {

    @Override
    public void write(List<POMDependencyObject> dependencies, WriterParams params) {
    	ObjectMapper objectMapper = new ObjectMapper();
        // Write the jsonContent to a JSON file in pretty format
        String filePath = params.getOutputDir() + File.separator + params.getOutputFileName() + ".json";
        try (FileWriter fileWriter = new FileWriter(filePath)) {
        	Logger.log("Writing JSON :" + objectMapper.writeValueAsString(dependencies), App.verbose);
            //Object json = objectMapper.readValue(jsonContent, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.writeValueAsString(dependencies));
            fileWriter.write(prettyJson);
            Logger.log("JSON file created successfully at: " + filePath, App.verbose);
        } catch (IOException e) {
            Logger.error("Error while writing JSON", e, App.verbose);
        }
    }
}
