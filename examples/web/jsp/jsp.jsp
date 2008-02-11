<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="benchmark.dao.*" %>
<%@page import="java.text.*" %>
<%@page import="java.util.*" %>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/styles/default.css"/>
        <title>Customer List</title>
    </head>
    <body>
        <%
           List customers = CustomerDao.getInstance().findAll();
           request.setAttribute("customers", customers);
           MessageFormat format = new MessageFormat("{0,date,MMMM d, yyyy}");
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
                <%
                customers = CustomerDao.getInstance().findAll();
                for(Iterator it = customers.iterator(); it.hasNext(); ) {
                Customer customer = (Customer) it.next();
                out.println("<tr>");
                out.print("<td name=\"firstName\">");
                out.print(customer.getFirstName());
                out.println("</td>");
                out.print("<td id=\"lastName\">");
                out.print(customer.getLastName());
                out.println("</td>");
                out.print("<td id=\"state\">");
                out.print(customer.getState());
                out.println("</td>");
                out.print("<td id=\"birthDate\">");
                Object[] args = new Object[] {customer.getBirthDate()};
                out.print(format.format(args));
                out.println("</td>");
                out.print("<td><a href=\"/benchmark/pages/edit-customer.htm?id=");
                out.print(customer.getId());
                out.print("\">Delete</a>");
                out.print(" ");
                out.print("<a href=\"/benchmark/pages/edit-customer.htm?id=");
                out.print(customer.getId());
                out.print("\">Edit</a></td>");
                out.println("</tr>");
                }
                %>
            </tbody>
        </table>
        
    </body>
</html>
