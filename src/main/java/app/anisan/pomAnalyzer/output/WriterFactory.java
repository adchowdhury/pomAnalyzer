package app.anisan.pomAnalyzer.output;

public class WriterFactory {

    public enum WriterType {
        JSON, HTML, EXCEL
    }

    public static Writer getWriter(WriterType type) {
        if (type == null) {
            return null;
        }
        if (type == WriterType.JSON) {
            return new JSONWriter();
        } else if (type == WriterType.HTML) {
            return new HTMLWriter();
        } else if (type == WriterType.EXCEL) {
            return new ExcelWriter();
        }
        return null;
    }
}