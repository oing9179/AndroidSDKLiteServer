<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <form class="card" action="repository/xml/${xmlRepository.name}/deletion.do" method="post">
        <div class="card-content" style="padding-top:0;">
            <div class="row" style="margin-bottom:0;">
                <div class="card-title col s12 red-text text-darken-4">Delete XML file</div>
                <div class="col s12 black-text">${xmlRepository.name}</div>
            </div>
            <div class="divider" style="margin:0 -20px;"></div>
            <div class="row" style="margin:0">
                <div class="col s12">
                    <h5 class="red-text text-darken-4 center-align">
                        You are going to delete xml file: ${xmlFile.fileName}
                    </h5>
                </div>
            </div>
            <div class="row" style="margin:0;">
                <input type="hidden" name="id" value="${xmlFile.id}"/>
                <div class="input-field col s12 m8 offset-m2 l6 offset-l3">
                    <input id="textBoxFileName" type="text" name="name" class="validate"
                           required="required" pattern="^${xmlFile.fileName}$"/>
                    <label for="textBoxFileName">Type repository name to confirm.</label>
                </div>
            </div>
            <jstlc:if test="${objException != null}">
                <div class="card-panel red darken-4 white-text">${objException}</div>
            </jstlc:if>
        </div>
        <div class="card-action right-align">
            <button type="submit" class="btn btn-less-padding waves-effect waves-light red white-text">
                <i class="material-icons left">delete</i>Delete
            </button>
            <a href="repository/xml/${xmlRepository.name}/" style="margin-right: 0;"
               class="btn btn-less-padding waves-effect white grey-text text-darken-4">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
