package oing.webapp.android.sdkliteserver.utils;

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
		for (int i = 0; i < patterns.length; i++) {
			if (patterns[i] == null) {
				patterns[i] = "";
			}
		}

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

	public static String getDir(String url) {
		if (url == null) return null;
		int index = url.lastIndexOf('/');
		if (index != -1) url = url.substring(0, index + 1);
		return url;
	}

	public static String getFileName(String url) {
		if (url == null) return null;
		int index = url.lastIndexOf('/');
		if (index != -1) url = url.substring(index + 1);
		return url;
	}
}
