package oing.webapp.android.sdkliteserver.tools.xmleditor.editor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.tools.xmleditor.*;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepoCommonEditorV1_12 implements IRepoCommonEditor {
	private RepoXmlFile mRepoXmlFile;
	private RepoZip mRepoZip;
	/**
	 * Where stores this xml file on internet.
	 */
	private final String mStrXmlDirUrl;
	private Document mDocument;

	private static final RemotePackageType DEFAULT_REMOTE_PACKAGE_TYPE = RemotePackageType.GENERIC_TYPE;
	private static final String DEFAULT_REMOTE_PACKAGE_TYPE_STRING = "unknown-to-generic-type";
	private static final BiMap<RemotePackageType, String> mBiMapRemotePackageType2XmlText;
	private File mFileRepoZipDir;

	static {
		mBiMapRemotePackageType2XmlText = HashBiMap.create();
		mBiMapRemotePackageType2XmlText.put(DEFAULT_REMOTE_PACKAGE_TYPE, DEFAULT_REMOTE_PACKAGE_TYPE_STRING);
		mBiMapRemotePackageType2XmlText.put(RemotePackageType.PLATFORM_TYPE, "platform");
		mBiMapRemotePackageType2XmlText.put(RemotePackageType.SOURCE_TYPE, "source");
		mBiMapRemotePackageType2XmlText.put(RemotePackageType.SYSTEM_IMAGE_TYPE, "system-image");
		mBiMapRemotePackageType2XmlText.put(RemotePackageType.ADDON_TYPE, "addon");
		mBiMapRemotePackageType2XmlText.put(RemotePackageType.ADDON_EXTRA_TYPE, "extra");
	}

	public RepoCommonEditorV1_12(String url, InputStream inputStreamXmlContent) throws IOException, DocumentException {
		this(url, inputStreamXmlContent, Charset.forName("UTF-8"));
	}

	public RepoCommonEditorV1_12(String url, InputStream inputStreamXmlContent, Charset charset) throws IOException, DocumentException {
		mStrXmlDirUrl = UrlTextUtil.getDir(url);
		mDocument = DocumentHelper.parseText(IOUtils.toString(inputStreamXmlContent, charset));
	}

	@Override
	public void setRepoXmlFile(RepoXmlFile repoXmlFile) {
		this.mRepoXmlFile = repoXmlFile;
	}

	@Override
	public void setRepoZip(RepoZip repoZip) {
		this.mRepoZip = repoZip;
		if (repoZip.getName() != null) {
			mFileRepoZipDir = ConfigurationUtil.getZipRepositoryDir(repoZip.getName());
		}
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
			if (lStrDisplayName == null) lStrDisplayName = element.elementText("description");
			if (lStrDisplayName == null) lStrDisplayName = element.getName();
		}
		// This RemotePackage is obsoleted if element "obsolete" exists.
		lzIsObsoleted = element.element("obsolete") != null;
		// Build a RemotePackage
		lRemotePackage = new RemotePackage.Builder()
				.type(mBiMapRemotePackageType2XmlText.inverse()
						.getOrDefault(element.getName(), DEFAULT_REMOTE_PACKAGE_TYPE).getFriendlyName()
				)
				// .type(RemotePackageTypeV1.forString(element.getName()).getFriendlyName())
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
			String lStrUrl = lElementArchive.elementText("url");
			String lStrFileNameWithPrefix = null;
			if (mRepoXmlFile != null && mRepoXmlFile.getZipSubDirectory() != null) {
				lStrFileNameWithPrefix = UrlTextUtil.concat(mRepoXmlFile.getZipSubDirectory(), UrlTextUtil.getFileName(lStrUrl));
			}
			File lFileZip = null;
			if (mFileRepoZipDir != null) {
				if (lStrFileNameWithPrefix != null) {
					lFileZip = new File(mFileRepoZipDir, lStrFileNameWithPrefix);
				} else {
					lFileZip = new File(mFileRepoZipDir, lStrUrl);
				}
			}
			CompleteArchive.Builder builder = new CompleteArchive.Builder(remotePackageRef)
					.size(Long.parseLong(lElementArchive.elementText("size")))
					.checksum(lElementArchive.elementText("checksum"))
					.url(lStrUrl)
					.hostOs(HostOsType.forString(lElementArchive.elementText("host-os")))
					.hostBits(HostBitsType.forString(lElementArchive.elementText("host-bits")))
					.isFileExisted(lFileZip != null && lFileZip.exists())
					.fileNameWithPrefix(lStrFileNameWithPrefix);
			lListArchives.add(builder.build());
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
	public void write(OutputStream out) throws IOException {
		XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
		writer.write(mDocument);
	}

	private Map<String, String> getNamespaceURIs() {
		Map<String, String> lMapNamespaceURI = new HashMap<>();
		for (Namespace namespace : mDocument.getRootElement().declaredNamespaces()) {
			lMapNamespaceURI.put(namespace.getPrefix(), namespace.getURI());
		}
		return lMapNamespaceURI;
	}
}
