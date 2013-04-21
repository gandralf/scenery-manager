package br.com.devx.scenery.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class IOHelper {
    public static void dump(PrintWriter out, String pathToFile) throws IOException {
        FileReader reader = new FileReader(pathToFile);
        try {
            char[] buffer = new char[1024];
            int size;
            while((size = reader.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
        } finally {
            reader.close();
        }
    }
}