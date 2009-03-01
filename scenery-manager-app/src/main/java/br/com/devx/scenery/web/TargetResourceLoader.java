package br.com.devx.scenery.web;

import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.commons.collections.ExtendedProperties;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Loads templates from webapp target
 */
public class TargetResourceLoader extends ResourceLoader {
    public void init(ExtendedProperties extendedProperties) {
    }

    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        File file = toFile(name);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException(file.getPath());
        }
    }

    public boolean isSourceModified(Resource resource) {
        return toFile(resource.getName()).lastModified() > resource.getLastModified();
    }

    public long getLastModified(Resource resource) {
        return toFile(resource.getName()).lastModified();
    }

    private File toFile(String name) {
        return new File(AppsConfig.getInstance().getTargetApp().getPath() + "/" + name);
    }
}
