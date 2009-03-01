package br.com.devx.scenery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.ArrayList;

public class ObjectWrapper {
    private Object m_source;

    public ObjectWrapper(Object source) {
        if (source == null) {
            throw new IllegalArgumentException("Source can't be null");
        }

        m_source = source;
    }

    public boolean containsProperty(String name) {
        return containsMethod(getterName(name));
    }

    private static String getterName(String name) {
        return "get" + Character.toUpperCase((name.charAt(0))) + name.substring(1);
    }

    private static String setterName(String name) {
        return "set" + Character.toUpperCase((name.charAt(0))) + name.substring(1);
    }

    public boolean containsMethod(String methodName) {
        Class sourceClass = m_source.getClass();
        try {
            sourceClass.getMethod(methodName);
            return true;
        } catch(NoSuchMethodException e) {
            return false;
        }
    }

    public Object getProperty(String propertyName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return call(getterName(propertyName), null);
    }

    public void setProperty(String propertyName, Object propertyValue) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        call(setterName(propertyName), new Object[] { propertyValue });
    }

    public Object call(String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return call(methodName, null);
    }

    public Object call(String methodName, Object[] parameters) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = findMethod(methodName, parameters);

        return method.invoke(m_source, parameters);
    }

    private Method findMethod(String methodName, Object[] parameters) throws NoSuchMethodException {
        if (parameters == null || parameters.length == 0) {
            return m_source.getClass().getMethod(methodName);
        }

        Method[] methods = m_source.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (ReflectionHelper.canCall(method, parameters)) {
                    return method;
                }
            }
        }

        throw new NoSuchMethodException("No such method (" + methodName + ") with given parameters");
    }

    public Collection<String> getProperties() {
        ArrayList<String> result = new ArrayList<String>();
        Method[] methods = m_source.getClass().getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.matches("^get[A-Z](.)*") &&
                    method.getModifiers() == Modifier.PUBLIC && method.getParameterTypes().length == 0) {
                result.add(Character.toLowerCase(name.charAt(3)) + name.substring(4));
            }
        }

        return result;
    }
}
