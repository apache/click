<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html>
<head>
<link rel="stylesheet" type="text/css" href="<c:out value="${pageContext.request.contextPath}"/>/styles/default.css"/>
<title>Customer List</title>
</head>
<body>

<h1>Customer List</h1>
<table class="decorated">
<thead>
<tr>
<td>First Name</td>
<td>Last Name</td>
<td>State</td>
<td>Birth Date</td>
<td>Options</td>
</tr>
</thead>
<tbody>
<logic:iterate name="customers" id="customer" indexId="index">
<c:choose>
 <c:when test="${lineInfo.count % 2 == 0}">
<tr class="odd">
 </c:when>
 <c:otherwise>
<tr class="even">
 </c:otherwise>
 </c:choose>
    <td><bean:write name="customer" property="firstName"/></td>
    <td><bean:write name="customer" property="lastName"/></td>
    <td><bean:write name="customer" property="state"/></td>
    <td><bean:write name="customer" format="MMMM d, yyyy" property="birthDate"/></td>
    <td>
      <a href="/struts/customer-list.do?id=<c:out value="${index}"/>&amp;value=0&amp;page=0">Edit</a> |
      <a href="/struts/customer-list.do?id=<c:out value="${index}"/>&amp;value=0&amp;page=0">Delete</a>
    </td>
</tr>
</logic:iterate>

</tbody>
</table>
</body>
</html>
