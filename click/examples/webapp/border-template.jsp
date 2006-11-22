<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<title>Click Examples - ${title} </title>
<link rel="stylesheet" type="text/css" href="${context}/style.css" title="Style">
</head>
<body>

  <div id="header">
    <a id="clickLink" target="blank" href="http://click.sourceforge.net"><img src="${context}/images/click-icon-blue-32.png" border="0"/></a>
  	<span id="title">${title}</span>
  	<a id="hostedTitle" target="blank" href="http://www.avoka.com">Hosted by Avoka Technologies</a>  
  </div>

<table cellpadding="8" class="menuTable">
<tr>
<td>
&nbsp;<a class="menu" href="${context}/home.htm" title="Examples Home page">Home</a>
&nbsp; | &nbsp;
<a class="menu" href="${context}${path}" title="Refresh Page">Refresh</a>
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

  <div id="container">
    <jsp:include page='${forward}'/>
  </div>

</body>
</html>