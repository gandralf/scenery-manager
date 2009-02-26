package br.com.devx.scenery.web;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles a scenery or redirects a request.
 */
public class ConfigServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AppsConfig config = AppsConfig.getInstance();

        TargetApp app = config.getTargetApp();
        app.setPath(nvl(request.getParameter("path"), app.getPath()));
        app.setUrl(nvl(request.getParameter("url"), app.getUrl()));

        Context ctx = new VelocityContext();
        ctx.put("targetApp", config.getTargetApp());
        
        try {
            Velocity.mergeTemplate("config.vm", response.getCharacterEncoding(), ctx, response.getWriter());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String nvl(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
}
