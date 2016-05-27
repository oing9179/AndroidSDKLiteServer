<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/common/jsp_header.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
    <script type="text/javascript" src="static/js/sha.min.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <c:if test="${objException != null}">
        <div class="card-panel red darken-4 white-text">${objException}</div>
    </c:if>
    <div class="card">
        <div class="card-content" style="padding-top:0;">
            <span class="card-title">Login</span>
            <div class="divider" style="margin:0 -20px;"></div>
            <form id="formLogin" action="admin/login.do" method="post" class="row">
                <div class="input-field col s12 m8 l6">
                    <input id="textBoxPassword" type="password" name="passwordSHA256"
                           required="required" maxlength="64"/>
                    <label for="textBoxPassword">Type your password</label>
                </div>
                <div class="col s12">
                    <button type="submit" class="btn indigo waves-effect waves-light">
                        <i class="material-icons left">send</i>Login
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<script type="text/javascript">
    function formLogin_onSubmit(e) {
        var $textBoxPassword = $("#textBoxPassword");
        var sha256sum = new jsSHA("SHA-256", "TEXT");
        sha256sum.update($textBoxPassword.val());
        $textBoxPassword.val(sha256sum.getHash("HEX"));
    }

    $(document).ready(function () {
        $("#formLogin").bind("submit", formLogin_onSubmit);
    });
</script>
</body>
</html>
