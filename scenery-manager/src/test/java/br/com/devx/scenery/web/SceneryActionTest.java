package br.com.devx.scenery.web;

import junit.framework.TestCase;

public class SceneryActionTest extends TestCase {
    public void testBlah() {

    }
}

/*
public class SceneryActionTest extends ServletTestCase {
    private ActionServlet m_servlet;

    public SceneryActionTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        m_servlet = new ActionServlet();
        m_servlet.init(config);
    }

    public void beginSimpleMerge(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/strutsSample.do", null, "");
    }

    public void testSimpleMerge() throws ServletException, IOException {
        m_servlet.service(request, response);
    }

    public void endSimpleMerge(WebResponse response) throws FileNotFoundException, ParseException {
        doTest(response);
    }

    public void beginDirectJspAccess(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/strutsSample.do", null, "");
        request.addParameter("jsp", "1");
    }

    public void testDirectJspAccess() throws ServletException, IOException {
        m_servlet.service(request, response);
    }

    public void endDirectJspAccess(WebResponse response) throws FileNotFoundException, ParseException {
        doTest(response);
    }

    public void beginFailure(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/strutsSample.do", null, "");
        request.addParameter("error", "1");
    }

    public void testFailure() throws ServletException, IOException {
        m_servlet.service(request, response);
    }

    public void endFailure(WebResponse response) throws FileNotFoundException, ParseException {
        String text = response.getText();
        assertTrue(text.indexOf("<script>") != -1);
        assertTrue(text.indexOf("Some error") != -1);
        assertTrue(text.indexOf("Another error") != -1);
        assertTrue(text.indexOf("Hello again...") != -1);
    }

    public void beginDirectVelocity(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/strutsSample.do", null, "");
        request.addParameter("velocity", "file");
    }

    public void testDirectVelocity() throws IOException, ServletException {
        m_servlet.service(request, response);
    }

    public void endDirectVelocity(WebResponse response) throws FileNotFoundException, ParseException {
        doTest(response);
    }

    public void beginVelocity(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/strutsSample.do", null, "");
        request.addParameter("velocity", "alias");
    }

    public void testVelocity() throws IOException, ServletException {
        m_servlet.service(request, response);
    }

    public void endVelocity(WebResponse response) throws FileNotFoundException, ParseException {
        doTest(response);
    }

    private void doTest(WebResponse response) throws ParseException, FileNotFoundException {
        TemplateAdapter expected = TemplateAdapter.load(new FileReader(JUnitHelper.getHomeDir() + "/sample1.scn"));

        String text = response.getText();
        assertTrue(text.indexOf("<script>") != -1);
        text = text.substring(0, text.indexOf("<")).replaceAll("&quot;", "\"");

        TemplateAdapter actual = (TemplateAdapter) TemplateAdapter.load(new StringReader(text)).adapt("templateAdapter");

        assertEquals(expected, actual);
    }
}
*/