package br.com.devx.scenery.web;

import junit.framework.TestCase;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

import java.io.File;

public class HttpUnitTestCase extends TestCase {
    protected ServletUnitClient m_client;

    @Override
    protected void setUp() throws Exception {
        ServletRunner sr = new ServletRunner(new File("WEB-INF/web.xml"));
        m_client = sr.newClient();
    }

    public void testNothing() {
        // Just to shut up maven
    }
}
