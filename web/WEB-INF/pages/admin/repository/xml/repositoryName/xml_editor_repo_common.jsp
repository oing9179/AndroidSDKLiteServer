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
    <c:if test="${objException != null}">
        <div class="card-panel red darken-4 white-text">${objException}</div>
    </c:if>
    <c:if test="${objException == null}">
        <div class="card">
            <div class="card-content" style="padding-top:0;">
                <h4 class="card-title" style="margin: 0;">XML editor</h4>
                <span>${xmlRepository.name} / ${xmlFile.fileName}</span>
                <span class="my-badge pink lighten-2 white-text">${xmlFile.url}</span>
                <div class="divider" style="margin:6px -20px 3px -20px;"></div>
                <form id="formXmlEditor" method="post"
                      action="admin/repository/xml/${xmlRepository.name}/xml_editor_for_repo_common.do">
                    <input type="hidden" name="id" value="${xmlFile.id}"/>
                    <div class="row" style="padding-top:6px;">
                        <div class="col s12 m12 l6 input-field" style="margin-top: 8px;">
                            <input id="textBoxZipSubDirectory" type="text" name="zipSubDirectory"
                                   value="${xmlFile.zipSubDirectory}" style="margin-bottom: 0; height: 2rem;"/>
                            <label for="textBoxZipSubDirectory" style="top:.4rem;">ZIP sub directory</label>
                        </div>
                        <div class="col s12 m12 l6 right-align">
                            <button class="btn btn-less-padding indigo waves-effect waves-light"
                                    type="button" data-form="#formXmlEditor" data-action="reset"
                                    title="Reset all form fields back to initial value, which is values they are shown after page loaded.">
                                <i class="material-icons left">backspace</i>Reset
                            </button>
                            <button title="Commit all changes back to file." type="submit"
                                    data-form="#formXmlEditor" data-action="submit"
                                    class="btn btn-less-padding green darken-2 waves-effect waves-light">
                                <i class="material-icons left">check</i>Save
                            </button>
                        </div>
                    </div>
                    <div class="row" style="margin-bottom: 0;">
                        <div class="col s12 m12 l6">
                            <button type="button" data-form="#formXmlEditor" data-action="urlToFileNameOnly"
                                    class="btn btn-less-padding deep-purple waves-effect waves-light"
                                    title="Edit all form fields automatically, if you don't know how to do.">
                                File name only
                            </button>
                        </div>
                    </div>
                    <div class="row" style="margin-bottom:0;">
                        <div class="col s12">
                            <table id="tableEditor" data-xml-original-url="${xmlFile.url}">
                                <thead>
                                <tr>
                                    <td style="width:60px;">#</td>
                                    <td>Value</td>
                                    <td style="width:60px;">Type</td>
                                </tr>
                                </thead>
                                <tbody>
                                <c:set var="lnIndex" value="${0}" scope="page"/>
                                <c:forEach var="remotePackage" items="${remotePackages}">
                                    <c:forEach var="archive" items="${remotePackage.archives}">
                                        <tr>
                                            <td>${lnIndex = lnIndex + 1}</td>
                                            <td>
                                                <input type="text" name="url" required="required" value="${archive.url}"
                                                       data-original-url="${archive.url}" placeholder="${archive.url}"/>
                                            </td>
                                            <td>
                                                <c:catch var="exception">
                                                    <c:set var="lStrPackageType" value="Patch: ${archive.basedOn}" scope="page"/>
                                                </c:catch>
                                                <c:if test="${not empty exception}">
                                                    <c:set var="lStrPackageType" value="Complete" scope="page"/>
                                                </c:if>${lStrPackageType}
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:forEach>
                                </tbody>
                            </table>
                            <style type="text/css">
                                #tableEditor td { padding: 6px; }
                                #tableEditor input[type='text'] { margin: 0; height: 2rem; }
                            </style>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </c:if>
</div>
<script type="text/javascript">
    var JQUERY_SELECTOR_TEXT_BOXES_URL = "#tableEditor > tbody > tr > td:nth-child(2) > input[type='text']";

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

    function formXmlEditor_buttonReset_onClick(e) {
        $(JQUERY_SELECTOR_TEXT_BOXES_URL).each(function (index, element) {
            element = $(element);
            element.val(element.attr("data-original-url"));
        });
    }

    function document_onReady() {
        $("#formXmlEditor button[data-action='urlToFileNameOnly']").bind("click", formXmlEditor_buttonUrlToFileNameOnly_onClick);
        $("#formXmlEditor button[data-action='reset']").bind("click", formXmlEditor_buttonReset_onClick);
    }

    $(document).ready(document_onReady);
</script>
</body>
</html>
