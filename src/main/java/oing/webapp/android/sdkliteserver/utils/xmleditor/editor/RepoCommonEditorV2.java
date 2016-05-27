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

public class RepoCommonEditorV2 implements IRepoCommonEditor {
	private final String mStrXmlDirUrl;
	private File mFileXml;
	private Document mDocument;
	private Map<String, String> mMapChannelRefs;

	RepoCommonEditorV2(String url, File xmlFile) throws IOException, DocumentException {
		mStrXmlDirUrl = UrlTextUtil.getDir(url);
		mFileXml = xmlFile;
		mDocument = DocumentHelper.parseText(FileUtils.readFileToString(mFileXml));
		initChannelRefs();
	}

	@Override
	public List<RemotePackage> getAllRemotePackages() {
		List<Element> lListElementRemotePackage;
		List<RemotePackage> lListRemotePackage = new LinkedList<>();
		//noinspection unchecked
		lListElementRemotePackage = (List<Element>)
				DocumentHelper.createXPath("/sdk-repository/remotePackage").selectNodes(mDocument);
		// Start parse XML file.
		lListRemotePackage.addAll(
				lListElementRemotePackage.stream().map(this::element2RemotePackage).collect(Collectors.toList())
		);

		return lListRemotePackage;
	}

	private RemotePackage element2RemotePackage(Element element) {
		Validate.isTrue("remotePackage".equals(element.getName()),
				"Undesired element, desired: remotePackage, given: " + element.getName());

		RemotePackage lRemotePackage;
		Boolean lzIsObsoleted;
		Integer lnApiLevel = null;
		String lStrType, lStrRevision, lStrDisplayName, lStrChannel;

		// Attribute ".obsolete"
		{
			Attribute lAttributeObsolete = element.attribute("obsolete");
			lzIsObsoleted = lAttributeObsolete != null && "true".equals(lAttributeObsolete.getValue());
		}
		// Element "type-details"
		{
			Element lElementTypeDetails = element.element("type-details");
			// Attribute "type-details.xsi:type"
			{
				Attribute lAttributeType = lElementTypeDetails.attribute("type");
				lStrType = RemotePackageTypeV2.forString(lAttributeType.getValue()).getFriendlyName();
			}
			// Element "type-details/api-level"
			{
				Element lElementApiLevel = lElementTypeDetails.element("api-level");
				if (lElementApiLevel != null) lnApiLevel = Integer.parseInt(lElementApiLevel.getText());
			}
		}
		// Element "revision"
		{
			Element lElementRevision = element.element("revision");
			lStrRevision = lElementRevision.elementText("major");
			String lStrTemp = lElementRevision.elementText("minor");
			if (lStrTemp != null) lStrRevision += "." + lStrTemp;
			lStrTemp = lElementRevision.elementText("micro");
			if (lStrTemp != null) lStrRevision += "." + lStrTemp;
			lStrTemp = lElementRevision.elementText("preview");
			if (lStrTemp != null) lStrRevision += "p" + lStrTemp;
		}
		// Element "display-name"
		lStrDisplayName = element.elementText("display-name");
		// Element "channelRef"
		lStrChannel = mMapChannelRefs.get(element.element("channelRef").attributeValue("ref"));
		// Build RemotePackage object
		lRemotePackage = new RemotePackage.Builder().type(lStrType).baseUrl(mStrXmlDirUrl).displayName(lStrDisplayName)
				.revision(lStrRevision).channel(lStrChannel).apiLevel(lnApiLevel).isObsoleted(lzIsObsoleted).build();
		// Element "archives"
		lRemotePackage.setArchives(element2Archives(element.element("archives"), lRemotePackage));
		return lRemotePackage;
	}

	private List<Archive> element2Archives(Element element, RemotePackage remotePackageRef) {
		Validate.isTrue("archives".equals(element.getName()),
				"Undesired element, desired: archive, given: " + element.getName());

		List<Archive> lListArchives = new LinkedList<>();
		// Elements "archive"
		//noinspection unchecked
		List<Element> lListElementArchive = (List<Element>) element.elements("archive");

		// Elements "archives/archive"
		for (Element lElementArchive : lListElementArchive) {
			final HostOsType HOST_OS = HostOsType.forString(lElementArchive.elementText("host-os"));
			final HostBitsType HOST_BITS = HostBitsType.forString(lElementArchive.elementText("host-bits"));

			// Element "complete", stands for "CompleteArchive".
			Element lElementCompleteArchive = lElementArchive.element("complete");
			lListArchives.add(new CompleteArchive.Builder(remotePackageRef)
					.size(Long.parseLong(lElementCompleteArchive.elementText("size")))
					.checksum(lElementCompleteArchive.elementText("checksum"))
					.url(lElementCompleteArchive.elementText("url"))
					.hostOs(HOST_OS).hostBits(HOST_BITS).build()
			);
			// Element "patches"
			Element lElementPatches = lElementArchive.element("patches");
			if (lElementPatches != null) {
				// Elements "patches/patch"
				//noinspection unchecked
				lListArchives.addAll(((List<Element>) lElementPatches.elements("patch")).stream().map(
						lElementPatch -> new PatchArchive.Builder(remotePackageRef)
								.size(Long.parseLong(lElementPatch.elementText("size")))
								.checksum(lElementPatch.elementText("checksum"))
								.url(lElementPatch.elementText("url"))
								.hostOs(HOST_OS).hostBits(HOST_BITS).basedOn("").build()
				).collect(Collectors.toList()));
			}
		}

		return lListArchives;
	}

	@Override
	public void updateArchivesUrl(List<String> listUrls) {
		//noinspection unchecked
		List<Element> lListElementUrl = (List<Element>)
				DocumentHelper.createXPath("/sdk-repository/remotePackage/archives/archive//url")
						.selectNodes(mDocument);
		if (lListElementUrl.size() != listUrls.size()) {
			throw new IllegalArgumentException(
					"Count of URLs does not match xml elements, desired: " + lListElementUrl.size() + ", give: " + listUrls.size());
		}
		for (int i = 0, size = lListElementUrl.size(); i < size; i++) {
			lListElementUrl.get(i).setText(listUrls.get(i));
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
			IOUtils.closeQuietly(writer);
		}
	}

	/**
	 * Load element data from "/channel"s.
	 */
	private void initChannelRefs() {
		mMapChannelRefs = new HashMap<>();
		//noinspection unchecked
		List<Element> lListElementChannels = (List<Element>)
				DocumentHelper.createXPath("/sdk-repository/channel").selectNodes(mDocument.getRootElement());
		for (Element lElementChannel : lListElementChannels) {
			mMapChannelRefs.put(lElementChannel.attributeValue("id"), lElementChannel.getText());
		}
	}
}
