package br.com.devx.scenery.web;

import junit.framework.TestCase;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

import java.io.File;

public class HttpUnitTestCase extends TestCase {
    protected ServletUnitClient m_client;
    private String previousPath;

    @Override
    protected void setUp() throws Exception {
        previousPath = System.getProperty("user.dir");
        System.setProperty("user.dir", previousPath + "/src/main/webapp");
        ServletRunner sr = new ServletRunner(new File("WEB-INF/web.xml"));
        m_client = sr.newClient();
    }

    @Override
    protected void tearDown() throws Exception {
        System.setProperty("user.dir", previousPath);
    }

    public void testNothing() {
        // Just to shut up maven
    }
}
