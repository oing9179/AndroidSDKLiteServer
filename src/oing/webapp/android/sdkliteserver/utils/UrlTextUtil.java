package oing.webapp.android.sdkliteserver.utils;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.ProxyInfo;
import jodd.http.net.SocketHttpConnectionProvider;

public class UrlTextUtil {
	public static String http2https(String url) {
		if (url.startsWith("http://")) {
			url = "https://" + url.substring(7);
		}
		return url;
	}

	public static String https2http(String url) {
		if (url.startsWith("https://")) {
			url = "http://" + url.substring(8);
		}
		return url;
	}

	public static String concat(String... patterns) {
		if (patterns.length < 2) throw new IllegalArgumentException("At least 2 patterns.");

		if (!patterns[0].endsWith("/")) patterns[0] += "/";
		for (int i = 0, end = patterns.length - 1; i < end; i++) {
			if (patterns[i].startsWith("/")) patterns[i] = patterns[i].substring(1);
			if (!patterns[i].endsWith("/")) patterns[i] += "/";
		}
		{
			int lastItem = patterns.length - 1;
			if (patterns[lastItem].startsWith("/")) patterns[lastItem] = patterns[lastItem].substring(1);
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (String str : patterns) stringBuilder.append(str);
		return stringBuilder.toString();
	}

	public static String getFileName(String url) {
		int index = url.lastIndexOf('/');
		if (index != -1) url = url.substring(index + 1);
		return url;
	}

	public static void main(String[] args) throws Exception {
		test0();
	}

	private static void test0(){
		final String lStrUrl = "http://dl-ssl.google.com/glass/gdk/addon.xml";
		ProxyInfo proxyInfo = new ProxyInfo(ProxyInfo.ProxyType.SOCKS5, "127.0.0.1", 9150, "tor", "");
		SocketHttpConnectionProvider provider = new SocketHttpConnectionProvider();
		provider.useProxy(proxyInfo);
//		HttpResponse response = new HttpRequest().open(provider).method("GET").set(lStrUrl).send();
		HttpResponse response = HttpRequest.get(lStrUrl).open(provider).send();
		System.out.println(response.bodyText());
	}

	private static void test1(){
	}
}
