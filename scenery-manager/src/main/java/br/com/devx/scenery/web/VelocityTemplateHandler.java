package br.com.devx.scenery.web;

import br.com.devx.scenery.TemplateAdapter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

public class VelocityTemplateHandler implements TemplateHandler {
    private String m_webPath;

    public VelocityTemplateHandler() throws IOException {

    }

    public void init(ServletConfig config) {
        // Standard template management
        Properties properties = new Properties();
        properties.put("resource.loader", "file");
        m_webPath = config.getServletContext().getRealPath("/");
        properties.put("file.resource.loader.path", m_webPath + ", .");

        try {
            Velocity.init(properties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e.toString());
        }

    }

    public boolean canHandle(String template) {
        return template.endsWith(".vm");
    }

    public void handle(String template, String encoding, HttpServletRequest request, HttpServletResponse response,
                       TemplateAdapter templateAdapter, boolean adapt) throws IOException, ServletException {
        Context ctx = new VelocityContext();

        Collection properties = templateAdapter.getProperties();
        Iterator i = properties.iterator();
        while (i.hasNext()) {
            String name = (String) i.next();
            ctx.put(name, adapt ? templateAdapter.adapt(name) : templateAdapter.get(name));
        }

        ctx.put("templateAdapter", templateAdapter);

        response.setContentType("text/html");
        try {
            Reader reader;
            if (encoding == null) {
                reader = new FileReader(m_webPath + "/" + template);
            } else {
                reader = new InputStreamReader(new FileInputStream(m_webPath + "/" + template), encoding);
            }
            try {
                Velocity.evaluate(ctx, response.getWriter(), "templateAdapter", reader);
            } finally {
                reader.close();
            }
        } catch (ParseErrorException e) {
            throw new ServletException(e);
        } catch (MethodInvocationException e) {
            throw new ServletException(e);
        } catch (ResourceNotFoundException e) {
            throw new ServletException(e);
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }
}
