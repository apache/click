<!doctype html>

<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title>${title} : Click Examples</title>
<link type="text/css" rel="stylesheet" href="${context}/assets/css/style.css" title="Style"/>
${headElements}
<link rel="shortcut icon" href="$context/favicon.ico" type="image/ico"/>
</head>
<body>

<div class="page">
  <div class="header">

    <%-- Title Header --%>
    <div class="title-icon">
      <a target="blank" href="http://click.apache.org/"><img src="${context}/assets/images/click-icon-blue-32.png" border="0" alt="Click"/></a>
    </div>
		<div class="title-left">
      Click Examples
    </div>
    <div class="title-hosted">
      <p>Version ${messages.version}</p>
      <a target="blank" href="http://www.avoka.com">Hosted by Avoka Technologies</a>
    </div>
    <div class="header-color">
    </div>

    <%-- Menu --%>
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
            <li class="topitem"><a target="_blank" href="${context}/source-viewer.htm?filename=WEB-INF/classes/${srcPath}" title="Page Java source"><img border="0" class="link" alt="" src="${context}/assets/images/lightbulb1.png"/> Page Java</a>
            </li>
            <li class="topitem"><a target="_blank" href="${context}/source-viewer.htm?filename=${path}" title="Page Content source"><img border="0" class="link" alt="" src="${context}/assets/images/lightbulb2.png"/> Page HTML</a>
            </li>
          </ul>
        </div>
      </td>
    </tr>
    </table>
  </div>

  <%-- Page Content --%>
  <div class="content">
	  <h2>${title}</h2>
    <p/>
    <jsp:include page='${forward}' flush="true"/>
	</div>

</div>

${jsElements}

</body>
</html>
