package br.com.devx.scenery;

import br.com.devx.scenery.parser.ParseException;
import br.com.devx.scenery.parser.SceneryParser;

import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Scenery manager main class. It holds all template data, which goes to the template itself.
 * Usually, its usages envolves two steps:<ol>
 * <li>Setting up a new <code>TemplateAdapter</code></li>
 * <li>Apply that <code>TemplateAdapter</code> to the template engine</li></ol>
 * <p><b>Step 1: setting up TemplateAdapter</b></p>
 * <p>While designing and testing interfaces, a new <code>TemplateAdapter</code>
 * is usually built from a scenery data file (.scn). Can be done using the {@link #load} method:</p>
 * <code>TemplateAdapter templateAdapter = TemplateAdapter.load(new FileReader("data.scn"));</code>
 * <p>A real application, however, doesn't load a TemplateAdapter from a file. You must call one of
 * <code>put</code> methods for each property and/or wrapp a class with the <code>wrapp</code> method.</p>
 * <pre><code>TemplateAdapter templateAdapter = new TemplateAdapter();
 * templateAdapter.put("currentDate", new Date()); // set a property...
 * templateAdapter.wrapp(userData); // ...or wrapp a class </code></pre>
 * <p><b>Step 2: applying the template adapter class to the template engine</b></p>
 * <p>This step is template engine dependent. If you are using Jsp template engine, for example,
 * you can use {@link br.com.devx.scenery.web.TemplateHandler#handle} method or manualy call
 * {@link javax.servlet.http.HttpServletRequest#setAttribute} and then {@link javax.servlet.RequestDispatcher#include}
 * or {@link javax.servlet.RequestDispatcher#forward} method.
 * <p><b>Other features</b></p>
 * <p>TemplateAdapter provides automatic formatting and class wrapping.<br>
 * Automatic formatting works with {@link #adapt(java.lang.String)} method, wich can return a formatted string.
 * It can be customized using a custom <code>TemplateFormatStrategy</code> class.<br>
 * Automatic class wrapping works with {@link #wrapp} method.</p>
 * @see #put(java.lang.String, java.lang.Object)
 * @see #adapt
 * @see #get
 * @see #load
 * @see br.com.devx.scenery.web.SceneryServlet
 * @see br.com.devx.scenery.web.TemplateHandler
 * @see TemplateFormatStrategy
 * @see SceneryParser
 */
public class TemplateAdapter extends HashMap<String, Object> {
    private TemplateFormatStrategy m_formatStrategy;
    private ObjectWrapper m_wrappedObject;
    private Set<String> m_accessedProperties;

    /**
     * Constructs a new TemplateAdapter object using the default format strategy.
     * @see TemplateFormatStrategy
     */
    public TemplateAdapter() {
        m_formatStrategy = new TemplateFormatStrategy();
        m_wrappedObject = null;
        m_accessedProperties = new HashSet<String>();
    }

    /**
     * @see TemplateFormatStrategy
     */
    public TemplateFormatStrategy getFormatStrategy() {
        return m_formatStrategy;
    }

    public void setFormatStrategy(TemplateFormatStrategy formatStrategy) {
        for (Object o : super.values()) {
            if (o instanceof TemplateAdapter) {
                ((TemplateAdapter) o).setFormatStrategy(formatStrategy);
            }
        }
        m_formatStrategy = formatStrategy;
    }

    /**
     * Adds an integer value to the property set.
     * @param name Property name
     * @param value Property value
     */
    public void put(String name, int value) {
        super.put(name, value);
    }

    /**
     * Adds an double value to the property set.
     * @param name Property name
     * @param value Property value
     */
    public void put(String name, double value) {
        super.put(name, value);
    }

    /**
     * Adds an <code>String</code> value to the property set.
     * @param name Property name
     * @param value Property value
     */
    public void put(String name, boolean value) {
        super.put(name, value);
    }

    /**
     * Adds an <code>String</code> value to the property set.
     * @param name Property name
     * @param value Property value
     * @see #adapt
     * @see #get
     */
    public Object put(String name, Object value) {
        return super.put(name, value);
    }

    /**
     * <p>Returns the value of property <code>name</code>. First, it looks to the standard property set,
     * wich was previously registered by <code>put</code> method calls. If the <code>name</code>
     * property was been found, then returns its value. Otherwise it looks to the wrapped class and
     * returns its property value, calling the apropriate <code>[wrapped class].adapt[PropertyName]()</code> method.
     * Differs from {@link #adapt} method becouse it does not apply any automatic formatting or wrapping actions.
     * For exemple:</p>
     * <p><code>
     * TemplateAdapter templateAdapter = new TemplateAdapter();<br/>
     * templateAdapter.put("doubleValue", 123456.1);<br/>
     * templateAdapter.put("userData", new UserData());<br/>
     * </code></p>
     * <ul><li><code>templateAdapter.get("doubleValue")</code> returns a instance of
     * <code>java.lang.Double</code> class, and <code>templateAdapter.adapt("doubleValue")</code>
     * returns "123,456.10" (default <code>TemplateFormatStrategy</code>, en_US locale)</li>
     * <li><code>templateAdapter.get("userData")</code> returns a instance of
     * <code>UserData</code> class, while <code>templateAdapter.adapt("userData")</code> retuns
     * another <code>TemplateAdapter</code> instance wrapping <code>UserData</code>.</li></ul>
     * @param name property name
     * @return the property value assigned to <code>name</code> parameter, or <code>null</code>
     *      if this property was not found <i>and</i> this <code>TemplateAdapter</code>
     *      has none wrapped class.
     * @see #adapt
     * @see #put(java.lang.String, java.lang.Object)
     * @see #wrapp
     * @throws IllegalArgumentException if TemplateAdapter does not contains such property <i>and</i>
     *      the wrapped class is not null, but it doesn't contains a adapt method for this property
     * @throws IllegalStateException if an <code>IllegalAccessException</code> or
     *      <code>InvocationTargetException</code> has been thrown while calling the apropriate
     *      adapt method.
     */
    public Object get(String name) {
        m_accessedProperties.add(name);
        Object result;
        if (m_wrappedObject == null || !m_wrappedObject.containsProperty(name)) {
            result = super.get(name);
        } else {
            try {
                result = m_wrappedObject.getProperty(name);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Unexpected exception: " + e.toString());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unexpected exception: " + e.toString());
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Unexpected exception: " + e.toString());
            }
        }

        return result;
    }

    public Object get(Object key) {
        m_accessedProperties.add(key.toString());
        return super.get(key);
    }

    /**
     * <p>Returns a formated or wrapped value of <code>get(name)</code> call result.
     * See {@link #get} for further details.</p>
     * The proccess is described as folowing:<ol>
     * <li>The property value is got by calling the {@link #get} method</li>
     * <li>If the property value can be formated (converted to a String) by the current
     * {@link TemplateFormatStrategy}, then this string is returned.</li>
     * <li>Otherwise, it returns an addapted form (or wrapped form, if you like)
     * of the property value. The adappted (class) type is dependent of the property type:
     * <table border="1">
     * <tr><td>If the property is</td><td>then the returnning type is</td></tr>
     * <tr><td>An array or a collection</td><td>an array or a collection, respectively
     * of adapted/formatted items (yes, it is recursive!)</td></tr>
     * <tr><td>An ordinary class instance</td><td>An <code>TemplateAdapter</code> instance,
     * wrapping that instance.</td></tr>
     * </table>
     * </li></ol>
     * @param name property name
     * @return a formated string or a wrapped class from the property <code>name</code> value.
     * @see #get
     * @see #put(java.lang.String, java.lang.Object)
     * @see #wrapp
     * @see TemplateFormatStrategy
     * @throws IllegalArgumentException if TemplateAdapter does not contains such property <i>and</i>
     *      the wrapped class is not null, but it doesn't contains a adapt method for this property
     * @throws IllegalStateException if an <code>IllegalAccessException</code> or
     *      <code>InvocationTargetException</code> has been thrown while calling the apropriate
     *      adapt method.
     */
    public Object adapt(String name) {
        Object value = get(name);

        return adaptValue(name, value);
    }

    public TemplateAdapter getAdapter(String name) {
        return (TemplateAdapter) adapt(name);
    }

    /**
     * Returns the formated or wrapped value parameter.
     */
    private Object adaptValue(String name, Object value) {
        Object result = m_formatStrategy.format(name, value);

        // Could not FormatStratagy format this stuff??? If so, wrapp-it!!!
        if ((result != null) && !(result instanceof String) && (result == value)) { // Strings can't be wrapped!
            if (value instanceof TemplateAdapter) {
                result = value;
            } else if (value instanceof Map) {
                HashMap<String, Object> mapResult = new HashMap<String, Object>((Map<String, Object>) value);
                for (String key : mapResult.keySet()) {
                    Object valueObject = mapResult.get(key);
                    mapResult.put(key, adaptValue(name, valueObject));
                }

                result = mapResult;
            } else if (value instanceof Collection) {
                Collection<Object> collectionResult = new ArrayList<Object>();
                Collection valueCollection = (Collection) value;
                for (Object o : valueCollection) {
                    collectionResult.add(adaptValue(name, o));
                }

                result = collectionResult;
            } else if (value.getClass().isArray()) {
                int arrayLength = Array.getLength(value);
                Object arrayResult;
                if (value instanceof int[] || value instanceof String[] ||
                        value instanceof boolean[] || value instanceof java.util.Date[]) {
                    arrayResult = new String[arrayLength];
                } else {
                    arrayResult = new Object[arrayLength];
                }
                for (int i=0; i < arrayLength; i++) {
                    Object itemValue = Array.get(value, i);
                    Array.set(arrayResult, i, adaptValue(name, itemValue));
                }

                result = arrayResult;
            } else {
                TemplateAdapter mapAdapter = new TemplateAdapter();
                mapAdapter.wrapp(value);

                result = mapAdapter;
            }
        }

        return result;
    }

    /**
     * Checks if <code>name</code> property exists.
     * @param name property to check
     * @return <code>true</code> if this <code>TemplateAdapter</code> contains the
     *      <code>name</code> property, or <code>false</code> otherwise
     */
    public boolean containsProperty(String name) {
         return (m_wrappedObject != null && m_wrappedObject.containsProperty(name)) ||
                 super.containsKey(name);
    }

    /**
     * <p>Returns the equivalent scenery file (.scn) syntax string, begining with "{" and ending with "}".
     * For example, if this class contains an String property "userName" equals "johnc" and a
     * array of ints called "groups" which the values are 501, 504 and 100, then it would return:</p>
     * <p><code>{
     *     userName = "johnc";
     *     groups = array: { 501, 504, 100};
     * }</code></p>
     * <p>For the complete .scn file syntax, see {@link #load}.</p>
     * @see #load
     * @see TemplateAdapterPrinter
     */
    public String toString() {
        return new TemplateAdapterPrinter(this).toString(0);
    }

    /**
     *  
     * @return the accessed properties in .scn file format content
     */
    public String toAccessedString() {
        return new TemplateAdapterPrinter(this).toUsedString(0);
    }

    /**
     * <p>Wrapps a source object, so furter access to {@link #adapt} or {@link #get} methods will
     * access <code>source</code>'s properties.</p>
     * If <code>source</code> is a <code>Map</code>, all it's entries will be imported like making a
     * call to <code>put(key, value)</code>, for each key of the <code>source Map</code>.
     * Otherwise, all <code>source</code>'s properties will be available to <code>adapt("xxx")</code>
     * and <code>get("xxx")</code> methods by making a call to adapt&lt;Xxx&gt;() method.
     * @param source object to wrapp.
     */
    public void wrapp(Object source) {
        m_wrappedObject = null;
        if (source instanceof Map) {
            importMap((Map) source);
        } else {
            m_wrappedObject = new ObjectWrapper(source);
        }
    }

    /**
     * Import all keys/values from <code>source</code> as properties
     * @param source
     */
    private void importMap(Map<String, Object> source) {
        Collection<String> keys = source.keySet();
        for (String key : keys) {
            put(key, source.get(key));
        }
    }

    /**
     * <p>Creates a new TemplateAdapter instance from the .scn source <code>input</code>.
     * It is a <i>Factory Method</i> pattern.</p>
     * <p>The scenery data source file (.scn) syntax is described as folowing. It is a property/value
     * set defined by the folowing (simplified) BNF:</p>
     * <pre>
     * sceneryData ::= (property "=" value ";")*
     * value ::= singleValue | arrayValue | collectionValue | mapValue
     * singleValue ::= null | true | false | &lt;INT&gt; | &lt;DOUBLE&gt; | &lt;STRING&gt; | &lt;DATE&gt; | innerAdapter
     * innerAdapter ::= { (property "=" value ";")* }
     * arrayValue ::= "array:" { [ value ("," value)* ] }
     * collectionValue ::= "collection:" { [ value ("," value)* ] }
     * mapValue ::= "map:" { [ (key "=" value) ("," key "=" value)* ] }
     * key ::= value
     * property ::= [a-zA-Z_][0-9a-zA-Z_]*
     * &lt;INT&gt; ::= [0-9]+
     * &lt;DOUBLE&gt; ::= &lt;INT&gt; "." &lt;INT&gt;
     * &lt;DATE&gt; ::= &lt;INT&gt;-&lt;INT&gt;-&lt;INT&gt; [ &lt;INT&gt; ":" &lt;INT&gt; [ ":" &lt;INT&gt; ] ]
     * </pre>
     * Note: date values are formated as year-month-day hour:minutes:seconds
     * @param input a scenery data (.scn) <code>Reader</code>. Usually a <code>FileReader</code>.
     * @return A new <code>TemplateAdapter</code> instance, loaded from
     * @throws ParseException if a syntax error has occurred.
     * @see #toString
     * @see SceneryParser
     */
    public static TemplateAdapter load(Reader input) throws ParseException {
        SceneryParser SceneryParser = new SceneryParser(input);
        
        return SceneryParser.parse();
    }

    /**
     * Returns a set of all <code>TemplateAdapter</code> properties: a union of properties set by
     * <code>put</code> method calls and wrapped class properties.
     * @see #put
     * @see #wrapp
     * @see #adapt
     * @see #get
     */
    public Collection<String> getProperties() {
        Collection<String> result = new ArrayList<String>();
        result.addAll(super.keySet());
        if (m_wrappedObject != null) {
            result.addAll(m_wrappedObject.getProperties());
        }

        return result;
    }

    public Collection<String> getAccessedProperties() {
        return m_accessedProperties;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemplateAdapter)) return false;

        final TemplateAdapter param = (TemplateAdapter) o;

        Collection<String> properties = getProperties();
        Collection paramProperties = param.getProperties();
        if (properties.size() != paramProperties.size()) {
            return false;
        }

        for (String key : properties) {
            Object value = adapt(key);
            Object paramValue = param.adapt(key);
            if (!equals(value, paramValue)) {
                return false;
            }
        }

        return true;
    }

    private boolean equals(Object a, Object b) {
        if ((a == null && b != null)) {
            return false;
        } else if (a.getClass().isArray()) {
            if (Array.getLength(a) != Array.getLength(b)) {
                return false;
            }

            for (int j = 0 ; j < Array.getLength(a); j++) {
                Object itemA = Array.get(a, j);
                Object itemB = Array.get(b, j);

                return equals(itemA, itemB);
            }
        } else {
            return a.equals(b);
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = super.hashCode();
        result = 29 * result + m_formatStrategy.hashCode();
        result = 29 * result + (m_wrappedObject != null ? m_wrappedObject.hashCode() : 0);
        return result;
    }

    public void assignTo(Object target) {
        ObjectWrapper wrapper = new ObjectWrapper(target);
        Collection<String> properties = getProperties();
        for (String propertyName : properties) {
            if (wrapper.containsProperty(propertyName)) {
                Object propertyValue = get(propertyName);
                try {
                    wrapper.setProperty(propertyName, propertyValue);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    throw new IllegalStateException(e.toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new IllegalStateException(e.toString());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    throw new IllegalStateException(e.toString());
                }
            }
        }
    }
}
