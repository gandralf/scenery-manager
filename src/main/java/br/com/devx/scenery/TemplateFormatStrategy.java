package br.com.devx.scenery;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

public class TemplateFormatStrategy {
    private Properties m_formatProperties;

    public TemplateFormatStrategy() {
        m_formatProperties = new Properties();
    }

    public TemplateFormatStrategy(Properties formatProperties) {
        m_formatProperties = formatProperties;
    }

    public Object format(String fieldName, Object value) {
        Object result;
        if (value == null) {
            result = null;
        } if (value instanceof Number) {
            result = formatNumber(fieldName, (Number) value);
        } else if (value instanceof Boolean) {
            result = value.toString();
        } else if (value instanceof Date) {
            result = formatDateTime(fieldName, (Date) value);
        } else {
            result = value;
        }

        return result;
    }

    protected String formatNumber(String fieldName, Number value) {
        String format = fieldName != null ? (String) m_formatProperties.get(fieldName) : null;
        String result;

        if (format == null) {
            result = NumberFormat.getInstance().format(value);
        } else if ("integer".equals(format)) {
            result = NumberFormat.getIntegerInstance().format(value);
        } else if ("currency".equals(format)) {
            result = NumberFormat.getCurrencyInstance().format(value);
        } else if ("percent".equals(format)) {
            result = NumberFormat.getPercentInstance().format(value);
        } else {
            result = new DecimalFormat(format).format(value);
        }

        String choiceFormat = m_formatProperties.getProperty("choice/" + fieldName);
        if (choiceFormat != null) {
            result = new ChoiceFormat(choiceFormat).format(value);
        }

        return result;
    }

    protected String formatDateTime(String fieldName, Date value) {
        String format = fieldName != null ? (String) m_formatProperties.get(fieldName) : null;
        String result;

        if (format == null) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(value);
            if (calendar.get(Calendar.HOUR) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0) {
                result = DateFormat.getDateInstance(DateFormat.DEFAULT).format(value);
            } else {
                result = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT).format(value);
            }
        } else {
            format = format.trim();
            String[] strFormats = format.split("\\s*;\\s*");
            if (strFormats.length == 0) {
                result = formatDate(format, value);
            } else if (strFormats.length == 1) {
                result = formatDate(strFormats[0], value);
            } else if (strFormats.length == 2) {
                if (strFormats[0].trim().length() == 0) {
                    result = formatTime(strFormats[1], value);
                } else {
                    result = formatDateTime(strFormats[0], strFormats[1], value);
                }
            } else {
                throw new IllegalArgumentException("Illegal format pattern: " + format);
            }
        }

        return result;
    }

    private String formatDate(String format, Date value) {
        String result;
        int formatId = dateTimeFormat(format);
        if (formatId != -1) {
            result = DateFormat.getDateInstance(formatId).format(value);
        } else {
            result = new SimpleDateFormat(format).format(value);
        }
        return result;
    }

    private String formatTime(String format, Date value) {
        String result;
        int formatId = dateTimeFormat(format);
        if (formatId != -1) {
            result = DateFormat.getTimeInstance(formatId).format(value);
        } else {
            result = new SimpleDateFormat(format).format(value);
        }
        return result;
    }

    private String formatDateTime(String dateFormat, String timeFormat, Date value) {
        String result;
        int dateFormatId = dateTimeFormat(dateFormat);
        int timeFormatId = dateTimeFormat(timeFormat);
        if (dateFormatId != -1 && timeFormatId != -1) {
            result = DateFormat.getDateTimeInstance(dateFormatId, timeFormatId).format(value);
        } else {
            result = "";
            if (dateFormatId != -1) {
                result = formatDate(dateFormat, value) + " ";
            }
            if (timeFormatId != -1) {
                result = result + formatTime(timeFormat, value);
            }
        }

        return result;
    }

    private int dateTimeFormat(String format) {
        format = format.trim();
        if ("short".equals(format)) {
            return DateFormat.SHORT;
        } else if ("medium".equals(format)) {
            return DateFormat.MEDIUM;
        } else if ("long".equals(format)) {
            return DateFormat.LONG;
        } else if ("full".equals(format)) {
            return DateFormat.FULL;
        } else {
            return -1;
        }
    }
}
