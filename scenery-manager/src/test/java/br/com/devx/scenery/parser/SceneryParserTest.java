package br.com.devx.scenery.parser;
import br.com.devx.scenery.TemplateAdapter;
import br.com.devx.scenery.WrapperTestTarget;
import junit.framework.TestCase;

import java.io.StringReader;
import java.util.*;

import br.com.devx.scenery.parser.ParseException;
import br.com.devx.scenery.parser.SceneryParser;
import br.com.devx.test.JUnitHelper;

public class SceneryParserTest extends TestCase {
    public SceneryParserTest(String s) {
        super(s);
    }

    public void testIntValue() throws ParseException {
        doTest("intValue = 123;");
        doTest("intValue = -123;");
    }

    public void testDoubleValue() throws ParseException {
        doTest("doubleValue = 123.456;");
        doTest("doubleValue = -123.456;");
        doTest("latitude = -22.578753;");
    }

    public void testStringValue() throws ParseException {
        doTest("stringValue = \"Hello, world!\";");
    }

    public void testXStringValue() throws ParseException {
        doTest("stringValue = <![CDATA[Hello, \"world\"!]]>;");
    }

    public void testBooleanValue() throws ParseException {
        doTest("trueValue = true; falseValue = false; ");
    }

    public void testDateValue() throws ParseException {
        doTest("dateValue = 1972-07-20;");
    }

    public void testDateTimeValue() throws ParseException {
        doTest("dateValue = 2000-02-30 13:30;");
        doTest("dateValue = 2000-02-30 13:30:49;");
    }

    public void testInnerAdapter() throws ParseException {
        String test =
                "adapter = {\n" +
                "    innerAdapter = {\n" +
                "        message = \"Hello again...\";\n" +
                "    };\n" +
                "};";
        doTest(test);
    }

    public void testArray() throws ParseException {
        doTest("array = array: { };"); // Empty array
        doTest("array = array: { 123 };"); // One-element
        doTest("array = array: { 123, 456, 789 };"); // array of ints
        doTest("array = array: { true, true, false };"); // array of booleans
        doTest("array = array: { 123.0, 456.1, 789.2 };"); // array of doubles
        doTest("array = array: { \"Hello\", \"world\", \"!!!\" };"); // array of strings
        doTest("array = array: { 2002-10-02, 2002-11-02, 2002-12-02 };"); // array of date/time
        doTest("array = array: { { name = \"blah\"; }, { name = \"bleh\"; }, { name = \"blih\"; } };"); // array of adapters
        doTest("array = array: { array: { 1, 2 }, array: { 3, 4 }, array: { 5, 6 } };"); // array of arrays
        doTest("array = array: { collection: { 1, 2 }, collection: { 3, 4 } };"); // array of collections
        doTest("array = array: { map: { \"a\" = 1, \"b\" = 2 }, map: { \"z\" = 100 } };"); // array of maps
        try {
            doTest("array = array: { 123, 456.0, 2002-02-02 };");
            fail("Mixed values are not alloud");
        } catch(ParseException e) {
            // OK
        }
    }

    public void testCollection() throws ParseException {
        doTest("col = collection: { };"); // Empty
        doTest("col = collection: { 123 };"); // One-element
        doTest("col = collection: { 123, 456, 789 };"); // Like an array (same types)
        doTest("col = collection: { 123, 456.0, 2002-02-02 18:30, \"Hello world!\" };"); // Primitive types: mixed
        doTest("col = collection: { " + // Composite types
                "    { name = \"blah\"; }, " +
                "    array: { 1, 2, 3 }, " +
                "    collection: { 4, 5, 6 }, " +
                "    map: { \"a\" = \"A\", \"b\" = \"B\"  }" +
               "};");
    }

    public void testMap() throws ParseException {
        doTest("map = map: { };"); // Empty
        doTest("map = map: { 2002-10-02 = \"Gandrious\" };"); // One-element
        doTest("map = map: { 2002-10-02 = \"Gandrious\", 2002-10-01 = \"Eugenius\" };"); // More elements...
        doTest("map = map: { " + // Composite types
                "    \"blah\" = { id = 1; name = \"Guedesgo�a\"; }, " +
                "    \"bleh\" = { id = 2; name = \"Imbefil\"; } " +
               "};");
    }

