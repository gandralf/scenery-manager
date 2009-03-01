/**
 * User: gandralf
 * Date: Dec 28, 2002
 * Time: 1:06:08 PM
 */
package br.com.devx.scenery.parser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

class CustomInterfaceHandler implements InvocationHandler {
    private HashMap<String, Object> m_properties = new HashMap<String, Object>();

    public static Object newInstance(Class customInterfaceClass) {
        return Proxy.newProxyInstance(customInterfaceClass.getClassLoader(),
                new Class[] { customInterfaceClass },
                new CustomInterfaceHandler());
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        // Assume that this method is already present on interface
        Object result;
        if (m.getName().startsWith("get") && (args == null || args.length == 0)) {
            result = m_properties.get(m.getName().substring(3));
        } else if (m.getName().startsWith("set") && (args != null && args.length == 1)) {
            m_properties.put(m.getName().substring(3), args[0]);
            result = null;
        } else {
            throw new UnsupportedOperationException("Unsupported method: " + m.getName());
        }

        return result;
    }
}
