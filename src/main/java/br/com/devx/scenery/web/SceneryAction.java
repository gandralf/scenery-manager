package br.com.devx.scenery.web;

import org.apache.struts.Globals;
import org.apache.struts.action.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;

import br.com.devx.scenery.manager.SceneryManagerResult;
import br.com.devx.scenery.manager.Scenery;
import br.com.devx.scenery.SceneryFileException;
import br.com.devx.scenery.TemplateAdapter;

/**
 * Struts specific action
 */
public class SceneryAction extends Action {
    private static Log s_log = LogFactory.getLog(SceneryAction.class);
    private TemplateHandler[] m_templateHandlers;

    public void setServlet(ActionServlet servlet) {
        super.setServlet(servlet);
        if (servlet != null) {
            try {
                m_templateHandlers = ServletHelper.getTemplateHandlers(servlet.getServletConfig());
            } catch (ServletException e) {
                s_log.warn("Dynamic template handlers configuration failed. Using default: only JspTemplateHandler", e);
                m_templateHandlers = new TemplateHandler[] { new JspTemplateHandler() };
            }

            s_log.info(m_templateHandlers.length + " template handlers loaded");
        } else {
            s_log.debug("servlet = null");
        }
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sceneryXml = ServletHelper.getSceneryXmlPath(getServlet().getServletConfig());
        String dataRoot = ServletHelper.getDataRoot(getServlet().getServletConfig());

        String baseURI = request.getRequestURI().substring(request.getContextPath().length());
        try {
            SceneryManagerResult sceneryManagerResult = ServletHelper.querySceneryManager(request, sceneryXml, dataRoot);

            Scenery scenery = sceneryManagerResult.getScenery();
            // Struts specific: template could be an actionForward alias
            ActionForward templateForward = mapping.findForward(scenery.getTemplate());
            if (templateForward != null) { // yes, it is an alias!
                handle(templateForward.getPath(), request, response, sceneryManagerResult.getTemplateAdapter(),
                        sceneryManagerResult.isAdapt(), sceneryManagerResult.getEncoding());
            } else {
                handle(scenery.getTemplate(), request, response, sceneryManagerResult.getTemplateAdapter(),
                        sceneryManagerResult.isAdapt(), sceneryManagerResult.getEncoding());
            }


            if (sceneryManagerResult.getScenerySet() != null) {
                SceneryPopupTemplateHandler popup = new SceneryPopupTemplateHandler(baseURI, sceneryManagerResult.getScenerySet(), sceneryManagerResult.getScenery());
                popup.init(getServlet().getServletConfig());
                popup.handle(scenery.getTemplate(), sceneryManagerResult.getEncoding(), request, response,
                        sceneryManagerResult.getTemplateAdapter(), sceneryManagerResult.isAdapt());
            }
        } catch (SceneryFileException e) {
            ServletHelper.notifyError(getServlet().getServletContext(), request, response, e);
        }

        return null;
    }

    public void handle(String template, HttpServletRequest request, HttpServletResponse response,
                       TemplateAdapter templateAdapter, boolean adapt, String encoding) throws IOException, ServletException {
        if (template == null) {
            template = "/defaultTemplate.jsp";
        }

        s_log.info("Using template: " + template);

        if (templateAdapter != null) {
            MessageHandler errorHandler = new MessageHandler(ActionErrors.GLOBAL_ERROR, ActionError.class, Globals.ERROR_KEY);
            errorHandler.handle(request, templateAdapter, "_STRUTS_ERRORS");

            MessageHandler messageHandler = new MessageHandler(ActionMessages.GLOBAL_MESSAGE, ActionMessage.class, Globals.MESSAGE_KEY);
            messageHandler.handle(request, templateAdapter, "_STRUTS_MESSAGES");
        }

        response.setContentType("text/html");

        if (template.charAt(0) != '/') {
            template = "/" + template; // JSP exige o "/" no inicio
        }

        for (int i = 0; i < m_templateHandlers.length; i++) {
            TemplateHandler templateHandler = m_templateHandlers[i];
            if (templateHandler.canHandle(template)) {
                templateHandler.handle(template, encoding, request, response, templateAdapter, adapt);
            }
        }
    }

    private class MessageHandler {
        private String m_globalId;
        private Class m_itemClass;
        private String m_requestKey;

        public MessageHandler(String globalId, Class itemClass, String requestKey) {
            m_globalId = globalId;
            m_itemClass = itemClass;
            m_requestKey = requestKey;
        }

        void handle(HttpServletRequest request, TemplateAdapter templateAdapter, String attributeName) {
            ActionMessages actionMessages = new ActionErrors();
            Collection strutsErrors = (Collection) templateAdapter.adapt(attributeName);
            if (strutsErrors != null) {
                for (Iterator iterator = strutsErrors.iterator(); iterator.hasNext();) {
                    TemplateAdapter error = (TemplateAdapter) iterator.next();
                    String key = (String) error.adapt("key");
                    String property = error.adapt("property") == null ? m_globalId : (String) error.adapt("property");
                    actionMessages.add(property, newMessageInstance(key));
                }
            }

            request.setAttribute(m_requestKey, actionMessages);
        }

        private ActionMessage newMessageInstance(String key) {
            try {
                Constructor constructor = m_itemClass.getConstructor(new Class[] { String.class });
                return (ActionMessage) constructor.newInstance(new Object[] { key } );
            } catch (Exception e) {
                throw (IllegalStateException) new IllegalStateException().initCause(e);
            }
        }
    }
}
