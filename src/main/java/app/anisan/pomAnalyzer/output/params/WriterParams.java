package app.anisan.pomAnalyzer.output.params;

import lombok.Getter;

/**
 * Created by sadhus on 22-03-2025.
 */
@Getter
public class WriterParams {
    private final String outputDir;
    private final String outputFileName;
    
    public WriterParams(String outputDir, String outputFileName) {
    	this.outputDir = outputDir;
        this.outputFileName = outputFileName;
    }

}
