package br.com.devx.scenery;

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

    public void testPort() throws InterruptedException, IOException {
        mainThread.start("-l 8000");
        URLConnection urlConnection = new URL("http://localhost:8000/").openConnection();
        urlConnection.getInputStream();
    }

    public void testPath() throws InterruptedException, IOException {
        mainThread.start("-p ../../test/webapp");
        String sb = fetch("http://localhost:8080/velocity.do");
        assertTrue(sb.contains("Hello"));
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

        public void start(String args) throws InterruptedException {
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
    }
}
