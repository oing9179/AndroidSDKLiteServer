<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/common/jsp_header.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <div class="card">
        <div class="card-content" style="padding-top:0;">
            <span class="card-title black-text">Redundancy cleanup</span>
            <div class="divider" style="margin:0 -20px 12px -20px;"></div>
            <div class="row">
                <div class="col s12">This is zip repository
                    <span class="my-badge blue darken-2 white-text">${zipRepository.name}</span>
                    <jstlc:if test="${xmlRepository != null}">depends on xml repository
                        <span class="my-badge blue darken-2 white-text">${xmlRepository.name}</span>
                    </jstlc:if>.
                </div>
            </div>
            <div class="row">
                <div class="col s12 right-align">
                    <form id="formRedundancyCleanup" method="post"
                          action="/repository/zip/${zipRepository.name}/get_no_longer_needed_archives.do">
                        <input id="checkBoxAbandonObsoletedArchives" type="checkbox" name="isAbandonObsoleted"
                               checked="checked" class="filled-in"/>
                        <label for="checkBoxAbandonObsoletedArchives" title="Abandon obsoleted archives.">Obsoleted</label>
                        <input id="checkBoxAbandonNotExistedArchives" type="checkbox" name="isAbandonNotExisted"
                               checked="checked" class="filled-in"/>
                        <label for="checkBoxAbandonNotExistedArchives" title="Abandon not-existed archives.">Not existed</label>
                        <button id="buttonPerformFilter" type="button"
                                class="btn btn-less-padding waves-effect waves-light indigo">
                            <i class="material-icons left">filter_list</i>Filter
                        </button>
                        <button id="buttonDeleteArchives" type="button"
                                class="btn btn-less-padding waves-effect waves-light red darken-3">
                            <i class="material-icons left">delete</i>Delete...
                        </button>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="col s12">
                    <table id="tableSdkArchiveList" class="striped">
                        <thead>
                        <tr>
                            <td style="padding:6px;">
                                <input id="checkBoxArchiveCheckAll" type="checkbox" class="filled-in"/>
                                <label for="checkBoxArchiveCheckAll">Check all archives</label>
                            </td>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="modalDeleteArchives" class="modal">
    <div class="modal-content">
        <h4 class="red-text text-darken-3">DELETE ARCHIVES</h4>
        You are going to DELETE some of archives, this operation CAN NOT undo!<br/>
        <pre></pre>
    </div>
    <div class="modal-footer">
        <form action="/repository/zip/${zipRepository.name}/redundancy_cleanup.do" method="post"></form>
        <a href="javascript:" class="btn-flat modal-action modal-close waves-effect waves-red" data-action-delete="">
            <i class="material-icons left">delete</i>DELETE ANYWAY</a>
        <a href="javascript:" class="btn-flat modal-action modal-close waves-effect">Cancel</a>
    </div>
