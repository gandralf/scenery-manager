package br.com.devx.scenery.web.templates;

import org.apache.log4j.Logger;
import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

class FreemarkerOptions implements ContentHandler {
    private static final Logger log = Logger.getLogger(FreemarkerOptions.class);
    private String webXml;
    private StringBuffer buffer = new StringBuffer();
    private boolean freemarkerServlet;
    private String paramName;
    private Map<String, String> initParams = new HashMap<String, String>();

    public FreemarkerOptions(String webappPath) {
        webXml = webappPath + "/WEB-INF/web.xml";
    }

    public String getInitParam(String name) {
        String result = null;

        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader parser = parserFactory.newSAXParser().getXMLReader();

            parser.setContentHandler(this);
            parser.setEntityResolver(new EntityResolver() { // Make it FAST! I don' want to validate your bloody xml!
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    return new InputSource(new StringReader(""));
                }
            });

            FileReader reader = new FileReader(webXml);
            try {
                parser.parse(new InputSource(reader));
            } finally {
                reader.close();
            }


            result = initParams.get(name); // "freemarker/macros.ftl as html"
        } catch (SAXException e) {
            log.warn("Error parsing web.xml", e);
        } catch (ParserConfigurationException e) {
            log.warn("Error parsing web.xml", e);
        } catch (IOException e) {
            log.warn("Error parsing web.xml", e);
        }

        return result;
    }

    public void setDocumentLocator(Locator locator) {

    }

    public void startDocument() throws SAXException {

    }

    public void endDocument() throws SAXException {

    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

    public void endPrefixMapping(String prefix) throws SAXException {

    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        buffer = new StringBuffer();
        if ("servlet-class".equals(qName)) {
            freemarkerServlet = false;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("servlet-class".equals(qName)) {
            String className = buffer.toString();
            freemarkerServlet = className.contains("FreemarkerDecoratorServlet") ||
                    className.contains("FreemarkerServlet");
        } else if ("param-name".equals(qName) && freemarkerServlet) {
            paramName = buffer.toString();
        } else if ("param-value".endsWith(qName) && freemarkerServlet) {
            String paramValue = buffer.toString();
            initParams.put(paramName, paramValue);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        buffer.append(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }
}
