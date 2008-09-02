package br.com.devx.scenery.manager;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.*;
import org.xml.sax.SAXException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

class SceneryConfigLoader implements ContentHandler {
    private String m_encoding;
    private ScenerySet m_scenerySet;
    private HashMap m_result = new HashMap();

    public SceneryConfigLoader() {
    }

    public Map load(String configPath) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        org.xml.sax.XMLReader parser = parserFactory.newSAXParser().getXMLReader();

        parser.setContentHandler(this);
        FileReader reader = new FileReader(configPath);
        try {
            parser.parse(new InputSource(reader));
        } finally {
            reader.close();
        }

        return m_result;
    }

    public String getEncoding() {
        return m_encoding;
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

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if ("scenery-set".equals(qName)) {
            boolean adapt = atts.getValue("adapt") == null || "true".equals(atts.getValue("adapt")) || "yes".equals(atts.getValue("adapt"));
            m_scenerySet = new ScenerySet(atts.getValue("template"), atts.getValue("data"), adapt, atts.getValue("description"));
            String uri = atts.getValue("uri");
            if (uri.charAt(0) != '/') {
                uri = '/' + uri;
            }
            m_result.put(uri, m_scenerySet);
        } else if ("template".equals(qName)) {
            m_scenerySet.setTemplate(atts.getValue("test"), atts.getValue("file"), atts.getValue("description"));
        } else if ("data".equals(qName)) {
            m_scenerySet.setData(atts.getValue("test"), atts.getValue("file"), atts.getValue("description"));
        } else if ("scenery-manager".equals(qName)) {
            m_encoding = atts.getValue("encoding");
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    public void characters(char ch[], int start, int length) throws SAXException {
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }
}
