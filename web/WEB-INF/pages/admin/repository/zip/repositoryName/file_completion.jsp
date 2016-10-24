<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/common/jsp_header.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
    <script type="text/javascript" src="static/js/jquery-fileSizeFormat.js"></script>
    <style type="text/css">
        ul#ulRemotePackageList ul > li i.material-icons.left {
            width: .6em;
            line-height: .8;
            height: .1em;
            margin-right: 12px;
        }

        ul#ulRemotePackageList ul > li {
            padding: 1px 6px;
            margin-bottom: 16px;
        }

        ul#ulRemotePackageList ul > li > div{
            padding-left: 16px;
        }

        ul#ulRemotePackageList ul > li:nth-child(even) {
            background-color: hsl(0, 0%, 95%);
        }
    </style>
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
                          action="admin/repository/zip/${zipRepository.name}/get_all_archives.do">
                        <input id="checkBoxIncludeSysLinux" name="isIncludeSysLinux" type="checkbox" class="filled-in" checked/>
                        <label for="checkBoxIncludeSysLinux" title="Include Linux archives.">Linux</label>
                        <input id="checkBoxIncludeSysMacOSX" name="isIncludeSysOSX" type="checkbox" class="filled-in" checked/>
                        <label for="checkBoxIncludeSysMacOSX" title="Include Mac OSX archives.">Mac OSX</label>
                        <input id="checkBoxIncludeSysWindows" name="isIncludeSysWin" type="checkbox" class="filled-in" checked/>
                        <label for="checkBoxIncludeSysWindows" title="Include Windows archives.">Windows</label>
                        <input id="checkBoxIncludeObsoleteArchives" name="isIncludeObsoleted" type="checkbox" class="filled-in"/>
                        <label for="checkBoxIncludeObsoleteArchives" title="Include obsoleted archives.">Obsoleted</label>
                        <input id="checkBoxIncludeExistedArchives" name="isIncludeExisted" type="checkbox" class="filled-in" checked/>
                        <label for="checkBoxIncludeExistedArchives" title="Include existed archives.">Existed</label>
                        <button id="buttonPerformFilter" type="button"
                                class="btn btn-less-padding waves-effect waves-light indigo">
                            <i class="material-icons left">filter_list</i>Filter
                        </button>
                        <button id="buttonShowExportToMetalink4Dialog" type="button"
                                class="btn btn-less-padding waves-effect waves-light green">
                            <i class="material-icons left">check</i>Export...
                        </button>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="col s12">
                    <ul id="ulRemotePackageList" class="collapsible" data-collapsible="expandable"></ul>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="modalExportToMetalink4" class="modal">
    <div class="modal-content">
        <h4>Export to Metalink4</h4>
        <h6 style="margin-bottom: 6px;">Total size: <span class="my-badge blue white-text spanTotalFileSize"></span></h6>
        <a id="aExportToMetalink4" type="button" download="AndroidSDKDownloads.meta4"
           class="btn btn-less-padding waves-effect waves-light green" target="_blank">
            <i class="material-icons left">file_download</i>Export to Metalink4
        </a>
    </div>
    <div class="modal-footer">
        <a href="javascript:" class="btn-flat modal-action modal-close waves-effect waves-green">Dismiss</a>
    </div>
