<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<title>Click Examples</title>
<link rel="stylesheet" type="text/css" href="${context}/style.css" title="Style"></link>
${imports}
</head>
<body>

<center>
<table height="100%" border="0" cellpadding="0" cellspacing="4" class="page">
<%-- Title Header --%>
<tr>
	<td width="100%">			
		<table cellpadding="0" cellspacing="0" width="100%" border="0" style="border-collapse:collapse;">
			<tr>
				<td class="title-icon">
					<a target="blank" href="http://click.sourceforge.net"><img src="${context}/images/click-icon-blue-32.png" border="0" alt="Click"/></a>
				</td>
				<td class="title-left" width="100%">
					${title}
				</td>
				<td class="title-hosted">
					<p>${messages.version}</p>
					<a target="blank" href="http://www.avoka.com">Hosted by Avoka Technologies</a>
				</td>
			</tr>
			<tr>
				<td class="header-color" colspan="3">&nbsp;</td>
			</tr>
		</table>
	</td>
</tr>
<%-- Menu --%>
<tr>		
	<td>
	
<table id="menuTable" border="0" width="100%" cellspacing="0" cellpadding="0" style="margin-top: 2px;">
 <tr>
  <td>
 <div class="menustyle" id="menu">
  <ul class="menubar" id="dmenu">
    <c:forEach items="${rootMenu.children}" var="topMenu">
        <li class="topitem">${topMenu}
          <ul class="submenu"
          <c:forEach items="${topMenu.children}" var="subMenu">
            ><li>${subMenu}</li
          </c:forEach>
          ></ul>
        </li>
    </c:forEach>
        <li class="topitem"><a target="_blank" href="${context}/source-viewer.htm?filename=WEB-INF/classes/${srcPath}" title="Page Java source"><img border="0" class="link" alt="" src="${context}/images/icons-16px/text_code_java.png"/> Page Java</a>
        </li>
        <li class="topitem"><a target="_blank" href="${context}/source-viewer.htm?filename=${path}" title="Page Content source"><img border="0" class="link" alt="" src="${context}/images/icons-16px/text_marked.png"/> Page HTML</a>
        </li>
  </ul>  
 </div>
  </td>
 </tr>
</table>

	</td>
</tr>
<%-- Page Content --%>
<tr>		
	<td class="content">
    <jsp:include page='${forward}'/>
	</td>
</tr>
<%-- Vertical Spacer --%>
<tr>		
	<td height="100%">&nbsp;</td>
</tr>
</table>
</center>

</body>
</html>
