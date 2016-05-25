package oing.webapp.android.sdkliteserver.utils;

import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.model.SdkArchive;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A xml parser for general sdk-repository like "repository-11.xml".
 * <p>
 * The mechanism of how "Android SDK Manager(SdkMgr for short)" works:<br/>
 * 1. SdkMgr try to fetch 2 of xml files:<br/>
 * https://dl.google.com/android/repository/addons_list-2.xml<br/>
 * https://dl.google.com/android/repository/repository-12.xml<br/>
 * 2. Go dive into "addons_list-2.xml", we could found some other xml files, we call it "Addon Sites".<br/>
 * 3. Put "addons_list-2.xml" for a while, the check out all of the remaining xml files,
 * a element called "&lt;sdk:url&gt;" that is what we interested.<br/>
 * 4. For example, if we got a "&lt;sdk:url&gt;" from repository-12.xml like this:<br/>
 * "android-ndk-r10e-linux-x86_64.zip"<br/>
 * Grab url where you download "repository-12.xml" and cutoff the xml file name, it should like this:<br/>
 * "https://dl.google.com/android/repository/"<br/>
 * Combine your zip file name, we got:<br/>
 * "https://dl.google.com/android/repository/android-ndk-r10e-linux-x86_64.zip"<br/>
 * 5. SdkMgr do its work.<br/>
 */
@Deprecated
public class RepositoryXmlEditor {
	private String mStrBaseURL = null;
	private File mFileDocument = null;
	private Document mDocument = null;

