package app.anisan.pomAnalyzer.output;

import java.util.List;

import app.anisan.pomAnalyzer.POMDependencyObject;
import app.anisan.pomAnalyzer.output.params.WriterParams;

public interface Writer {

    void write(List<POMDependencyObject> dependencies, WriterParams params);

}
