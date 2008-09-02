<jsp:useBean id="sceneryBaseURI" class="java.lang.String" scope="request" />
<jsp:useBean id="sceneryParameters" class="java.lang.String" scope="request" />

<script>
    if (window.name == "") {
        window.name = "sceneryTarget";
    }

    var popup = window.open("<%= request.getContextPath() %>/sceneryPopup.jsp?target=" + window.name + "&baseURI=<%= sceneryBaseURI + sceneryParameters %>",
        "sceneryPopup", "left=50, top=50, width=200, height=400, resizable=1, scrollbars=1, status=1");
</script>
