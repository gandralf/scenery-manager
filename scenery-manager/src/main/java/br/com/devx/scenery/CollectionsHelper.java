package br.com.devx.scenery;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Collections utility class
 */
public class CollectionsHelper {
    public static List<String> makeList(String[] array) {
        List<String> result;
        if (array != null) {
            result = new ArrayList<String>(array.length);
            result.addAll(Arrays.asList(array));
        } else {
            result = null;
        }
        return result;
    }
}
