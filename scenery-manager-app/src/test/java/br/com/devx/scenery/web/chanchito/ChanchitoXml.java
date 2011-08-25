package br.com.devx.scenery.web.chanchito;

import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChanchitoXml implements ContentHandler {
    private boolean readingViewContent;
    private StringBuilder viewContent;
    private String readingZoneId;
    private StringBuilder zoneBuilder;
    private Map<String, String> zones;
    private String structure;

    public ChanchitoXml(String fileName) throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        XMLReader parser = parserFactory.newSAXParser().getXMLReader();

        parser.setContentHandler(this);
        FileReader reader = new FileReader(fileName);
        try {
            parser.parse(new InputSource(reader));
        } finally {
            reader.close();
        }

    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
        viewContent = null;
        readingViewContent = false;
        zones = null;
        readingZoneId = null;
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if ("view".equals(qName)) {
            viewContent = new StringBuilder(); // Found it!
            readingViewContent = true;
            zones = new HashMap<String, String>();
            structure = atts.getValue("structure");
        } else if ("zone".equals(qName)) {
            readingZoneId = atts.getValue("id");
            zoneBuilder = new StringBuilder();
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("view".equals(qName)) {
            readingViewContent = false;
        } else if ("zone".equals(qName)) {
            zones.put(readingZoneId, zoneBuilder.toString());
            readingZoneId = null;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (readingZoneId != null) {
            zoneBuilder.append(ch, start, length);
        } else if (readingViewContent) {
            viewContent.append(ch, start, length);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public String getViewContent() {
        return viewContent != null ? viewContent.toString() : null;
    }

    public String getStructure() {
        return structure;
    }

    public Map<String, String> getZones() {
        return zones;
    }
}
