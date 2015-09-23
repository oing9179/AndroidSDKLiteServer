<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/pages/common/html_head.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/pages/common/navbar_materialize.jsp" %>
<div class="container">
    <div class="card-panel red darken-4 white-text" style="display:${errorMessage != null ? "block" : "none"};">
        ${errorMessage}
    </div>
    <div class="card">
        <div class="card-content" style="padding:0;">
            <div class="row" style="margin:0;">
                <div class="card-title col s12 black-text">Manual addition</div>
                <div class="col s12 black-text">${xmlRepository.name}</div>
            </div>
            <div class="divider" style="margin-bottom:12px;"></div>
            <div class="row" style="margin:0 0 8px 0;">
                <div class="input-field col s12">
                        <textarea id="textareaLog" class="materialize-textarea"
                                  style="max-height:320px; overflow-y:scroll; padding:0; margin:0;"></textarea>
                    <label for="textareaLog">Paste log here from SDK Manager</label>
                </div>
            </div>
            <div class="row" style="margin-left:0; margin-right:0;">
                <div class="col s12 right-align">
                    <button id="buttonParseLog" class="btn btn-less-padding waves-effect waves-light indigo">
                        <i class="material-icons left">android</i>Parse log
                    </button>
                </div>
            </div>
            <form action="/repository/xml/${xmlRepository.name}/manual_addition.do"
                  method="post" enctype="multipart/form-data">
                <div class="row" style="margin-left:0; margin-right:0;">
                    <div class="col s12">
                        <ul id="ulXmlUrlList" class="collection"></ul>
                        <!-- Template: #ulXmlUrlList list item -->
                        <script id="templateXmlUrlListItem" type="text/x-handlebars-template">
                            <li class="collection-item">
                                <div class="row">
                                    <div class="col s8 m10 l10">
                                        <input type="file" name="file" accept="text/xml" required="required"/>
                                    </div>
                                    <div class="col s4 m2 l2 right-align">
                                        <button class="btn btn-less-padding waves-effect waves-light red"
                                                title="Delete">
                                            <i class="material-icons">delete</i>
                                        </button>
                                    </div>
                                    <div class="col s12">
                                        <input type="text" name="url" value="{{url}}" required="required"/>
                                    </div>
                                </div>
                            </li>
                        </script>
                        <style type="text/css">
                            #ulXmlUrlList > li { padding: 6px; }
                            #ulXmlUrlList > li > div { margin: 0; padding: 6px; }
                            #ulXmlUrlList > li > div > div:nth-child(1) { padding-left: 0; }
                            #ulXmlUrlList > li > div > div:nth-child(1) > input { padding: 6px 6px 6px 0; width: 100%; }
                            #ulXmlUrlList > li > div > div:nth-child(2) { padding-right: 0; }
                            #ulXmlUrlList > li > div > div:nth-child(3) { padding: 0; }
                            #ulXmlUrlList > li > div > div:nth-child(3) > input { height: 2rem; margin-bottom: 6px; }
                        </style>
                    </div>
                </div>
                <div class="row" style="margin-left:0; margin-right:0;">
                    <div class="col s12 center-align">
                        <button type="submit" class="btn btn-less-padding waves-effect waves-light">
                            <i class="material-icons left">done</i>Commit
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<script type="text/javascript">
    var mCompiledTemplate_templateXmlUrlListItem;

    function document_onReady() {
        $("#buttonParseLog").bind("click", buttonParseLog_onClick);
        mCompiledTemplate_templateXmlUrlListItem = Handlebars.compile($("#templateXmlUrlListItem").html());
    }

    function buttonParseLog_onClick(e) {
        var fnReloadXmlUrlList = function (data, textStatus, jqXHR) {
            var $ul = $("#ulXmlUrlList");
            var fnButtonDelete_onClick = function (e) {
                $(e.currentTarget).parent().parent().parent().remove();
            };
            for (var i in data) {
                var lJsonObjTemplateData = {"url": data[i]};
                var $html = $(mCompiledTemplate_templateXmlUrlListItem(lJsonObjTemplateData));
                $html.find("div.row > div:nth-child(2) > button").bind("click", fnButtonDelete_onClick);
                $ul.append($html);
            }
        };
        $.ajax({
            url: "/repository/xml/${xmlRepository.name}/parse_log_for_sdkmanager.do",
            method: "POST",
            data: {"log": $("#textareaLog").val()},
            dataType: "json",
            beforeSend: function (jqXHR, settings) {
                $("#buttonParseLog").attr("disabled", "disabled");
                $("#ulXmlUrlList").html("");// Clear all <li>
            },
            success: fnReloadXmlUrlList,
            error: function (jqXHR, textStatus, errorThrown) {
                Materialize.toast("Failed to parse(" + textStatus + "): " + errorThrown, 10000, "red darken-4");
            },
            complete: function (jqXHR, textStatus) {
                $("#buttonParseLog").removeAttr("disabled");
            }
        });
    }

    $(document).ready(document_onReady);
</script>
</body>
</html>
