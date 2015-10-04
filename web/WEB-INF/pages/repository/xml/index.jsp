<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <div class="card">
        <div class="card-content" style="padding: 0;">
            <!-- card title -->
            <div class="row" style="margin:0;">
                <div class="col s12 m8 l8">
                    <span class="card-title black-text">XML Repository</span>
                </div>
                <div class="divider col s12 hide-on-med-and-up"></div>
                <div class="col s12 m4 l4 right-align" style="margin:0; padding:6px;">
                    <a href="repository/xml/creation.do"
                       class="btn btn-less-padding waves-effect waves-light green" title="Add">
                        <i class="material-icons">add</i>
                    </a>
                </div>
            </div>
            <div class="divider"></div>
            <!-- card content area -->
            <style type="text/css">
                #tableRepoList thead th:nth-last-child(2) { text-align: right; }
                #tableRepoList tbody td:nth-last-child(2) { text-align: right; }
            </style>
            <table id="tableRepoList" class="striped table-in-a-card" style="table-layout:fixed;">
                <thead>
                <tr>
                    <th style="width:50px;">#</th>
                    <th style="width:50%;">Name</th>
                    <th class="hide-on-small-and-down" style="width: 150px;">Last modified</th>
                    <th style="width: 130px;">Action</th>
                </tr>
                </thead>
                <tbody>
                <jstlc:forEach var="xmlRepository" items="${xmlRepositories}" varStatus="varStatus">
                    <tr>
                        <td>${varStatus.index+1}</td>
                        <td class="truncate-nonblock" style="width:50%;">${xmlRepository.name}</td>
                        <td class="hide-on-small-and-down">
                            <jstlfmt:formatDate value="${xmlRepository.dateLastModified}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            <!-- Date of create: ${xmlRepository.dateCreation} -->
                        </td>
                        <td>
                            <a href="repository/xml/${xmlRepository.name}/" title="Edit.."
                               class="btn btn-less-padding waves-effect waves-light">
                                <i class="material-icons">edit</i>
                            </a>
                            <a href="repository/xml/deletion.do?id=${xmlRepository.id}" title="Delete repository"
                               class="btn btn-less-padding waves-effect waves-light red">
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
