<%@ page import="java.util.Iterator"%>
<jsp:useBean id="message" type="java.lang.String" scope="request" />
<jsp:useBean id="errorLine" type="java.lang.Integer" scope="request" />
<jsp:useBean id="errorBeginColumn" type="java.lang.Integer" scope="request" />
<jsp:useBean id="errorEndColumn" type="java.lang.Integer" scope="request" />
<jsp:useBean id="lines" type="java.util.List" scope="request" />

<html>
<head>
<title><%= message %></title>
<body>
<h2>Scenery data file error</h2>
<%= message %>
<hr>
<% Iterator i = lines.iterator(); %>
<% int count = 0; %>
<table border="0">
<% while (i.hasNext()) { %>
    <tr>
        <td>
            <font color="gray" size="-1"><%= ++count %></font>
        </td>
        <td>
            <% String line = (String) i.next(); %>
            <tt>
                <% for (int j = 0; j < line.length(); j++) {
                    char ch = line.charAt(j);
                    if (count == errorLine.intValue() && j == errorBeginColumn.intValue() -1) {
                        out.write("<font color=\"red\">");
                    }
                    if (ch != ' ') {
                        out.write(ch);
                    } else {
                        out.write("&nbsp;");
                    }
                    if (count == errorLine.intValue() && j == errorEndColumn.intValue() -1) {
                        out.write("</font>");
                    }
                } %>
            </tt>
        </td>
    </tr>
<% } %>
</table>
</body>
</html>