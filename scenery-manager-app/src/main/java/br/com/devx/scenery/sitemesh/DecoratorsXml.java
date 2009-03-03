package br.com.devx.scenery.sitemesh;

import org.xml.sax.*;
import org.apache.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

class DecoratorsXml implements ContentHandler {
    private static final Logger s_log = Logger.getLogger(DecoratorsXml.class);

    private String m_defaultDir;
    private StringBuffer m_content;

    private static enum Parsing {
        decorator, excludes
    }

    private Parsing m_parsing;

    private Decorator m_currentDecorator;
    private List<String> m_excludes;
    private List<Decorator> m_decorators;

    public DecoratorsXml(String configPath) throws ParserConfigurationException, SAXException, IOException {
        m_defaultDir = "";
        m_decorators = new ArrayList<Decorator>();
        m_excludes = new ArrayList<String>();

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        XMLReader parser = parserFactory.newSAXParser().getXMLReader();

        parser.setContentHandler(this);
        FileReader reader = new FileReader(configPath);
        try {
            parser.parse(new InputSource(reader));
        } finally {
            reader.close();
        }
    }

    public String getTemplate(String requestUri) {
        for (String pattern: m_excludes) {
            boolean b = requestUri.matches(pattern.replaceAll("\\*", ".*"));
            if (b) {
                return null;
            }
        }

        for (Decorator dec: m_decorators) {
            for (String pattern: dec.m_patterns) {
                boolean b = requestUri.matches(pattern.replaceAll("\\*", ".*"));
                if (b) {
                    return m_defaultDir + "/" + dec.m_page;
                }
            }
        }

        return null;
    }

    public boolean isActive(String requestUri) {
        return getTemplate(requestUri) != null;
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
        m_content = new StringBuffer();
        if ("decorators".equals(qName)) {
            String defaultDir = atts.getValue("defaultdir");
            if (defaultDir != null) {
                m_defaultDir = defaultDir;
            }
        } else if ("decorator".equals(qName)) {
            m_currentDecorator = new Decorator(atts.getValue("name"), atts.getValue("page"));
            m_parsing = Parsing.decorator;
        } else if ("excludes".equals(qName)) {
            m_parsing = Parsing.excludes;
        } else {
            s_log.warn("Ignoring: " + qName);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("decorator".equals(qName)) {
            m_decorators.add(m_currentDecorator);
        } else if ("pattern".equals(qName)) {
            if (m_parsing == Parsing.decorator) {
                m_currentDecorator.addPattern(m_content.toString());
            } else {
                m_excludes.add(m_content.toString());
            }
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        m_content.append(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    private static class Decorator {
        String m_name;
        String m_page;
        List<String> m_patterns;

        private Decorator(String name, String page) {
            m_name = name;
            m_page = page;
            m_patterns = new ArrayList<String>();
        }

        public void addPattern(String pattern) {
            m_patterns.add(pattern);
        }
    }
}
