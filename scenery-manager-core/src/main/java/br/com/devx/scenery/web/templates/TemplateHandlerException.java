package br.com.devx.scenery.web.templates;

public class TemplateHandlerException extends Exception {
    private String fileName;
    private int lineNumber;
    private int columnNumber;
    
    public TemplateHandlerException(Exception e) {
        super(e);
        lineNumber = -1;
        columnNumber = -1;
        fileName = null;
    }
    
    public TemplateHandlerException(String fileName, int lineNumber, int columnNumber, Exception cause) {
        super(cause);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public int getColumnNumber() {
        return columnNumber;
    }
}
