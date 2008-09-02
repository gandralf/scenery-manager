package br.com.devx.scenery;

import br.com.devx.scenery.parser.ParseException;
import br.com.devx.scenery.manager.SceneryManagerException;

public class SceneryFileException extends SceneryManagerException {
    private String m_fileName;
    private int m_line;
    private int m_beginColumn;
    private int m_endColumn;

    public SceneryFileException(String fileName, ParseException e) {
        this(fileName, e.currentToken != null ? e.currentToken.next.beginLine : 0,
                e.currentToken != null ? e.currentToken.next.beginColumn : 0,
                e.currentToken != null ? e.currentToken.next.endColumn : 0,
                e.getMessage());
    }

    public SceneryFileException(String fileName, int line, int beginColumn, int endColumn, String message) {
        super(message);
        m_fileName = fileName;
        m_line = line;
        m_beginColumn = beginColumn;
        m_endColumn = endColumn;
    }

    public String getMessage() {
        return m_fileName + ": " + super.getMessage();
    }

    public String getFileName() {
        return m_fileName;
    }

    public int getLine() {
        return m_line;
    }

    public int getBeginColumn() {
        return m_beginColumn;
    }

    public int getEndColumn() {
        return m_endColumn;
    }
}
