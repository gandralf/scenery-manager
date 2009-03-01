package br.com.devx.scenery.manager;

import br.com.devx.scenery.SceneryFileException;
import br.com.devx.scenery.TemplateAdapter;
import br.com.devx.scenery.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SceneryManager {
    private SceneryFinder m_sceneryFinder;
    private String m_dataRoot;

    public SceneryManager(String sceneryXml, String dataRoot) throws SceneryManagerException {
        m_sceneryFinder = loadSceneryFinder(sceneryXml);
        m_dataRoot = dataRoot;
    }

    public SceneryManagerResult query(String baseURI, Map parameterMap, List<String> dataList, String template, Boolean adapt) throws SceneryManagerException {
        Scenery scenery = m_sceneryFinder.getScenery(baseURI, parameterMap);
        if (scenery == null) {
            scenery = new Scenery(dataList, template, null, null);
        }

        if (template != null) {
            scenery.setTemplate(template);
        }

        TemplateAdapter templateAdapter;
        if (dataList == null) {
            templateAdapter = buildTemplateAdapter(baseURI, parameterMap);
        } else {
            templateAdapter = buildTemplateAdapter(dataList);
            scenery.setDataList(dataList);
        }

        ScenerySet scenerySet = m_sceneryFinder.getSceneries(baseURI);
        if (adapt == null && scenerySet != null) {
            adapt = scenerySet.getAdapt();
        } else if (adapt == null) {
            adapt = Boolean.FALSE; // default
        }

        return new SceneryManagerResult(scenery, templateAdapter, scenerySet,
                adapt, m_sceneryFinder.getEncoding());
    }

    public TemplateAdapter buildTemplateAdapter(String baseURI, Map parameterMap) throws SceneryManagerException {
        Scenery foundScenery = m_sceneryFinder.getScenery(baseURI, parameterMap);
        if (foundScenery == null) {
            throw new IllegalArgumentException("Scenery not found for " + baseURI + " and parameters " + parameterMap);
        }

        List<String> dataList = foundScenery.getDataList();

        return buildTemplateAdapter(dataList);
    }

    private TemplateAdapter buildTemplateAdapter(List<String> dataList) throws SceneryManagerException {
        TemplateAdapter templateAdapter = new TemplateAdapter();
        if (dataList != null) {
            dataList = appendOnList(m_dataRoot + "/", dataList);

            for (String data : dataList) {
                try {
                    Reader reader;
                    if (m_sceneryFinder.getEncoding() == null) {
                        reader = new FileReader(data);
                    } else {
                        reader = new InputStreamReader(new FileInputStream(data), m_sceneryFinder.getEncoding());
                    }
                    try {
                        TemplateAdapter currentTemplateAdapter = TemplateAdapter.load(reader);

                        templateAdapter.putAll(currentTemplateAdapter);
                    } finally {
                        reader.close();
                    }
                } catch (ParseException e) {
                    throw new SceneryFileException(data, e);
                } catch (IOException e) {
                    throw new SceneryManagerException(e);
                }
            }
        }

        return templateAdapter;
    }

    private SceneryFinder loadSceneryFinder(String sceneryXml) throws SceneryManagerException {
        SceneryFinder result = new SceneryFinder();
        if (sceneryXml != null) {
            try {
                result.load(sceneryXml);
            } catch (ParserConfigurationException e) {
                throw new SceneryManagerException(e);
            } catch (SAXException e) {
                throw new SceneryManagerException(e);
            } catch (IOException e) {
                throw new SceneryManagerException(e);
            }
        }

        return result;
    }

    private List<String> appendOnList(String prefix, List<String> sufixes) {
        List<String> result = new ArrayList<String>(sufixes.size());
        for (String sufix : sufixes) {
            result.add(prefix + sufix);
        }
        return result;
    }
}
