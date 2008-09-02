package br.com.devx.scenery.manager;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * Finds <code>scenery-set</code> and <code>scenery</code> elements from a <code>scenery.xml</code> config file
 */
class SceneryFinder {
    private Map m_uriMap = new HashMap();
    private String m_encoding;

    public SceneryFinder() {
    }

    public void load(String configFileName) throws SAXException, IOException, ParserConfigurationException {
        SceneryConfigLoader loader = new SceneryConfigLoader();
        m_uriMap = loader.load(configFileName);
        m_encoding = loader.getEncoding();
    }

    /**
     * Returns the current encoding
     * @return current encoding, defined at a scenery.xml, or null if not defined (which means: use default
     *   encoding, anyway)
     */
    public String getEncoding() {
        return m_encoding;
    }

    /**
     * Returns a scenery for the given uri/constraints
     * @param uri <code>scenery-set</code> key attribute
     * @param constraints used to find the correct scenery, looking up <code>test</code> attribute
     * @return A <code>Scenery</code> instance, or <code>null</code> if this uri is not present
     */
    public Scenery getScenery(String uri, Map constraints) {
        Scenery result = null;
        ScenerySet scenerySet = getSceneries(uri);
        if (scenerySet != null) {
            result = scenerySet.getScenery(constraints);
        }

        return result;
    }

    /**
     * Returns a scenery for the given uri/constraints
     * @param uri <code>scenery-set</code> key attribute
     * @return A <code>ScenerySet</code> instance, or <code>null</code> if the uri is not present
     */
    public ScenerySet getSceneries(String uri) {
        if (uri.charAt(0) != '/') {
            uri = '/' + uri;
        }

        return (ScenerySet) m_uriMap.get(uri);
    }
}
