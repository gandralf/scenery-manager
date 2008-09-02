package br.com.devx.scenery.manager;

import br.com.devx.scenery.manager.Scenery;

import java.util.*;

/*
 * User: agandra
 * Date: Dec 16, 2002
 * Time: 4:57:30 PM
 */

public class ScenerySet {
    private String m_defaultTemplate;
    private String m_defaultData;
    private String m_defaultDescription;
    private boolean m_adapt;
    private Collection m_templateTests = new ArrayList();
    private Collection m_dataTests = new ArrayList();

    public ScenerySet(String defaultTemplate, String defaultData, boolean adapt, String description) {
        m_defaultTemplate = defaultTemplate;
        m_defaultData = defaultData;
        m_defaultDescription = description;
        m_adapt = adapt;
    }

    public boolean getAdapt() {
        return m_adapt;
    }

    public Scenery getScenery(Map parameters) {
        Scenery result = new Scenery(getData(parameters), getTemplate(parameters), getDescription(parameters));

        return result;
    }

    private String getTemplate(Map parameters) {
        return findValue(m_defaultTemplate, parameters, m_templateTests);
    }

    private String getData(Map parameters) {
        return findValue(m_defaultData, parameters, m_dataTests);
    }

    private String getDescription(Map parameters) {
        String dataDescription = findDescription(m_dataTests, parameters);
        String templateDescription = findDescription(m_templateTests, parameters);
        if (dataDescription == null && templateDescription == null) {
            return m_defaultDescription;
        } else if (dataDescription != null && templateDescription == null) {
            return m_defaultDescription + " / " + dataDescription;
        } else {
            return m_defaultDescription + " / " + templateDescription;
        }
    }

    private String findDescription(Collection tests, Map parameters) {
        Iterator i = tests.iterator();
        while (i.hasNext()) {
            Testable test = (Testable) i.next();
            if (test.test(parameters)) {
                return test.getDescription();
            }
        }

        return null;
    }

    private String findValue(String defaultValue, Map parameters, Collection tests) {
        String result = defaultValue;
        Iterator i = tests.iterator();
        while (i.hasNext()) {
            Testable test = (Testable) i.next();
            if (test.test(parameters)) {
                return test.getResult();
            }
        }

        return result;
    }

    public void setTemplate(String test, String file, String description) {
        m_templateTests.add(new Testable(description, test, file));
    }

    public void setData(String test, String file, String description) {
        m_dataTests.add(new Testable(description, test, file));
    }

    public Collection getSceneries() {
        ArrayList result = new ArrayList();
        result.add(new Scenery(m_defaultData, m_defaultTemplate, m_defaultDescription, null));
        HashMap tests = new HashMap();

        Iterator i = m_dataTests.iterator();
        while (i.hasNext()) {
            Testable testable = (Testable) i.next();
            Scenery scenery = new Scenery(testable.getResult(), m_defaultTemplate, testable.getDescription(), testable.getTest());
            tests.put(testable.getTest(), scenery);
        }

        i = m_templateTests.iterator();
        while (i.hasNext()) {
            Testable testable = (Testable) i.next();
            Scenery scenery = (Scenery) tests.get(testable.getTest());
            if (scenery == null) {
                scenery = new Scenery(m_defaultData, testable.getResult(), testable.getDescription(), testable.getTest());
            } else {
                scenery.setTemplate(testable.getResult());
                if (scenery.getDescription() != null) {
                    scenery.setDescription(scenery.getDescription() + " / " + testable.getDescription());
                } else {
                    scenery.setDescription(testable.getDescription());
                }
            }

            tests.put(testable.getTest(), scenery);
        }

        result.addAll(tests.values());

        return result;
    }

    class Testable {
        private String m_test;
        private String m_result;
        private String m_description;

        public Testable(String description, String test, String result) {
            m_description = description;
            m_test = test;
            m_result = result;
        }

        public boolean test(Map parameters) {
            String testKey = m_test.split("=")[0].trim();
            String[] parameterValues = (String[]) parameters.get(testKey);
            if (parameterValues != null) {
                for (int i = 0; i < parameterValues.length; i++) {
                    if (m_test.matches(testKey + "\\s*=\\s*" + parameterValues[i])) {
                        return true;
                    }
                }
            }

            return false;
        }

        public String getResult() {
            return m_result;
        }

        public String getTest() {
            return m_test;
        }

        public String getDescription() {
            if (m_description != null) {
                return m_description;
            } else {
                return m_test;
            }
        }
    }
}
