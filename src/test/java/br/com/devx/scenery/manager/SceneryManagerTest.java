package br.com.devx.scenery.manager;

import junit.framework.TestCase;
import br.com.devx.test.JUnitHelper;
import br.com.devx.scenery.parser.ParseException;
import br.com.devx.scenery.manager.SceneryManager;
import br.com.devx.scenery.manager.SceneryManagerException;
import br.com.devx.scenery.TemplateAdapter;

import java.util.HashMap;
import java.io.FileReader;
import java.io.FileNotFoundException;

public class SceneryManagerTest extends TestCase {
    private SceneryManager m_sceneryManager;

    public SceneryManagerTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        m_sceneryManager = new SceneryManager(JUnitHelper.getHomeDir() + "/WEB-INF/scenery.xml", JUnitHelper.getHomeDir());
    }

    public void testTemplateAdapter() throws SceneryManagerException, FileNotFoundException, ParseException {
        TemplateAdapter actual = m_sceneryManager.buildTemplateAdapter("sample1.do", new HashMap());
        TemplateAdapter expected = loadTemplateAdapter("sample1.scn");

        assertEquals(expected, actual);
    }

    public void testUriNotFound() throws SceneryManagerException {
        try {
            m_sceneryManager.buildTemplateAdapter("__not_found.do", new HashMap());
            fail("IllegalArgumentException expected");
        } catch(IllegalArgumentException e) {
            // ok
        }
    }

    private TemplateAdapter loadTemplateAdapter(String fileName) throws ParseException, FileNotFoundException {
        TemplateAdapter expected = TemplateAdapter.load(new FileReader(JUnitHelper.getHomeDir() + "/" + fileName));
        return expected;
    }
}
