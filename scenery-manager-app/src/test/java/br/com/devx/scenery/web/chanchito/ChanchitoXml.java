package br.com.devx.scenery.web.chanchito;

import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileReader;
import java.io.IOException;

public class ChanchitoXml implements ContentHandler {
    private boolean readingZoneContent;
    private StringBuilder zoneContent;

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
        zoneContent = null;
        readingZoneContent = false;
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if ("zone".equals(qName)) {
            zoneContent = new StringBuilder(); // Found it!
            readingZoneContent = true;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        readingZoneContent = false;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (readingZoneContent) {
            zoneContent.append(ch, start, length);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public String getZoneContent() {
        return zoneContent != null ? zoneContent.toString() : null;
    }
}
