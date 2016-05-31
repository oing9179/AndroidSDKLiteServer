package oing.webapp.android.sdkliteserver.utils.xmleditor.editor;

import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import org.apache.commons.lang3.Validate;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;

public final class RepoXmlEditorFactory {
	public static IRepoCommonEditor createRepoCommonEditor(String url, File xmlFile) throws IOException, DocumentException {
		Validate.isTrue(url.startsWith("http://") || url.startsWith("https://"),
				"URL must start with 'http://' or 'https://'");
		String lStrFileName = UrlTextUtil.getFileName(url);

		if (lStrFileName.startsWith("repository2")) {
			return new RepoCommonEditorV2(url, xmlFile);
		} else if (lStrFileName.startsWith("addon2")) {
			return new RepoCommonEditorV2(url, xmlFile);
		} else if (lStrFileName.startsWith("sys-img2")) {
			return new RepoCommonEditorV2(url, xmlFile);
		} else if (lStrFileName.startsWith("repository-")) {
			return new RepoCommonEditorV1(url, xmlFile);
		} else if (lStrFileName.startsWith("addon")) {
			return new RepoCommonEditorV1(url, xmlFile);
		} else if (lStrFileName.startsWith("sys-img")) {
			return new RepoCommonEditorV1(url, xmlFile);
		}
		throw new IllegalArgumentException("Unknown repo-common xml: " + url);
	}

	public static IRepoSitesEditor createAddonsListEditor(String url, File xmlFile) throws IOException, DocumentException {
		String lStrFileName = UrlTextUtil.getFileName(url);

		if (lStrFileName.equals("addons_list-3.xml")) {
			return new RepoSitesEditorV3(url, xmlFile);
		}
		throw new IllegalArgumentException("Unknown addons_list xml: " + url);
	}
}
