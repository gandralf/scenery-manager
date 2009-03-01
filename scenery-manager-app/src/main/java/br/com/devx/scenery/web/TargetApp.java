package br.com.devx.scenery.web;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * todo check path and URL
 */
public class TargetApp {
    private static final Logger s_log = Logger.getLogger(TargetApp.class);

    private String m_path;
    private String m_url;
    private ClassLoader m_classLoader;

    public TargetApp(String path) {
        m_path = path;
        m_url = pathToUrl(path);
    }

    public String getPath() {
        return m_path;
    }

    public void setPath(String path) {
        if (isSync(m_path, m_url)) {
            m_url = pathToUrl(path);
        }

        m_path = path;

        setupClassLoader();
    }

    private void setupClassLoader() {
        m_classLoader = new TargetAppClassLoader(m_path);
    }

    private boolean isSync(String path, String url) {
        return pathToUrl(path).equals(url);
    }

    private String pathToUrl(String path) {
        try {
            return "file://" + new File(path).getCanonicalPath();
        } catch (IOException e) {
            s_log.warn(e.toString(), e);
            return "";
        }
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
}
