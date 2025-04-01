package app.anisan.pomAnalyzer;

import app.anisan.pomAnalyzer.log.Logger;
import app.anisan.pomAnalyzer.output.WriterFactory;

public class App {

	public static boolean			verbose			= true;
	public static ProjectDetails	projectDetails	= null;
	
    public static void main(String[] args) {
        if (args.length < 1) {
            Logger.log("Usage: java -jar pomAnalyzer.jar <pathOfPomFile> [verbose] [writerType]", true);
            System.exit(1);
        }

        String pathOfPomFile = args[0];
        verbose = args.length > 1 && Boolean.parseBoolean(args[1]);
        WriterFactory.WriterType writerType = args.length > 2 ? WriterFactory.WriterType.valueOf(args[3]) : WriterFactory.WriterType.HTML;
        Logger.log("Parsing POM file: " + pathOfPomFile, true);
        Logger.log("Verbose: " + verbose, true);
        Logger.log("Writer type: " + writerType, true);

        try {
            new ParseMavenDependencyOutput().parse(pathOfPomFile, WriterFactory.WriterType.HTML);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}