    public void testSimpleValues() throws ParseException {
        String input =
                "intValue = 123;\n" +
                "doubleValue = 123.45;\n" +
                "trueValue = true;\n" +
                "falseValue = false;\n" +
                "stringValue = \"Hello, world!\";\n" +
                "dateValue = 2002-09-20;\n" +
                "dateTimeValue = 2002-09-20 15:30;\n" +
                "dateTimeSecValue = 2002-09-20 15:30:20;\n" +
                "emptyString = \"\";\n" +
                "nullValue = null;\n";

        TemplateAdapter scenery = doTest(input);

        assertEquals(new Integer(123), scenery.get("intValue"));
        assertEquals(new Double(123.45), scenery.get("doubleValue"));
        assertEquals(Boolean.TRUE, scenery.get("trueValue"));
        assertEquals(Boolean.FALSE, scenery.get("falseValue"));
        assertEquals(new Double(123.45), scenery.get("doubleValue"));
        assertEquals("Hello, world!", scenery.get("stringValue"));
        assertEquals(new GregorianCalendar(2002, 8, 20).getTime(), scenery.get("dateValue"));
        assertEquals(new GregorianCalendar(2002, 8, 20, 15, 30).getTime(), scenery.get("dateTimeValue"));
        assertEquals(new GregorianCalendar(2002, 8, 20, 15, 30, 20).getTime(), scenery.get("dateTimeSecValue"));
        assertEquals("", scenery.get("emptyString"));
        assertNull("", scenery.get("nullValue"));
    }

    public void testAdapterValue() throws ParseException {
        String input =
                "otherAdapter = {\n" +
                "    id = 123;\n" +
                "    message = \"Hello again...\";\n" +
                "};\n";


        TemplateAdapter otherAdapter = (TemplateAdapter) doTest(input).adapt("otherAdapter");

        assertNotNull(otherAdapter);
        assertEquals(new Integer(123), otherAdapter.get("id"));
        assertEquals("Hello again...", otherAdapter.get("message"));
    }

    public void testMapValue() throws ParseException {
        String input =
                "mapValue = map: {\n" +
                "    \"x\" = {\n" +
                "        message = \"Hello again...\";\n" +
                "    }, \n" +
                "    \"b\" = 1617, \n" +
                "    \"a\" = 1415\n" +
                "};\n";

        Map map = (Map) doTest(input).get("mapValue");

        assertNotNull(map);
        assertEquals(new Integer(1415), map.get("a"));
        assertEquals(new Integer(1617), map.get("b"));

        TemplateAdapter x = (TemplateAdapter) map.get("x");
        assertNotNull(x);
        assertEquals("Hello again...", x.adapt("message"));
    }

    public void testCollectionValue() throws ParseException {
        String input =
                "collectionValue = collection: { 12, 34, array: { 5, 6 }, { message = \"Hello again...\"; } };";

        TemplateAdapter scenery = doTest(input);
        Collection collectionValue = (Collection) scenery.get("collectionValue");
        Iterator i = collectionValue.iterator();
        assertEquals(new Integer(12), i.next());
        assertEquals(new Integer(34), i.next());

        int[] arr56 = (int[]) i.next();
        assertEquals(5, arr56[0]);
        assertEquals(6, arr56[1]);

        TemplateAdapter innerAdapter = (TemplateAdapter) i.next();
        assertEquals("Hello again...", innerAdapter.adapt("message"));
    }

