package br.com.devx.scenery.web.templates;

import br.com.devx.scenery.TemplateAdapter;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FreemarkerTemplateHandler implements CustomTemplateHandler {
    public boolean handle(String targetPath, String template, String encoding, PrintWriter out, TemplateAdapter templateAdapter, boolean adapt) throws IOException, TemplateHanlerException {
        if (template.endsWith(".ftl")) {
            handleFile(targetPath, template, encoding, out, templateAdapter, adapt);

            return true;
        }

        return false;
    }

    private void handleFile(String targetPath, String template, String encoding, PrintWriter out, TemplateAdapter templateAdapter, boolean adapt) throws IOException, TemplateHanlerException {
        Configuration cfg = getConfiguration(targetPath, encoding);
        Template temp = cfg.getTemplate(template);
        render(temp, templateAdapter, out, adapt);
    }

    public void handleContent(String targetPath, String content, String encoding, PrintWriter out, TemplateAdapter templateAdapter, boolean adapt) throws IOException, TemplateHanlerException {
        Configuration cfg = getConfiguration(targetPath, encoding);
        Template temp = new Template("content", new StringReader(content), cfg, encoding);
        render(temp, templateAdapter, out, adapt);
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

    private void render(Template template, TemplateAdapter values, PrintWriter out, boolean adapt) throws IOException, TemplateHanlerException {
        // Create a data-model
        Map<String, Object> root = new HashMap<String, Object>();
        for (Object property : values.getProperties()) {
            String name = (String) property;
            root.put(name, adapt ? values.adapt(name) : values.get(name));
        }
        root.put("templateAdapter", values);

        // Merge data-model with template
        try {
            template.process(root, out);
        } catch (TemplateException e) {
            throw new TemplateHanlerException(e);
        }
        out.flush();
    }
}
