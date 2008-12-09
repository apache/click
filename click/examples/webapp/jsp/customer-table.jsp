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

<style type="text/css">
th { color: white; }
</style>

<p>
Example JSP customers table.
</p>

<table style="border: 1px solid black;" cellpadding="6" cellspacing="0">
  <tr valign="baseline" bgcolor="404060">
    <th align="center"> ID </th>
    <th align="left"> Name </th>
    <th align="left"> Email </th>
    <th align="center"> Age </th>
    <th align="left"> Category </th>
    <th align="center"> Portfolio </th>
    <th align="right"> Date Joined </th>
    <th align="center"> Active </th>
  </tr>

<c:forEach var="customer" items="${customers}" varStatus="lineInfo"> 

 <c:choose>
   <c:when test="${lineInfo.count % 2 == 0}"> <tr bgcolor="#f7f7e7"> </c:when> 
   <c:otherwise> <tr bgcolor="white"> </c:otherwise>
 </c:choose> 

    <td align="center"> ${customer.id} </td> 
    <td align="left"> ${customer.name} </td>
    <td align="left"> <a href="${customer.email}">${customer.email}</a> </td>
    <td align="center"> ${customer.age} </td>
    <td align="left"> ${customer.investments} </td>
    <td align="right"> <fmt:formatNumber value="${customer.holdings}" type="currency"/></td>
    <td align="right"> <fmt:formatDate value="${customer.dateJoined}" pattern="dd MMM yyyy"/> </td>
    
    <td align="center">    
      <c:choose>
        <c:when test="${customer.active}"> <input type="checkbox" checked="checked"/> </c:when> 
        <c:otherwise> <input type="checkbox"/> </c:otherwise>
      </c:choose>     
    </td> 

  </tr>
  
</c:forEach>

</table>

<p>&nbsp;</p>

The <tt>CustomerTable</tt> page is automatically mapped to the request:

<pre class="codeConfig">
GET customer-table.htm </pre>

When pages are configured to use automapping the <tt>ClickServlet</tt>
will automatically associated the file path <tt>customer-table.jsp</tt>
with the page class <tt>CustomerTable</tt>. 