</div>
<script id="templateRemotePackages" type="text/x-handlebars-template">
{{#forEach remotePackages}}
<li>
    <div class="collapsible-header">
        <input id="checkBoxRemotePackageGroup_API{{key}}" type="checkbox" class="filled-in"/>
        <label for="checkBoxRemotePackageGroup_API{{key}}">API {{key}}</label>
    </div>
    <div class="collapsible-body" style="padding: 3px 12px;">
        <ul>
            {{#forEach data}}
            <li>
                <h6>
                    {{#if data.obsoleted}}<i class="material-icons left red-text text-darken-3" title="This archive is obsoleted.">watch_later</i>{{/if}}
                    {{data.type}} - {{data.displayName}} - {{data.revision}}
                </h6>
                {{#forEach data.archives}}
                <div>
                    {{#if data.fileExisted}}<i class="material-icons left green-text text-darken-3" title="This archive already exist.">check</i>{{/if}}
                    <input id="checkBoxArchive_ordinal-{{data._ordinal}}" type="checkbox" class="filled-in" data-json="{{data.asJson}}"/>
                    <label for="checkBoxArchive_ordinal-{{data._ordinal}}">{{data.fileName}}</label>
                    <span class="my-badge blue white-text">{{#humanReadableFileSize data.size}}{{/humanReadableFileSize}}</span>
                </div>
                {{/forEach}}
            </li>
            {{/forEach}}
        </ul>
    </div>
</li>
{{/forEach}}
</script>
<script type="text/javascript">
    var mTemplate_templateRemotePackages = null;

    function buttonPerformFilter_onClick(e) {
        $form = $("#formGetAllArchives");
        $("#ulSdkArchives").html("");
        $.ajax({
            url: $form.attr("action"),
            data: new FormData($form[0]),
            type: $form.attr("method"),
            dataType: "json",
            processData: false,
            contentType: false,
            success: function (data, textStatus, jqXHR) {
                $("#ulRemotePackageList").trigger("updateContent", data);
            }
        });
    }

    function ulRemotePackageList_onUpdateContent(e, data) {
        data = data.data;
        $ul = $("#ulRemotePackageList").html("");
        lJsonObjRemotePackages = {};
        // Group RemotePackages by apiLevel.
        for (i in data) {
            lJsonObjRemotePackage = data[i];
            lStrApiLevel = lJsonObjRemotePackage["apiLevel"];
            if (lStrApiLevel == null) {
                lStrApiLevel = "others";
            }
            if(!lJsonObjRemotePackages[lStrApiLevel]) {
                lJsonObjRemotePackages[lStrApiLevel] = [];
            }
            lnIndex = lJsonObjRemotePackages[lStrApiLevel].length;
            lJsonObjRemotePackages[lStrApiLevel][lnIndex] = lJsonObjRemotePackage;
        }
        // Add ordinal to every archive, they will be used to prevent CheckBox id conflict.
        {
            lnOrdinal = 0;
            for (lStrApiLevel in lJsonObjRemotePackages) {
                lJsonArrRemotePackages = lJsonObjRemotePackages[lStrApiLevel];
                for (lnIndex in lJsonArrRemotePackages) {
                    lJsonObjRemotePackage = lJsonArrRemotePackages[lnIndex];
                    for (lnIndex2 in lJsonObjRemotePackage["archives"]) {
                        lJsonObjArchive = lJsonObjRemotePackage["archives"][lnIndex2];
                        lJsonObjArchive["_ordinal"] = lnOrdinal++;
                        lJsonObjArchive["asJson"] = JSON.stringify(lJsonObjArchive);
                    }
                }
            }
        }
        // Update html
        $ul.html(mTemplate_templateRemotePackages({"remotePackages": lJsonObjRemotePackages}));
        // Bind events
        $("input[type='checkbox'][id^='checkBoxRemotePackageGroup_API']").bind("change", function (e) {
            $target = $(e.target);
            $($target).parent().parent().find("ul > li > div > input[type='checkbox']")
                    .prop("checked", $(e.target).prop("checked"));
        });
    }

    function buttonShowExportToMetalink4Dialog_onClick(e) {
        $checkBoxies = $("#ulRemotePackageList input[type='checkbox'][id^='checkBoxArchive_ordinal-']:checked");
        if ($checkBoxies.length < 1) {
            return false;
        }
        // Generate Metalink4 XML.
        lDocumentMetalink4 = $.parseXML("<metalink xmlns=\"urn:ietf:params:xml:ns:metalink\"/>");
        lDocumentMetalink4.xmlStandalone = true;
        lDocumentMetalink4.xmlEncoding = "utf-8";
        lElementRoot = lDocumentMetalink4.documentElement;
        lnTotalFileSize = 0;
        $checkBoxies.each(function (index, element) {
            element = $(element);
            lJsonObj = JSON.parse(element.attr("data-json"));
            lnTotalFileSize += parseInt(lJsonObj["size"]);
            // Create element <file/>
            lElementFile = lDocumentMetalink4.createElement("file");
            lStrFileName = lJsonObj["fileNameWithPrefix"];
            if (lStrFileName == null) lStrFileName = lJsonObj["fileName"];
            lElementFile.setAttribute("name", lStrFileName);
            // Element <file>/<hash>
            lElementTemp = lDocumentMetalink4.createElement("hash");
            lElementTemp.setAttribute("type", "sha-1");
            lElementTemp.textContent = lJsonObj["checksum"];
            lElementFile.appendChild(lElementTemp);
            // Element <file>/<size>
            lElementTemp = lDocumentMetalink4.createElement("size");
            lElementTemp.textContent = lJsonObj["size"];
            lElementFile.appendChild(lElementTemp);
            // Element <file>/<url>
            lElementTemp = lDocumentMetalink4.createElement("url");
            lElementTemp.textContent = lJsonObj["absoluteUrl"];
            lElementFile.appendChild(lElementTemp);
            // Append element <file> into document.
            lElementRoot.appendChild(lElementFile);
        });
        $("#aExportToMetalink4").attr("href",
                "data:text/plain;charset=utf-8," +
                encodeURIComponent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + lElementRoot.outerHTML)
        );
        // Prepare for modal dialog.
        $modal = $("#modalExportToMetalink4");
        $modal.find("span.spanTotalFileSize").text($.format.fileSize(lnTotalFileSize));
        $modal.openModal();
    }

    function handlebarsHelper_forEach(items, options) {
        lStrHtml = "";
        lnIndex = 0;
        for (key in items) {
            lJsonObjItem = {
                "index": lnIndex,
                "key": key,
                "_random": parseInt(Math.random() * 100000000) + "",
                "data": items[key]
            };
            lStrHtml += options.fn(lJsonObjItem);
        }
        if (lStrHtml.length > 0) return lStrHtml;
        return options.inverse(this);
    }

    function handlebarsHelper_humanReadableFileSize(item, options) {
        if (item != null) {
            return $.format.fileSize(item);
        }
        return item;
    }

    function document_onReady() {
        Handlebars.registerHelper("ifExists", function (items, options) {
            if (items != "undefined" && items != null) return options.fn(this);
            return options.inverse(this);
        });
        Handlebars.registerHelper("forEach", handlebarsHelper_forEach);
        Handlebars.registerHelper("humanReadableFileSize", handlebarsHelper_humanReadableFileSize);
        mTemplate_templateRemotePackages = Handlebars.compile($("#templateRemotePackages").html());
        $("#buttonPerformFilter").bind("click", buttonPerformFilter_onClick);
        $("#buttonShowExportToMetalink4Dialog").bind("click", buttonShowExportToMetalink4Dialog_onClick);
        $("#ulRemotePackageList").bind("updateContent", ulRemotePackageList_onUpdateContent);
    }

    $(document).ready(document_onReady);
</script>
</body>
</html>
