package br.com.devx.scenery.web.templates;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.XMLToolboxManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.File;
import java.util.Map;

public class VelocityHelper {
    private static final Logger s_log = Logger.getLogger(VelocityHelper.class);

    public static void setupTools(String targetPath, Context ctx) throws IOException {
        if (targetPath == null) {
            s_log.warn("targetPath = null. Using current dir (\".\")");
            targetPath = ".";
        }

        String toolboxXml = targetPath + "/WEB-INF/toolbox.xml";
        File file = new File(toolboxXml);
        if (file.exists()) {
            XMLToolboxManager manager = new XMLToolboxManager();
            try {
                manager.load(toolboxXml);
                Map toolbox = manager.getToolbox("");
                for (Object o : toolbox.keySet()) {
                    String key = (String) o;
                    Object value = toolbox.get(key);
                    ctx.put(key, value);
                }
                // Context context = manager.createContext();
                // myVelocityEngine.evaluate(context, myOutputWriter, "This is a $text.test", "Test template");
            } catch (Exception e) {
                s_log.warn("Ignoring toolbox.xml error: " + e.toString(), e);
            }
        }
    }
}
