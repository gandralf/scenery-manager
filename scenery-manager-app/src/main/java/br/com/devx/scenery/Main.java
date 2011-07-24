package br.com.devx.scenery;

import org.apache.log4j.Logger;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Handler;
import br.com.devx.scenery.web.*;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    private Server server;

    public Main(String[] args) {
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
            } else if ("/?".equals(arg) || "/h".equals(arg) || "-?".equals(arg) || "-h".equals(arg) || "--help".equals(arg)) {
                usage();
                return;
            } else {
                usage();
                return;
            }

            i++;
        }

        log.info("Server ready. Access http://localhost:" + port + "/ to browse files at " + app.getPath());
        server = new Server(port);
        Context root = new Context(server,"/", Context.SESSIONS);
        root.addFilter(new FilterHolder(new SceneryFilter()), "/*", Handler.DEFAULT);
        root.addServlet(DummyServlet.class, "/*"); // Without a servlet, the filter won't work
    }

    public void run() throws Exception {
        server.start();
    }

    public boolean ready() {
        return server.isStarted();
    }

    public static void main(String[] args) throws Exception {
        new Main(args).run();
    }

    private static void usage() {
        System.err.println("Usage: scenery [-l port] [-p path] [-u url]\n" +
                "Where: \n" +
                "\t-l\tlistening port. Default = 8080\n" +
                "\t-p\tpath to target webapp. You can see and config that at config.do\n" +
                "\t-u\tURL to be accessed if a file isn't found on the given <path> (see -p). Default = file://<path>"
        );
    }
}
