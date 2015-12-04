<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/common/jsp_header.jsp" %>
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
一只敏捷的棕色狐狸跳过一条懒惰的狗.<br/>
<button class="btn btn-info waves-effect waves-light">
    <i class="material-icons left">send</i>Hello world
</button>
</body>
</html>
