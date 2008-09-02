package br.com.devx.scenery.web;

import junit.framework.TestCase;

public class SceneryServletTest extends TestCase {
    public void testBlah() {
        
    }
}
/*
public class SceneryServletTest extends ServletTestCase {
    private Locale m_oldLocale;

    public SceneryServletTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        m_oldLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    protected void tearDown() throws Exception {
        Locale.setDefault(m_oldLocale);
    }

    public void beginVelocityTemplate(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/test/sample1.do", null, "");
        request.addParameter("sceneryTemplate", "sample1.vm");
        request.addParameter("sceneryData", "sample1.scn");
        request.addParameter("adapt", "true");
    }

    public void testVelocityTemplate() throws ServletException, IOException {
        doTest();
    }

    public void endVelocityTemplate(WebResponse response) {
        verify(response.getText());
    }

    public void beginJspTemplate(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/test/sample1.do", null, "");
        request.addParameter("sceneryTemplate", "sample1.jsp");
        request.addParameter("sceneryData", "sample1.scn");
        request.addParameter("adapt", "true");
    }

    public void testJspTemplate() throws ServletException, IOException {
        doTest();
    }

    public void endJspTemplate(WebResponse response) {
        verify(response.getText());
    }

    public void beginNoAdapt(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/test/sample1.do", null, "");
        request.addParameter("sceneryTemplate", "noAdapt.jsp");
        request.addParameter("sceneryData", "sample1.scn");
    }

    public void testNoAdapt() throws ServletException, IOException {
        doTest();
    }

    public void endNoAdapt(WebResponse response) {
        verify(response.getText());
    }

    public void beginSceneryXml(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/sample1.do", null, "");
    }

    public void testSceneryXml() throws ServletException, IOException {
        doTest();
    }

    public void endSceneryXml(WebResponse response) {
        verify(response.getText());
    }

    public void beginVelocityPath(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/test/pathTest.do", null, "");
        request.addParameter("sceneryTemplate", "pathTest/pathTest.vm");
        request.addParameter("sceneryData", "sample1.scn");
        request.addParameter("adapt", "true");
    }

    public void testVelocityPath() throws ServletException, IOException {
        doTest();
    }

    public void endVelocityPath(WebResponse response) {
        verify(response.getText());
    }

    public void beginJspPath(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/test/pathTest.do", null, "");
        request.addParameter("sceneryTemplate", "pathTest/pathTest.jsp");
        request.addParameter("sceneryData", "sample1.scn");
        request.addParameter("adapt", "true");
    }

    public void testJspPath() throws ServletException, IOException {
        doTest();
    }

    public void endJspPath(WebResponse response) {
        verify(response.getText());
    }

    public void beginMultipleDataByXml(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/multipleData.do", null, "");
    }

    public void testMultipleDataByXml() throws IOException, ServletException {
        doTest();
    }

    public void endMultipleDataByXml(WebResponse response) {
        verify(response.getText());
    }

    public void beginMultipleDataByParameter(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/test/multipleData.do", null, "");
        request.addParameter("sceneryData", "multipleData1.scn");
        request.addParameter("sceneryData", "multipleData2.scn");
        request.addParameter("sceneryTemplate", "sample1.jsp");
        request.addParameter("adapt", "true");
    }

    public void testMultipleDataByParameter() throws IOException, ServletException {
        doTest();
    }

    public void endMultipleDataByParameter(WebResponse response) {
        verify(response.getText());
    }

    public void beginExceptionReport(WebRequest request) {
        request.setURL("localhost", "/sceneryTest", "/test/multipleData.do", null, "");
        request.addParameter("sceneryData", "illegal.scn");
        request.addParameter("sceneryTemplate", "sample1.jsp");
    }

    public void testExceptionReport() throws IOException, ServletException {
        doTest();
    }

    public void endExceptionReport(WebResponse response) {
        assertTrue(response.getText().indexOf("mapValue") != -1);
    }

    private void doTest() throws ServletException, IOException {
        SceneryServlet servlet = new SceneryServlet();
        config.setInitParameter("scenery.xml", "WEB-INF/scenery.xml");
        config.setInitParameter("templateHandlers",
                "br.com.devx.scenery.web.JspTemplateHandler, br.com.devx.scenery.web.VelocityTemplateHandler");
        servlet.init(config);
        servlet.doGet(request, response);
    }

    private void verify(String actual) {
        // Remove the popup script
        int scriptIndex = actual.indexOf("<");
        if (scriptIndex != -1) {
            actual = actual.substring(0, scriptIndex -1);
        }

        Locale oldLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        try {
            String expected =
                    "intValue = 123; " +
                    "doubleValue = 123.45; " +
                    "booleanValue = true; " +
                    "dateValue = Jul 20, 2002; " +
                    "stringValue = \"Hello, world!\"; " +
                    "otherAdapter = { message = \"Hello again...\"; }; " +
                    "arrayValue = array: { 123 456 }; " +
                    "mapValue = map: { \"x\" = { message = \"Hello again...\"; }, \"b\" = 1,617, \"a\" = 1,415 }; " +
                    "collectionValue = collection: { 789, 1,011, { message = \"Hello again...\"; } };";

            expected = expected.replaceAll("\\s\\s*", " ");
            actual = actual.replaceAll("\\s\\s*", " ").trim();

            if (!expected.trim().equals(actual.trim())) {
                System.out.println("expected/actual\n");
                System.out.println(expected.trim());
                System.out.println(actual.trim());
            }
            assertEquals(expected.trim(), actual.trim());
        } finally {
            Locale.setDefault(oldLocale);
        }
    }
}
*/