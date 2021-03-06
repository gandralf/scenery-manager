package br.com.devx.scenery;

import br.com.devx.scenery.web.AppsConfig;
import br.com.devx.scenery.web.TargetApp;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainTest extends TestCase {
    private MainThread mainThread;

    @Override
    protected void setUp() throws Exception {
        mainThread = new MainThread();
    }

    @Override
    protected void tearDown() throws Exception {
        TargetApp app = AppsConfig.getInstance().getTargetApp();
        app.setDataFilesPath(null);
        mainThread.stopServer();
    }

    public void testPort() throws InterruptedException, IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        mainThread.start("-l 8000");
        URLConnection urlConnection = new URL("http://localhost:8000/").openConnection();
        urlConnection.getInputStream();
    }

    public void testPath() throws InterruptedException, IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        mainThread.start("-p src/test/webapp");
        String sb = fetch("http://localhost:9090/velocity.do");
        assertTrue(sb.contains("Hello"));
    }

    public void testScnRoot() throws InterruptedException, IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        mainThread.start("-p src/test/webapp -s src/test/webapp/scn");
        String sb = fetch("http://localhost:9090/velocity.do");
        assertTrue(sb.contains("Hello, alternative scn root"));
    }

    public void testCustomTemplateHandler() throws InterruptedException, IOException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        mainThread.start("-p src/test/webapp -t br.com.devx.scenery.web.chanchito.ChanchitoTemplateHandler");
        String sb = fetch("http://localhost:9090/custom-template.do");
        assertTrue(sb.contains("Hello, Mr. Custom Template"));
    }

    public void testNoTemplateHandler() throws InterruptedException, IOException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        mainThread.start("-p src/test/webapp");
        String sb = fetch("http://localhost:9090/no-template.do");
        assertTrue(sb.contains("No template handler for .txt files"));
    }

    private String fetch(String url) throws IOException {
        InputStream inputStream = new URL(url).openStream();
        StringBuilder sb = new StringBuilder();
        byte[] buff = new byte[1024];
        int buffSize;
        while ((buffSize = inputStream.read(buff)) != -1) {
            sb.append(new String(buff, 0, buffSize));
        }

        return sb.toString();
    }

    private static class MainThread extends Thread {
        public Exception failed;
        private Main main;
        public MainThread() {
            super("server");
        }

        public void start(String args) throws InterruptedException, IOException, ClassNotFoundException,
                IllegalAccessException, InstantiationException {
            main = new Main(args.split("\\s+"));
            start();
            while (!main.ready()) {
                sleep(100);
            }
        }

        @Override
        public void run() {
            try {
                main.run();
            } catch (Exception e) {
                failed = e;
            }
        }

        public void stopServer() throws Exception {
            main.stop();
            while (main.ready()) {
                System.out.println("omg!!!!!!!!!!!!!!!!!");
                sleep(100);
            }
        }
    }
}
