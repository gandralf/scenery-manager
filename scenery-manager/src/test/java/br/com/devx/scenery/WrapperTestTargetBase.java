package br.com.devx.scenery;

public class WrapperTestTargetBase {
    protected int m_intValue;
    protected String m_stringValue;

    public int getIntValue() {
        return m_intValue;
    }

    public String getStringValue() {
        return m_stringValue;
    }

    public void setIntValue(int intValue) {
        m_intValue = intValue;
    }

    public void setStringValue(String stringValue) {
        m_stringValue = stringValue;
    }

    protected String getDontCallMessage() {
        return "This method is't suposed to be called. It's a protected method!";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WrapperTestTargetBase)) return false;

        final WrapperTestTargetBase wrapperTestTargetBase = (WrapperTestTargetBase) o;

        if (m_intValue != wrapperTestTargetBase.m_intValue) return false;
        if (m_stringValue != null ? !m_stringValue.equals(wrapperTestTargetBase.m_stringValue) : wrapperTestTargetBase.m_stringValue != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = m_intValue;
        result = 29 * result + (m_stringValue != null ? m_stringValue.hashCode() : 0);
        return result;
    }
}
