package br.com.devx.scenery.manager;

import java.util.List;
import java.util.ArrayList;

/*
 * User: agandra
 * Date: Dec 17, 2002
 * Time: 1:05:45 PM
 */
public class Scenery {
    private String m_test;
    private List m_dataList;
    private String m_template;
    private String m_description;

    public Scenery() {
    }

    public Scenery(String data, String template, String description) {
        m_dataList = makeDataList(data);
        m_template = template;
        m_description = description;
        m_test = null;
    }

    public Scenery(String data, String template, String description, String test) {
        m_dataList = makeDataList(data);
        m_template = template;
        m_description = description;
        m_test = test;
    }

    public Scenery(List dataList, String template, String description, String test) {
        m_dataList = dataList;
        m_template = template;
        m_description = description;
        m_test = test;
    }

    private List makeDataList(String data) {
        if (data != null) {
            String[] dataFiles = data.split("\\s*,\\s*");
            List result = new ArrayList(dataFiles.length);
            for (int i = 0; i < dataFiles.length; i++) {
                result.add(dataFiles[i]);
            }
            return result;
        } else {
            return new ArrayList();
        }
    }

    public String getTest() {
        return m_test;
    }

    public List getDataList() {
        return m_dataList;
    }

    public void setDataList(List dataList) {
        m_dataList = dataList;
    }

    public String getTemplate() {
        return m_template;
    }

    public void setTemplate(String template) {
        m_template = template;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String description) {
        m_description = description;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scenery)) return false;

        final Scenery scenery = (Scenery) o;

        if (!m_dataList.equals(scenery.m_dataList)) return false;
        if (m_description != null ? !m_description.equals(scenery.m_description) : scenery.m_description != null) return false;
        if (m_template != null ? !m_template.equals(scenery.m_template) : scenery.m_template != null) return false;
        if (m_test != null ? !m_test.equals(scenery.m_test) : scenery.m_test != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (m_test != null ? m_test.hashCode() : 0);
        result = 29 * result + m_dataList.hashCode();
        result = 29 * result + (m_template != null ? m_template.hashCode() : 0);
        result = 29 * result + (m_description != null ? m_description.hashCode() : 0);
        return result;
    }
}
