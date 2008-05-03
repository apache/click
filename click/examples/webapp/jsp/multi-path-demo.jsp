<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<title>Click Examples</title>
<link rel="stylesheet" type="text/css" href="${context}/assets/css/style.css" title="Style"/>
</head>
<body>
This demo shows how to dynamically change the path of a jsp page.
<p>
    Click <a href=${changePath.href}>here</a> to navigate to the jsp file
    <span class='st'>'/jsp/dummy.jsp'</span>.    
    <pre class="javaCode">
        public void changePath() {
            setPath(<span class='st'>"/jsp/dummy.jsp"</span>);
            return true;
        }
    </pre>
</p>
<p>
    <strong>Note:</strong> the path <span class='st'>'/jsp/dummy.jsp'</span>
    is not mapped by Click, and there is no corresponding Page class for this
    path.
</p>

</body>
</html>  

