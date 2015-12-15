<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/common/jsp_header.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
    <script type="text/javascript" src="static/js/jquery-fileSizeFormat.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <div class="card">
        <div class="card-content" style="padding-top:0;">
            <span class="card-title black-text">File completion</span><br/>
            <div class="divider" style="margin:0 -20px 12px -20px;"></div>
            <div class="row">
                <div class="col s12">This is zip repository
                    <span class="my-badge blue darken-2 white-text">${zipRepository.name}</span>
                    <c:if test="${xmlRepository != null}">depends on xml repository
                        <span class="my-badge blue darken-2 white-text">${xmlRepository.name}</span>
                    </c:if>.
                </div>
            </div>
            <div class="row">
                <div class="col s12 right-align">
                    <form id="formGetAllArchives" method="post"
                          action="/repository/zip/${zipRepository.name}/get_all_archives.do">
                        <input id="checkBoxIncludeSysLinux" name="isIncludeSysLinux" type="checkbox" class="filled-in"/>
                        <label for="checkBoxIncludeSysLinux" title="Include Linux archives.">Linux</label>
                        <input id="checkBoxIncludeSysMacOSX" name="isIncludeSysOSX" type="checkbox" class="filled-in"/>
                        <label for="checkBoxIncludeSysMacOSX" title="Include Mac OSX archives.">Mac OSX</label>
                        <input id="checkBoxIncludeSysWindows" name="isIncludeSysWin" type="checkbox" class="filled-in"/>
                        <label for="checkBoxIncludeSysWindows" title="Include Windows archives.">Windows</label>
                        <input id="checkBoxIncludeObsoleteArchives" name="isIncludeObsoleted" type="checkbox" class="filled-in"/>
                        <label for="checkBoxIncludeObsoleteArchives" title="Include obsoleted archives.">Obsoleted</label>
                        <input id="checkBoxIncludeExistedArchives" name="isIncludeExisted" type="checkbox" class="filled-in"/>
                        <label for="checkBoxIncludeExistedArchives" title="Include existed archives.">Existed</label>
                        <button id="buttonPerformFilter" type="button"
                                class="btn btn-less-padding waves-effect waves-light indigo">
                            <i class="material-icons left">filter_list</i>Filter
                        </button>
                        <button id="buttonExportURLs" type="button"
                                class="btn btn-less-padding waves-effect waves-light green">
                            <i class="material-icons left">check</i>Export URLs
                        </button>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="col s12"><ul id="ulSdkArchives" class="collapsible" data-collapsible="expandable"></ul></div>
            </div>
        </div>
    </div>
</div>
<div id="modalExportURLs" class="modal">
    <div class="modal-content">
        <h4>Export URLs</h4>
        <pre></pre>
    </div>
    <div class="modal-footer">
        <a href="javascript:" class="btn-flat modal-action modal-close waves-effect waves-green">Dismiss</a>
    </div>
