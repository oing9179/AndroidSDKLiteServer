package oing.webapp.android.sdkliteserver.utils;

import jodd.io.FileUtil;
import org.dom4j.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A xml parser for "addons_list-2.xml"
 */
public class AddonsListXmlParser {
	private String mStrBaseURL = null;
	private Document mDocument = null;

	/**
	 * Init a parser for "addons_list-2.xml".
	 *
	 * @param baseURL Where you downloaded it.
	 * @param xmlFile The xml file.
	 */
	public AddonsListXmlParser(String baseURL, File xmlFile) throws IOException, DocumentException {
		mStrBaseURL = baseURL;
		if (mStrBaseURL.endsWith(".xml")) {
			mStrBaseURL = mStrBaseURL.substring(0, mStrBaseURL.lastIndexOf('/') + 1);
		}
		mDocument = DocumentHelper.parseText(FileUtil.readUTFString(xmlFile));
	}

	public Document getDocument() {
		return mDocument;
	}

	public String getBaseURL() {
		return mStrBaseURL;
	}

	/**
	 * Get all URLs from this document.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getURLs() {
		List<Node> lListNodeURLs = getURLNodes();
		ArrayList<String> lListStrURLs = new ArrayList<>(lListNodeURLs.size());
		for (int i = 0, size = lListNodeURLs.size(); i < size; i++) {
			lListStrURLs.add(lListNodeURLs.get(i).getText());
		}
		return lListStrURLs;
	}

	/**
	 * Get all URLs from this document
	 *
	 * @param prependBaseURL Prepend base url into it.
	 * @param forceHttps     Force HTTPS urls, otherwise Force HTTP urls.
	 */
	public List<String> getURLs(boolean prependBaseURL, boolean forceHttps) {
		List<String> list = getURLs();

		for (int i = 0, size = list.size(); i < size; i++) {
			String lStrURL = list.get(i);

			// concat base url
			if (prependBaseURL) {
				if (lStrURL.startsWith("http://") || lStrURL.startsWith("https://")) {
					// Remove it if it starts with (regexp)"$http[s]?://"
					lStrURL = lStrURL.substring(lStrURL.indexOf('/') + 2);
				}
				lStrURL = mStrBaseURL + lStrURL;
			}
			// Prepend (regexp)"$http[s]?://" or not.
			if (forceHttps && lStrURL.startsWith("http://")) {
				// 7 is the length of "http"
				lStrURL = "https" + lStrURL.substring(4);
			} else if (!forceHttps && lStrURL.startsWith("https://")) {
				// 8 is the length of "https"
				lStrURL = "http" + lStrURL.substring(5);
			}
			list.set(i, lStrURL);
		}
		return list;
	}

	/**
	 * Update URLs to document from given list.
	 */
	public void updateURLs(List<String> urlsList) {
		List<Node> lListNodeURLs = getURLNodes();
		if (lListNodeURLs.size() != urlsList.size()) {
			throw new IllegalArgumentException(
					"Count of URLs does not match, desired: " + lListNodeURLs.size() + ", give: " + urlsList.size());
		}
		for (int i = 0, size = lListNodeURLs.size(); i < size; i++) {
			lListNodeURLs.get(i).setText(urlsList.get(i));
		}
	}

	/**
	 * Get all "&lt;sdk:url&gt;" from document.
	 */
	private List<Node> getURLNodes() {
		XPath lXPathSdkUrls = DocumentHelper.createXPath("//sdk:url");
		{
			HashMap<String, String> lMapNamespaceURLs = new HashMap<>();
			lMapNamespaceURLs.put("sdk", mDocument.getRootElement().getNamespaceURI());
			lXPathSdkUrls.setNamespaceURIs(lMapNamespaceURLs);
		}
		//noinspection unchecked
		List<Node> lListNodeURLs = lXPathSdkUrls.selectNodes(mDocument.getRootElement());
		return lListNodeURLs;
	}
}
