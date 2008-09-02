<%@ page import="java.util.Iterator, br.com.devx.scenery.TemplateAdapter" %>

<jsp:useBean id="date" scope="request" class="java.lang.String" />
<jsp:useBean id="userName" scope="request" class="java.lang.String" />
<jsp:useBean id="emailList" scope="request" type="java.util.Collection" />
<jsp:useBean id="usedSpace" scope="request" class="java.lang.String" />
<jsp:useBean id="totalSpace" scope="request" class="java.lang.String" />
<jsp:useBean id="mailboxFull" scope="request" class="java.lang.String" />
<jsp:useBean id="previousPage" scope="request" class="java.lang.String" />
<jsp:useBean id="nextPage" scope="request" class="java.lang.String" />

<%= date %><br>
Ol�, <%= userName %>!<br>

Espa�o usado: <%= usedSpace %>K de <%= totalSpace %>K<br>
<% if ("true".equals(mailboxFull)) { %>
    <b>Aten��o: a sua caixa postal est� cheia! Bla-bla-bla...</b><br>
<% } %>

<table border="1" width="100%">
	<tr><td>Assunto</td><td>de</td>
	    <td>Data</td></tr>
	<% Iterator it = emailList.iterator(); %>
	<% while(it.hasNext()) { %>
	<% TemplateAdapter email =
            (TemplateAdapter) it.next(); %>
	<tr>
		<td><%= email.adapt("subject") %></td>
		<td><%= email.adapt("from") %></td>
		<td><%= email.adapt("date") %></td>
	</tr>
	<% } %>

    <tr>
        <td align="left">
            <% if(previousPage.length() != 0) { %>
                <a href="<%= previousPage %>">P�gina anterior</a>
            <% } %>
        </td>
        <td></td>
        <td align="right">
            <% if(nextPage.length() != 0) { %>
                <a href="<%= nextPage %>">Pr�xima p�gina</a>
            <% } %>
        </td>
    </tr>
</table>

