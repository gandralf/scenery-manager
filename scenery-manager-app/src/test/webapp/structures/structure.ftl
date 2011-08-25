<!DOCTYPE html>

[#macro includeZone zoneName]
	[#local zoneViewName="___zones_" + zoneName]
	[#if .vars[zoneViewName]??]
		${.vars[zoneViewName]}
	[#elseif .vars[zoneName]??]
		[@.vars[zoneName] /]
	[#else]
		<span class="error">Zona con nombre '${zoneName}' no ha sido definida!!!!!!</span>
	[/#if]
[/#macro]

[#assign __body]
    [@includeZone 'container' /]
[/#assign]

<html>
<head>
    <title>Title</title>
</head>
<body>
[ ${__body} ]
</body>
</html>