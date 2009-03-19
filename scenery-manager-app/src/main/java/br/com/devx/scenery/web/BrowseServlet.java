package br.com.devx.scenery.web;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.File;

public class BrowseServlet extends HttpServlet {
    private static final Logger s_log = Logger.getLogger(BrowseServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getParameter("path");
        if (path == null) {
            path = ".";
        }

        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory() || !dir.canRead()) {
            s_log.warn("Invalid directory: " + dir.getCanonicalPath() + ". Using current dir");
            dir = new File("."); // Recover time
        }

        BrowseDirectory browseDirectory = new BrowseDirectory(dir, true);

        Context ctx = new VelocityContext();
        ctx.put("dir", browseDirectory);

        try {
            Velocity.mergeTemplate("browse.vm", response.getCharacterEncoding(), ctx, response.getWriter());
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }
}
