package oing.webapp.android.sdkliteserver.tools.xmleditor.editor;

import oing.webapp.android.sdkliteserver.tools.xmleditor.*;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepoCommonEditorV2 implements IRepoCommonEditor {
	private final String mStrXmlDirUrl;
	private Document mDocument;
	private Map<String, String> mMapChannelRefs;

	public RepoCommonEditorV2(String url, InputStream inputStreamXmlContent) throws IOException, DocumentException {
		this(url, inputStreamXmlContent, Charset.forName("UTF-8"));
	}

	public RepoCommonEditorV2(String url, InputStream inputStreamXmlContent, Charset charset) throws IOException, DocumentException {
		mStrXmlDirUrl = UrlTextUtil.getDir(url);
		mDocument = DocumentHelper.parseText(IOUtils.toString(inputStreamXmlContent, charset));
		loadChannelRefs();
	}

	@Override
	public List<RemotePackage> extractAll() {
		List<Node> lListNodeRemotePackage = DocumentHelper.createXPath("/*/remotePackage").selectNodes(mDocument);
		List<RemotePackage> lListRemotePackage = new LinkedList<>();
		// Start parse XML file.
		lListRemotePackage.addAll(lListNodeRemotePackage.stream().map(
				node -> element2RemotePackage((Element) node)
		).collect(Collectors.toList()));

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
		lRemotePackage = new RemotePackage.Builder().type(lStrType).sourceUrl(mStrXmlDirUrl).displayName(lStrDisplayName)
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
		List<Element> lListElementArchive = element.elements("archive");

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
				List<Element> lElementArrPatch = lElementPatches.elements();
				for (Element lElementPatch : lElementArrPatch) {
					String lStrBasedOn;
					{
						Element lElementBasedOn = lElementPatch.element("based-on");
						lStrBasedOn = lElementBasedOn.elementText("major");
						String lStrTemp = lElementBasedOn.elementText("minor");
						if (lStrTemp != null) lStrBasedOn += "." + lStrTemp;
						lStrTemp = lElementBasedOn.elementText("micro");
						if (lStrTemp != null) lStrBasedOn += "." + lStrTemp;
						lStrTemp = lElementBasedOn.elementText("preview");
						if (lStrTemp != null) lStrBasedOn += "p" + lStrTemp;
					}
					Archive lArchive = new PatchArchive.Builder(remotePackageRef)
							.size(Long.parseLong(lElementPatch.elementText("size")))
							.checksum(lElementPatch.elementText("checksum"))
							.url(lElementPatch.elementText("url"))
							.hostOs(HOST_OS).hostBits(HOST_BITS).basedOn(lStrBasedOn).build();
					lListArchives.add(lArchive);
				}

				/*lListArchives.addAll(lElementPatches.elements("patch").stream().map(
						lElementPatch -> new PatchArchive.Builder(remotePackageRef)
								.size(Long.parseLong(lElementPatch.elementText("size")))
								.checksum(lElementPatch.elementText("checksum"))
								.url(lElementPatch.elementText("url"))
								.hostOs(HOST_OS).hostBits(HOST_BITS).basedOn("").build()
				).collect(Collectors.toList()));*/
			}
		}

		return lListArchives;
	}

	@Override
	public void updateArchivesUrl(List<String> listUrls) {
		List<Node> lListNodeUrl = DocumentHelper.createXPath("/*/remotePackage/archives/archive//url")
				.selectNodes(mDocument);
		if (lListNodeUrl.size() != listUrls.size()) {
			throw new IllegalArgumentException(
					"Count of URLs does not match xml elements, desired: " + lListNodeUrl.size()
							+ ", given: " + listUrls.size());
		}
		for (int i = 0, size = lListNodeUrl.size(); i < size; i++) {
			lListNodeUrl.get(i).setText(listUrls.get(i));
		}
	}

	@Override
	public void write(OutputStream out) throws IOException {
		XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
		writer.write(mDocument);
	}

	/**
	 * Load element data from "/channel"s.
	 */
	private void loadChannelRefs() {
		mMapChannelRefs = new HashMap<>();
		List<Node> lListNodeChannels = DocumentHelper.createXPath("/sdk-repository/channel").selectNodes(mDocument);
		for (Node node : lListNodeChannels) {
			Element element = (Element) node;
			mMapChannelRefs.put(element.attributeValue("id"), element.getText());
		}
	}
}