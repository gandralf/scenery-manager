package br.com.devx.scenery.web;

import br.com.devx.scenery.TemplateAdapter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.FileInputStream;

/**
 * @author agandra
 *     Jul 14, 2003
 */
public class NullTemplateHandler implements TemplateHandler {
    private String m_webRoot;

    public void init(ServletConfig config) throws ServletException {
        m_webRoot = config.getServletContext().getRealPath("/");
    }

    public boolean canHandle(String template) {
        return true;
    }

    public void handle(String template, String encoding, HttpServletRequest request, HttpServletResponse response,
                       TemplateAdapter templateAdapter, boolean adapt) throws IOException, ServletException {
        FileInputStream input = new FileInputStream(m_webRoot + "/" + template);
        try {
            ServletOutputStream out = response.getOutputStream();
            byte[] buff = new byte[512];
            int count;
            while ((count = input.read(buff)) != -1) {
                out.write(buff, 0, count);
            }
        } finally {
            input.close();
        }
    }
}
