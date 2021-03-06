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
        <div class="card-content" style="padding-top:0; padding-bottom:0;">
            <span class="card-title black-text">Automatic add</span><br/>
            <span class="black-text">${xmlRepository.name}</span>
            <div class="divider" style="margin:0 -20px;"></div>
            <form id="formTaskOptions" action="admin/repository/xml/${xmlRepository.name}/automatic_addition.do"
                  method="post" onsubmit="return false;" enctype="multipart/form-data" style="padding-bottom: 1px;">
                <div class="row">
                    <!-- CheckBox: force https -->
                    <div class="col switch s12 m12 l12" style="padding-top:12px;">
                        <label style="position:relative; top:-2px;">
                            <input id="checkBoxForceHttps" name="isPreferHttpsConnection" type="checkbox"
                                   disabled="disabled" checked="checked"/>
                            <span class="lever"></span>
                        </label>Prefer HTTPS connection
                    </div>
                </div>
                <!-- Disable proxy caused by jodd's http library doesn't support HTTPS through socks5. -->
                <div class="row" style="margin:0; padding-top:12px; display: none;">
                    <div class="col s12 m12 l12 center-align">Server-side proxy settings</div>
                </div>
                <div class="row" style="margin-bottom: 0; display: none;">
                    <!-- Input field: proxy type -->
                    <div class="input-field col s12 m12 l3">
                        <select id="selectProxyInfo_type" name="proxyInfo.type" required="required">
                            <option value="direct" selected="selected">Direct</option>
                            <option value="http">HTTP</option>
                            <option value="socks4">SOCKS4</option>
                            <option value="socks5">SOCKS5</option>
                        </select>
                        <label for="selectProxyInfo_type">Proxy type</label>
                    </div>
                    <!-- Input field: proxy address -->
                    <div class="input-field col s8 m8 l6">
                        <input id="textBoxProxyInfo_address" name="proxyInfo.address" type="text"/>
                        <label for="textBoxProxyInfo_address">Proxy address</label>
                    </div>
                    <!-- Input field: proxy port -->
                    <div class="input-field col s4 m4 l3">
                        <input id="textBoxProxyInfo_port" name="proxyInfo.port"
                               type="number" min="1" max="65535" maxlength="5"/>
                        <label for="textBoxProxyInfo_port">Proxy port</label>
                    </div>
                </div>
                <div class="row" style="display: none;">
                    <!-- Input field: proxy user name(optional) -->
                    <div class="input-field col s12 m6 l6">
                        <input id="textBoxProxyInfo_userName" name="proxyInfo.userName" type="text"/>
                        <label for="textBoxProxyInfo_userName">Proxy username(optional)</label>
                    </div>
                    <!-- Input field: proxy password(optional) -->
                    <div class="input-field col s12 m6 l6">
                        <input id="textBoxProxyInfo_password" name="proxyInfo.password" type="password"/>
                        <label for="textBoxProxyInfo_password">Proxy password(optional)</label>
                    </div>
                </div>
                <div class="row center-align">
                    <ul id="ulXmlDownloadUrlSelection">
                        <c:forEach var="lStrUrl" items="${listXmlDownloadUrls}" varStatus="varStatus">
                            <li>
                                <input id="checkBoxXmlDownloadUrlSelection_${varStatus.index}" type="checkbox"
                                       name="xmlDownloadUrlSelection" class="filled-in" value="${varStatus.index}"/>
                                <label for="checkBoxXmlDownloadUrlSelection_${varStatus.index}">${lStrUrl}</label>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="row">
                    <!-- Button: Start task -->
                    <div class="col s12 m12 l12 center-align">
                        <button id="buttonStartTask" type="button" class="btn btn-less-padding waves-effect waves-light">
                            <i class="material-icons left">file_download</i>Start task
                        </button>
                    </div>
                </div>
            </form>
            <div id="divProgressIndicator" class="row" style="padding-top:16px; display:none;">
                <div id="divProgressBar" class="progress progressbar-blue col s12 m12 l12">
                    <div class="indeterminate"></div>
                </div>
                <div class="col s12 m12 l12 center-align">
                    <button id="buttonAbortTask" type="button"
                            class="btn btn-less-padding waves-effect waves-light red darken-2">
                        <i class="material-icons left">close</i>Abort
                    </button>
                    <a id="buttonBackToRepositoryPage" href="admin/repository/xml/${xmlRepository.name}/"
                       class="btn btn-less-padding waves-effect waves-light blue" style="display:none;">
                        <i class="material-icons left">arrow_back</i>Back to repository page
                    </a>
                </div>
                <ul id="ulProgressMessageList" class="collection col s12 m12 l12" style="padding:0;"></ul>
            </div>
        </div>
    </div>
