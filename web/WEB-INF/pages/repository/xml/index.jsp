<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <div class="row">
        <div class="col s12">
            <div class="card">
                <div class="card-content" style="padding: 0;">
                    <!-- card title area -->
                    <span class="card-title black-text s12" style="padding-left: 12px;">XML Repository</span>
                    <a href="/repository/xml/creation.do" class="btn btn-less-padding waves-effect waves-light right"
                       style="margin-top: 6px; margin-right: 12px;" title="Add repository">
                        <span class="hide-on-small-only"><i class="material-icons left">add</i>Add</span>
                        <span class="hide-on-med-and-up"><i class="material-icons">add</i></span>
                    </a>
                    <div class="divider"></div>
                    <!-- card content area -->
                    <style type="text/css">
                        #tableRepoList thead th:nth-last-child(2) { text-align: right; }
                        #tableRepoList tbody td:nth-last-child(2) { text-align: right; }
                    </style>
                    <table id="tableRepoList" class="striped s12 m12 l12 table-in-a-card">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th class="hide-on-small-and-down" style="width: 180px;">Last modified</th>
                            <th style="width: 160px;">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <jstlc:forEach var="xmlRepository" items="${xmlRepositories}">
                            <tr>
                                <td>${xmlRepository.name}</td>
                                <td class="hide-on-small-only">
                                    <jstlfmt:formatDate value="${xmlRepository.dateLastModified}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <a href="/repository/xml/deletion.do?id=${xmlRepository.id}" title="Delete repository"
                                       class="btn btn-less-padding waves-effect waves-light red">
                                        <span class="hide-on-med-and-up"><i class="material-icons">delete</i></span>
                                        <span class="hide-on-small-only"><i class="material-icons left">delete</i>Delete</span>
                                    </a>
                                </td>
                            </tr>
                        </jstlc:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
