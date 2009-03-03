package br.com.devx.scenery.web;

public class SitemeshException extends Exception {
    public SitemeshException(String message) {
        super(message);
    }

    public SitemeshException(String path, Throwable cause) {
        super("Error loading " + path + " file: " + cause.toString(), cause);
    }
}
