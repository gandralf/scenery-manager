package br.com.devx.scenery.web;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import org.xml.sax.SAXException;

import java.io.IOException;

public class ConfigServletTest extends HttpUnitTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        AppsConfig.getInstance().reset();
    }

    public void testDefaultConfig() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://blog.gonow.intranet/config.do" );
        WebResponse response = m_client.getResponse(request);
        String text = response.getText().toLowerCase();
        // System.out.println(text);

        assertContains(text, "\".\"");
        assertContains(text, "file://");
        assertContains(text, "scenery-manager-app/src/main/webapp");
    }

    public void testBrowseIntegration() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://blog.gonow.intranet/config.do" );
        WebResponse response = m_client.getResponse(request);
        String text = response.getText().toLowerCase();
        assertFalse("Toolbox isn't working", text.contains("$escape"));
    }

    public void testSetConfig() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://blog.gonow.intranet/config.do?path=../../test/webapp" );
        WebResponse response = m_client.getResponse(request);
        String text = response.getText().toLowerCase();

        assertContains(text, "\"../../test/webapp\"");
        assertContains(text, "file:/");
        assertContains(text, "scenery-manager-app/src/test/webapp");
    }

    private static void assertContains(String text, String subText) {
        assertTrue(subText + " not found", text.contains(subText));
    }
}
