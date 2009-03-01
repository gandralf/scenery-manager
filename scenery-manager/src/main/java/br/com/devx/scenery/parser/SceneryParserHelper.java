package br.com.devx.scenery.parser;

import br.com.devx.scenery.ReflectionHelper;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

public class SceneryParserHelper {
    private static ClassLoader s_classLoader = Thread.currentThread().getContextClassLoader();

    public static void setClassLoader(ClassLoader classLoader) {
        s_classLoader = classLoader;
    }

    public static int[] toIntArray(ArrayList arrayList) {
        int[] result = new int[arrayList.size()];
        for (int i=0; i < arrayList.size(); i++) {
            result[i] = (Integer) arrayList.get(i);
        }

        return result;
    }

    public static double[] toDoubleArray(ArrayList arrayList) {
        double[] result = new double[arrayList.size()];
        for (int i=0; i < arrayList.size(); i++) {
            result[i] = (Double) arrayList.get(i);
        }

        return result;
    }

    public static boolean[] toBooleanArray(ArrayList arrayList) {
        boolean[] result = new boolean[arrayList.size()];
        for (int i=0; i < arrayList.size(); i++) {
            result[i] = (Boolean) arrayList.get(i);
        }

        return result;
    }

    public static Date toDate(String date, String time) {
        String[] ymd = date.split("-");
        String[] hms = { "0", "0", "0" };
        if (time != null) {
            hms = time.split(":");
            if (hms.length == 2) {
                hms = new String[] { hms[0], hms[1], "0" };
            }
        }

        GregorianCalendar calendar = new GregorianCalendar(
                Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]) -1, Integer.parseInt(ymd[2]),
                Integer.parseInt(hms[0]), Integer.parseInt(hms[1]), Integer.parseInt(hms[2])
            );

        return calendar.getTime();
    }

    public static Object makeArray(ArrayList arrayList) {
        Object result;

        if (arrayList.size() == 0) {
            result = new Object[] {};
        } else if (arrayList.get(0) instanceof Integer) {
            result = toIntArray(arrayList);
        } else if (arrayList.get(0) instanceof Double) {
            result = toDoubleArray(arrayList);
        } else if (arrayList.get(0) instanceof Boolean) {
            result = toBooleanArray(arrayList);
        } else {
            result = Array.newInstance(arrayList.get(0).getClass(), arrayList.size());
            for (int i = 0; i < arrayList.size(); i++) {
                Array.set(result, i, arrayList.get(i));
            }
        }

        return result;
    }

    public static Object newCustomType(String customClassName, Collection constructorParameters)
            throws ParseException {
        try {
            Class customClass = Class.forName(customClassName, true, s_classLoader);
            if (customClass.isInterface()) {
                return CustomInterfaceHandler.newInstance(customClass);
            } else if (constructorParameters == null || constructorParameters.size() == 0) {
                return customClass.newInstance();
            } else {
                Constructor constructor = findConstructor(customClass, constructorParameters.toArray());
                return constructor.newInstance(constructorParameters.toArray());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ParseException(e.toString());
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new ParseException(e.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new ParseException(e.toString());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new ParseException(e.toString());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new ParseException(e.toString());
        }
    }

    private static Constructor findConstructor(Class customClass, Object[] parameters) throws NoSuchMethodException {
        if (parameters == null || parameters.length == 0) {
            return customClass.getConstructor();
        }

        Constructor[] constructors = customClass.getConstructors();
        for (Constructor constructor : constructors) {
            if (ReflectionHelper.canCall(constructor, parameters)) {
                return constructor;
            }
        }

        throw new NoSuchMethodException("No such constructor with given parameters");
    }
}
