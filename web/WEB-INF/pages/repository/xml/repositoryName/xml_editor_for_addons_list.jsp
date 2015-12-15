<%@ taglib prefix="form" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/common/jsp_header.jsp" %>
<%@ page import="oing.webapp.android.sdkliteserver.model.SdkAddonSite" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
    <style type="text/css">
        .btn-flat:focus { background-color: transparent; }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <c:if test="${objException != null}">
        <div class="card-panel red darken-4 white-text">${objException}</div>
    </c:if>
    <div class="card">
        <div class="card-content" style="padding-top:0;">
            <span class="card-title">XML editor</span><br/>
            <span>${xmlRepository.name} / ${xmlFile.fileName}</span>
            <div class="divider" style="margin:0 -20px;"></div>
            <form id="formXmlEditor" method="post"
                  action="repository/xml/${xmlRepository.name}/xml_editor_for_addons_list.do">
                <input type="hidden" name="id" value="${xmlFile.id}"/>
                <div class="row" style="padding-top:6px;">
                    <div class="col s12 m12 l6">
                        <button type="button" data-form="#formXmlEditor" data-action="urlToFileNameOnly"
                                class="btn btn-less-padding deep-purple waves-effect waves-light"
                                title="Edit all form fields automatically, if you don't know how to do.">
                            File name only
                        </button>
                        <button type="button" data-form="#formXmlEditor" data-action="addPrefixToUrl"
                                class="btn btn-less-padding deep-purple waves-effect waves-light">
                            Add prefix...
                        </button>
                    </div>
                    <div class="col s12 m12 l6 right-align">
                        <button class="btn btn-less-padding indigo waves-effect waves-light"
                                type="button" data-form="#formXmlEditor" data-action="reset"
                                title="Reset all form fields back to initial value, which is values they are shown after page loaded.">
                            <i class="material-icons left">backspace</i>Reset
                        </button>
                        <button title="Commit all changes back to file."
                                data-form="#formXmlEditor" data-action="submit"
                                class="btn btn-less-padding green darken-2 waves-effect waves-light">
                            <i class="material-icons left">check</i>Submit
                        </button>
                    </div>
                </div>
                <div class="row" style="margin-bottom:0;">
                    <div class="col s12 center-align">
                        <table id="tableEditor" data-xml-original-url="${xmlFile.url}">
                            <thead>
                            <tr>
                                <td style="width:40px;">#</td>
                                <td style="width:140px;">Type</td>
                                <td>Name</td>
                                <td>URL</td>
                                <td style="width:50px;"></td>
                            </tr>
                            </thead>
                            <tbody>
                            <c:set var="SdkAddonSite_Types" value="<%=SdkAddonSite.Type.values()%>" scope="page"/>
                            <c:forEach var="sdkAddonSite" items="${sdkAddonSites}">
                                <tr>
                                    <td></td>
                                    <td>
                                        <select name="sdkAddonSite.type" class="browser-default" required="required">
                                            <option disabled="disabled">Select an option</option>
                                            <c:forEach var="SdkAddonSite_Type" items="${SdkAddonSite_Types}">
                                                <option value="${SdkAddonSite_Type.toString()}"
                                                    ${SdkAddonSite_Type.equals(sdkAddonSite.getType()) ? "selected" : ""}>
                                                        ${SdkAddonSite_Type.toString()}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                    <td>
                                        <input type="text" name="sdkAddonSite.name" required="required" value="${sdkAddonSite.name}"/>
                                    </td>
                                    <td>
                                        <input type="text" name="sdkAddonSite.url" required="required" value="${sdkAddonSite.url}"
                                               placeholder="${sdkAddonSite.url}" data-original-url="${sdkAddonSite.url}"/>
                                    </td>
                                    <td>
                                        <button type="button" data-action="deleteAddonSiteByOrdinal"
                                                class="btn-flat btn-less-padding red-text waves-effect waves-red">
                                            <i class="material-icons">delete</i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        <button type="button" data-action="addAddonSite"
                                class="btn cyan darken-2 waves-effect waves-light">
                            <i class="material-icons left">add</i>Add
                        </button>
                        <style type="text/css">
                            #tableEditor td { padding: 6px; }
                            #tableEditor input[type='text'] { margin: 0; height: 2rem; }
                        </style>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<script id="templateTableRow" type="text/x-handlebars-template" data-for="#tableEditor">
