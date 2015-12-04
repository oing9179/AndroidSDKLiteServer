<%@ taglib prefix="jstlc" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jstlfmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String contextPath = request.getContextPath() + "/";
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" +
            request.getServerPort() + contextPath;
%>