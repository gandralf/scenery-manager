package br.com.devx.scenery;

import br.com.devx.scenery.parser.ParseException;
import br.com.devx.scenery.parser.SceneryParser;
import br.com.devx.test.JUnitHelper;
import junit.framework.TestCase;

import java.io.StringReader;
import java.util.*;
import java.text.NumberFormat;

public class TemplateAdapterTest extends TestCase {
    public TemplateAdapterTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
    }

    public void testEquals() throws ParseException {
        String srcA =
                "mapValue = map: {\n" +
                "    \"a\" = 1415, \n" +
                "    \"b\" = 1617, \n" +
                "    \"x\" = {\n" +
                "        message = \"Hello again...\";\n" +
                "    }\n" +
                "};\n" +
                "collectionValue = collection: { \n" +
                "    789,\n" +
                "    1011,\n" +
                "    {\n" +
                "        message = \"Hello again...\";\n" +
                "    }\n" +
                "};\n" +
                "otherAdapter = {\n" +
                "    message = \"Hello again...\";\n" +
                "};\n" +
                "booleanValue = false;\n" +
                "intValue = 123;\n" +
                "doubleValue = 123.45;\n" +
                "arrayValue = array: { 123, 456 };\n" +
                "stringValue = \"Hello, world!\";\n";
        String srcB =
                "arrayValue = array: { 123, 456 };\n" +
                "otherAdapter = {\n" +
                "    message = \"Hello again...\";\n" +
                "};\n" +
                "collectionValue = collection: { \n" +
                "    789,\n" +
                "    1011,\n" +
                "    {\n" +
                "        message = \"Hello again...\";\n" +
                "    }\n" +
                "};\n" +
                "booleanValue = false;\n" +
                "intValue = 123;\n" +
                "mapValue = map: {\n" +
                "    \"a\" = 1415, \n" +
                "    \"b\" = 1617, \n" +
                "    \"x\" = {\n" +
                "        message = \"Hello again...\";\n" +
                "    }\n" +
                "};\n" +
                "doubleValue = 123.45;\n" +
                "stringValue = \"Hello, world!\";\n";

        TemplateAdapterTestHelper.assertEquals(srcA, srcB);
    }

    public void testToString() throws ParseException {
        String expectedStr =
                "mapValue = map: {\n" +
                "    \"a\" = 1415, \n" +
                "    \"b\" = 1617, \n" +
                "    \"x\" = {\n" +
                "        message = \"Hello again...\";\n" +
                "    }\n" +
                "};\n" +
                "collectionValue = collection: { \n" +
                "    789,\n" +
                "    1011,\n" +
                "    {\n" +
                "        message = \"Hello again...\";\n" +
                "    }\n" +
                "};\n" +
                "otherAdapter = {\n" +
                "    message = \"Hello again...\";\n" +
                "};\n" +
                "booleanValue = false;\n" +
                "intValue = 123;\n" +
                "doubleValue = 123.45;\n" +
                "stringValue = \"Hello, world!\";\n" +
                "arrayValue = array: { 123, 456 };\n";

        TemplateAdapter expected = new SceneryParser(new StringReader(expectedStr)).parse();
        TemplateAdapter actual = sampleTemplateAdapter();

        TemplateAdapterTestHelper.assertEquals(expected, actual);
    }

    public void testWrappedClassToString() throws ParseException {
        TemplateAdapter actual = new TemplateAdapter();
        actual.put("mapProperty", 100);

        WrapperTestTarget target = new WrapperTestTarget(4, "Hello, world!", new GregorianCalendar(1972, 6, 20).getTime(), false);
        actual.wrapp(target);

        String expectedStr =
                "mapProperty = 100; " +
                "dateValue = 1972-07-20; " +
                "booleanValue = false; " +
                "stringValue = \"Hello, world!\"; " +
                "intValue = 4;";
        TemplateAdapter expected = new SceneryParser(new StringReader(expectedStr)).parse();

        TemplateAdapterTestHelper.assertEquals(expected, actual);
    }

    public void testGetObj() {
        TemplateAdapter templateAdapter = sampleTemplateAdapter();
        assertNull(templateAdapter.get("notFound"));
        assertEquals(new Integer(123), templateAdapter.get("intValue"));
        assertEquals(new Double(123.45), templateAdapter.get("doubleValue"));
        assertEquals("Hello, world!", templateAdapter.get("stringValue"));

        TemplateAdapter otherAdapter = (TemplateAdapter) templateAdapter.get("otherAdapter");
        assertEquals("Hello again...", otherAdapter.get("message"));

        assertEquals(123, ((int[]) templateAdapter.get("arrayValue"))[0]);
        assertEquals(456, ((int[]) templateAdapter.get("arrayValue"))[1]);
    }

    public void testGetPrimitive() {
        TemplateAdapter templateAdapter = sampleTemplateAdapter();
        assertNull(templateAdapter.adapt("notFound"));
        assertEquals("123", templateAdapter.adapt("intValue"));
        assertEquals(JUnitHelper.doubleString(123.45), templateAdapter.adapt("doubleValue"));
        assertEquals("Hello, world!", templateAdapter.adapt("stringValue"));
        assertEquals("false", templateAdapter.adapt("booleanValue"));

        TemplateAdapter otherAdapter = (TemplateAdapter) templateAdapter.get("otherAdapter");
        assertEquals("Hello again...", otherAdapter.adapt("message"));
    }

    public void testCDataString() throws ParseException {
        String expected = "Hello, \"world\"!\n";
        String text = "message = <![CDATA[" + expected + "]]>;";
        TemplateAdapter templateAdapter = TemplateAdapter.load(new StringReader(text));
        String actual = (String) templateAdapter.adapt("message");
        assertEquals(expected, actual);
    }

    public void testWrappGenericClass() throws Exception {
        TemplateAdapter templateAdapter = new TemplateAdapter();
        templateAdapter.put("mapProperty", 100);

        WrapperTestTarget target = new WrapperTestTarget(4, "Hello, world!", new GregorianCalendar(1972, 6, 20).getTime(), false);
        templateAdapter.wrapp(target);

        assertTrue(templateAdapter.containsProperty("intValue"));
        assertTrue(!templateAdapter.containsProperty("notFoundProperty"));
        assertTrue(templateAdapter.containsProperty("mapProperty"));

        assertEquals("4", templateAdapter.adapt("intValue"));
        assertEquals("Hello, world!", templateAdapter.adapt("stringValue"));
        assertEquals(JUnitHelper.dateString(1972, 6, 20), templateAdapter.adapt("dateValue"));
        assertEquals("100", templateAdapter.adapt("mapProperty"));
        assertEquals("false", templateAdapter.adapt("booleanValue"));
    }

    public void testGetMap() {
        HashMap hashMap = new HashMap();
        hashMap.put("intValue", new Integer(123));

        TemplateAdapter templateAdapter = new TemplateAdapter();
        templateAdapter.put("testMap", hashMap);
        Object o = templateAdapter.adapt("testMap");

        assertTrue(o instanceof Map);
        Map mapAdapter = (Map) o;

        assertEquals("123", mapAdapter.get("intValue"));
    }

    public void testWrappMap() throws Exception {
        HashMap hashMap = new HashMap();
        hashMap.put("intValue", new Integer(123));

        TemplateAdapter templateAdapter = new TemplateAdapter();
        templateAdapter.wrapp(hashMap);

        assertEquals("123", templateAdapter.adapt("intValue"));
    }

    public void testGetCollection() throws Exception {
        Collection collection = new ArrayList();
        collection.add(new Integer(123)); // Primitivo
        WrapperTestTarget wrappedClass = new WrapperTestTarget(4, "Hello, world!", new GregorianCalendar(1972, 6, 20).getTime(), false);
        collection.add(wrappedClass);
        HashMap map = new HashMap();
        map.put("intValue", new Integer(123));
        collection.add(map);

        TemplateAdapter templateAdapter = new TemplateAdapter();
        templateAdapter.put("collectionValue", collection);

        Object o = templateAdapter.adapt("collectionValue");
        assertTrue(o instanceof Collection);
        Collection wrappedResult = (Collection) o;

        assertEquals(3, wrappedResult.size());

        Iterator i = wrappedResult.iterator();
        assertEquals("123", i.next());

        TemplateAdapter wrappedClassAdapter = (TemplateAdapter) i.next();
        assertEquals("4", wrappedClassAdapter.adapt("intValue"));

        assertEquals("123", ((Map) i.next()).get("intValue"));
    }

    public void testPrimitiveArray() {
        TemplateAdapter templateAdapter = new TemplateAdapter();

        int[] primitiveArray = { 123, 456 };
        templateAdapter.put("primitiveArray", primitiveArray);

        Object o = templateAdapter.adapt("primitiveArray");
        assertTrue(o instanceof String[]);
        String[] wrappedPrimitiveArray = (String[]) o;
        assertEquals(2, wrappedPrimitiveArray.length);
        assertEquals("123", wrappedPrimitiveArray[0]);
        assertEquals("456", wrappedPrimitiveArray[1]);
    }

    public void testWrappedArray() {
        HashMap map = new HashMap();
        map.put("intValue", new Integer(123));
        WrapperTestTarget wrappedClass = new WrapperTestTarget(4, "Hello, world!", new GregorianCalendar(1972, 6, 20).getTime(), false);
        Object[] objectArray = { new Integer(123), wrappedClass, map };

        TemplateAdapter templateAdapter = new TemplateAdapter();
        templateAdapter.put("objectArray", objectArray);

        Object o = templateAdapter.adapt("objectArray");
        assertTrue(o instanceof Object[]);
        Object[] wrappedResult = (Object[]) o;

        assertEquals(3, wrappedResult.length);

        assertEquals("123", wrappedResult[0]);

        TemplateAdapter wrappedClassAdapter = (TemplateAdapter) wrappedResult[1];
        assertEquals("4", wrappedClassAdapter.adapt("intValue"));

        assertEquals("123", ((Map) wrappedResult[2]).get("intValue"));
    }

    public void testGetProperties() {
        TemplateAdapter templateAdapter = new TemplateAdapter();
        templateAdapter.put("greetings", "Hello, world!");
        templateAdapter.put("someValue", new Integer(456));

        Collection properties = templateAdapter.getProperties();
        assertEquals(2, properties.size());
        assertTrue(properties.contains("greetings"));
        assertTrue(properties.contains("someValue"));

        WrapperTestTarget target = new WrapperTestTarget(4, "Hello, world!", new GregorianCalendar(1972, 6, 20).getTime(), false);
        templateAdapter.wrapp(target);

        properties = templateAdapter.getProperties();
        assertEquals(6, properties.size());
        assertTrue(properties.contains("intValue"));
        assertTrue(properties.contains("stringValue"));
        assertTrue(properties.contains("dateValue"));
        assertTrue(properties.contains("booleanValue"));

        templateAdapter.put("anotherProperty", "Hello, world!");
        properties = templateAdapter.getProperties();
        assertEquals(7, properties.size());
        assertTrue(properties.contains("anotherProperty"));
    }

    public void testCustomFormatStrategy() throws ParseException {
        String src =
                "root = { \n" +
                "   defaultFormat = -23.226566; \n" +
                "   rootValue = -29.121314; \n" +
                "   inner = { \n" +
                "       innerValue = -0.3023444; \n" +
                "   }; \n" +
                "   rootValue2 = 12.121314; \n" +
                "};";

        TemplateFormatStrategy formatStrategy = new TemplateFormatStrategy() {
            protected String formatNumber(String fieldName, Number value) {
                if ("rootValue".equals(fieldName)) {
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(6);
                    return nf.format(value);
                } else if ("innerValue".equals(fieldName)) {
                    NumberFormat nf = NumberFormat.getPercentInstance();
                    nf.setMaximumFractionDigits(4);
                    return nf.format(value);
                } else if ("rootValue2".equals(fieldName)) {
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMaximumFractionDigits(4);
                        return nf.format(value);
                } else {
                    return super.formatNumber(fieldName, value);
                }
            }
        };

        TemplateAdapter templateAdapter = TemplateAdapter.load(new StringReader(src));
        templateAdapter.setFormatStrategy(formatStrategy);

        final TemplateAdapter root = templateAdapter.getAdapter("root");
        assertEquals(JUnitHelper.doubleString(-23.226566), root.adapt("defaultFormat"));
        assertEquals(JUnitHelper.doubleString(-29.121314, 6, 6), root.adapt("rootValue"));
        assertEquals(JUnitHelper.doubleString(-30.2344, 4, 4) + "%", root.getAdapter("inner").adapt("innerValue"));
        assertEquals(JUnitHelper.doubleString(12.1213, 4, 4), root.adapt("rootValue2"));
    }

    private TemplateAdapter sampleTemplateAdapter() {
        TemplateAdapter otherAdapter = new TemplateAdapter();
        otherAdapter.put("message", "Hello again...");
        Collection collectionValue = new ArrayList();
        collectionValue.add(new Integer(789));
        collectionValue.add(new Integer(1011));
        collectionValue.add(otherAdapter);
        Map mapValue = new HashMap();
        mapValue.put("x", otherAdapter);
        mapValue.put("b", new Integer(1617));
        mapValue.put("a", new Integer(1415));

        TemplateAdapter templateAdapter = new TemplateAdapter();
        templateAdapter.put("stringValue", "Hello, world!");
        templateAdapter.put("intValue", 123);
        templateAdapter.put("doubleValue", 123.45);
        templateAdapter.put("booleanValue", false);
        templateAdapter.put("arrayValue", new int[] { 123, 456 });
        templateAdapter.put("otherAdapter", otherAdapter);
        templateAdapter.put("collectionValue", collectionValue);
        templateAdapter.put("mapValue", mapValue);

        return templateAdapter;
    }

    /**
     * A template adapter must be a map
     */
    public void testIsMap() {
        TemplateAdapter templateAdapter = new TemplateAdapter();
        templateAdapter.put("foo", "bar");

        assertTrue("A template adapter must implement a map interface!", templateAdapter instanceof Map);
        assertTrue(templateAdapter instanceof Map);
        assertEquals("bar", templateAdapter.adapt("foo"));
    }
}
