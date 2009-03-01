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

        AppsConfig config = AppsConfig.getInstance();
        config.reset();
        m_app = config.getTargetApp();
        m_app.setPath("../../test/webapp");
    }

    /**
     * Se o cenario estah mapeado, usa-o
     */
    public void testVelocity() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://localhost/velocity.do" );
        WebResponse response = m_client.getResponse( request );
        assertEquals("Hello, Zeh maneh", response.getText().trim());
    }

    /**
     * Freemarker
     */
    public void testFreemarker() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://localhost/freemarker.do" );
        WebResponse response = m_client.getResponse( request );
        assertEquals("Hello, Zeh maneh", response.getText().trim());
    }

    /**
     * senao, aciona o outro site
     */
    public void testRedirect() throws IOException, SAXException {
        m_app.setUrl("http://blog.gonow.intranet/");

        WebRequest request   = new GetMethodWebRequest( "http://localhost/blah.do" );
        WebResponse response = m_client.getResponse( request );
        assertTrue(response.getText().toLowerCase().contains("blog da gonow"));
    }

    /**
     * senao, aciona o outro site
     */
    public void testError() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://localhost/velocity.do?error=true" );
        WebResponse response = m_client.getResponse( request );
        String text = response.getText();
        assertTrue(text.contains("<pre>     \"I'll forget a comma here\"</pre>"));
    }

    public void testVelocityMacro() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://localhost/velocityx.do" );
        WebResponse response = m_client.getResponse( request );
        String text = response.getText();
        assertTrue(text.contains("<td bgcolor=\"blue\">hello</td>"));
    }

    public void testVelocityParse() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://localhost/velocityx.do?test=parse" );
        WebResponse response = m_client.getResponse( request );
        String text = response.getText();
        assertTrue(text.contains("<td bgcolor=\"blue\">hello</td>"));
    }

    public void testWebInfClasses() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://localhost/classloader.do" );
        WebResponse response = m_client.getResponse( request );
        String text = response.getText();
        assertTrue(text.contains("Hello, Me!, your id is 1"));
    }

    public void testWebInfLib() throws IOException, SAXException {
        WebRequest request   = new GetMethodWebRequest( "http://localhost/classloader.do?from=lib" );
        WebResponse response = m_client.getResponse( request );
        String text = response.getText();
        assertTrue(text.contains("Hello, Me!, your id is 1"));
    }
}
