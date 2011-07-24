package br.com.devx.scenery.web;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * todo check path and URL
 */
public class TargetApp {
    private static final Logger s_log = Logger.getLogger(TargetApp.class);

    private String m_path;
    private String m_url;
    private ClassLoader m_classLoader;

    public TargetApp(String path) {
        setPath(path);
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

    public void setUrl(String url) {
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
}
