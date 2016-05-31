package oing.webapp.android.sdkliteserver.utils.xmleditor.editor;

import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import oing.webapp.android.sdkliteserver.utils.xmleditor.AddonSite;
import oing.webapp.android.sdkliteserver.utils.xmleditor.AddonSiteTypeV3;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RepoSitesEditorV3 implements IRepoSitesEditor {
	private final String mStrXmlDirUrl;
	private File mFileXml;
	private Document mDocument;

	public RepoSitesEditorV3(String url, File xmlFile) throws IOException, DocumentException {
		mStrXmlDirUrl = UrlTextUtil.getDir(url);
		mFileXml = xmlFile;
		mDocument = DocumentHelper.parseText(FileUtils.readFileToString(xmlFile));
	}

	@Override
	public List<AddonSite> extractAll() {
		List<AddonSite> lListAddonSite = new LinkedList<>();
		List<Element> lListElement = mDocument.getRootElement().elements("site");
		final QName lQNameAttribute_type = createQNameAttribute_type();

		for (Element lElement : lListElement) {
			System.out.println(lElement.getName() + ", " + lElement.attribute(lQNameAttribute_type));
			AddonSite lAddonSite = new AddonSite.Builder().sourceUrl(mStrXmlDirUrl)
					.type(AddonSiteTypeV3.forString(lElement.attributeValue(lQNameAttribute_type)))
					.displayName(lElement.elementText("displayName"))
					.url(lElement.elementText("url")).build();
			lListAddonSite.add(lAddonSite);
		}
		return lListAddonSite;
	}

	public void rebuild(List<AddonSite> listAddonSite) {
		Element lElementRoot = mDocument.getRootElement();
		lElementRoot.elements().clear();
		final QName lQNameAttribute_type = createQNameAttribute_type();

		for (AddonSite addonSite : listAddonSite) {
			Element lElementSite = lElementRoot.addElement("site")
					.addAttribute(lQNameAttribute_type, addonSite.getType().value());
			lElementSite.addElement("displayName").setText(addonSite.getDisplayName());
			lElementSite.addElement("url").setText(addonSite.getUrl());
		}
		System.out.println(mDocument.asXML());
	}

	@Override
	public void save() throws IOException {
		save(mFileXml);
	}

	@Override
	public void save(File targetFile) throws IOException {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileWriter(targetFile), OutputFormat.createPrettyPrint());
			writer.write(mDocument);
		} finally {
			try {
				if (writer != null) writer.close();
			} catch (Exception ignore) {
			}
		}
	}

	private QName createQNameAttribute_type() {
		Map<String, String> lMapNamespaceURI = getNamespaceURIs();
		Namespace lNamespaceXsi = new Namespace("xsi", lMapNamespaceURI.get("xsi"));
		return new QName("type", lNamespaceXsi);
	}

	private Map<String, String> getNamespaceURIs() {
		Map<String, String> lMapNamespaceURI = new HashMap<>();
		for (Namespace namespace : mDocument.getRootElement().declaredNamespaces()) {
			lMapNamespaceURI.put(namespace.getPrefix(), namespace.getURI());
		}
		return lMapNamespaceURI;
	}
}
