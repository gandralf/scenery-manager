<%@ page import="java.lang.reflect.Array,
                 br.com.devx.scenery.TemplateAdapter,
                 java.util.Iterator" %>

<jsp:useBean id="templateAdapter" scope="request" class="br.com.devx.scenery.TemplateAdapter" />

<jsp:useBean id="intValue" scope="request" class="java.lang.String" />
<jsp:useBean id="doubleValue" scope="request" class="java.lang.String" />
<jsp:useBean id="booleanValue" scope="request" class="java.lang.String" />
<jsp:useBean id="dateValue" scope="request" class="java.lang.String" />
<jsp:useBean id="stringValue" scope="request" class="java.lang.String" />
<jsp:useBean id="otherAdapter" scope="request" class="br.com.devx.scenery.TemplateAdapter" />
<jsp:useBean id="arrayValue" scope="request" class="java.lang.Object" />
<jsp:useBean id="mapValue" scope="request" type="java.util.Map" />
<jsp:useBean id="collectionValue" scope="request" type="java.util.Collection" />

intValue = <%= intValue %>;
doubleValue = <%= doubleValue %>;
booleanValue = <%= booleanValue %>;
dateValue = <%= dateValue %>;
stringValue = "<%= stringValue %>";

otherAdapter = {
    message = "<%= otherAdapter.adapt("message") %>";
};

arrayValue = array: {
    <% for (int i=0; i < Array.getLength(arrayValue); i++) { %>
        <%= Array.get(arrayValue, i) %>
    <% } %>
};

mapValue = map: {
    "x" = { message = "<%= ((TemplateAdapter) mapValue.get("x")).adapt("message") %>"; },
    "b" = <%= mapValue.get("b") %>,
    "a" = <%= mapValue.get("a") %>
};

collectionValue = collection: {
<% Iterator it = collectionValue.iterator(); %>
<% while(it.hasNext()) { %>
    <%= it.next() %>, <%= it.next() %>, { message = "<%= ((TemplateAdapter) it.next()).adapt("message") %>"; }
<% } %>
};
