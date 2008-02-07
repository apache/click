<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<title>Click Examples</title>
<link rel="stylesheet" type="text/css" href="${context}/assets/css/style.css" title="Style"/>
<style type="text/css">body { background: white; font-size: 10pt; }</style>
</head>
<body>
<h2>JSP Hello World</h2>

<p>    
Hello world from Click at ${time}
</p>  

<p>
To return home click here <a href="${context}/home.htm">here</a>.
</p>  
</body>
</html>  

