package br.com.devx.scenery;

import java.util.*;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class TemplateAdapterPrinter {
    private static int MAX_LEVEL = 15;
    private TemplateAdapter templateAdapter;

    public TemplateAdapterPrinter(TemplateAdapter templateAdapter) {
        this.templateAdapter = templateAdapter;
    }

    String toString(int ident) {
        return toString(ident, true);
    }


    String toUsedString(int ident) {
        return toString(ident, false);
    }

    private String toString(int ident, boolean all) {
        Collection<String> properties = all ? templateAdapter.getProperties() : templateAdapter.getAccessedProperties();
        String identString = getIdent(ident);
        StringBuffer result;
        result = new StringBuffer("{\n");

        Iterator i = properties.iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            Object value = templateAdapter.get(key);

            result.append(identString);
            result.append("    ");
            result.append(key);
            result.append(" = ");
            if (value != null) {
                if (value.getClass().isArray()) {
                    result.append("array: ");
                } else if (value instanceof Map && !(value instanceof TemplateAdapter)) { // As TemplateAdapter is a map
                    result.append("map: ");
                } else if (value instanceof Collection) {
                    result.append("collection: ");
                }
            }

            appendValue(value, result, ident, all);

            result.append(";\n");
        }

        result.append(identString + "}");

        return result.toString();
    }

    private String getIdent(int ident) {
        StringBuffer result = new StringBuffer();
        for(int k=0; k < 4*ident; k++) {
            result.append(' ');
        }

        return result.toString();
    }

    private void appendValue(Object value, StringBuffer result, int ident, boolean all) {
        if (value == null) {
            result.append("null");
        } else if (value instanceof String) {
            result.append("\"");
            result.append(value);
            result.append("\"");
        } else if (value instanceof Double || value instanceof Float ||
                value instanceof Integer || value instanceof Character || value instanceof Short ||
                value instanceof Boolean) {
            result.append(value);
        } else if (value instanceof java.util.Date) {
            Date dateValue = (Date) value;
            GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
            calendar.setTime(dateValue);

            DateFormat df   = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dtf  = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            DateFormat dtfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if ((calendar.get(Calendar.HOUR) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0) ||
                value instanceof java.sql.Date) {
                result.append(df.format(dateValue));
            } else if (calendar.get(Calendar.SECOND) == 0) {
                result.append(dtf.format(dateValue));
            } else {
                result.append(dtfs.format(dateValue));
            }
        } else if (value instanceof TemplateAdapter) {
            TemplateAdapter adapter = (TemplateAdapter) value;
            result.append(new TemplateAdapterPrinter(adapter).toString(ident + 1, all));
        } else if (value.getClass().isArray()) {
            result.append("{ ");
            for (int j = 0; j < Array.getLength(value); j++) {
                appendValue(Array.get(value, j), result, ident, all);
                if (j != Array.getLength(value) -1) {
                    result.append(", ");
                }
            }

            result.append(" }");
        } else if (value instanceof Map) {
            result.append("{\n");
            Map map = (Map) value;
            Iterator j = map.keySet().iterator();
            String identStr = getIdent(ident + 2);
            while(j.hasNext()) {
                Object key = j.next();
                Object mapValue = map.get(key);
                result.append(identStr);
                appendValue(key, result, ident, all);
                result.append(" = ");
                appendValue(mapValue, result, ident + 1, all);
                if (j.hasNext()) {
                    result.append(", ");
                }
                result.append("\n");
            }
            result.append(getIdent(ident + 1));
            result.append("}");
        } else if (value instanceof Collection) {
            result.append("{\n");
            Collection collection = (Collection) value;
            Iterator j = collection.iterator();
            String identStr = getIdent(ident + 2);
            while(j.hasNext()) {
                Object item = j.next();
                result.append(identStr);
                appendValue(item, result, ident + 1, all);
                if (j.hasNext()) {
                    result.append(", ");
                }
                result.append("\n");
            }
            result.append(getIdent(ident + 1));
            result.append("}");
        } else {
            if (ident < MAX_LEVEL) {
                TemplateAdapter wrappedValue = new TemplateAdapter();
                wrappedValue.wrapp(value);
                TemplateAdapterPrinter adapterPrinter = new TemplateAdapterPrinter(wrappedValue);
                result.append(adapterPrinter.toString(ident + 1, all));
            } else {
                result.append(value);
            }
        }
    }
}
