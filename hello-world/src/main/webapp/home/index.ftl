<html>
<head>
<title>Hello world</title>
</head>
<body>
<div>
    <#if user??>
        Welcome back, ${user.name}.
    <#else>
        Have an account? <a href="/auth/signin">Sign in</a>. Or <a href="/auth/signup">sign up</a>
    </#if>
</div>
<div class="footer">
    Hello app, version ${app.version}. Updated at ${app.updatedAt ?datetime}
</div>
</body>
</html>