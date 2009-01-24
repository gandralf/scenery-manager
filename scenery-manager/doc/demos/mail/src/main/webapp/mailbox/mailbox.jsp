<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Mailbox</title>
</head>
<body>
${date}<br />
Hello, ${userName}!<br />

Used space: ${usedSpace}K of ${totalSpace}K<br>
<c:if test="$mailboxFull">
    <strong>Warning: you mailbox is full!</strong>
</c:if>

<table border="1" width="100%">
	<tr><td>Subject</td>
        <td>from</td>
	    <td>Date</td>
    </tr>
    <c:forEach var="email" items="${emailList}">
        <tr>
            <td>${email.subject}</td>
            <td>${email.from}</td>
            <td>${email.date}</td>
        </tr>
    </c:forEach>

    <c:if test="${!empty previousPage or !empty nextPage}">
        <tr>
            <td align="left">
                <c:if test="${!empty previousPage}">
                    <a href="${previousPage}"> &lt;&lt; previews </a>
                </c:if>
            </td>
            <td></td>
            <td align="right">
                <c:if test="${!empty nextPage}">
                    <a href="${nextPage}"> next &gt;&gt; </a>
                </c:if>
            </td>
        </tr>
    </c:if>
</table>
</body>
</html>
