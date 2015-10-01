<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <jstlc:if test="${errorMessage != null}">
        <div class="card-panel red darken-4 white-text">${errorMessage}</div>
    </jstlc:if>
    <div class="card" style="display:${errorMessage==null ? "block" : "none"};">
        <div class="card-content" style="padding:0;">
            <div class="row" style="margin:0;">
                <div class="col s12 m8 l8">
                    <span class="card-title black-text">Edit repository</span><br/>
                    <span class="black-text">${xmlRepository.name}</span>
                </div>
                <div class="divider col s12 hide-on-med-and-up"></div>
                <div class="col s12 m4 l4 right-align" style="padding:6px;">
                    <a href="/repository/xml/${xmlRepository.name}/automatic_addition.do"
                       class="btn btn-less-padding waves-effect waves-light" title="Automatic add">
                        <i class="material-icons">android</i>
                    </a>
                    <a href="/repository/xml/${xmlRepository.name}/manual_addition.do"
                       class="btn btn-less-padding waves-effect waves-light green" title="Manual add">
                        <i class="material-icons">add</i>
                    </a>
                    <a href="/repository/xml/deletion.do?id=${xmlRepository.id}" title="Delete repository"
                       class="btn btn-less-padding waves-effect waves-light red white-text" style="display:none;">
                        <i class="material-icons">delete</i>
                    </a>
                </div>
            </div>
            <div class="divider"></div>
            <table id="tableFileList" class="striped table-in-a-card" style="table-layout:fixed;">
                <thead>
                <tr>
                    <th style="width:50px;">#</th>
                    <th>File name</th>
                    <th class="hide-on-small-and-down">URL</th>
                    <th style="width:130px;">Action</th>
                </tr>
                </thead>
                <tbody>
                <jstlc:forEach var="xmlFile" items="${xmlFiles}" varStatus="varStatus">
                    <tr>
                        <td>${varStatus.index+1}</td>
                        <td class="truncate-nonblock" title="${xmlFile.fileName}">${xmlFile.fileName}</td>
                        <td class="hide-on-small-and-down truncate-nonblock" title="${xmlFile.url}">${xmlFile.url}</td>
                        <td>
                            <a href="/repository/xml/${xmlRepository.name}/xml_editor.do?id=${xmlFile.id}"
                               class="btn btn-less-padding waves-effect waves-light" title="Edit">
                                <i class="material-icons">edit</i>
                            </a>
                            <a href="/repository/xml/${xmlRepository.name}/deletion.do?id=${xmlFile.id}"
                               class="btn btn-less-padding waves-effect waves-light red white-text" title="Delete">
                                <i class="material-icons">delete</i>
                            </a>
                        </td>
                    </tr>
                </jstlc:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
