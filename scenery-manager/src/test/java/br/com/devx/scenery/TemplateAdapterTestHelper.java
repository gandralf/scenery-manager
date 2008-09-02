package br.com.devx.scenery;

import br.com.devx.scenery.parser.ParseException;
import br.com.devx.scenery.parser.SceneryParser;

import java.io.StringReader;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Array;

import junit.framework.AssertionFailedError;

/**
 * User: gandralf
 * Date: Dec 15, 2002
 * Time: 11:49:30 AM
 */
class TemplateAdapterTestHelper {
    public static void assertEquals(String expected, String actual) throws ParseException {
        SceneryParser parser = new SceneryParser(new StringReader(expected));
        TemplateAdapter a = parser.parse();
        parser.ReInit(new StringReader(actual));
        TemplateAdapter b = parser.parse();

        assertEquals(a, b);
    }

    public static void assertEquals(TemplateAdapter expected, TemplateAdapter actual) {
        if (!expected.equals(actual)) {
            Collection diferences = findDifferences(expected, actual);
            throw new AssertionFailedError(diferences.toString());
        }
    }

    public static void assertNotEquals(String expected, String actual) throws ParseException {
        SceneryParser parser = new SceneryParser(new StringReader(expected));
        TemplateAdapter a = parser.parse();
        parser.ReInit(new StringReader(actual));
        TemplateAdapter b = parser.parse();

        if (a.equals(b)) {
            throw new AssertionFailedError("Parameters are equals");
        }
    }

    private static Collection findDifferences(TemplateAdapter expected, TemplateAdapter actual) {
        Collection result = new ArrayList();

        Collection expectedProperties = expected.getProperties();
        Collection actualProperties = actual.getProperties();

        if (actualProperties.size() != expectedProperties.size()) {
            result.add("size differs: expected/actual = " + expectedProperties.size() + "/" + actualProperties.size());
        }

        Iterator i = expectedProperties.iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            Object expectedValue = expected.adapt(key);
            Object actualValue = actual.adapt(key);

            if ((expectedValue == null && actualValue != null) || !expectedValue.equals(actualValue)) {
                result.add(key + ": expected/actual = " + toString(expectedValue) + "/" + toString(actualValue));
            }
        }

        return result;
    }

    private static String toString(Object value) {
        if (value == null) {
            return "null";
        } else if (!value.getClass().isArray()) {
            return value.toString();
        } else {
            StringBuffer result = new StringBuffer("[ ");
            for (int i=0 ; i < Array.getLength(value); i++) {
                result.append(Array.get(value, i));
                if (i +1 < Array.getLength(value)) {
                    result.append(", ");
                }
            }
            result.append(" ]");

            return result.toString();
        }
    }
}
