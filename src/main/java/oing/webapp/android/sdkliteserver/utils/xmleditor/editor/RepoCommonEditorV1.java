package oing.webapp.android.sdkliteserver.utils.xmleditor.editor;

import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import oing.webapp.android.sdkliteserver.utils.xmleditor.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	public List<RemotePackage> extractAll() {
		List<Node> lListNode;
		{
			XPath lXPath = DocumentHelper.createXPath("/*/sdk:*/sdk:archives");
			lXPath.setNamespaceURIs(getNamespaceURIs());
			lListNode = lXPath.selectNodes(mDocument);
		}
		List<RemotePackage> lListRemotePackages = new LinkedList<>();
		lListRemotePackages.addAll(lListNode.stream().map(
				node -> element2RemotePackage(node.getParent())
		).collect(Collectors.toList()));
		return lListRemotePackages;
	}


	private RemotePackage element2RemotePackage(Element element) {
		/* We use Validate.isTrue instead of Validate.notNull, because we want a element that have a child element
		 * named "archives". Validate.notNull throws "NullPointerException" which is doesn't make sense. */
		Validate.isTrue(element.element("archives") != null, "Undesired element, given: " + element.getName());

		Integer lnApiLevel;
		String lStrRevision, lStrChannel = "stable", lStrDisplayName;
		Boolean lzIsObsoleted;
		RemotePackage lRemotePackage;

		{
			// Parse api-level to int or null.
			String lStrApiLevel = element.elementText("api-level");
			lnApiLevel = lStrApiLevel != null ? Integer.parseInt(lStrApiLevel) : null;
		}
		{
			// Parse version and revision and combine them.
			String version = element.elementText("version"), revision = "";
			Element lElementRevision = element.element("revision");

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
			lStrDisplayName = element.elementText("name-display");
			if (lStrDisplayName == null) element.elementText("description");
		}
		// This RemotePackage is obsoleted if element "obsolete" exists.
		lzIsObsoleted = element.element("obsolete") != null;
		// Build a RemotePackage
		lRemotePackage = new RemotePackage.Builder()
				.type(RemotePackageTypeV1.forString(element.getName()).getFriendlyName())
				.sourceUrl(mStrXmlDirUrl).displayName(lStrDisplayName).revision(lStrRevision).channel(lStrChannel)
				.apiLevel(lnApiLevel).isObsoleted(lzIsObsoleted).build();
		// Convert element "archives" to List<Archive>.
		lRemotePackage.setArchives(element2Archives(element.element("archives"), lRemotePackage));
		return lRemotePackage;
	}

	private List<Archive> element2Archives(Element element, RemotePackage remotePackageRef) {
		Validate.isTrue("archives".equals(element.getName()),
				"Undesired element, desired: archives, given: " + element.getName());
		List<Archive> lListArchives = new LinkedList<>();
		// Parse "sdk:archives"
		List<Element> lListElementArchives = element.elements("archive");
		for (Element lElementArchive : lListElementArchives) {
			CompleteArchive archive = new CompleteArchive.Builder(remotePackageRef)
					.size(Long.parseLong(lElementArchive.elementText("size")))
					.checksum(lElementArchive.elementText("checksum"))
					.url(lElementArchive.elementText("url"))
					.hostOs(HostOsType.forString(lElementArchive.elementText("host-os")))
					.hostBits(HostBitsType.forString(lElementArchive.elementText("host-bits")))
					.build();
			lListArchives.add(archive);
		}
		return lListArchives;
	}

	@Override
	public void updateArchivesUrl(List<String> listUrls) {
		List<Node> lListNodeURL;
		{
			XPath lXPath = DocumentHelper.createXPath("/*/sdk:*/sdk:archives/sdk:archive/sdk:url");
			lXPath.setNamespaceURIs(getNamespaceURIs());
			lListNodeURL = lXPath.selectNodes(mDocument);
		}
		if (lListNodeURL.size() != listUrls.size()) {
			throw new IllegalArgumentException("Count of URLs does not match xml elements, "
					+ "desired: " + lListNodeURL.size() + ", give: " + listUrls.size());
		}
		for (int i = 0, size = lListNodeURL.size(); i < size; i++) {
			lListNodeURL.get(i).setText(listUrls.get(i));
		}
	}

	@Override
	public void save() throws IOException {
		save(mFileXml);
	}

	@Override
	public void save(File targetFile) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(targetFile);
			mDocument.write(writer);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	private Map<String, String> getNamespaceURIs() {
		Map<String, String> lMapNamespaceURI = new HashMap<>();
		for (Namespace namespace : mDocument.getRootElement().declaredNamespaces()) {
			lMapNamespaceURI.put(namespace.getPrefix(), namespace.getURI());
		}
		return lMapNamespaceURI;
	}
}
