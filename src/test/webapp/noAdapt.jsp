<%@ page import="java.lang.reflect.Array,
                 br.com.devx.scenery.TemplateAdapter,
                 java.util.Iterator,
                 java.text.NumberFormat,
                 br.com.devx.scenery.TemplateFormatStrategy" %>

<jsp:useBean id="templateAdapter" scope="request" class="br.com.devx.scenery.TemplateAdapter" />

<jsp:useBean id="intValue" scope="request" type="java.lang.Integer" />
<jsp:useBean id="doubleValue" scope="request" type="java.lang.Double" />
<jsp:useBean id="booleanValue" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="dateValue" scope="request" type="java.util.Date" />
<jsp:useBean id="stringValue" scope="request" type="java.lang.String" />
<jsp:useBean id="otherAdapter" scope="request" type="br.com.devx.scenery.TemplateAdapter" />
<jsp:useBean id="arrayValue" scope="request" type="java.lang.Object" />
<jsp:useBean id="mapValue" scope="request" type="java.util.Map" />
<jsp:useBean id="collectionValue" scope="request" type="java.util.Collection" />

<% TemplateFormatStrategy formatStrategy = new TemplateFormatStrategy(); %>

intValue = <%= formatStrategy.format(null, intValue) %>;
doubleValue = <%= formatStrategy.format(null, doubleValue) %>;
booleanValue = <%= formatStrategy.format(null, booleanValue) %>;
dateValue = <%= formatStrategy.format(null, dateValue) %>;
stringValue = "<%= formatStrategy.format(null, stringValue) %>";

otherAdapter = {
    message = "<%= formatStrategy.format(null, otherAdapter.adapt("message")) %>";
};

arrayValue = array: {
    <% for (int i=0; i < Array.getLength(arrayValue); i++) { %>
        <%= formatStrategy.format(null, Array.get(arrayValue, i)) %>
    <% } %>
};

mapValue = map: {
    "x" = { message = "<%= formatStrategy.format(null, ((TemplateAdapter) mapValue.get("x")).adapt("message")) %>"; },
    "b" = <%= formatStrategy.format(null, mapValue.get("b")) %>,
    "a" = <%= formatStrategy.format(null, mapValue.get("a")) %>
};

collectionValue = collection: {
<% Iterator it = collectionValue.iterator(); %>
<% while(it.hasNext()) { %>
    <%= formatStrategy.format(null, it.next()) %>, <%= formatStrategy.format(null, it.next()) %>, { message = "<%= formatStrategy.format(null, ((TemplateAdapter) it.next()).adapt("message")) %>"; }
<% } %>
};
