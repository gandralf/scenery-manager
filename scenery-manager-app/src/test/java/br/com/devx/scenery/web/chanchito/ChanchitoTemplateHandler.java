package br.com.devx.scenery.web.chanchito;

import br.com.devx.scenery.TemplateAdapter;
import br.com.devx.scenery.web.templates.CustomTemplateHandler;
import br.com.devx.scenery.web.templates.FreemarkerTemplateHandler;
import br.com.devx.scenery.web.templates.TemplateHanlerException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ChanchitoTemplateHandler implements CustomTemplateHandler {
    public final Logger log = Logger.getLogger(ChanchitoTemplateHandler.class);

    public boolean handle(String targetPath, String template, String encoding, PrintWriter out,
                          TemplateAdapter templateAdapter, boolean adapt)
            throws IOException, TemplateHanlerException {
        boolean result = false;
        if (template.endsWith(".xml")) {
            try {
                ChanchitoXml xml = new ChanchitoXml(new File(targetPath, template).getCanonicalPath());
                String chanchitoFtlContent = xml.getZoneContent();
                if (chanchitoFtlContent != null) {
                    render(targetPath, chanchitoFtlContent, encoding, templateAdapter, out, adapt);
                    result = true;
                }
            } catch (IOException e) {
                log.debug("Error reading xml file (" + template + "): " + e.getMessage());
            } catch (SAXException e) {
                log.debug("Error reading xml file (" + template + "): " + e.getMessage());
            } catch (ParserConfigurationException e) {
                log.debug("Error reading xml file (" + template + "): " + e.getMessage());
            }
        }

        return result;
    }

    private void render(String targetPath, String templateContent, String encoding, TemplateAdapter values, PrintWriter out, boolean adapt) throws TemplateHanlerException, IOException {
        FreemarkerTemplateHandler freemarkerTemplateHandler = new FreemarkerTemplateHandler();
        freemarkerTemplateHandler.handleContent(targetPath, templateContent, encoding, out, values, adapt);
    }
}