	/**
	 * Init a parser for sdk-repository.
	 *
	 * @param baseURL Where you download this xml file.
	 * @param xmlFile XML file.
	 */
	public RepositoryXmlEditor(String baseURL, File xmlFile) throws IOException, DocumentException {
		Validate.isTrue(baseURL.startsWith("http://") || baseURL.startsWith("https://"),
				"baseURL must start with 'http://' or 'https://'");
		mStrBaseURL = baseURL;
		if (mStrBaseURL.endsWith(".xml")) {
			// "+1" means include last of character "/".
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

	/**
	 * Get all SDK archive
	 */
	@SuppressWarnings("Duplicates")
	public List<SdkArchive> getSdkArchives() {
		List<Element> lListElements;
		{
			XPath lXPathSdkArchives = DocumentHelper.createXPath("//sdk:archives");
			HashMap<String, String> lMapNamespaceURLs = new HashMap<>();
			lMapNamespaceURLs.put("sdk", mDocument.getRootElement().getNamespaceURI());
			lXPathSdkArchives.setNamespaceURIs(lMapNamespaceURLs);
			//noinspection unchecked
			lListElements = (List<Element>) lXPathSdkArchives.selectNodes(mDocument.getRootElement());
		}
		ArrayList<SdkArchive> lListSdkArchives = new ArrayList<>(lListElements.size());
		for (int i = 0, size = lListElements.size(); i < size; i++) {
			Element element = lListElements.get(i);
			// Element values for the parent of <sdk:archives>.
			String lStrType, lStrDisplayName, lStrDescription, lStrVersion, lStrRevision = null;
			Integer lnApiLevel = null;
			Boolean mzIsObsolete;
			// The parent of <sdk:archives>
			element = element.getParent();
			lStrType = element.getName();
			// <sdk:name-display>
			lStrDisplayName = element.elementText("name-display");
			// <sdk:description>
			lStrDescription = element.elementText("description");
			// <sdk:version>
			lStrVersion = element.elementText("version");
			// <sdk:revision>
			{
				Element elementRevision = element.element("revision");// probably null
				if (elementRevision != null) {
					if (elementRevision.elements().size() == 0) {
						/**
						 * Condition 1:
						 * <sdk:revision>20.1.2</sdk:revision>
						 */
						lStrRevision = elementRevision.getText();
					} else {
						/**
						 * Condition 2:
						 * <sdk:revision>
						 *     <sdk:major>20</sdk:major>
						 *     <sdk:minor>1</sdk:minor>
						 *     <sdk:micro>2</sdk:micro>
						 *     <sdk:preview>1</sdk:preview>
						 * </sdk:revision>
						 */
						lStrRevision = elementRevision.elementText("major");
						String lStrTemp = elementRevision.elementText("minor");
						if (lStrTemp != null) lStrRevision += "." + lStrTemp;
						lStrTemp = elementRevision.elementText("micro");
						if (lStrTemp != null) lStrRevision += "." + lStrTemp;
						lStrTemp = elementRevision.elementText("preview");
						if (lStrTemp != null) lStrRevision += "p" + lStrTemp;
					}
				}
			}
			// <sdk:api-level>
			{
				String apiLevel = element.elementText("api-level");
				if (apiLevel != null) lnApiLevel = Integer.parseInt(apiLevel);
			}
			// <sdk:obsolete>
			mzIsObsolete = element.element("obsolete") != null;
			// <sdk:archive>
			{
				//noinspection unchecked
				List<Element> lListElementArchives = element.element("archives").elements("archive");
				for (Element elementArchive : lListElementArchives) {
					SdkArchive lSdkArchive = new SdkArchive();
					lSdkArchive.setType(lStrType);
					lSdkArchive.setDisplayName(lStrDisplayName);
					lSdkArchive.setDescription(lStrDescription);
					lSdkArchive.setVersion(lStrVersion);
					lSdkArchive.setRevision(lStrRevision);
					lSdkArchive.setApiLevel(lnApiLevel);
					lSdkArchive.setIsObsoleted(mzIsObsolete);
					// <sdk:size>
					lSdkArchive.setSize(Long.parseLong(elementArchive.elementText("size")));
					// <sdk:checksum type="checksum type">
					lSdkArchive.setChecksumType(elementArchive.element("checksum").attributeValue("type"));
					// <sdk:checksum>checksum here</sdk:checksum>
					lSdkArchive.setChecksum(elementArchive.elementText("checksum"));
					// <sdk:url>
					lSdkArchive.setUrl(elementArchive.elementText("url"));
					// <sdk:host-os> or <sdk:archives os="?">
					{
						String hostOs = elementArchive.elementText("host-os");
						if (hostOs == null) hostOs = elementArchive.attributeValue("os");
						lSdkArchive.setHostOs(hostOs);
					}
					// <sdk:host-bits>
					{
						String hostBits = elementArchive.elementText("host-bits");
						if (hostBits == null) elementArchive.attributeValue("arch");
						lSdkArchive.setHostBits(hostBits);
					}
					lListSdkArchives.add(lSdkArchive);
				}
			}
		}
		return lListSdkArchives;
	}

	/**
	 * Get all SDK archive
	 *
	 * @param prependBaseURL Prepend base url into the url of sdk-archive
	 * @param forceHttps     Force HTTPS urls, otherwise Force HTTP urls.
	 */
	public List<SdkArchive> getSdkArchives(boolean prependBaseURL, boolean forceHttps) {
		List<SdkArchive> list = getSdkArchives();

		for (SdkArchive lSdkArchive : list) {
			String lStrURL = lSdkArchive.getUrl();

			// concat base url
			if (prependBaseURL && !lStrURL.startsWith("http://") && !lStrURL.startsWith("https://")) {
				// Remove it if it starts with (regexp)"$http[s]?://"
				// lStrURL = lStrURL.substring(lStrURL.indexOf('/') + 2);
				lStrURL = mStrBaseURL + lStrURL;
			}
			// Prepend (regexp)"$http[s]?://" or not.
			if (forceHttps && lStrURL.startsWith("http://")) {
				// 4 is the length of "http"
				lStrURL = "https" + lStrURL.substring(4);
			} else if (!forceHttps && lStrURL.startsWith("https://")) {
				// 5 is the length of "https"
				lStrURL = "http" + lStrURL.substring(5);
			}
			lSdkArchive.setUrl(lStrURL);
		}
		return list;
	}

	/**
	 * Update urls to document from given getAll.
	 */
	@SuppressWarnings("Duplicates")
	public void updateURLs(List<String> urlsList) {
		List<Element> lListElementURLs;
		{
			XPath lXPathURLs = DocumentHelper.createXPath("//sdk:url");
			HashMap<String, String> lMapNamespaceURLs = new HashMap<>();
			lMapNamespaceURLs.put("sdk", mDocument.getRootElement().getNamespaceURI());
			lXPathURLs.setNamespaceURIs(lMapNamespaceURLs);
			//noinspection unchecked
			lListElementURLs = (List<Element>) lXPathURLs.selectNodes(mDocument);
		}
		if (lListElementURLs.size() != urlsList.size()) {
			throw new IllegalArgumentException(
					"Count of URLs does not match xml elements, desired: " + lListElementURLs.size() + ", give: " + urlsList.size());
		}
		for (int i = 0, size = lListElementURLs.size(); i < size; i++) {
			lListElementURLs.get(i).setText(urlsList.get(i));
		}
	}

	public void write() throws IOException {
		write(mFileDocument);
	}

	public void write(File target) throws IOException {
		OutputStreamWriter lWriter = null;
		try {
			lWriter = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(target)));
			mDocument.write(lWriter);
		} finally {
			IOUtils.closeQuietly(lWriter);
		}
	}
}
