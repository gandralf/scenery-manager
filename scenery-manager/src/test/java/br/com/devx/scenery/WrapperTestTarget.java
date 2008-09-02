package br.com.devx.scenery;

import java.util.Date;

public class WrapperTestTarget extends WrapperTestTargetBase {
    private Date m_dateValue;
    private boolean m_booleanValue;

    public WrapperTestTarget() {

    }

    public WrapperTestTarget(int intValue, String stringValue, Date dateValue, boolean booleanValue) {
        m_intValue = intValue;
        m_stringValue = stringValue;
        m_dateValue = dateValue;
        m_booleanValue = booleanValue;
    }

    public Date getDateValue() {
        return m_dateValue;
    }

    public boolean getBooleanValue() {
        return m_booleanValue;
    }

    public void setDateValue(Date dateValue) {
        m_dateValue = dateValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        m_booleanValue = booleanValue;
    }

    public String message() {
        return "Message: \"" + getStringValue() + "\"";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WrapperTestTarget)) return false;
        if (!super.equals(o)) return false;

        final WrapperTestTarget wrapperTestTarget = (WrapperTestTarget) o;

        if (m_booleanValue != wrapperTestTarget.m_booleanValue) return false;
        if (m_dateValue != null ? !m_dateValue.equals(wrapperTestTarget.m_dateValue) : wrapperTestTarget.m_dateValue != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_dateValue != null ? m_dateValue.hashCode() : 0);
        result = 29 * result + (m_booleanValue ? 1 : 0);
        return result;
    }
}
