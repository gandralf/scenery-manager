/**
 * User: gandralf
 * Date: Dec 28, 2002
 * Time: 11:15:05 AM
 */
package br.com.devx.scenery;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class ReflectionHelper {
    public static boolean canCall(Method method, Object[] parameters) {
        return isCompatible(method.getParameterTypes(), parameters);
    }

    public static boolean canCall(Constructor method, Object[] parameters) {
        return isCompatible(method.getParameterTypes(), parameters);
    }

    private static boolean isCompatible(Class[] parameterTypes, Object[] parameters) {
        if ((parameterTypes.length != 0 && parameters == null) || parameterTypes.length != parameters.length) {
            return false;
        }

        boolean result = true;
        for (int j = 0; j < parameterTypes.length; j++) {
            if (parameters[j] != null && !isCompatibleArg(parameterTypes[j], parameters[j].getClass())) {
                result = false;
                break;
            }
        }

        return result;
    }

    private static boolean isCompatibleArg(Class target, Class source) {
        if (target.isAssignableFrom(source)) {
            return true;
        } else if ((target.equals(Boolean.TYPE) && source.equals(Boolean.class)) ||
            (target.equals(Character.TYPE) && source.equals(Character.class)) ||
            (target.equals(Byte.TYPE) && source.equals(Byte.class)) ||
            (target.equals(Short.TYPE) && source.equals(Short.class)) ||
            (target.equals(Integer.TYPE) && source.equals(Integer.class)) ||
            (target.equals(Long.TYPE) && source.equals(Long.class)) ||
            (target.equals(Float.TYPE) && source.equals(Float.class)) ||
            (target.equals(Double.TYPE) && source.equals(Double.class))) {
            return true;
        } else {
            return false;
        }
    }
}
