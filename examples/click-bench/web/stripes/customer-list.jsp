<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

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

<c:forEach var="customer" items="${actionBean.customers}" varStatus="lineInfo">

<c:choose>
<c:when test="${lineInfo.count % 2 == 0}">
<tr class="odd">
</c:when>
<c:otherwise>
<tr class="even">
</c:otherwise>

</c:choose>
 <td><c:out value="${customer.firstName}"/></td>
 <td><c:out value="${customer.lastName}"/></td>
 <td><c:out value="${customer.state}"/></td>
 <td> <fmt:formatDate value="${customer.birthDate}" pattern="MMMM d, yyyy"/></td>
 <td>
  <a href="${pageContext.request.contextPath}/CustomerList.action?id=<c:out value="${index}"/>&amp;value=0&amp;page=0">Edit</a> |
  <a href="${pageContext.request.contextPath}/CustomerList.action?id=<c:out value="${index}"/>&amp;value=0&amp;page=0">Delete</a>
 </td>
</tr>
</c:forEach>

</tbody>
</table>
</body>
</html>
