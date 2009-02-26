package br.com.devx.scenery;

import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Handler;
import br.com.devx.scenery.web.SceneryFilter;
import br.com.devx.scenery.web.ConfigServlet;
import br.com.devx.scenery.web.AppsConfig;
import br.com.devx.scenery.web.TargetApp;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        int i = 0;
        TargetApp app = AppsConfig.getInstance().getTargetApp();

        while (i < args.length) {
            String arg = args[i];
            if ("-l".equals(arg)) {
                port = Integer.parseInt(args[++i]);
            } else if ("-p".equals(arg)) {
                app.setPath(args[++i]);
            } else if ("-u".equals(arg)) {
                app.setUrl(args[++i]);
            } else {
                System.err.println("Usage: sm [-l port] [-n name] [-p path] [-u url]");
            }

            i++;
        }

        Server server = new Server(port);
        Context root = new Context(server,"/", Context.SESSIONS);
        root.addFilter(new FilterHolder(new SceneryFilter()), "/*", Handler.DEFAULT);
        root.addServlet(new ServletHolder(new ConfigServlet()), "/*");
        server.start();
    }
}
