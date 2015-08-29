<!DOCTYPE html>
<html>
<head>
    <%@ include file="common/html_head.jsp" %>
</head>
<body>
<%@ include file="common/navbar_materialize.jsp" %>
basePath=<%=basePath%><br/>
contextPath=<%=contextPath%><br/>
userAgent=<%=request.getHeader("user-agent")%><br/>
remotePort=<%=request.getRemoteHost()%><br/>
<button class="btn btn-info waves-effect waves-light">
    <i class="material-icons left">send</i>Hello world
</button>
</body>
</html>
