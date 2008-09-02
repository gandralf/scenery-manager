package br.com.devx.scenery.web;

import br.com.devx.scenery.manager.ScenerySet;
import br.com.devx.scenery.manager.Scenery;
import br.com.devx.scenery.TemplateAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Iterator;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

/**
 * User: agandra
 * Date: 13/05/2003
 * Time: 18:43:46
 */
public class SceneryPopupTemplateHandler implements TemplateHandler {
    private String m_baseURI;
    private ScenerySet m_sceneries;
    private Scenery m_currentScenery;
    private ServletConfig m_config;

    public SceneryPopupTemplateHandler(String baseURI, ScenerySet sceneries, Scenery currentScenery) {
        m_baseURI = baseURI;
        m_sceneries = sceneries;
        m_currentScenery = currentScenery;
    }

    public void init(ServletConfig config) throws ServletException {
        m_config = config;
    }

    public boolean canHandle(String template) {
        return true;
    }

    public void handle(String template, String encoding, HttpServletRequest request, HttpServletResponse response,
                       TemplateAdapter templateAdapter, boolean adapt) throws IOException, ServletException {
        StringBuffer buffer = new StringBuffer();
        Iterator i = m_sceneries.getSceneries().iterator();
        while (i.hasNext()) {
            Scenery scenery = (Scenery) i.next();
            String test = scenery.getTest();
            if (test != null) {
                test = test.replaceAll("\\s+=\\s+", "=");
            }
            boolean current = m_currentScenery.getTemplate().equals(scenery.getTemplate())
                    && m_currentScenery.getDataList().equals(scenery.getDataList());
            String sceneryDataList = "";
            Iterator j = scenery.getDataList().iterator();
            while (j.hasNext()) {
                sceneryDataList = j.next() + (j.hasNext() ? "sceneryData=" : "");
            }

            String str = test + "$" + sceneryDataList + "$" + scenery.getTemplate() +
                    "$" + scenery.getDescription() + "$" + current;
            try {
                buffer.append("&scenery=");
                buffer.append(URLEncoder.encode(str, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new IllegalStateException(e.toString());
            }
        }

        String sceneryParameters = buffer.toString();
        request.setAttribute("sceneryBaseURI", m_baseURI);
        request.setAttribute("sceneryParameters", sceneryParameters);

        m_config.getServletContext().getRequestDispatcher("/sceneryScript.jsp").include(request, response);
    }
}
