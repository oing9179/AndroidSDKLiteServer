package oing.webapp.android.sdkliteserver.utils.xmleditor.editor;

import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import oing.webapp.android.sdkliteserver.utils.xmleditor.*;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RepoCommonEditorV1 implements IRepoCommonEditor {
	/**
	 * Where stores this xml file on internet.
	 */
	private final String mStrXmlDirUrl;
	private File mFileXml;
	private Document mDocument;

	RepoCommonEditorV1(String url, File xmlFile) throws IOException, DocumentException {
		mStrXmlDirUrl = UrlTextUtil.getDir(url);
		mFileXml = xmlFile;
		mDocument = DocumentHelper.parseText(FileUtils.readFileToString(mFileXml));
	}

	@Override
	public List<RemotePackage> getAllRemotePackages() {
		List<Element> lListElements;
		{
			XPath lXPathSdkArchives = DocumentHelper.createXPath("//sdk:archives");
			HashMap<String, String> lMapNamespaceURLs = new HashMap<>();
			lMapNamespaceURLs.put("sdk", mDocument.getRootElement().getNamespaceURI());
			lXPathSdkArchives.setNamespaceURIs(lMapNamespaceURLs);
			//noinspection unchecked
			lListElements = (List<Element>) lXPathSdkArchives.selectNodes(mDocument.getRootElement());
		}
		ArrayList<RemotePackage> lListRemotePackages = new ArrayList<>(lListElements.size());
		for (int i = 0, size = lListElements.size(); i < size; i++) {
			Element lElement = lListElements.get(i).getParent();
			Integer lnApiLevel;
			String lStrRevision, lStrChannel = "stable", lStrDisplayName;
			Boolean lzIsObsoleted = false;
			List<Archive> lListArchives = new LinkedList<>();
			RemotePackage lRemotePackage = null;

			{
				// Parse api-level to int or null.
				String lStrApiLevel = lElement.elementText("api-level");
				lnApiLevel = lStrApiLevel != null ? Integer.parseInt(lStrApiLevel) : null;
			}
			{
				// Parse version and revision and combine them.
				String version = lElement.elementText("version"), revision = "";
				Element lElementRevision = lElement.element("revision");

				if (lElementRevision != null) {
					if (lElementRevision.elements().size() == 0) {
						// revision is a number if there is no sub-elements in it.
						revision = lElementRevision.getText();
					} else {
						// Otherwise, there will be 4 sub-elements.
						revision = lElementRevision.elementText("major");// This element should be appear every time.
						String lStrTemp = lElementRevision.elementText("minor");
						if (lStrTemp != null) revision += "." + lStrTemp;
						lStrTemp = lElementRevision.elementText("micro");
						if (lStrTemp != null) revision += "." + lStrTemp;
						lStrTemp = lElementRevision.elementText("preview");
						if (lStrTemp != null) {
							revision += "p" + lStrTemp;
							lStrChannel = "preview";// Since we found element "preview".
						}
					}
					revision = "r" + revision;
				}
				if (version != null) {
					lStrRevision = version + "-" + revision;
				} else {
					lStrRevision = revision;
				}
			}
			{
				lStrDisplayName = lElement.elementText("name-display");
				if (lStrDisplayName == null) lElement.elementText("description");
			}
			// This RemotePackage is obsoleted if element "obsolete" exists.
			lzIsObsoleted = lElement.element("obsolete") != null;
			// Build a RemotePackage
			lRemotePackage = new RemotePackage.Builder()
					.type(RemotePackageTypeV1.forString(lElement.getName()).getFriendlyName())
					.baseUrl(mStrXmlDirUrl).displayName(lStrDisplayName).revision(lStrRevision).channel(lStrChannel)
					.apiLevel(lnApiLevel).isObsoleted(lzIsObsoleted).build();
			{
				// Parse "<sdk:archives>"
				//noinspection unchecked
				List<Element> lListElementArchives = lElement.element("archives").elements("archive");
				for (Element element : lListElementArchives) {
					CompleteArchive archive = (CompleteArchive) new CompleteArchive.Builder(lRemotePackage)
							.size(Long.parseLong(element.elementText("size")))
							.checksum(element.elementText("checksum"))
							.url(element.elementText("url"))
							.hostOs(HostOsType.forString(element.elementText("host-os")))
							.hostBits(HostBitsType.forString(element.elementText("host-bits")))
							.build();
					lListArchives.add(archive);
				}
			}
			lRemotePackage.setArchives(lListArchives);
			lListRemotePackages.add(lRemotePackage);
		}
		return lListRemotePackages;
	}

	@Override
	public void updateArchivesUrl(List<String> listUrls) {
		List<Element> lListElementURLs;
		{
			XPath lXPathURLs = DocumentHelper.createXPath("//sdk:url");
			HashMap<String, String> lMapNamespaceURLs = new HashMap<>();
			lMapNamespaceURLs.put("sdk", mDocument.getRootElement().getNamespaceURI());
			lXPathURLs.setNamespaceURIs(lMapNamespaceURLs);
			//noinspection unchecked
			lListElementURLs = (List<Element>) lXPathURLs.selectNodes(mDocument);
		}
		if (lListElementURLs.size() != listUrls.size()) {
			throw new IllegalArgumentException(
					"Count of URLs does not match xml elements, desired: " + lListElementURLs.size() + ", give: " + listUrls.size());
		}
		for (int i = 0, size = lListElementURLs.size(); i < size; i++) {
			lListElementURLs.get(i).setText(listUrls.get(i));
		}
	}

	@Override
	public void save() throws IOException {
		save(mFileXml);
	}

	@Override
	public void save(File target) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(target);
			mDocument.write(writer);
		} finally {
			if (writer != null) {
				//noinspection ThrowFromFinallyBlock
				writer.close();
			}
		}
	}
}