    public void testArrayValue() throws ParseException {
        String input =
                "emptyArray       = array: { }; " +  // Empty array
                "intArray         = array: { 123, 456, 789 }; " +  // array of ints
                "doubleArray      = array: { 123.0, 456.1, 789.2 }; " + // array of doubles
                "booleanArray     = array: { true, false, true }; " +   // array of booleans
                "stringArray      = array: { \"Hello\", \"world\", \"!!!\" }; " +  // array of strings
                "dateArray        = array: { 2002-10-02, 2002-11-02, 2002-12-02 }; " + // array of date/time
                "adapterArray     = array: { { name = \"blah\"; }, { name = \"bleh\"; }, { name = \"blih\"; } }; " + // array of adapters
                "collectionArray  = array: { collection: { 1, 2 }, collection: { 3, 4 } }; " + // array of collections
                "mapArray         = array: { map: { \"a\" = 1, \"b\" = 2 }, map: { \"z\" = 100 } };" +  // array of maps
                "arrayIntArray    = array: { array: { 1, 2 }, array: { 3 } }; " + // array of int arrays
                "arrayStringArray = array: { array: { \"Hello\", \"world\" }, array: { \"!!!\" } }; "; // array of string arrays

        TemplateAdapter scenery = doTest(input);

        int[]             ints         = (int[])             scenery.get("intArray");
        double[]          doubles      = (double[])          scenery.get("doubleArray");
        boolean[]         booleans     = (boolean[])         scenery.get("booleanArray");
        String[]          strings      = (String[])          scenery.get("stringArray");
        Date[]            dates        = (Date[])            scenery.get("dateArray");
        Map[]             maps         = (Map[])             scenery.get("mapArray");
        Collection[]      collections  = (Collection[])      scenery.get("collectionArray");
        TemplateAdapter[] adapters     = (TemplateAdapter[]) scenery.get("adapterArray");
        int[][]           intArrays    = (int[][])           scenery.get("arrayIntArray");
        String[][]        stringArrays = (String[][])        scenery.get("arrayStringArray");

        assertEquals(3, ints.length);
        assertEquals(123, ints[0]);
        assertEquals(456, ints[1]);
        assertEquals(789, ints[2]);

        assertEquals(3, doubles.length);
        assertEquals(123.0, doubles[0], 0);
        assertEquals(456.1, doubles[1], 0);
        assertEquals(789.2, doubles[2], 0);

        assertEquals(3, booleans.length);
        assertEquals(true,  booleans[0]);
        assertEquals(false, booleans[1]);
        assertEquals(true,  booleans[2]);

        assertEquals(3, strings.length);
        assertEquals("Hello", strings[0]);
        assertEquals("world", strings[1]);
        assertEquals("!!!",   strings[2]);

        assertEquals(3, dates.length);
        assertEquals(new GregorianCalendar(2002,  9, 2).getTime(), dates[0]);
        assertEquals(new GregorianCalendar(2002, 10, 2).getTime(), dates[1]);
        assertEquals(new GregorianCalendar(2002, 11, 2).getTime(), dates[2]);

        assertEquals("blah", adapters[0].get("name"));
        assertEquals("bleh", adapters[1].get("name"));
        assertEquals("blih", adapters[2].get("name"));

        assertEquals(new Integer(3), collections[1].iterator().next());
        assertEquals(new Integer(100), maps[1].get("z"));
        assertEquals(0, ((Object[]) scenery.adapt("emptyArray")).length);

        assertEquals(2, intArrays.length);
        assertEquals(1, intArrays[0][0]);
        assertEquals(2, intArrays[0][1]);
        assertEquals(3, intArrays[1][0]);

        assertEquals(2, stringArrays.length);
        assertEquals("Hello", stringArrays[0][0]);
        assertEquals("world", stringArrays[0][1]);
        assertEquals("!!!",   stringArrays[1][0]);
    }

