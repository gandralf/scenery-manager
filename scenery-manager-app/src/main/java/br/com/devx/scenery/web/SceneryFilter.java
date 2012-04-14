package br.com.devx.scenery.web;

import br.com.devx.scenery.CollectionsHelper;
import br.com.devx.scenery.SceneryFileException;
import br.com.devx.scenery.TemplateAdapter;
import br.com.devx.scenery.manager.Scenery;
import br.com.devx.scenery.manager.SceneryManager;
import br.com.devx.scenery.manager.SceneryManagerException;
import br.com.devx.scenery.manager.SceneryManagerResult;
import br.com.devx.scenery.parser.SceneryParserHelper;
import br.com.devx.scenery.sitemesh.SimpleSitemesh;
import br.com.devx.scenery.web.templates.CustomTemplateHandler;
import br.com.devx.scenery.web.templates.TemplateHandlerException;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class SceneryFilter implements Filter {
    private static final Logger s_log = Logger.getLogger(SceneryFilter.class);
    private CustomTemplateHandler[] templateHandlers;

    public void init(FilterConfig config) throws ServletException {
        // Standard template management
        Properties properties = new Properties();
        properties.put("resource.loader", "targetApp");
        properties.put("targetApp.resource.loader.class", TargetResourceLoader.class.getName());

        properties.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
          "org.apache.velocity.runtime.log.Log4JLogChute" );
        properties.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity");


        try {
            Velocity.init(properties);
        } catch (Exception e) {
            throw (IllegalStateException) new IllegalStateException(e.toString()).initCause(e);
        }

        List<CustomTemplateHandler> handlers = AppsConfig.getInstance().getTargetApp().getTemplateHandlers();
        templateHandlers = handlers.toArray(new CustomTemplateHandler[handlers.size()]);
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        TargetApp targetApp = AppsConfig.getInstance().getTargetApp();
        try {
            if (targetApp.hasSceneryXml()) {
                SceneryManagerResult smr = querySceneryManager(request,
                        targetApp.getPath() + "/WEB-INF/scenery.xml",
                        targetApp.getPath(),
                        targetApp.getClassLoader());
                String template = smr.getScenery().getTemplate();
                handleTemplate(targetApp.getPath(), template, smr.getEncoding(), request, response,
                        smr.getTemplateAdapter(), smr.isAdapt());
            } else {
                s_log.warn("Target app doesn't have a scenery.xml file");
                redirect(targetApp, request, response);
            }
        } catch (IllegalArgumentException e) {
            // todo damn ugly
            if (!e.getMessage().contains("Scenery not found")) {
                 throw e;
            }
            redirect(targetApp, request, response);
        } catch (SceneryFileException e) { // Scn file error
            showSyntaxError(response, e);
        } catch (SceneryManagerException e) { // Wtf error
            throw new ServletException(e);
        }
    }

    private void showSyntaxError(HttpServletResponse response, SceneryFileException e) throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Context ctx = new VelocityContext();
        error(ctx, e);
        Velocity.evaluate(ctx, out, "templateAdapter",
                new InputStreamReader(getClass().getResourceAsStream("/errorReport.vm")));
    }

    public static SceneryManagerResult querySceneryManager(HttpServletRequest request, String sceneryXml,
                                                           String dataRoot, ClassLoader classLoader)
            throws SceneryManagerException {
        SceneryParserHelper.setClassLoader(classLoader);

        List<String> sceneryDataList = CollectionsHelper.makeList(request.getParameterValues("sceneryData"));
        String sceneryTemplate = request.getParameter("sceneryTemplate");
        String adaptParam = request.getParameter("adapt");
        boolean adaptAux = !("false".equals(adaptParam) || "no".equals(adaptParam) || "0".equals(adaptParam));
        Boolean adapt = adaptParam != null ? adaptAux : null;
        String baseURI = request.getRequestURI().substring(request.getContextPath().length());

        SceneryManager sceneryManager = new SceneryManager(sceneryXml, dataRoot);
        SceneryManagerResult sceneryManagerResult = sceneryManager.query(baseURI, request.getParameterMap(), sceneryDataList, sceneryTemplate, adapt);
        if (s_log.isInfoEnabled()) {
            Scenery scenery = sceneryManagerResult.getScenery();
            s_log.info("scenery found: " + scenery.getTemplate() + "/" + scenery.getDataList() +
                    ", test=" + scenery.getTest());
        }

        return sceneryManagerResult;
    }

    private void redirect(TargetApp app, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        URL url = buildUrl(app, request);

        s_log.debug("Redirecting to " + url);
        URLConnection urlConnection = url.openConnection();
        Map<String,List<String>> fields = urlConnection.getHeaderFields();
        for (String key: fields.keySet()) {
            StringBuilder values = new StringBuilder();
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
        try {
            ServletOutputStream out = response.getOutputStream();
            byte[] buff = new byte[1024];
            int len;
            while((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
        } finally {
            in.close();
        }
    }

    private URL buildUrl(TargetApp app, HttpServletRequest request) throws MalformedURLException {
        URL url = new URL(app.getUrl() + request.getRequestURI());
        if (url.getProtocol().equals("file")) {
            File file = new File(url.getPath());
            if (file.exists() && file.isDirectory()) {
                File indexHtml = new File(file, "index.html");
                if (indexHtml.exists()) {
                    s_log.debug("index.html found");
                    url = indexHtml.toURI().toURL();
                }
            }
        }
        return url;
    }

    /**
     * Renders the template content
     * @param targetPath base template path
     * @param template (relative) path to file
     * @param encoding which encoding to use
     * @param request web request
     * @param response where to write
     * @param templateAdapter messy data to export
     * @param adapt transform each occurence into string
     * @throws java.io.IOException on error reading config/template/data files
     * @throws javax.servlet.ServletException probably on sitemesh issues
     */
    public void handleTemplate(String targetPath, String template, String encoding,
                               HttpServletRequest request, HttpServletResponse response,
                       TemplateAdapter templateAdapter, boolean adapt) throws IOException, ServletException {
        try {
            // todo rewriteble default
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            Sitemesh sitemesh = new SimpleSitemesh(targetPath, request.getRequestURI());
            if (!sitemesh.isActive()) {
                doHandleTemplate(targetPath, template, encoding, templateAdapter, out);
            } else {
                StringWriter sout = new StringWriter();
                templateAdapter.put("base", new URL(new URL(request.getRequestURL().toString()), "/" + request.getContextPath()).toString());
                // Write the decorator to a memory out
                doHandleTemplate(targetPath, template, encoding, templateAdapter, new PrintWriter(sout));
                // and decorate it
                sitemesh.decorate(sout.toString());

                templateAdapter.put("head", sitemesh.get("head"));
                templateAdapter.put("body", sitemesh.get("body"));
                doHandleTemplate(targetPath, sitemesh.getTemplate(), encoding, templateAdapter, out);
            }
        } catch (SitemeshException e) {
            throw new ServletException(e);
        }
    }

    private void doHandleTemplate(String targetPath, String template, String encoding, TemplateAdapter templateAdapter, PrintWriter out) throws ServletException, IOException {
        for (CustomTemplateHandler handler: templateHandlers) {
            try {
                if (handler.handle(targetPath, template, encoding, out, templateAdapter)) {
                    break;
                }
            } catch (TemplateHandlerException e) {
                s_log.error(e);
            }
        }
    }

    private void error(Context result, SceneryFileException e) throws IOException, ServletException {
        s_log.info("Error on " + e.getFileName());
        FileReader fileReader = new FileReader(e.getFileName());
        try {
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
                            .insert(errorEndColumn + gambi.length(), "</span>")
                            .toString();
                }
                lines.add(line);
                currentLine++;
            }

            result.put("message", e.getMessage());
            result.put("errorLine", errorLine);
            result.put("errorBeginColumn", e.getBeginColumn());
            result.put("errorEndColumn", e.getEndColumn());
            result.put("lines", lines);
        } finally {
            fileReader.close();
        }
    }
}
