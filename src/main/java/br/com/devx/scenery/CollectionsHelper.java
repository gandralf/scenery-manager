package br.com.devx.scenery;

import java.util.List;
import java.util.ArrayList;

/**
 * Collections utility class
 */
public class CollectionsHelper {
    public static List makeList(String[] array) {
        List result;
        if (array != null) {
            result = new ArrayList(array.length);
            for (int i = 0; i < array.length; i++) {
                result.add(array[i]);
            }
        } else {
            result = null;
        }
        return result;
    }
}
