package br.com.devx.scenery.web;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import org.xml.sax.SAXException;

import java.io.IOException;

public class SceneryFilterTest extends HttpUnitTestCase {
    private TargetApp m_app;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        m_app = AppsConfig.getInstance().getTargetApp();
        m_app.setPath("../../test/webapp");
    }

    /**
     * Se o cenario estah mapeado, usa-o
     */
    public void testScenery() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://blog.gonow.intranet/mailbox.do" );
        WebResponse response = m_client.getResponse( request );
        assertEquals("Hello, Zeh maneh", response.getText().trim());
    }

    /**
     * senao, aciona o outro site
     */
    public void testRedirect() throws IOException, SAXException {
        m_app.setUrl("http://blog.gonow.intranet/");

        WebRequest request   = new GetMethodWebRequest( "http://blog.gonow.intranet/blah.do" );
        WebResponse response = m_client.getResponse( request );
        assertTrue(response.getText().toLowerCase().contains("blog da gonow"));
    }

    /**
     * senao, aciona o outro site
     */
    public void testError() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://blog.gonow.intranet/mailbox.do?error=true" );
        WebResponse response = m_client.getResponse( request );
        String text = response.getText();
        assertTrue(text.contains("<pre>     \"I'll forget a comma here\"</pre>"));
    }
}
