<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<title>Click Examples - ${title} </title>
<link rel="stylesheet" type="text/css" href="${context}/style.css" title="Style">
</head>
<body>

<table border="0" cellpadding="0" cellspacing="0">
 <tr>
  <td style="padding-left:4px;">
   <a target="topic" href="http://click.sourceforge.net"><img src="${context}/images/click-icon-blue-32.png" border="0"/></a>
  </td>
  <td class="appnameText" style="padding-left:4px;">
  ${title}
  </td>
 </tr>
</table>

<table cellpadding="8" class="menuTable">
<tr>
<td>
&nbsp;<a class="menu" href="${context}/home.htm" title="Examples Home page">Home</a>
&nbsp; | &nbsp;
<a class="menu" href="${context}/source-viewer.htm?filename=WEB-INF/click.xml" title="Click application descriptor">click.xml</a>
&nbsp; | &nbsp;
<a class="menu" href="${context}/source-viewer.htm?filename=WEB-INF/web.xml" title="Web application descriptor">web.xml</a>
&nbsp; | &nbsp;
<a class="menu" href="${context}/javadoc/index.html" title="Examples HTML Javadoc API">Javadoc API</a>
&nbsp; | &nbsp;
<a class="menu" href="${context}/source-viewer.htm?filename=WEB-INF/classes/net/sf/click/examples/page/BorderPage.java" title="BorderPage Java source">BorderPage</a> 
&nbsp; | &nbsp;
<a class="menu" href="${context}/source-viewer.htm?filename=/border-template.jsp" title="Page border JSP template">Border Template JSP </a>
&nbsp; | &nbsp;
<a class="menu" href="${context}/source-viewer.htm?filename=WEB-INF/classes/${srcPath}" title="Page Java source">Page Java</a> 
&nbsp; | &nbsp;
<a class="menu" href="${context}/source-viewer.htm?filename=${forward}" title="Page Content source">Page JSP</a>
</td>
<td width="100px" style="color:d0d0d0;text-align:right">
${messages.version}
</td>
</tr>
</table>

<jsp:include page='${forward}'/>

</body>
</html>