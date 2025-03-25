
package app.anisan.pomAnalyzer.output;

import java.util.List;

import app.anisan.pomAnalyzer.POMDependencyObject;
import app.anisan.pomAnalyzer.log.Logger;
import app.anisan.pomAnalyzer.output.params.WriterParams;

public class ExcelWriter implements Writer {

    @Override
    public void write(List<POMDependencyObject> dependencies, WriterParams params) {
        Logger.log("Writing to Excel file");
    }
}
