
package app.anisan.pomAnalyzer.log;

/**
 * Created by sadhus on 22-03-2025.
 */
public class Logger {

    public static void log(String message) {
        System.out.println(message);
    }

    public static void log(String message, boolean verbose) {
        if (verbose) {
            log(message);
        }
    }

    public static void error(String message) {
        System.err.println(message);
    }

    public static void error(int message) {
        error(message + "");
    }

    public static void error(String message, boolean verbose) {
        if (verbose) {
            error(message);
        }
    }

    public static void error(String message, Throwable e) {
        error(message);
        e.printStackTrace();
    }

    public static void error(String message, Throwable e, boolean verbose) {
        if (verbose) {
            error(message, e);
        }
    }
}
