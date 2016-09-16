package oing.webapp.android.sdkliteserver.tools.xmleditor.editor;

import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import org.apache.commons.lang3.Validate;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.io.InputStream;

public final class RepoXmlEditorFactory {
	public static IRepoCommonEditor createRepoCommonEditor(String url, InputStream inputStreamXmlContent) throws IOException, DocumentException {
		validateUrlStartWith(url);
		String lStrFileName = UrlTextUtil.getFileName(url);

		if (lStrFileName.startsWith("repository2")) {
			return new RepoCommonEditorV2(url, inputStreamXmlContent);
		} else if (lStrFileName.startsWith("addon2")) {
			return new RepoCommonEditorV2(url, inputStreamXmlContent);
		} else if (lStrFileName.startsWith("sys-img2")) {
			return new RepoCommonEditorV2(url, inputStreamXmlContent);
		} else if (lStrFileName.startsWith("repository-")) {
			return new RepoCommonEditorV1(url, inputStreamXmlContent);
		} else if (lStrFileName.startsWith("addon")) {
			return new RepoCommonEditorV1(url, inputStreamXmlContent);
		} else if (lStrFileName.startsWith("sys-img")) {
			return new RepoCommonEditorV1(url, inputStreamXmlContent);
		}
		throw new IllegalArgumentException("Unknown repo-common xml: " + url);
	}

	public static IRepoSitesEditor createRepoSitesEditor(String url, InputStream inputStreamXmlContent) throws IOException, DocumentException {
		validateUrlStartWith(url);
		String lStrFileName = UrlTextUtil.getFileName(url);

		if (lStrFileName.equals("addons_list-3.xml")) {
			return new RepoSitesEditorV3(url, inputStreamXmlContent);
		}
		throw new IllegalArgumentException("Unknown addons_list xml: " + url);
	}

	private static void validateUrlStartWith(String url) {
		Validate.isTrue(url.startsWith("http://") || url.startsWith("https://"),
				"URL must start with 'http://' or 'https://'");
	}
}
