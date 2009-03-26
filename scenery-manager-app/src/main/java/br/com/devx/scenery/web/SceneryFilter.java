package br.com.devx.scenery.web;

import br.com.devx.scenery.sitemesh.SimpleSitemesh;
import br.com.devx.scenery.SceneryFileException;
import br.com.devx.scenery.TemplateAdapter;
import br.com.devx.scenery.CollectionsHelper;
import br.com.devx.scenery.parser.SceneryParserHelper;
import br.com.devx.scenery.manager.SceneryManagerException;
import br.com.devx.scenery.manager.SceneryManagerResult;
import br.com.devx.scenery.manager.SceneryManager;
import br.com.devx.scenery.manager.Scenery;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeConstants;
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
import java.util.*;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class SceneryFilter implements Filter {
    private static final Logger s_log = Logger.getLogger(SceneryFilter.class);

    public void init(FilterConfig config) throws ServletException {
        // Standard template management
        Properties properties = new Properties();
        properties.put("resource.loader", "file, targetApp");
        String webPath = config.getServletContext().getRealPath("/");
        properties.put("file.resource.loader.path", webPath + ", .");
        properties.put("targetApp.resource.loader.class", TargetResourceLoader.class.getName());

        properties.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
          "org.apache.velocity.runtime.log.Log4JLogChute" );
        properties.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity");


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

        if (request.getRequestURI().endsWith("config.do") || request.getRequestURI().endsWith("browse.do")) {
            chain.doFilter(req, resp);
        } else {
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
                handleTemplate(".", "errorReport.vm", "iso-8859-1", request, response, error(e), false);
            } catch (SceneryManagerException e) { // Wtf error
                throw new ServletException(e);
            }
        }
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

    /**
     * Renders the template content
     * @param template (relative) path to file
     * @param encoding which encoding to use
     * @param response where to write
     * @param templateAdapter messy data to export
     * @param adapt transform each occurence into string
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
                doHandleTemplate(targetPath, template, encoding, templateAdapter, adapt, out);
            } else {
                StringWriter sout = new StringWriter();
                templateAdapter.put("base", new URL(new URL(request.getRequestURL().toString()), "/" + request.getContextPath()).toString());
                // Write the decorator to a memory out
                doHandleTemplate(targetPath, template, encoding, templateAdapter, adapt, new PrintWriter(sout));
                // and decorate it
                sitemesh.decorate(sout.toString());

                templateAdapter.put("head", sitemesh.get("head"));
                templateAdapter.put("body", sitemesh.get("body"));
                doHandleTemplate(targetPath, sitemesh.getTemplate(), encoding, templateAdapter, adapt, out);
            }
        } catch (SitemeshException e) {
            new ServletException(e);
        }
    }

    private void doHandleTemplate(String targetPath, String template, String encoding, TemplateAdapter templateAdapter, boolean adapt, PrintWriter out) throws ServletException, IOException {
        if (template.endsWith(".vm")) {
            handleVelocityTemplate(targetPath, template, encoding, out, templateAdapter, adapt);
        } else {
            handleFreemarkerTemplate(targetPath, template, encoding, out, templateAdapter, adapt);
        }
    }

    private void handleFreemarkerTemplate(String targetPath, String template, String encoding,
                                          PrintWriter out, TemplateAdapter templateAdapter, boolean adapt)
            throws IOException, ServletException {
        // Create and adjust the configuration
        Configuration cfg = new Configuration();
        cfg.setEncoding(Locale.getDefault(), encoding);
        cfg.setDirectoryForTemplateLoading(new File(targetPath));
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);

        // You usually do these for many times in the application life-cycle:

        // Get or create a template
        Template temp = cfg.getTemplate(template);

        // Create a data-model
        Map<String, Object> root = new HashMap<String, Object>();
        for (Object property : templateAdapter.getProperties()) {
            String name = (String) property;
            root.put(name, adapt ? templateAdapter.adapt(name) : templateAdapter.get(name));
        }
        root.put("templateAdapter", templateAdapter);

        // Merge data-model with template
        try {
            temp.process(root, out);
        } catch (TemplateException e) {
            throw new ServletException(e);
        }
        out.flush();

    }

    private void handleVelocityTemplate(String targetPath, String template, String encoding,
                                        PrintWriter out, TemplateAdapter templateAdapter, boolean adapt)
            throws ServletException {
        Context ctx = new VelocityContext();
        for (Object property : templateAdapter.getProperties()) {
            String name = (String) property;
            ctx.put(name, adapt ? templateAdapter.adapt(name) : templateAdapter.get(name));
        }
        ctx.put("templateAdapter", templateAdapter);

        try {
            VelocityHelper.setupTools(targetPath, ctx);
            Reader reader;
            if (encoding == null) {
                reader = new FileReader(targetPath + "/" + template);
            } else {
                reader = new InputStreamReader(new FileInputStream(targetPath + "/" + template), encoding);
            }
            try {
                Velocity.evaluate(ctx, out, "templateAdapter", reader);
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