</div>
<script id="templateToastError" type="text/x-handlebars-template">
    <i class="material-icons left">android</i>{{text}}
</script>
<script type="text/javascript">
    var mCompiledTemplate_templateToastError;

    var TaskManager = {
        mJqXHRRequest: null,
        startTask: function () {
            var $form = $("#formTaskOptions");
            // Validate form "#formTaskOptions"
            try {
                if ($("#selectProxyInfo_type").val() != "direct") {
                    if ($("#textBoxProxyInfo_address").val().length < 1) throw new Error("Required: Proxy address");
                    if (isNaN(parseInt($("#textBoxProxyInfo_port").val()))) throw new Error("Required: Proxy port");
                }
            } catch (e) {
                var lJsonObj = {"text": e};
                Materialize.toast(mCompiledTemplate_templateToastError(lJsonObj), 5000, "red darken-4");
                throw e;
            }
            // Validate checkBoxXmlDownloadUrlSelection select at least one.
            if ($("#ulXmlDownloadUrlSelection input[type='checkbox']:checked").length < 1) {
                Materialize.toast(mCompiledTemplate_templateToastError({"text": "Select at least one url."}), 5000, "red darken-4");
                return;
            }
            // Hide form and show ProgressBar.
            $form.hide();
            $("#divProgressIndicator").show();
            // Send http request
            this.mJqXHRRequest = $.ajax({
                url: $form.attr("action"),
                method: "POST",
                data: new FormData($form[0]),
                "processData": false,
                "contentType": false,
                "mnLastLoaded": 0, /* Progress for last time */
                xhr: function () {
                    var xhr = $.ajaxSettings.xhr();
                    var onProgress = this.onProgress;
                    xhr.addEventListener("progress", function (e) {
                        onProgress(e);
                    });
                    return xhr;
                },
                onProgress: function (e) {
                    TaskManager.updateProgressInfo(e.target.responseText);
                },
                complete: function (jqXHR, textStatus) {
                    $("#buttonAbortTask").hide();
                    $("#buttonBackToRepositoryPage").show();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    var lJsonObj = {"text": "Error(" + textStatus + "): " + errorThrown};
                    Materialize.toast(mCompiledTemplate_templateToastError(lJsonObj), 10000, "red darken-4");
                }
            });
        },
        abortTask: function () {
            this.mJqXHRRequest.abort();
        },
        updateProgressInfo: function (response) {
            var lJsonArrResponse = [];
            var $ulProgressMessageList = $("#ulProgressMessageList");

            response = response.split("\n");
            for (var i = 0, length = response.length - 1; i < length; i++) {
                lJsonArrResponse[i] = JSON.parse(response[i]);
            }
            for (var i = $ulProgressMessageList.find("li").length, length = lJsonArrResponse.length; i < length; i++) {
                // Prepend getAll item into #ulProgressMessageList
                $ulProgressMessageList.prepend(
                        $("<li></li>").addClass("collection-item").html(lJsonArrResponse[i]["message"])
                );
                {
                    // Update progress bar
                    var $divProgressBar = $("#divProgressBar");
                    var progress = lJsonArrResponse[lJsonArrResponse.length - 1]["progress"];
                    $divProgressBar.removeClass("progressbar-red progressbar-blue progressbar-green")
                            .find(".determinate,.indeterminate").removeClass("indeterminate determinate");
                    if (progress == -1) {
                        // Progress bar goes to red.
                        $divProgressBar.addClass("progressbar-red").find("div").addClass("determinate");
                    } else if (progress == 0) {
                        // Progress bar goes to blue-indeterminate.
                        $divProgressBar.addClass("progressbar-blue").find("div").addClass("indeterminate");
                    } else if (progress == 100) {
                        // Progress bar goes to green.
                        $divProgressBar.addClass("progressbar-green").find("div").addClass("determinate")
                                .css("width", progress + "%");
                    } else {
                        // Progress bar goes to blue.
                        $divProgressBar.addClass("progressbar-blue").find("div").addClass("determinate")
                                .css("width", progress + "%");
                    }
                }
            }
        }
    };

    function document_onReady() {
        mCompiledTemplate_templateToastError = Handlebars.compile($("#templateToastError").html());
        $("#buttonAbortTask").bind("click", buttonAbortTask_onClick)
        $("#buttonStartTask").bind("click", buttonStartTask_onClick);
        $(document).ready(function () {
            $("select").material_select();
        });
    }

    function buttonAbortTask_onClick(e) {
        TaskManager.abortTask();
    }

    function buttonStartTask_onClick(e) {
        TaskManager.startTask();
    }

    $(document).ready(document_onReady);
</script>
</body>
</html>
