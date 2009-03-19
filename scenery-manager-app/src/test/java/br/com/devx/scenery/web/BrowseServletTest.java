package br.com.devx.scenery.web;

import org.xml.sax.SAXException;

import java.io.IOException;

import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebResponse;

public class BrowseServletTest extends HttpUnitTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCurrentDir() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://blog.gonow.intranet/browse.do?path=." );
        WebResponse response = m_client.getResponse(request);
        String text = response.getText().toLowerCase();

        assertContains(text, "..");
        assertContains(text, "web-inf");
        assertFalse("It should be ready", text.contains("warning"));
    }

    public void testSrcDir() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://blog.gonow.intranet/browse.do?path=.." );
        WebResponse response = m_client.getResponse(request);
        String text = response.getText().toLowerCase();

        assertContains(text, "..");
        assertContains(text, "java");
        assertContains(text, "resources");
        assertContains(text, "sh");
        assertContains(text, "webapp");
        assertContains(text, "warning");
    }

    public void testNotFound() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://blog.gonow.intranet/browse.do?path=./sblowblows" );
        WebResponse response = m_client.getResponse(request);
        String text = response.getText().toLowerCase();

        assertContains(text, "webapp");
        assertFalse("It should be ready", text.contains("warning"));
    }

    private static void assertContains(String text, String subText) {
        assertTrue(subText + " not found", text.contains(subText));
    }
}