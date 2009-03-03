package br.com.devx.scenery.sitemesh;

import br.com.devx.scenery.web.Sitemesh;
import br.com.devx.scenery.web.SitemeshException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

import java.io.IOException;

public class SimpleSitemesh implements Sitemesh {
    private static final Logger s_log = Logger.getLogger(SimpleSitemesh.class);
    private String m_requestUri;
    private DecoratorsXml m_xml;

    public SimpleSitemesh(String targetPath, String requestURI) throws SitemeshException {
        String filePath = targetPath + "/WEB-INF/decorators.xml";
        m_requestUri = requestURI;
        try {
            m_xml = new DecoratorsXml(filePath);
        } catch (ParserConfigurationException e) {
            throw new SitemeshException(filePath, e);
        } catch (SAXException e) {
            throw new SitemeshException(filePath, e);
        } catch (IOException e) {
            s_log.info("sitemesh: " + filePath + " not found.");
        }
    }

    public boolean isActive() {
        return m_xml != null && m_xml.isActive(m_requestUri);
    }

    public String getTemplate() {
        return m_xml.getTemplate(m_requestUri);
    }
}
