<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <form class="card" action="repository/zip/deletion.do" method="post">
        <div class="card-content">
            <span class="card-title red-text text-darken-4">Delete ZIP Repository</span>
            <div class="divider" style="margin:0 -20px;"></div>
            <h5 class="red-text text-darken-4 center-align">You are going to delete zip repository: ${zipRepository.name}</h5>
            <div class="row" style="margin-bottom:0;">
                <span class="col s12 m12 l6">Date of create:
                    <jstlfmt:formatDate value="${zipRepository.dateCreation}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </span>
                <span class="col s12 m12 l6">Date of last modified:
                    <jstlfmt:formatDate value="${zipRepository.dateLastModified}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </span>
            </div>
            <h6 class="red-text text-darken-4 center-align">
                All files in this zip repository will be deleted and UNRECOVERABLE.
            </h6>
            <div class="row" style="margin-bottom: 0;">
                <input type="hidden" name="id" value="${zipRepository.id}"/>
                <div class="input-field col s12 m8 offset-m2 l6 offset-l3">
                    <input id="textBoxRepoName" type="text" name="name" class="validate"
                           required="required" pattern="^${zipRepository.name}$"/>
                    <label for="textBoxRepoName">Type repository name to confirm.</label>
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
            <a href="repository/zip/" style="margin-right: 0;"
               class="btn btn-less-padding waves-effect white grey-text text-darken-4">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
