/*
*	Coded by oing9179
*	Last update: 2014.02.02
*	Version: 0.1
*/
jQuery.ajaxFileUpload = function(options)
{
	var formData = new FormData();//存放要上传的文件
	var $inputFile = null;
	if(options["fileElementId"])
	{
		$inputFile = $("#" + options["fileElementId"])
	}
	else if(options["fileElement"])
	{
		$inputFile = $(options["fileElement"]);
	}
	if(!$inputFile)
	{
		throw new Error("No argument 'fileElementId' or 'fileElement'.");
	}
	for(var i = 0; i < fileElement.files.length; i++)
	{
		formData.append(fileElement.name, fileElement.files[i]);
	}
	if(options["onUploadProgress"])
	{
		options["upload.onprogress"] = function(e)
		{
			//e参数说明：{loaded:已经上传的字节数, total:总字节数, lengthComputable:false则表示total为0, timeStamp:本次onprogress触发时候的时间戳}
			/*开发者可能用到的现成参数：{
				bytesPerSecond:int 每秒上传多少字节,
				timeStampStartUpload:long 起始上传时间戳,
				percentOfUploaded:float 已上传百分比 保留两位小数,
				timeElapsedMillisecond:long 已用多少上传时间 单位ms,
				timeRemainingMillisecond:long 剩余多少时间上传完毕 单位ms
			}*/
			if(this.timeStampStartUpload > 0)//如果该事件在本次上传不是第一次执行
			{
				var elapsedMillisecondLastUpload = e.timeStamp - this.lastCountTimeStamp;//距离上次计数差多少毫秒
				var bytesLastUpload = e.loaded - this.lastTotalUploadedBytes;//上次上传了多少字节
				e.bytesPerSecond = bytesLastUpload / elapsedMillisecondLastUpload * 1000;//上次的上传速度 bytes/s
				e.percentOfUploaded = (100.0 * e.loaded / e.total).toFixed(2);//已上传百分比 保留两位小数
				e.timeElapsedMillisecond = e.timeStamp - this.timeStampStartUpload;//已用多少上传时间 单位ms
				e.timeRemainingMillisecond = parseInt((e.total - e.loaded) / e.bytesPerSecond * 1000);//剩余多少时间上传完毕 单位ms
			}
			if(!this.timeStampStartUpload)//如果本次上传文件的第一次计时的时间戳不存在，此语句在每次上传只执行一次
			{
				this.timeStampStartUpload = e.timeStamp;//保存第一次计数时候的时间戳
				e.timeStampStartUpload = e.timeStamp;//同上
				e.bytesPerSecond = 0;//下面四个初始化参数为0，呈现到界面上好看些
				e.percentOfUploaded = 0;
				e.timeElapsedMillisecond = 0;
				e.timeRemainingMillisecond = 0;
			}
			this.lastCountTimeStamp = e.timeStamp;//存放本次时间戳供下次计算时长使用
			this.lastTotalUploadedBytes = e.loaded;//存放本次上传总量供下次计算速度使用
			options.onUploadProgress(e);//调用开发者提供的监听上传进度的函数
		};
	}
	options["xhr"] = function()
	{
		//jQuery会调用该函数获取跨浏览器的XMLHttpRequest对象，而这个函数被我定制了，因此我可以干预要返回给jQuery用的XMLHttpRequest对象。
		var xhr = $.ajaxSettings.xhr();//获取跨浏览器的XMLHttpRequest对象
		for(var option in options)//找到调用者要给xhr.upload对象里添加什么事件。为xhr.upload.onprogress添加事件可以监听上传进度什么的。
		{
			if(option.indexOf("upload.") == 0)
			{
				xhr.upload[option.substr("upload.".length)] = options[option];
				this[option] = null;
			}
		}
		return xhr;
	};
	//下面代码为提交请求提供默认值
	if(!options["contentType"])
	{
		options["contentType"] = false;
	}
	if(!options["processData"])
	{
		options["processData"] = false;
	}
	if(!options["type"])
	{
		options["type"] = "post";
	}
	if(!options["data"])
	{
		options["data"] = formData;
	}
	$.ajax(options);
};