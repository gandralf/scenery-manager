package br.com.devx.scenery.manager;

import junit.framework.TestCase;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import br.com.devx.test.JUnitHelper;

/*
 * User: agandra
 * Date: Dec 16, 2002
 * Time: 2:27:42 PM
 */
public class SceneryFinderTest extends TestCase {
    private SceneryFinder m_finder;

    public SceneryFinderTest(String name) {
        super(name);
    }

    public void setUp() throws SAXException, IOException, ParserConfigurationException {
        String testHome = JUnitHelper.getHomeDir();
        m_finder = new SceneryFinder();
        m_finder.load(testHome + "/WEB-INF/scenery.xml");
    }

    public void testNotFound() {
        doTest("notfound.do", new HashMap<String, String[]>(), null, null);
    }

    public void testSampleFind() {
        doTest("sample1.do", new HashMap<String, String[]>(), "sample1.jsp", "sample1.scn");
    }

    public void testSameTemplate() {
        HashMap<String, String[]> case1 = newMap("param", "1");
        HashMap<String, String[]> case2 = newMap("param", "2");

        doTest("sameTemplate.do", new HashMap<String, String[]>(), "default.jsp", "default.scn");
        doTest("sameTemplate.do", case1, "default.jsp", "case1.scn");
        doTest("sameTemplate.do", case2, "default.jsp", "case2.scn");
    }

    public void testSameData() {
        HashMap<String, String[]> case1 = newMap("param", "1");
        HashMap<String, String[]> case2 = newMap("param", "2");

        doTest("sameData.do", new HashMap<String, String[]>(), "default.jsp", "default.scn");
        doTest("sameData.do", case1, "case1.jsp", "default.scn");
        doTest("sameData.do", case2, "case2.jsp", "default.scn");
    }

    public void testMixed() {
        HashMap<String, String[]> case1 = newMap("param", "1");
        HashMap<String, String[]> case2 = newMap("param", "2");
        HashMap<String, String[]> case3 = newMap("param", "3");
        HashMap<String, String[]> case4 = newMap("param", "4");

        doTest("mixed.do", new HashMap<String, String[]>(), "default.jsp", "default.scn");
        doTest("mixed.do", case1, "case1.jsp", "default.scn");
        doTest("mixed.do", case2, "case2.jsp", "default.scn");
        doTest("mixed.do", case3, "default.jsp", "case1.scn");
        doTest("mixed.do", case4, "default.jsp", "case2.scn");
    }

    public void testEachCase() {
        HashMap<String, String[]> case1 = newMap("param", "1");
        HashMap<String, String[]> case2 = newMap("param", "2");

        doTest("eachCase.do", new HashMap<String, String[]>(), "default.jsp", "default.scn");
        doTest("eachCase.do", case1, "case1.jsp", "case1.scn");
        doTest("eachCase.do", case2, "case2.jsp", "case2.scn");
    }

    public void testGetSceneries() {
        ScenerySet scenerySet = m_finder.getSceneries("sample1.do");
        Collection sceneries = scenerySet.getSceneries();
        assertEquals(1, sceneries.size());
        Scenery scenery = (Scenery) sceneries.iterator().next();
        assertEquals("sample1.jsp", scenery.getTemplate());
        assertEqualsEx(new String[] { "sample1.scn" }, scenery.getDataList());

        sceneries = m_finder.getSceneries("mixed.do").getSceneries();
        assertEquals(5, sceneries.size());
        assertTrue(sceneries.contains(new Scenery("default.scn", "default.jsp", "Mixed default", null)));
        assertTrue(sceneries.contains(new Scenery("default.scn", "case1.jsp", "Alternative template", "param = 1")));
        assertTrue(sceneries.contains(new Scenery("default.scn", "case2.jsp", "Another template", "param = 2")));
        assertTrue(sceneries.contains(new Scenery("case1.scn", "default.jsp", "Alternative data", "param = 3")));
        assertTrue(sceneries.contains(new Scenery("case2.scn", "default.jsp", "param = 4", "param = 4")));

        sceneries = m_finder.getSceneries("eachCase.do").getSceneries();
        assertEquals(3, sceneries.size());
        assertTrue(sceneries.contains(new Scenery("default.scn", "default.jsp", null, null)));
        assertTrue(sceneries.contains(new Scenery("case1.scn", "case1.jsp", "Alternative data / Alternative template", "param = 1")));
        assertTrue(sceneries.contains(new Scenery("case2.scn", "case2.jsp", "param = 2 / Another template", "param = 2")));
    }

    private void assertEqualsEx(String[] expected, List actual) {
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }
    }

    public void testNoAdaptConfig() throws ServletException, IOException {
        Scenery scenery = m_finder.getScenery("noAdapt.do", new HashMap());
        assertEquals("noAdapt.jsp", scenery.getTemplate());
        assertEqualsEx(new String[] { "sample1.scn" }, scenery.getDataList());
        assertTrue(!m_finder.getSceneries("noAdapt.do").getAdapt());
    }

    private void doTest(String uri, HashMap<String, String[]> parameters, String expectedTemplate, String expectedData) {
        Scenery scenery = m_finder.getScenery(uri, parameters);
        assertEquals(expectedTemplate, scenery != null ? scenery.getTemplate() : null);
        if (scenery != null) {
            assertEqualsEx(new String[] { expectedData }, scenery.getDataList());
        } else {
            assertNull(expectedData);
        }
    }

    private HashMap<String, String[]> newMap(String param, String value) {
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        map.put(param, new String[] { value });

        return map;
    }
}
