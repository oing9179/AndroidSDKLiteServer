<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <div class="card-panel red darken-4 white-text" style="display:${errorMessage!=null ? "block" : "none"}">
        ${errorMessage}
    </div>
    <div class="card" style="display:${errorMessage==null ? "block" : "none"};">
        <div class="card-content" style="padding:0;">
            <span class="card-title black-text" style="padding-left:12px;">Edit repository</span><br/>
            <span class="black-text" style="padding-left:12px;">${xmlRepository.name}</span>
            <div class="divider"></div>
            <div class="row right-align" style="margin:0; padding:6px;">
                <a href="/repository/xml/${xmlRepository.name}/automatic_addition.do"
                   class="btn btn-less-padding waves-effect waves-light" title="Automatic add">
                    <i class="material-icons">android</i>
                </a>
                <a href="/repository/xml/${xmlRepository.name}/manual_addition.do"
                   class="btn btn-less-padding waves-effect waves-light green" title="Manual add">
                    <i class="material-icons">add</i>
                </a>
                <a href="/repository/xml/deletion.do?id=${xmlRepository.id}" title="Delete repository"
                   class="btn btn-less-padding waves-effect waves-light red white-text">
                    <i class="material-icons">delete</i>
                </a>
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
                            <button class="btn btn-less-padding waves-effect waves-light">
                                <i class="material-icons">edit</i>
                            </button>
                            <button class="btn btn-less-padding waves-effect waves-light red white-text">
                                <i class="material-icons">delete</i>
                            </button>
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
