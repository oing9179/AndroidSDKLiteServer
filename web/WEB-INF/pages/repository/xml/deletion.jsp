<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/common/jsp_header.jsp" %>
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
    <form class="card" action="repository/xml/deletion.do" method="post">
        <div class="card-content">
            <span class="card-title red-text text-darken-4">Delete XML Repository</span>
            <div class="divider" style="margin: 0 -20px;"></div>
            <h5 class="red-text text-darken-4 center-align">You are going to delete xml repository: ${xmlRepository.name}</h5>
            <div class="row" style="margin-bottom:0;">
                <span class="col s12 m6 l6">Date of create:
                    <jstlfmt:formatDate value="${xmlRepository.dateCreation}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </span>
                <span class="col s12 m6 l6">Date of last modified:
                    <jstlfmt:formatDate value="${xmlRepository.dateLastModified}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </span>
            </div>
            <div class="row">
                <!-- Warning text -->
                <div class="col s12 m12 l6" style="padding-top: 10px;">
                    Read below before delete this repository:
                    <ol>
                        <li class="red-text text-darken-4">All files in this xml repository will be deleted and UNRECOVERABLE.</li>
                        <li class="green-text text-darken-4">The zip repositories(and its files) who depends on this xml repository will safe.</li>
                    </ol>
                </div>
                <!-- Table: zip repositories who depends on this xml repository -->
                <div class="col s12 m12 l6">
                    <style type="text/css">
                        #tableZipRepoDependencyList th,td{padding: 10px 5px;}
                    </style>
                    <table id="tableZipRepoDependencyList" class="bordered centered">
                        <thead>
                        <tr>
                            <th>These zip repositories depends on this repository</th>
                        </tr>
                        </thead>
                        <tbody>
                        <jstlc:if test="${zipRepositories!=null && zipRepositories.size()==0}">
                            <tr class="green-text text-darken-4"><td>&lt;No one depends on it.&gt;</td></tr>
                        </jstlc:if>
                        <jstlc:forEach var="zipRepository" items="${zipRepositories}">
                            <tr><td>${zipRepository.name}</td></tr>
                        </jstlc:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="row" style="margin-bottom: 0;">
                <input type="hidden" name="id" value="${xmlRepository.id}"/>
                <div class="input-field col s12 m8 offset-m2 l6 offset-l3">
                    <input id="textBoxRepoName" type="text" name="name" class="validate"
                           required="required" pattern="^${xmlRepository.name}$"/>
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
            <a href="repository/xml/" style="margin-right: 0;"
               class="btn btn-less-padding waves-effect white grey-text text-darken-4">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