<tr>
    <td></td>
    <td>
        <select name="sdkAddonSite.type" class="browser-default" required>
            <option disabled selected>Select an option</option>
            <c:forEach var="SdkAddonSite_Type" items="${SdkAddonSite_Types}">
                <option value="${SdkAddonSite_Type.toString()}">${SdkAddonSite_Type.toString()}</option>
            </c:forEach>
        </select>
    </td>
    <td><input type="text" name="sdkAddonSite.name" required/></td>
    <td><input type="text" name="sdkAddonSite.url" required/></td>
    <td>
        <button type="button" data-action="deleteAddonSiteByOrdinal"
                class="btn-flat btn-less-padding red-text waves-effect waves-red">
            <i class="material-icons">delete</i>
        </button>
    </td>
</tr>
</script>
<script type="text/javascript">
    var JQUERY_SELECTOR_TEXT_BOXES_URL = "#tableEditor > tbody > tr > td:nth-child(4) > input[type='text']";
    var mTemplate_templateTableRow = null;

    function formXmlEditor_buttonUrlToFileNameOnly_onClick(e) {
        $(JQUERY_SELECTOR_TEXT_BOXES_URL).each(function (index, element) {
            element = $(element);
            var lStrFileName = element.val();
            if (lStrFileName.lastIndexOf("/") != -1) {
                lStrFileName = lStrFileName.substr(lStrFileName.lastIndexOf("/") + 1);
                element.val(lStrFileName);
            }
        });
    }

    function formXmlEditor_buttonAddPrefixToUrl_onClick(e) {
        var lStrPrefix = window.prompt("The prefix that you want prepend to file names.\n" +
                "NOTE: PREFIX will prepend to file name only, not some url like starts with \"http://\".");
        if (lStrPrefix == null || lStrPrefix.length == 0) return;
        $(JQUERY_SELECTOR_TEXT_BOXES_URL).each(function (index, element) {
            element = $(element);
            var lStrFileName = element.val();
            if (lStrFileName.indexOf("http://") != -1 || lStrFileName.indexOf("https://") != -1) {
                // It's a URL, so we skip iteration this time,
                return true;// Just like java "continue;".
            }
            lStrFileName = lStrPrefix + lStrFileName;
            element.val(lStrFileName);
        });
    }

    function formXmlEditor_buttonReset_onClick(e) {
        $(JQUERY_SELECTOR_TEXT_BOXES_URL).each(function (index, element) {
            element = $(element);
            element.val(element.attr("data-original-url"));
        });
    }

    function formXmlEditor_buttonAddAddonSite_onClick(e) {
        $("#tableEditor > tbody").append(mTemplate_templateTableRow());
        $("#tableEditor").trigger("updateOrdinal");
    }

    function formXmlEditor_buttonDeleteAddonSiteByOrdinal_onClick(e) {
        $(this).parent().parent().remove();
        $("#tableEditor").trigger("updateOrdinal");
        console.log(e);
    }

    function formXmlEditor_onSubmit(e) {
        $("#tableEditor > tbody > tr").each(function (index, element) {
            var lnIndex = index;
            $(element).find("td > *[name^='sdkAddonSite.']").each(function (index, element) {
                element = $(element);
                var lStrName = element.attr("name");
                lStrName = lStrName.substr(0, lStrName.indexOf(".")) + "[" + lnIndex + "]" +
                        lStrName.substr(lStrName.indexOf("."));
                element.attr("name", lStrName);
            });
        });
    }

    function tableEditor_onUpdateOrdinal(e) {
        var $tableRows = $(e.target).find("tbody > tr");
        $tableRows.each(function (index, element) {
            element = $(element);
            element.find("td:nth-child(1)").html(index + 1);
        });
        $("#formXmlEditor button[data-action='deleteAddonSiteByOrdinal']").unbind("click")
                .bind("click", formXmlEditor_buttonDeleteAddonSiteByOrdinal_onClick);
    }

    function document_onReady() {
        mTemplate_templateTableRow = Handlebars.compile($("#templateTableRow").html());
        $("#formXmlEditor button[data-action='urlToFileNameOnly']").bind("click", formXmlEditor_buttonUrlToFileNameOnly_onClick);
        $("#formXmlEditor button[data-action='addPrefixToUrl']").bind("click", formXmlEditor_buttonAddPrefixToUrl_onClick);
        $("#formXmlEditor button[data-action='reset']").bind("click", formXmlEditor_buttonReset_onClick);
        $("#formXmlEditor button[data-action='addAddonSite']").bind("click", formXmlEditor_buttonAddAddonSite_onClick);
        $("#formXmlEditor").bind("submit", formXmlEditor_onSubmit);
        $("#tableEditor").bind("updateOrdinal", tableEditor_onUpdateOrdinal);
        $("#tableEditor").trigger("updateOrdinal");
    }

    $(document).ready(document_onReady);
</script>
</body>
</html>
