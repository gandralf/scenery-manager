package br.com.devx.scenery.web;

import br.com.devx.scenery.web.templates.CustomTemplateHandler;
import br.com.devx.scenery.web.templates.FreemarkerTemplateHandler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * todo check path and URL
 */
public class TargetApp {
    private String m_path;
    private String m_url;
    private ClassLoader m_classLoader;
    private List<CustomTemplateHandler> templateHandlers;

    public TargetApp(String path) {
        setPath(path);
        CustomTemplateHandler[] standardTemplateHandlers = new CustomTemplateHandler[] {
                new FreemarkerTemplateHandler(),
                new br.com.devx.scenery.web.templates.VelocityTemplateHandler()
        };
        templateHandlers = new ArrayList<CustomTemplateHandler>();
        templateHandlers.addAll(Arrays.asList(standardTemplateHandlers));
    }

    public String getPath() {
        return m_path;
    }

    public void setPath(String path) {
        // preconditions
        try {
            File file = new File(path).getCanonicalFile();
            path = file.getCanonicalPath();
            if (!file.exists() || !file.isDirectory()) {
                throw new IllegalArgumentException("Directory not found: " + file.getAbsolutePath());
            }

            if (m_url == null || isSync(m_path, m_url)) {
                m_url = pathToUrl(path);
            }

            m_path = path;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        setupClassLoader();
    }

    private void setupClassLoader() {
        m_classLoader = new TargetAppClassLoader(m_path);
    }

    private boolean isSync(String path, String url) throws MalformedURLException {
        return pathToUrl(path).equals(url);
    }

    private String pathToUrl(String path) throws MalformedURLException {
        File file = new File(path);
        return file.toURI().toURL().toString();
    }

    public String getUrl() {
        return m_url;
    }

    public void setAlternativeUrl(String url) {
        m_url = url;
    }

    public ClassLoader getClassLoader() {
        return m_classLoader;
    }

    public boolean hasSceneryXml() {
        try {
            File file = new File(m_path + "/WEB-INF/scenery.xml").getCanonicalFile();
            return file.exists() && file.canRead();
        } catch (IOException e) {
            return false;
        }
    }

    public List<CustomTemplateHandler> getTemplateHandlers() {
        return templateHandlers;
    }

    public void addTemplateHandler(CustomTemplateHandler templateHandler) {
        templateHandlers.add(templateHandler);
    }

    public void addTemplateHandlers(String handlers) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String[] classes = handlers.split(",");
        for (String className: classes) {
            addTemplateHandler((CustomTemplateHandler) Class.forName(className).newInstance());
        }
    }
}
