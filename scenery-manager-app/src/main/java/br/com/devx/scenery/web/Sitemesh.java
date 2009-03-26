package br.com.devx.scenery.web;

public interface Sitemesh {
    public boolean isActive();

    String getTemplate();

    void decorate(String html);

    String get(String tag);
}
