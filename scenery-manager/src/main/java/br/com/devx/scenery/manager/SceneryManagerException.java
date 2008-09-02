package br.com.devx.scenery.manager;

public class SceneryManagerException extends Exception {
    public SceneryManagerException() {
    }

    public SceneryManagerException(String message) {
        super(message);
    }

    public SceneryManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SceneryManagerException(Throwable cause) {
        super(cause);
    }
}