</div>
<script id="templateTableRow" type="text/x-handlebars-template">
    <tr>
        <td style="padding:6px;">
            <input id="checkBoxArchive_{{ordinal}}" type="checkbox" class="filled-in"
                   title="Check to include this archive." data-json="{{json}}"/>
            <label for="checkBoxArchive_{{ordinal}}" title="{{url}}">
                {{#if obsoleted}}<i class="material-icons left red-text text-darken-3" title="This archive is obsoleted.">watch_later</i>{{/if}}
                {{#if existed}}{{else}}<i class="material-icons left red-text text-darken-3"
                                          title="This archive was not found in xml repository '${xmlRepository.name}'.">delete</i>{{/if}}
                {{type}}
                {{#ifExists displayName}}
                {{displayName}}
                {{else}}
                {{description}}
                {{/ifExists}}{{#ifExists revision}} - r{{revision}}{{/ifExists}}
                {{#ifExists hostOs}} {{hostOs}} {{/ifExists}}
                {{#ifExists hostBits}} {{hostBits}}bit {{/ifExists}}
            </label>
        </td>
    </tr>
</script>
<script type="text/javascript">
    var SELECTOR_SDK_ARCHIVE_CHECKBOXES = "#tableSdkArchiveList > tbody > tr > td:nth-child(1) > input[type='checkbox']";
    var mTemplate_templateTableRow = null;

    function buttonPerformFilter_onClick(e) {
        var $form = $("#formRedundancyCleanup");
        $("#tableSdkArchiveList>tbody").html("");
        $.ajax({
            url: $form.attr("action"),
            data: new FormData($form[0]),
            type: $form.attr("method"),
            dataType: "json",
            processData: false,
            contentType: false,
            success: function (data, textStatus, jqXHR) {
                $("#tableSdkArchiveList").trigger("updateContent", data);
            }
        });
    }

    function buttonDeleteArchives_onClick(e) {
        $("#modalDeleteArchives").trigger("tryOpenModal");
    }

    function checkBoxArchiveCheckAll_onChange(e) {
        var $checkBoxes = $(SELECTOR_SDK_ARCHIVE_CHECKBOXES);
        $checkBoxes.prop("checked", $(e.target).prop("checked"));
    }

    function tableSdkArchiveList_onUpdateContent(e, data) {
        data = data.data;

        if (data.length == 0) {
            Materialize.toast("No files can be found in this condition.", 3000);
            return;
        }
        var $tbody = $(e.target).find("tbody");
        for (var i = 0; i < data.length; i++) {
            var lJsonObj = data[i];
            lJsonObj["ordinal"] = i;
            lJsonObj["json"] = JSON.stringify(lJsonObj);
            $tbody.append(mTemplate_templateTableRow(lJsonObj));
        }
    }

    function modalDeleteArchives_onTryOpenModal(e){
        var $checkBoxes = $(SELECTOR_SDK_ARCHIVE_CHECKBOXES);

        var $divModal = $(e.target);
        var $pre = $divModal.find("div.modal-content > pre").html("");
        var $form = $divModal.find("div.modal-footer > form").html("");
        $checkBoxes.each(function (index, element) {
            element = $(element);
            if (!element.prop("checked")) return true;// Go next loop (aka java: continue;).
            var lJsonObj = JSON.parse(element.attr("data-json"));
            $pre.append(lJsonObj["fileName"] + "\n");
            $form.append(
                    $("<input/>").attr("type", "hidden").attr("name", "fileNames").attr("value", lJsonObj["fileName"])
            );
        });
        if ($form.find("input").length == 0) {
            Materialize.toast("No files need to delete.", 3000);
            return;
        }
        $divModal.openModal();
    }

    function aPerformArchiveDelete_onClick(e) {
        var $form = $("#modalDeleteArchives > div.modal-footer > form");
        Materialize.toast("Deleting archives...", 2000);
        $.ajax({
            url:$form.attr("action"),
            data:new FormData($form[0]),
            type:$form.attr("method"),
            dataType: "json",
            processData: false,
            contentType: false,
            success: function (data, textStatus, jqXHR) {
                if(data.success){
                    Materialize.toast("Archives are deleted.", 3000);
                } else {
                    var lStrMessage = "An error occurred while deleting archive:<br/>" + data.message;
                    Materialize.toast(lStrMessage, 10000, "red darken-3 white-text");
                }
                $("#buttonPerformFilter").trigger("click");
            }
        });
    }

    function document_onReady() {
        Handlebars.registerHelper("ifExists", function (p0, options) {
            if (p0 != "undefined" && p0 != null) return options.fn(this);
            return options.inverse(this);
        });
        mTemplate_templateTableRow = Handlebars.compile($("#templateTableRow").html());
        $("#buttonPerformFilter").bind("click", buttonPerformFilter_onClick);
        $("#buttonDeleteArchives").bind("click", buttonDeleteArchives_onClick);
        $("#tableSdkArchiveList").bind("updateContent", tableSdkArchiveList_onUpdateContent);
        $("#checkBoxArchiveCheckAll").bind("change", checkBoxArchiveCheckAll_onChange);
        $("#modalDeleteArchives a[data-action-delete]").bind("click", aPerformArchiveDelete_onClick);
        $("#modalDeleteArchives").bind("tryOpenModal", modalDeleteArchives_onTryOpenModal);
    }

    $(document).ready(document_onReady);
</script>
</body>
</html>
