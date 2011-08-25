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
import java.io.StringWriter;
import java.util.Map;

public class ChanchitoTemplateHandler implements CustomTemplateHandler {
    public final Logger log = Logger.getLogger(ChanchitoTemplateHandler.class);

    public boolean handle(String targetPath, String template, String encoding, PrintWriter out,
                          TemplateAdapter templateAdapter)
            throws IOException, TemplateHanlerException {
        boolean result = false;
        if (template.endsWith(".xml")) {
            try {
                ChanchitoXml xml = new ChanchitoXml(new File(targetPath, template).getCanonicalPath());
                String chanchitoFtlContent = xml.getViewContent();
                if (chanchitoFtlContent != null) {
                    String structure = xml.getStructure() != null ? xml.getStructure() : "/structures/structure.ftl";
                    StringWriter xmlFtlOut = new StringWriter();

                    FreemarkerTemplateHandler freemarkerTemplateHandler = new FreemarkerTemplateHandler();

                    freemarkerTemplateHandler.handleContent(targetPath, template, chanchitoFtlContent, encoding,
                            new PrintWriter(xmlFtlOut), templateAdapter);
                    templateAdapter.put("___zones_content", xmlFtlOut.toString().trim());

                    Map<String, String> zones = xml.getZones();
                    if (!zones.isEmpty()) {
                        for(Map.Entry<String, String> entry: zones.entrySet()) {
                            xmlFtlOut = new StringWriter();
                            freemarkerTemplateHandler.handleContent(targetPath, template, entry.getValue(), encoding,
                                    new PrintWriter(xmlFtlOut), templateAdapter);
                            templateAdapter.put("___zones_" + entry.getKey(), xmlFtlOut.toString());
                        }
                    }

                    freemarkerTemplateHandler.handle(targetPath, structure, encoding, out, templateAdapter);

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

}