</div>
<script id="templateLiArchiveGroup" type="text/x-handlebars-template">
    <li>
        <div class="collapsible-header">
            <input id="checkBoxArchiveGroup_{{ordinal}}" type="checkbox" class="filled-in"/>
            <label for="checkBoxArchiveGroup_{{ordinal}}">
                {{#ifExists apiLevel}}
                API {{apiLevel}}
                {{else}}
                others
                {{/ifExists}}
            </label>
        </div>
        <div class="collapsible-body" style="padding-left:30px;"><table><tbody></tbody></table></div>
    </li>
</script>
<script id="templateCheckBoxWithLabel" type="text/x-handlebars-template">
    <tr>
        <td style="padding:6px;">
            <input id="checkBoxArchive_{{ordinal}}" type="checkbox" class="filled-in" data-json="{{json}}"
                   data-parent-checkbox="{{idParentCheckBox}}" title="Check to include this archive."/>
            <label for="checkBoxArchive_{{ordinal}}" title="{{htmlTitle}}">
                {{#if existed}}<i class="material-icons left green-text text-darken-3" title="This archive already exist.">check</i>{{/if}}
                {{#if obsoleted}}<i class="material-icons left red-text text-darken-3" title="This archive is obsoleted.">watch_later</i>{{/if}}
                {{type}}
                {{#ifExists displayName}}
                {{displayName}}
                {{else}}
                {{description}}
                {{/ifExists}} - r{{revision}}
                {{#ifExists hostOs}} {{hostOs}} {{/ifExists}}
                {{#ifExists hostBits}} {{hostBits}}bit {{/ifExists}}
            </label>
        <td/>
    </tr>
</script>
<script type="text/javascript">
    var mTemplate_templateLiArchiveGroup = null;
    var mTemplate_templateCheckBoxWithLabel = null;

    function buttonPerformFilter_onClick(e) {
        var $form = $("#formGetAllArchives");
        $("#ulSdkArchives").html("");
        $.ajax({
            url: $form.attr("action"),
            data: new FormData($form[0]),
            type: $form.attr("method"),
            dataType: "json",
            processData: false,
            contentType: false,
            success: function (data, textStatus, jqXHR) {
                $("#ulSdkArchives").trigger("updateContent", data);
            }
        });
    }

    function ulSdkArchives_onUpdateContent(e, data) {
        data = data.data;
        var lnOrdinal = 0;
        var $ul = $("#ulSdkArchives").html("");
        for (var lStrKey in data) {
            var lJsonArrArchives = data[lStrKey];
            var $li, $tbody, lStrIdCheckBoxParent;
            {
                var lJsonObj = lJsonArrArchives[0];
                lnOrdinal++;
                lJsonObj["ordinal"] = lnOrdinal;
                $li = $(mTemplate_templateLiArchiveGroup(lJsonObj));
                $tbody = $li.find("table > tbody");
                lStrIdCheckBoxParent = "#" + $li.find("input[type='checkbox']").attr("id");
            }
            for(var i in lJsonArrArchives){
                var lJsonObj = lJsonArrArchives[i];
                lnOrdinal++;
                lJsonObj["ordinal"] = lnOrdinal;
                lJsonObj["idParentCheckBox"] = lStrIdCheckBoxParent;
                lJsonObj["json"] = JSON.stringify(lJsonObj);
                lJsonObj["htmlTitle"] = lJsonObj["fileName"] + "\n" +
                        "Size: " + $.format.fileSize(lJsonObj["size"]) + " (" + lJsonObj["size"] + " Bytes)\n" +
                        lJsonObj["checksumType"] + ": " + lJsonObj["checksum"] + "\n" +
                        lJsonObj["url"];
                $tbody.append(mTemplate_templateCheckBoxWithLabel(lJsonObj));
            }
            $ul.append($li);
            $(lStrIdCheckBoxParent).bind("change", checkBoxArchiveGroup_onChange);
        }
    }

    function checkBoxArchiveGroup_onChange(e) {
        var $target = $(e.target);
        var $checkBoxies = $target.parent().parent().find("input[data-parent-checkbox='" + ("#" + $target.attr("id")) + "']");
        $checkBoxies.prop("checked", $target.prop("checked"));
    }

    function buttonExportURLs_onClick(e){
        var $checkBoxies = $("#ulSdkArchives input[type='checkbox'][data-parent-checkbox]:checked");
        var lStrUrls = "";
        $checkBoxies.each(function (index, element) {
            element = $(element);
            var lJsonObj = JSON.parse(element.attr("data-json"));
            lStrUrls += lJsonObj.url + "\n";
        });
        var $modal = $("#modalExportURLs");
        $modal.find("div.modal-content>pre").text(lStrUrls);
        $modal.openModal();
    }

    function document_onReady() {
        Handlebars.registerHelper("ifExists", function (p0, options) {
            if (p0 != "undefined" && p0 != null) return options.fn(this);
            return options.inverse(this);
        });
        mTemplate_templateLiArchiveGroup = Handlebars.compile($("#templateLiArchiveGroup").html());
        mTemplate_templateCheckBoxWithLabel = Handlebars.compile($("#templateCheckBoxWithLabel").html());
        $("#buttonPerformFilter").bind("click", buttonPerformFilter_onClick);
        $("#buttonExportURLs").bind("click", buttonExportURLs_onClick);
        $("#ulSdkArchives").bind("updateContent", ulSdkArchives_onUpdateContent);
    }

    $(document).ready(document_onReady);
</script>
</body>
</html>
