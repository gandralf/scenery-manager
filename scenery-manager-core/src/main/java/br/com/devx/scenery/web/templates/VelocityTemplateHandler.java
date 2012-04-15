package br.com.devx.scenery.web.templates;

import br.com.devx.scenery.TemplateAdapter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.*;

public class VelocityTemplateHandler implements CustomTemplateHandler {
    public boolean handle(String targetPath, String template, String encoding, PrintWriter out, TemplateAdapter templateAdapter) throws IOException, TemplateHandlerException {
        if (template.endsWith(".vm")) {
            doHandle(targetPath, template, encoding, out, templateAdapter);
            return true;
        }
        return false;
    }

    private void doHandle(String targetPath, String template, String encoding, PrintWriter out, TemplateAdapter templateAdapter) throws IOException, TemplateHandlerException {
        Context ctx = new VelocityContext();
        for (Object property : templateAdapter.getProperties()) {
            String name = (String) property;
            ctx.put(name, templateAdapter.get(name));
        }
        ctx.put("templateAdapter", templateAdapter);

        String fileName = new File(targetPath + "/" + template).getCanonicalPath();
        try {
            VelocityHelper.setupTools(targetPath, ctx);
            Reader reader;
            if (encoding == null) {
                reader = new FileReader(fileName);
            } else {
                reader = new InputStreamReader(new FileInputStream(fileName), encoding);
            }
            try {
                Velocity.evaluate(ctx, out, "templateAdapter", reader);
            } finally {
                reader.close();
            }
        } catch (ParseErrorException e) {
            throw new TemplateHandlerException(fileName, e.getLineNumber(), e.getColumnNumber(), e);
        } catch (MethodInvocationException e) {
            throw new TemplateHandlerException(fileName, e.getLineNumber(), e.getColumnNumber(), e);
        } catch (ResourceNotFoundException e) {
            throw new TemplateHandlerException(e);
        } catch (IOException e) {
            throw new TemplateHandlerException(e);
        }

    }
}
