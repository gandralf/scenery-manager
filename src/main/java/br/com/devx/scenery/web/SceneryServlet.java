package br.com.devx.scenery.web;

import br.com.devx.scenery.SceneryFileException;
import br.com.devx.scenery.manager.SceneryManagerException;
import br.com.devx.scenery.manager.SceneryManagerResult;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Scenery assembler servlet
 */
public class SceneryServlet extends HttpServlet {
    private static Log s_log = LogFactory.getLog(SceneryServlet.class);
    private String m_dataRoot = null;
    private TemplateHandler[] m_templateHandlers;
    private String m_sceneryXml;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        m_sceneryXml = ServletHelper.getSceneryXmlPath(config);
        m_dataRoot = ServletHelper.getDataRoot(config);
        m_templateHandlers = ServletHelper.getTemplateHandlers(config);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String baseURI = request.getRequestURI().substring(request.getContextPath().length());
            SceneryManagerResult sceneryManagerResult = ServletHelper.querySceneryManager(request, m_sceneryXml, m_dataRoot);

            processTemplate(baseURI, request, response, sceneryManagerResult);
        } catch (SceneryFileException e) {
            ServletHelper.notifyError(getServletContext(), request, response, e);
        } catch (SceneryManagerException e) {
            throw new ServletException(e);
        }
    }

    private void processTemplate(String baseURI, HttpServletRequest request, HttpServletResponse response,
                                 SceneryManagerResult sceneryManagerResult) throws IOException, ServletException {
        String template = sceneryManagerResult.getScenery().getTemplate();
        if (template == null) {
            template = "/defaultTemplate.jsp";
        }

        s_log.info("Using template: " + template);
        boolean handled = false;
        for (int i = 0; i < m_templateHandlers.length; i++) {
            TemplateHandler templateHandler = m_templateHandlers[i];
            if (templateHandler.canHandle(template)) {
                templateHandler.handle(template, sceneryManagerResult.getEncoding(), request, response,
                        sceneryManagerResult.getTemplateAdapter(),
                        sceneryManagerResult.isAdapt());
                handled = true;
                break;
            }
        }

        if (!handled) {
            throw new ServletException("Cannot handle template: " + template);
        }

        if (sceneryManagerResult.getScenerySet() != null) {
            SceneryPopupTemplateHandler popup = new SceneryPopupTemplateHandler(baseURI,
                    sceneryManagerResult.getScenerySet(), sceneryManagerResult.getScenery());
            popup.init(getServletConfig());
            popup.handle(template, sceneryManagerResult.getEncoding(), request, response,
                    sceneryManagerResult.getTemplateAdapter(), sceneryManagerResult.isAdapt());
        }
    }
}
