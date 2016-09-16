package oing.webapp.android.sdkliteserver.tools.xmleditor.editor;

import oing.webapp.android.sdkliteserver.tools.xmleditor.AddonSite;
import oing.webapp.android.sdkliteserver.tools.xmleditor.AddonSiteTypeV3;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import org.apache.commons.io.IOUtils;
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

public class RepoSitesEditorV3 implements IRepoSitesEditor {
	private final String mStrXmlDirUrl;
	private Document mDocument;

	public RepoSitesEditorV3(String url, InputStream inputStreamXmlContent) throws IOException, DocumentException {
		this(url, inputStreamXmlContent, Charset.forName("UTF-8"));
	}

	public RepoSitesEditorV3(String url, InputStream inputStreamXmlContent, Charset charset) throws IOException, DocumentException {
		mStrXmlDirUrl = UrlTextUtil.getDir(url);
		mDocument = DocumentHelper.parseText(IOUtils.toString(inputStreamXmlContent, charset));
	}

	@Override
	public List<AddonSite> extractAll() {
		List<AddonSite> lListAddonSite = new LinkedList<>();
		List<Element> lListElement = mDocument.getRootElement().elements("site");
		final QName lQNameAttribute_type = createQNameAttribute_type();

		for (Element lElement : lListElement) {
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
	}

	@Override
	public void write(OutputStream out) throws IOException {
		XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
		writer.write(mDocument);
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
