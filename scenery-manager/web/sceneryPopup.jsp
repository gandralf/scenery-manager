<html>
<head><title>SceneryManager</title></head>
<body>
<% String baseURI = request.getParameter("baseURI"); %>
<% String target = request.getParameter("target"); %>
<% String[] sceneryParameters = request.getParameterValues("scenery"); %>

<h3><%= baseURI %></h3><ol>
<% for (int i = 0; i < sceneryParameters.length; i++) { %>
    <% String[] scenery = sceneryParameters[i].split("\\$"); %>
    <% String param = "null".equals(scenery[0]) ? "" : "?" + scenery[0]; %>
    <% if ("true".equals(scenery[4])) { %> <b> <% } %>
    <li><a href="<%= request.getContextPath() + baseURI + param %>" target="<%= target %>"><%= scenery[3] %> [<%= param %>]</a></li>
    <% if ("true".equals(scenery[4])) { %> </b> <% } %>
<% } %>
</ol>

</body>
