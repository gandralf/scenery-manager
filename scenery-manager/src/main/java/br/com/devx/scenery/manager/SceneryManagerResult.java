package br.com.devx.scenery.manager;

import br.com.devx.scenery.TemplateAdapter;

/*
 * User: agandra
 * Date: 13/05/2003
 * Time: 17:31:35
 */
public class SceneryManagerResult {
    private Scenery m_scenery;
    private TemplateAdapter m_templateAdapter;
    private ScenerySet m_scenerySet;
    private boolean m_adapt;
    private String m_encoding;

    public SceneryManagerResult() {
    }

    public SceneryManagerResult(Scenery scenery, TemplateAdapter templateAdapter, ScenerySet scenerySet, boolean adapt,
                                String encoding) {
        m_scenery = scenery;
        m_templateAdapter = templateAdapter;
        m_scenerySet = scenerySet;
        m_adapt = adapt;
        m_encoding = encoding;
    }

    public TemplateAdapter getTemplateAdapter() {
        return m_templateAdapter;
    }

    public void setTemplateAdapter(TemplateAdapter templateAdapter) {
        m_templateAdapter = templateAdapter;
    }

    public Scenery getScenery() {
        return m_scenery;
    }

    public void setScenery(Scenery scenery) {
        m_scenery = scenery;
    }

    public ScenerySet getScenerySet() {
        return m_scenerySet;
    }

    public void setScenerySet(ScenerySet scenerySet) {
        m_scenerySet = scenerySet;
    }

    public boolean isAdapt() {
        return m_adapt;
    }

    public void setAdapt(boolean adapt) {
        m_adapt = adapt;
    }

    public String getEncoding() {
        return m_encoding;
    }

    public void setEncoding(String encoding) {
        m_encoding = encoding;
    }
}
