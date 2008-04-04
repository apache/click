<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@page import="benchmark.dao.*" %>
<%@page import="java.text.*" %>
<%@page import="java.util.*" %>
<html>
<head>
<link rel="stylesheet" type="text/css" href="<c:out value="${pageContext.request.contextPath}"/>/styles/default.css"/>
<title>Customer List</title>
</head>
<body>
        <%
           List customers = CustomerDao.getInstance().findAll();
           request.setAttribute("customers", customers);
        %>
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
<c:forEach var="customer" items="${customers}" varStatus="lineInfo"> 
<c:choose>
<c:when test="${lineInfo.count % 2 == 0}"> <tr class="odd"> </c:when> 
<c:otherwise> <tr class="even"> </c:otherwise>
</c:choose> 
<td id="table-firstName_<c:out value="${lineInfo.count}"/>"><c:out value="${customer.firstName}"/></td> 
<td id="table-lastName_<c:out value="${lineInfo.count}"/>"><c:out value="${customer.lastName}"/></td>
<td id="table-state_<c:out value="${lineInfo.count}"/>"> <c:out value="${customer.state}"/> </td>
<td id="table-birthDate_<c:out value="${lineInfo.count}"/>"> <fmt:formatDate value="${customer.birthDate}" pattern="dd MMM yyyy"/> </td>
<td align="left">
<a href="/jsp/jsp-customer-list.htm?id=${customer.id}&value=0&page=0">Edit</a> |
<a href="/jsp/jsp-customer-list.htm?id=${customer.id}&value=0&page=0">Delete</a>                            
</td>
</tr>
</c:forEach>
</tbody>
</table>
</body>
</html>
