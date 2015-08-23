<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
    <script type="text/javascript" src="/static/js/jquery-dateFormat.min.js"></script>

    <script type="text/javascript">
        var lJsonArrTableData = ${data};
        var TableManagerRepoList = {
            reloadData: function () {
                var $table = $("#tableRepoList tbody");
                for (var i = 0; i < lJsonArrTableData.length; i++) {
                    var $tdName = $("<td></td>").html(lJsonArrTableData[i].name);
                    var $tdDateLastModified = $("<td></td>").attr("class", "hide-on-small-and-down")
                            .html($.format.date(lJsonArrTableData[i].dateLastModified, "MMM/dd/yyyy HH:mm:ss"));
                    // button: delete
                    var $buttonDelete = $("<button></button>")
                            .attr("class", "btn btn-less-padding waves-effect waves-light red").html("Delete");
                    var $iIconTrash = $("<i></i>").attr("class", "material-icons left").html("delete");
                    $buttonDelete.prepend($iIconTrash);
                    // td: button container
                    var $tdButtons = $("<td></td>").append($buttonDelete);
                    $table.append($("<tr></tr>").append($tdName).append($tdDateLastModified).append($tdButtons));
                }
            }
        }

        $(document).ready(function () {
            TableManagerRepoList.reloadData();
        });
    </script>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div style="padding:6px;">
    <div class="container">
        <div class="row">
            <div class="col s12">
                <div class="card">
                    <div class="card-content" style="padding: 0;">
                        <!-- card title area -->
                        <span class="card-title black-text s12 m12 l12" style="padding-left: 12px;">
                            XML Repository
                        </span>
                        <a href="/repository/xml/creation.do"
                           class="btn btn-less-padding waves-effect waves-light right"
                           style="margin-top: 6px; margin-right: 12px;"><i class="material-icons left">add</i>Add
                        </a>
                        <div class="divider"></div>
                        <!-- card content area -->
                        <style type="text/css">
                            #tableRepoList thead th:nth-last-child(2) {text-align: right;}
                            #tableRepoList tbody td:nth-last-child(2) {text-align: right;}
                        </style>
                        <table id="tableRepoList" class="striped s12 m12 l12 table-in-a-card">
                            <thead>
                            <th>Name</th>
                            <th class="hide-on-small-and-down" style="width: 180px;">Last modified</th>
                            <th style="width: 160px;">Action</th>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
