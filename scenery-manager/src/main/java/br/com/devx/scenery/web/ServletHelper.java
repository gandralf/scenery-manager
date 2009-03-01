package br.com.devx.scenery.web;

import br.com.devx.scenery.*;
import br.com.devx.scenery.manager.SceneryManager;
import br.com.devx.scenery.manager.SceneryManagerException;
import br.com.devx.scenery.manager.SceneryManagerResult;
import br.com.devx.scenery.manager.Scenery;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * User: agandra
 * Date: 13/05/2003
 * Time: 19:39:30
 */
public class ServletHelper {
    private static Log s_log = LogFactory.getLog(ServletHelper.class);

    public static String getSceneryXmlPath(ServletConfig config) {
        String result = config.getInitParameter("scenery.xml");
        if (result == null) {
            result = config.getServletContext().getRealPath("/WEB-INF/scenery.xml");
        } else {
            result = config.getServletContext().getRealPath("/" + result);
        }

        return result;
    }

    public static String getDataRoot(ServletConfig config) {
        String result = config.getInitParameter("dataRoot");
        if (result == null) {
            result = config.getServletContext().getRealPath("/");
        } else {
            result = config.getServletContext().getRealPath("/" + result);
        }

        return result;
    }

    public static TemplateHandler[] getTemplateHandlers(ServletConfig config) throws ServletException {
        Collection<TemplateHandler> templateHandlers = new ArrayList<TemplateHandler>();
        String initParameter = config.getInitParameter("templateHandlers");
        if (initParameter == null) {
            initParameter = "br.com.devx.scenery.web.JspTemplateHandler"; // Default template handler
        }

        String[] templateHandlerClasses = initParameter.split(",\\s*");
        try {
            for (String templateHandlerClass : templateHandlerClasses) {
                TemplateHandler templateHandler = (TemplateHandler) Class.forName(templateHandlerClass).newInstance();
                templateHandler.init(config);
                templateHandlers.add(templateHandler);
            }
        } catch (InstantiationException e) {
            throw new ServletException(e);
        } catch (IllegalAccessException e) {
            throw new ServletException(e);
        } catch (ClassNotFoundException e) {
            throw new ServletException(e);
        }
        NullTemplateHandler nullTemplateHandler = new NullTemplateHandler();
        nullTemplateHandler.init(config);
        templateHandlers.add(nullTemplateHandler);

        return templateHandlers.toArray(new TemplateHandler[]{});
    }

    public static SceneryManagerResult querySceneryManager(HttpServletRequest request, String sceneryXml, String dataRoot) throws SceneryManagerException {
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

    public static void notifyError(ServletContext context, HttpServletRequest request, HttpServletResponse response, SceneryFileException e)
            throws IOException, ServletException {
        s_log.info("Error on " + e.getFileName());
        FileReader fileReader = new FileReader(e.getFileName());
        try {
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            ArrayList<String> lines = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            request.setAttribute("message", e.getMessage());
            request.setAttribute("errorLine", e.getLine());
            request.setAttribute("errorBeginColumn", e.getBeginColumn());
            request.setAttribute("errorEndColumn", e.getEndColumn());
            request.setAttribute("lines", lines);

            context.getRequestDispatcher("/errorReport.jsp").include(request, response);
        } finally {
            fileReader.close();
        }
    }
}
