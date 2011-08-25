package br.com.devx.scenery.web.templates;

import br.com.devx.scenery.TemplateAdapter;

import java.io.IOException;
import java.io.PrintWriter;

public interface CustomTemplateHandler {
    boolean handle(String targetPath, String template, String encoding, PrintWriter out, TemplateAdapter templateAdapter,
                   boolean adapt) throws IOException, TemplateHanlerException;
}
