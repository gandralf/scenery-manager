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
        ServletRunner sr = new ServletRunner(new File("src/main/webapp/WEB-INF/web.xml"));
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
