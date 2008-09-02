package br.com.devx.scenery;

import junit.framework.TestCase;

import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Collection;
import java.lang.reflect.InvocationTargetException;

public class ObjectWrapperTest extends TestCase {
    public ObjectWrapperTest(String s) {
        super(s);
    }

    public void testWrapper() throws Exception {
        Date happyDay = new GregorianCalendar(1972, 6, 20).getTime();
        WrapperTestTarget target = new WrapperTestTarget(4, "Hello, world!", happyDay, true);
        ObjectWrapper wrapper = new ObjectWrapper(target);

        assertTrue(wrapper.containsProperty("intValue"));
        assertTrue(wrapper.containsProperty("booleanValue"));
        assertTrue(!wrapper.containsProperty("notFoundProperty"));
        assertTrue(wrapper.containsMethod("message"));
        assertTrue(!wrapper.containsMethod("notFoundMethod"));

        assertEquals(new Integer(4),  wrapper.getProperty("intValue"));
        assertEquals(Boolean.TRUE,  wrapper.getProperty("booleanValue"));
        assertEquals("Hello, world!", wrapper.getProperty("stringValue"));
        assertEquals(happyDay,        wrapper.getProperty("dateValue"));
        assertEquals("Message: \"Hello, world!\"", wrapper.call("message"));

        try {
            wrapper.call("methodNotFound");
            fail("Method not found: NoSuchMethodException expected");
        } catch (NoSuchMethodException e) {
            // OK
        }
    }

    public void testGetProperties() {
        Date happyDay = new GregorianCalendar(1972, 6, 20).getTime();
        WrapperTestTarget target = new WrapperTestTarget(4, "Hello, world!", happyDay, true);
        ObjectWrapper wrapper = new ObjectWrapper(target);

        Collection properties = wrapper.getProperties();

        assertEquals(4, properties.size());
        assertTrue(properties.contains("intValue"));
        assertTrue(properties.contains("stringValue"));
        assertTrue(properties.contains("dateValue"));
        assertTrue(properties.contains("booleanValue"));
    }

    public void testSetProperty() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Date happyDay = new GregorianCalendar(1972, 6, 20).getTime();
        WrapperTestTarget target = new WrapperTestTarget();
        ObjectWrapper wrapper = new ObjectWrapper(target);

        wrapper.setProperty("stringValue", "Hello, world!");
        wrapper.setProperty("dateValue", happyDay);
        wrapper.setProperty("booleanValue", Boolean.TRUE);
        wrapper.setProperty("intValue", new Integer(4));

        assertEquals(new Integer(4),  wrapper.getProperty("intValue"));
        assertEquals(Boolean.TRUE,    wrapper.getProperty("booleanValue"));
        assertEquals("Hello, world!", wrapper.getProperty("stringValue"));
        assertEquals(happyDay,        wrapper.getProperty("dateValue"));
        try {
            wrapper.setProperty("intValue", new Short((short) 33));
            // We are not so nice with parameters conversion...
            fail("NoSuchMethodException expected");
        } catch(NoSuchMethodException e) {
            // OK
        }
    }
}
