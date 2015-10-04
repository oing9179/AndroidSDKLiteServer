<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="jstlc" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jstlfmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String contextPath = request.getContextPath() + "/";
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" +
            request.getServerPort() + contextPath;
%>
<base href="<%=basePath%>">
<title>Android SDK Lite Server</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link type="text/css" rel="stylesheet" href="static/css/materialize.min.css" media="screen,projection"/>
<link type="text/css" rel="stylesheet" href="static/css/MaterialIcons.css"/>
<link type="text/css" rel="stylesheet" href="static/css/common.css"/>

<script type="text/javascript" src="static/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="static/js/handlebars.min-v4.0.2.js"></script>
<script type="text/javascript" src="static/js/materialize.min.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        $(".button-collapse").sideNav();
    });
</script>