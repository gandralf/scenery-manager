package br.com.devx.scenery.web;

public class AppsConfig {
    private static AppsConfig s_instance = new AppsConfig();

    private TargetApp m_targetApp;

    private AppsConfig() {
        reset();
    }

    public void reset() {
        m_targetApp = new TargetApp(".");
    }

    public static AppsConfig getInstance() {
        return s_instance;
    }

    public TargetApp getTargetApp() {
        return m_targetApp;
    }
}
