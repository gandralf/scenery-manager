package br.com.devx.scenery.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BrowseDirectory {
    private String name;
    private String canonicalName;
    private String parent;
    private boolean ready;
    private ArrayList<BrowseDirectory> subdirs;

    public BrowseDirectory(File dir, boolean scanSubdirs) throws IOException {
        dir = new File(dir.getCanonicalPath());
        name = dir.getName();
        canonicalName = dir.getCanonicalPath();
        parent = dir.getParent();
        File sceneryXml = new File(dir, "WEB-INF/scenery.xml");

        ready = sceneryXml.exists() && sceneryXml.canRead();

        if (scanSubdirs) {
            scanSubdirs(dir);
        } else {
            subdirs = null;
        }
    }

    public String getName() {
        return name;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public String getParent() {
        return parent;
    }

    public boolean isReady() {
        return ready;
    }

    public ArrayList<BrowseDirectory> getSubdirs() {
        return subdirs;
    }

    private void scanSubdirs(File dir) throws IOException {
        ArrayList<BrowseDirectory> result = new ArrayList<BrowseDirectory>();
        for(File child: dir.listFiles()) {
            if (child.isDirectory()) {
                result.add(new BrowseDirectory(child, false));
            }
        }

        subdirs = result;
    }
}
