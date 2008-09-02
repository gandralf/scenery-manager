/**
 * User: gandralf
 * Date: Dec 28, 2002
 * Time: 11:43:56 AM
 */
package br.com.devx.scenery.parser;

import java.util.Date;

public interface TestInterface {
    int getIntValue();
    void setIntValue(int intValue);

    String getStringValue();
    void setStringValue(String stringValue);

    Date getDateValue();
    void setDateValue(Date dateValue);

    boolean getBooleanValue();
    public void setBooleanValue(boolean booleanValue);
}
