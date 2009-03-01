package br.com.devx.scenery.web;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class TargetAppClassLoader extends ClassLoader {
    private static final Logger s_log = Logger.getLogger(TargetAppClassLoader.class);
    private File m_webAppPath;

    public TargetAppClassLoader(String path) {
        super(TargetAppClassLoader.class.getClassLoader());
        m_webAppPath = new File(path);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            InputStream in = openClass(name);
            try {
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int len;
                while ((len = in.read(buff)) != -1) {
                    data.write(buff, 0, len);
                }

                byte[] b = data.toByteArray();
                return defineClass(name, b, 0, b.length);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            s_log.error(e.getMessage(), e);
            throw new ClassNotFoundException(name + ": " + e.getMessage());
        }
    }

    private InputStream openClass(String name) throws IOException, ClassNotFoundException {
        InputStream result = null;
        String fqfName = name.replaceAll("\\.", "/") + ".class";

        // Try in WEB-INF/classes
        File file = new File(m_webAppPath.getCanonicalPath() + "/WEB-INF/classes/" + fqfName);
        if (file.exists()) {
            s_log.info("Loading class " + name + " from " + file);
            result = new FileInputStream(file);
        } else {
            // try WEB-INF/lib/*.jar
            File webInfLib = new File(m_webAppPath.getCanonicalPath() + "/WEB-INF/lib/");
            if (webInfLib.exists() && webInfLib.isDirectory()) {
                File[] files = webInfLib.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jar");
                    }
                });
                for (int i = 0; i < files.length && result == null; i++) {
                    result = openClassFromJar(files[i], name);
                }
            }
        }

        if (result == null) {
            throw new ClassNotFoundException(name);
        }
        return result;
    }


    private InputStream openClassFromJar(File jar, String fqfName) throws IOException, ClassNotFoundException {
        InputStream result = null;
        JarFile jarFile = new JarFile(jar);
        ZipEntry zipEntry = jarFile.getEntry(fqfName.replaceAll("\\.", "/") + ".class");
        if (zipEntry != null) {
            s_log.debug("Class found on " + jar);
            return jarFile.getInputStream(zipEntry);
        }

        return result;
    }
}
