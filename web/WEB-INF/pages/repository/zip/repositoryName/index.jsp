<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <jstlc:if test="${objException != null}">
        <div class="card-panel red darken-4 white-text">${objException}</div>
    </jstlc:if>
    <div class="card">
        <div class="card-content" style="padding-top:0;">
            <span class="card-title black-text">${zipRepository.name}</span>
            <div class="divider" style="margin:0 -20px 12px -20px;"></div>
            <div class="row">
                <div class="col s12 m6 l6">Date of create:
                    <jstlfmt:formatDate value="${zipRepository.dateCreation}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </div>
                <div class="col s12 m6 l6">Date of last modified:
                    <jstlfmt:formatDate value="${zipRepository.dateLastModified}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </div>
            </div>
            <div class="row">
                <div class="col s12 m6 l6" style="margin-bottom:6px;">Storage usage:<br/>
                    <jstlc:set var="fileSize" scope="page"
                               value="${zipRepository.totalFileSize / 1024 / 1024 / 1024}"/>
                    <span class="badge-span blue darken-2 white-text">${zipRepository.totalFileCount}</span> files up to
                    <span class="badge-span green darken-2 white-text">
                        <jstlfmt:formatNumber value="${fileSize}" pattern="0.00"/> GiB
                    </span>
                </div>
                <form id="formChangeRepositoryDependency" class="input-field col s12 m6 l6" method="post"
                        action="repository/zip/${zipRepository.name}/update_repository_dependency.do">
                    <select name="xmlRepository.id">
                        <option value="-1">&lt;No&gt;</option>
                        <jstlc:forEach var="xmlRepository" items="${xmlRepositories}">
                            <option value="${xmlRepository.id}"
                                ${zipRepository.idRepoXml == xmlRepository.id ? "selected" : ""}>${xmlRepository.name}
                            </option>
                        </jstlc:forEach>
                    </select>
                    <label>XML repository dependency</label>
                </form>
            </div>
            <div class="row">
                <div class="col s12 center-align" style="padding-bottom:6px;">Choose what you want to do</div>
                <div class="col s12 m6 l6">
                    <a href="javascript:;" style="width:100%; margin-bottom:6px;"
                       class="btn light-green darken-1 waves-effect waves-light"
                       title="Parse zip URLs from specified xml repository to complete missing files.">
                        <i class="material-icons left">done_all</i>File completion
                    </a>
                </div>
                <div class="col s12 m6 l6">
                    <a href="javascript:;" style="width:100%; margin-bottom:6px;"
                       class="btn light-green darken-1 waves-effect waves-light"
                       title="Cleanup files what no longer needed(eg: obsoleted).">
                        <i class="material-icons left">clear_all</i>Clear redundancy
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
<style type="text/css">
    .badge-span { padding: 2px 6px; border-radius: 3px; }
</style>
<script type="text/javascript">
    function document_onReady(){
        $("select").material_select();
        $("#formChangeRepositoryDependency select").bind("change", function(e){
            $("#formChangeRepositoryDependency").trigger("submit");
        });
    }

    $(document).ready(document_onReady);
</script>
</body>
</html>
