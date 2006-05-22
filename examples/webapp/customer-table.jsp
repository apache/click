<%@ page language="java"
    contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.List"
    import="net.sf.click.util.Format"
    import="net.sf.click.examples.domain.Customer"%>

<html>
<head>
<title>Click Examples - <%=request.getAttribute("title")%> </title>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("context")%>/style.css" title="Style">
</head>
<body>

<h1><%=request.getAttribute("title")%></h1> 

<div style="background-color: navy; color: white; padding: 0.5em;">
&nbsp;
<a class="menu" href="<%=request.getAttribute("context")%>/index.html" title="Examples Index">Examples</a>
&nbsp; | &nbsp;
<a class="menu" href="<%=request.getAttribute("context")%>/source-viewer.htm?filename=WEB-INF/classes/<%=request.getAttribute("srcPath")%>" title="Page Java source">Page Java</a> 
&nbsp; | &nbsp;
<a class="menu" href="<%=request.getAttribute("context")%>/source-viewer.htm?filename=customer-table.jsp" title="Page Content source">Page JSP</a>
&nbsp; | &nbsp;
<a class="menu" href="<%=request.getAttribute("context")%>/source-viewer.htm?filename=border.htm" title="Page broder HTML template">Border HTML</a>
&nbsp; | &nbsp;
<a class="menu" href="<%=request.getAttribute("context")%>/source-viewer.htm?filename=WEB-INF/click.xml" title="Click application descriptor">click.xml</a>
&nbsp; | &nbsp;
<a class="menu" href="<%=request.getAttribute("context")%>/source-viewer.htm?filename=WEB-INF/web.xml" title="Web application descriptor">web.xml</a>
&nbsp; | &nbsp;
<a class="menu" href="<%=request.getAttribute("context")%>/javadoc/index.html" title="Examples HTML Javadoc API">Javadoc API</a>
</div>
<br/>

Example JSP customers table.
<p/>

<style type="text/css">
th { color: white; }
</style>

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
  
<% List customers = (List) request.getAttribute("customers");
   Format format = (Format) request.getAttribute("format");

for (int i = 0; i < customers.size(); i++) { 
    Customer customer = (Customer) customers.get(i);
    String color = "white";
    if (i % 2 == 0) {
        color = "#f7f7e7";
    }
%>
  <tr bgcolor="<%=color%>"> 
    <td align="center"> <%=customer.getId()%> </td> 
    <td align="left"> <%=customer.getName()%> </td>
    <td align="left"> <%=format.email(customer.getEmail())%> </td>
    <td align="center"> <%=customer.getAge()%> </td>
    <td align="left"> <%=customer.getInvestments()%> </td>
    <td align="right"> <%=format.currency(customer.getHoldings())%> </td>
    <td align="right"> <%=format.date(customer.getDateJoined())%> </td>
    <td align="center"> 
  <% if (customer.getActive().booleanValue()) { %>
      <input type="checkbox" checked="checked"/>
  <% } else { %>
      <input type="checkbox"/>
  <% } %>
    </td>
  </tr>
<% } %>
</table>

<p>&nbsp;</p>

The <tt>CustomerTable</tt> page is automatically mapped to the request:

<pre class="codeConfig">
GET customer-table.htm </pre>

When pages are configured to use automapping the <tt>ClickServlet</tt>
will automatically associated the file path <tt>customer-table.jsp</tt>
with the page class <tt>CustomerTable</tt>. 

</body>
</html>

