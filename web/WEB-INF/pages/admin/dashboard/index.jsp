<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/common/jsp_header.jsp" %>
<%@ page import="oing.webapp.android.sdkliteserver.misc.ApplicationConstants" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<%
    pageContext.setAttribute("repositoryXmlId", application.getAttribute(ApplicationConstants.KEY_REPOSITORY_XML_ID));
    pageContext.setAttribute("repositoryZipId", application.getAttribute(ApplicationConstants.KEY_REPOSITORY_ZIP_ID));
    pageContext.setAttribute("upstreamSpeedLimit", application.getAttribute(ApplicationConstants.KEY_UPSTREAM_SPEED_LIMIT));
%>
<div class="container">
    <div class="card">
        <div class="card-content" style="padding-top:0;">
            <span class="card-title">Deployment</span>

            <div class="divider" style="margin:0 -20px;"></div>
            <form action="admin/dashboard/deploy.do" method="get">
                <div class="row">
                    <div class="input-field col s12 m6 l6">
                        <select name="xmlRepositoryId">
                            <option value="">&lt;Disabled&gt;</option>
                            <c:forEach var="xmlRepository" items="${xmlRepositories}">
                                <option value="${xmlRepository.id}"
                                    ${repositoryXmlId == xmlRepository.id ? "selected" : ""}
                                >${xmlRepository.name}</option>
                            </c:forEach>
                        </select>
                        <label>XML Repository</label>
                    </div>
                    <div class="input-field col s12 m6 l6">
                        <select name="zipRepositoryId">
                            <option value="">&lt;Disabled&gt;</option>
                            <c:forEach var="zipRepository" items="${zipRepositories}">
                                <option value="${zipRepository.id}"
                                    ${repositoryZipId == zipRepository.id ? "selected" : ""}
                                >${zipRepository.name}</option>
                            </c:forEach>
                        </select>
                        <label>ZIP Repository</label>
                    </div>
                    <div class="input-field col s12 m6 l6">
                        <input id="textBoxUpstreamSpeedLimit" name="upstreamSpeedLimit" class="validate"
                               value="${upstreamSpeedLimit}" type="number" step="1" min="-1"/>
                        <label for="textBoxUpstreamSpeedLimit">Server-side upstream speed limit(Bytes/s)</label>
                    </div>
                    <div class="col s12">
                        <button class="btn indigo waves-effect waves-light">
                            <i class="material-icons left">check</i>Deploy
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        $('select').material_select();
    });
</script>
</body>
</html>