    public void testCustomClass() throws ParseException {
        String input =
                "allPropertiesSet = new br.com.devx.scenery.WrapperTestTarget() { " +
                "   intValue = 123; " +
                "   stringValue = \"Hello, world!\"; " +
                "   dateValue = 2002-12-20; " +
                "   booleanValue = true; " +
                "}; " +
                "notAllPropertiesSet = new br.com.devx.scenery.WrapperTestTarget() { " +
                "   intValue = 456; " +
                "   stringValue = \"O rato roeu a roupa do rei de Roma\"; " +
                "}; " +
                "nonePropertiesSet = new br.com.devx.scenery.WrapperTestTarget() {}; " +
                "justTheConstructor = new br.com.devx.scenery.WrapperTestTarget(); ";

        TemplateAdapter scenery = doTest(input);
        assertEquals(TemplateAdapter.class, scenery.adapt("allPropertiesSet").getClass());
        assertEquals(WrapperTestTarget.class, scenery.get("allPropertiesSet").getClass());

        TemplateAdapter allPropertiesAdapter = (TemplateAdapter) scenery.adapt("allPropertiesSet");
        assertEquals("123", allPropertiesAdapter.adapt("intValue"));
        assertEquals("Hello, world!", allPropertiesAdapter.adapt("stringValue"));
        assertEquals(JUnitHelper.dateString(2002, 11, 20), allPropertiesAdapter.adapt("dateValue"));
        assertEquals("true", allPropertiesAdapter.adapt("booleanValue"));

        WrapperTestTarget allPropertiesScenery = (WrapperTestTarget) scenery.get("allPropertiesSet");
        assertEquals(123, allPropertiesScenery.getIntValue());
        assertEquals("Hello, world!", allPropertiesScenery.getStringValue());
        assertEquals(new GregorianCalendar(2002, 11, 20).getTime(), allPropertiesScenery.getDateValue());
        assertEquals(true, allPropertiesScenery.getBooleanValue());

        WrapperTestTarget notAllPropertiesScenery = ((WrapperTestTarget) scenery.get("notAllPropertiesSet"));
        assertEquals(456, notAllPropertiesScenery.getIntValue());
        assertNull(notAllPropertiesScenery.getDateValue());

        assertEquals(new WrapperTestTarget(), scenery.get("nonePropertiesSet"));
        assertEquals(new WrapperTestTarget(), scenery.get("justTheConstructor"));
    }

    public void testCustomClassConstructor() throws ParseException {
        String input =
                "allPropertiesSet = " +
                "   new br.com.devx.scenery.WrapperTestTarget(123, \"Hello, world!\", 2002-12-20, true) { " +
                "       stringValue = \"Hello again...\";" +
                "   }; ";

        TemplateAdapter scenery = doTest(input);

        WrapperTestTarget allPropertiesScenery = (WrapperTestTarget) scenery.get("allPropertiesSet");
        assertEquals(123, allPropertiesScenery.getIntValue());
        assertEquals("Hello again...", allPropertiesScenery.getStringValue());
        assertEquals(new GregorianCalendar(2002, 11, 20).getTime(), allPropertiesScenery.getDateValue());
        assertEquals(true, allPropertiesScenery.getBooleanValue());
    }

    public void testCustomInterface() throws ParseException {
        String input =
                "testInterface = new br.com.devx.scenery.parser.TestInterface() { " +
                "   intValue = 123; " +
                "   stringValue = \"Hello, world!\"; " +
                "   dateValue = 2002-12-20; " +
                "   booleanValue = true; " +
                "}; ";

        TemplateAdapter scenery = doTest(input);

        TemplateAdapter templateAdapter = (TemplateAdapter) scenery.adapt("testInterface");
        assertEquals("123", templateAdapter.adapt("intValue"));
        assertEquals("Hello, world!", templateAdapter.adapt("stringValue"));
        assertEquals(JUnitHelper.dateString(2002, 11, 20), templateAdapter.adapt("dateValue"));
        assertEquals("true", templateAdapter.adapt("booleanValue"));

        TestInterface testInterface = (TestInterface) scenery.get("testInterface");
        assertEquals(123, testInterface.getIntValue());
        assertEquals("Hello, world!", testInterface.getStringValue());
        assertEquals(new GregorianCalendar(2002, 11, 20).getTime(), testInterface.getDateValue());
        assertEquals(true, testInterface.getBooleanValue());
    }

    private TemplateAdapter doTest(String scenery) throws ParseException {
        SceneryParser parser = new SceneryParser(new StringReader(scenery));
        return parser.parse();
    }
}
