package br.com.devx.scenery.manager;

import br.com.devx.scenery.TemplateAdapter;
import br.com.devx.test.JUnitHelper;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/*
 * User: agandra
 * Date: Dec 16, 2002
 * Time: 2:27:42 PM
 */
public class EncodingTest extends TestCase {
    private static final String EXPECTED = "abc\u00E1\u00E9\u00ED\u00F3\u00FA\u00E7";
    private static final String SCN = "blah=\"" + EXPECTED + "\";";
    public File m_dir;

    public EncodingTest(String name) {
        super(name);
    }

    public void setUp() throws IOException {
        m_dir = new File(JUnitHelper.getHomeDir() + "/encoding");
        cleanup(m_dir);

        write(new File(m_dir, "default.scn"), SCN, null);
        write(new File(m_dir, "default-scenery.xml"), sceneryXml(null), null);
        write(new File(m_dir, "utf-8.scn"), SCN, "UTF-8");
        write(new File(m_dir, "utf-8-scenery.xml"), sceneryXml("UTF-8"), "UTF-8");
        write(new File(m_dir, "iso-8859-1.scn"), SCN, "ISO-8859-1");
        write(new File(m_dir, "iso-8859-1-scenery.xml"), sceneryXml("ISO-8859-1"), "ISO-8859-1");
    }

    private String sceneryXml(String encoding) {
        String enc = encoding == null ? "" : (" encoding=\"" + encoding + "\"");
        String encFile = encoding == null ? "default" : encoding.toLowerCase();

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<scenery-manager" + enc + ">\n" +
                "    <scenery-set uri=\"blah.do\" template=\"blah.vm\" data=\"" + encFile + ".scn\" />\n" +
                "</scenery-manager>";
    }

    private void write(File file, String content, String encoding) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        try {
            out.write(encoding == null ? content.getBytes() : content.getBytes(encoding));
        } finally {
            out.close();
        }
    }

    public void testDefaultEncoding() throws SceneryManagerException, IOException {
        doTest("default");
        doTest("utf-8");
        doTest("iso-8859-1");
    }

    private void doTest(String enc) throws SceneryManagerException {
        SceneryManager sceneryManager = new SceneryManager(m_dir.getAbsolutePath() + "/" + enc + "-scenery.xml",
                m_dir.getAbsolutePath());
        SceneryManagerResult sceneryManagerResult = sceneryManager.query("blah.do", new HashMap(), null, null, Boolean.TRUE);
        TemplateAdapter templateAdapter = sceneryManagerResult.getTemplateAdapter();
        assertEquals("Error in " + enc + " file", EXPECTED, templateAdapter.get("blah"));
    }

    private void cleanup(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i=0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    cleanup(files[i]);
                }

                files[i].delete();
            }
        }

        dir.mkdirs();
    }
}