<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            $("select").material_select();
        });
    </script>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <form class="card" action="repository/xml/creation.do" method="post">
        <div class="card-content">
            <span class="card-title black-text">Create XML Repository</span>
            <div class="divider" style="margin: 0 -20px;"></div>
            <div class="row">
                <!-- form field: new repository name -->
                <div class="input-field col s12 m12 l6">
                    <input id="inputRepoName" type="text" name="name" class="validate"
                           title="Alphabets, numbers and underscores are allowed, at least 6 characters."
                           required="required" maxlength="32" pattern="^\w{6,32}$"/>
                    <label for="inputRepoName">XML repository name</label>
                </div>
                <!-- form field: create form existing repository -->
                <div class="input-field col s12 m12 l6">
                    <select name="createFrom">
                        <option value="">&lt;No&gt;</option>
                        <jstlc:forEach var="xmlRepository" items="${xmlRepositories}">
                            <option value="${xmlRepository.id}">${xmlRepository.name}</option>
                        </jstlc:forEach>
                    </select>
                    <label>From existing repository</label>
                </div>
            </div>
            <jstlc:if test="${objException != null}">
                <div class="card-panel red darken-4 white-text">${objException}</div>
            </jstlc:if>
        </div>
        <div class="card-action right-align">
            <button type="submit" class="btn btn-less-padding waves-effect waves-light">
                <i class="material-icons left">done</i>Submit
            </button>
            <a href="repository/xml/" style="margin-right: 0;"
               class="btn btn-less-padding waves-effect white grey-text text-darken-4">
                <i class="material-icons left">close</i>Cancel
            </a>
        </div>
    </form>
</div>
</body>
</html>
