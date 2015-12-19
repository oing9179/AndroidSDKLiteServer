package oing.webapp.android.sdkliteserver.utils;

import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.model.SdkAddonSite;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A xml parser designed for "addons_list-2.xml" only.<br/>
 * <br/>
 * This class uses the same mechanism as {@link RepositoryXmlEditor},
 * But we just need "&lt;sdk:name&gt;" and "&lt;sdk:url&gt;" instead.
 */
public class AddonsListXmlEditor {
	private String mStrBaseURL = null;
	private File mFileDocument = null;
	private Document mDocument = null;

	public AddonsListXmlEditor(String baseURL, File xmlFile) throws IOException, DocumentException {
		mStrBaseURL = baseURL;
		if (mStrBaseURL.endsWith(".xml")) {
			mStrBaseURL = mStrBaseURL.substring(0, mStrBaseURL.lastIndexOf('/') + 1);
		}
		mFileDocument = xmlFile;
		mDocument = DocumentHelper.parseText(FileUtil.readUTFString(mFileDocument));
	}

	public Document getDocument() {
		return mDocument;
	}

	public String getBaseURL() {
		return mStrBaseURL;
	}

	public List<SdkAddonSite> getAll() {
		ArrayList<SdkAddonSite> lListSdkAddonSites = new ArrayList<>();
		Iterator<Element> iterator = mDocument.getRootElement().elementIterator();
		while (iterator.hasNext()) {
			Element lElement = iterator.next();
			SdkAddonSite lSdkAddonSite = new SdkAddonSite();
			lSdkAddonSite.setType(SdkAddonSite.Type.forString(lElement.getName()));
			lSdkAddonSite.setUrl(lElement.elementText("url"));// <sdk:url>
			lSdkAddonSite.setName(lElement.elementText("name"));// <sdk:name>
			lListSdkAddonSites.add(lSdkAddonSite);
		}
		return lListSdkAddonSites;
	}

	public List<SdkAddonSite> getAll(boolean prependBaseUrl, boolean forceHttps) {
		List<SdkAddonSite> lListSdkAddonSites = getAll();

		for (SdkAddonSite lSdkAddonSite : lListSdkAddonSites) {
			String lStrUrl = lSdkAddonSite.getUrl();
			if (prependBaseUrl && !(lStrUrl.startsWith("http://") || lStrUrl.startsWith("https://"))) {
				lStrUrl = UrlTextUtil.concat(mStrBaseURL, lStrUrl);
			}
			if (lStrUrl.startsWith("http://") || lStrUrl.startsWith("https://")) {
				if (forceHttps) lStrUrl = UrlTextUtil.http2https(lStrUrl);
				else lStrUrl = UrlTextUtil.https2http(lStrUrl);
			}
			lSdkAddonSite.setUrl(lStrUrl);
		}

		return lListSdkAddonSites;
	}

	public void replaceAll(List<SdkAddonSite> addonSites) {
		Element lElementRoot = mDocument.getRootElement();
		Namespace lNamespace = lElementRoot.getNamespace();
		// Clear all elements under <sdk:sdk-addons-list>
		lElementRoot.elements().clear();
		// addition
		for (SdkAddonSite lSdkAddonSite : addonSites) {
			Element lElementAddonSite = lElementRoot.addElement(lNamespace.getPrefix() + ":" + lSdkAddonSite.getType().getName());
			Element lElementUrl = lElementAddonSite.addElement(lNamespace.getPrefix() + ":url");
			lElementUrl.setText(lSdkAddonSite.getUrl());
			Element lElementName = lElementAddonSite.addElement(lNamespace.getPrefix() + ":name");
			lElementName.setText(lSdkAddonSite.getName());
		}
	}

	public void write() throws IOException {
		write(mFileDocument);
	}

	public void write(File target) throws IOException {
		XMLWriter lWriter = null;
		try {
			lWriter = new XMLWriter(new FileWriter(target), OutputFormat.createPrettyPrint());
			lWriter.write(mDocument);
		} finally {
			try {
				if (lWriter != null) {
					lWriter.close();
				}
			} catch (Exception ignore) {
			}
		}
	}
}
