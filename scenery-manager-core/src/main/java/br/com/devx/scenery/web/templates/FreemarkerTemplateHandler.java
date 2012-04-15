package br.com.devx.scenery.web.templates;

import br.com.devx.scenery.TemplateAdapter;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.core.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FreemarkerTemplateHandler implements CustomTemplateHandler {
    public boolean handle(String targetPath, String template, String encoding, PrintWriter out, TemplateAdapter templateAdapter) throws IOException, TemplateHandlerException {
        if (template.endsWith(".ftl")) {
            handleFile(targetPath, template, encoding, out, templateAdapter);

            return true;
        }

        return false;
    }

    private void handleFile(String targetPath, String template, String encoding, PrintWriter out, TemplateAdapter templateAdapter) throws IOException, TemplateHandlerException {
        try {
            Configuration cfg = getConfiguration(targetPath, encoding);
            Template temp = cfg.getTemplate(template);
            render(temp, templateAdapter, out);
        } catch (ParseException e) {
            throw new TemplateHandlerException(targetPath + "/" + template, e.getLineNumber(), e.getColumnNumber(), e);
        }
    }

    public void handleContent(String targetPath, String templateName, String content, String encoding,
                              PrintWriter out, TemplateAdapter templateAdapter)
            throws IOException, TemplateHandlerException {
        Configuration cfg = getConfiguration(targetPath, encoding);
        Template temp = new Template(templateName, new StringReader(content), cfg, encoding);
        render(temp, templateAdapter, out);
    }

    private Configuration getConfiguration(String targetPath, String encoding) throws IOException {
        // Create and adjust the configuration
        Configuration cfg = new Configuration();
        cfg.setEncoding(Locale.getDefault(), encoding);
        cfg.setDirectoryForTemplateLoading(new File(targetPath));
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
        return cfg;
    }

    private void render(Template template, TemplateAdapter values, PrintWriter out) throws IOException, TemplateHandlerException {
        // Create a data-model
        Map<String, Object> root = new HashMap<String, Object>();
        for (Object property : values.getProperties()) {
            String name = (String) property;
            root.put(name, values.get(name));
        }
        root.put("templateAdapter", values);

        // Merge data-model with template
        try {
            template.process(root, out);
        } catch (TemplateException e) {
            throw new TemplateHandlerException(e);
        }
        out.flush();
    }
}
