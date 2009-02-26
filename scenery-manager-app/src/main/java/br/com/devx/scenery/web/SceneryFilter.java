package br.com.devx.scenery.web;

import br.com.devx.scenery.SceneryFileException;
import br.com.devx.scenery.TemplateAdapter;
import br.com.devx.scenery.manager.SceneryManagerException;
import br.com.devx.scenery.manager.SceneryManagerResult;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SceneryFilter implements Filter {
    private static final Logger s_log = Logger.getLogger(SceneryFilter.class);

    public void init(FilterConfig config) throws ServletException {
        // Standard template management
        Properties properties = new Properties();
        properties.put("resource.loader", "file");
        String webPath = config.getServletContext().getRealPath("/");
        properties.put("file.resource.loader.path", webPath + ", .");

        try {
            Velocity.init(properties);
        } catch (Exception e) {
            throw (IllegalStateException) new IllegalStateException(e.toString()).initCause(e);
        }
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (request.getRequestURI().endsWith("config.do")) {
            chain.doFilter(req, resp);
        } else {
            TargetApp targetApp = AppsConfig.getInstance().getTargetApp();
            try {
                SceneryManagerResult smr = ServletHelper.querySceneryManager(request, targetApp.getPath() + "/WEB-INF/scenery.xml",
                        targetApp.getPath());
                String template = smr.getScenery().getTemplate();
                handleTemplate(targetApp.getPath(), template, smr.getEncoding(), response, smr.getTemplateAdapter(),
                        smr.isAdapt());
            } catch (IllegalArgumentException e) {
                // todo damn ugly
                if (!e.getMessage().contains("Scenery not found")) {
                    throw e;
                }
                redirect(targetApp, request, response);
            } catch (SceneryFileException e) { // Scn file error
                handleTemplate(".", "errorReport.vm", "iso-8859-1", response, error(e), false);
            } catch (SceneryManagerException e) { // Wtf error
                throw new ServletException(e);
            }
        }
    }

    private void redirect(TargetApp app, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        URL url = new URL(app.getUrl() + request.getRequestURI());
        s_log.debug("Redirecting to " + url);
        URLConnection urlConnection = url.openConnection();
        Map<String,List<String>> fields = urlConnection.getHeaderFields();
        for (String key: fields.keySet()) {
            StringBuffer values = new StringBuffer();
            boolean comma = false;
            for(String value: fields.get(key)) {
                if (comma) {
                    values.append(", ");
                }
                values.append(value);
                comma = true;
            }

            if (key != null) {
                response.setHeader(key, values.toString());
            } else {
                response.setStatus(Integer.parseInt(values.toString().split(" ")[1]));
            }
        }

        InputStream in = urlConnection.getInputStream();
        ServletOutputStream out = response.getOutputStream();
        byte[] buff = new byte[1024];
        int len;
        while((len = in.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
    }

    /**
     * Renders the template content
     * @param template (relative) path to file
     * @param encoding which encoding to use
     * @param response where to write
     * @param templateAdapter messy data to export
     * @param adapt transform each occurence into string
     */
    public void handleTemplate(String targetPath, String template, String encoding, HttpServletResponse response,
                       TemplateAdapter templateAdapter, boolean adapt) throws IOException, ServletException {
        Context ctx = new VelocityContext();
        for (Object property : templateAdapter.getProperties()) {
            String name = (String) property;
            ctx.put(name, adapt ? templateAdapter.adapt(name) : templateAdapter.get(name));
        }
        ctx.put("templateAdapter", templateAdapter);

        // todo rewriteble default
        response.setContentType("text/html");
        try {
            Reader reader;
            if (encoding == null) {
                reader = new FileReader(targetPath + "/" + template);
            } else {
                reader = new InputStreamReader(new FileInputStream(targetPath + "/" + template), encoding);
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

    private TemplateAdapter error(SceneryFileException e) throws IOException, ServletException {
        s_log.info("Error on " + e.getFileName());
        FileReader fileReader = new FileReader(e.getFileName());
        try {
            TemplateAdapter result = new TemplateAdapter();
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            ArrayList<String> lines = new ArrayList<String>();
            int currentLine = 1;
            int errorLine = e.getLine();
            int errorBeginColumn = e.getBeginColumn();
            int errorEndColumn = e.getEndColumn();

            while((line = reader.readLine()) != null) {
                if (currentLine == errorLine) {
                    String gambi = "<span style=\"color:red\">";
                    line = new StringBuffer(line)
                            .insert(errorBeginColumn - 1, gambi)
                            .insert(errorEndColumn - 1 + gambi.length(), "</span>")
                            .toString();
                }
                lines.add(line);
                currentLine++;
            }

            result.put("message", e.getMessage());
            result.put("errorLine", new Integer(errorLine));
            result.put("errorBeginColumn", new Integer(e.getBeginColumn()));
            result.put("errorEndColumn", new Integer(e.getEndColumn()));
            result.put("lines", lines);

            return result;
        } finally {
            fileReader.close();
        }
    }
}
