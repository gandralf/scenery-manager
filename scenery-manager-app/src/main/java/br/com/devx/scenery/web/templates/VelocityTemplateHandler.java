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
    public boolean handle(String targetPath, String template, String encoding, PrintWriter out, TemplateAdapter templateAdapter) throws IOException, TemplateHanlerException {
        if (template.endsWith(".vm")) {
            doHandle(targetPath, template, encoding, out, templateAdapter);
            return true;
        }
        return false;
    }

    private void doHandle(String targetPath, String template, String encoding, PrintWriter out, TemplateAdapter templateAdapter) throws IOException, TemplateHanlerException {
        Context ctx = new VelocityContext();
        for (Object property : templateAdapter.getProperties()) {
            String name = (String) property;
            ctx.put(name, templateAdapter.get(name));
        }
        ctx.put("templateAdapter", templateAdapter);

        try {
            VelocityHelper.setupTools(targetPath, ctx);
            Reader reader;
            String fileName = new File(targetPath + "/" + template).getCanonicalPath();
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
            throw new TemplateHanlerException(e);
        } catch (MethodInvocationException e) {
            throw new TemplateHanlerException(e);
        } catch (ResourceNotFoundException e) {
            throw new TemplateHanlerException(e);
        } catch (IOException e) {
            throw new TemplateHanlerException(e);
        }

    }
}
