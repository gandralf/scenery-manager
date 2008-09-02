package br.com.devx.scenery.web;

import br.com.devx.scenery.TemplateAdapter;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;

public class JspTemplateHandler implements TemplateHandler {
    private String m_webPath;

    public JspTemplateHandler() {
    }

    public void init(ServletConfig config) {
        m_webPath = config.getServletContext().getRealPath("/");
    }

    public boolean canHandle(String template) {
        return template.endsWith(".jsp");
    }

    public void handle(String template, String encoding, HttpServletRequest request, HttpServletResponse response,
                       TemplateAdapter templateAdapter, boolean adapt) throws IOException, ServletException {
        response.setContentType("text/html");

        if (template.charAt(0) != '/') {
            template = "/" + template; // JSP exige o "/" no inicio
        }

        if (templateAdapter != null) {
            Collection properties = templateAdapter.getProperties();
            Iterator i = properties.iterator();
            while (i.hasNext()) {
                String name = (String) i.next();
                request.setAttribute(name, adapt ? templateAdapter.adapt(name) : templateAdapter.get(name));
            }
        }

        File templateFile = new File(m_webPath + "/" + template);
        if (!templateFile.exists()) {
            throw new FileNotFoundException(templateFile.getCanonicalPath());
        }

        request.setAttribute("templateAdapter", templateAdapter);
        request.getRequestDispatcher(template).include(request, response);
    }
}
