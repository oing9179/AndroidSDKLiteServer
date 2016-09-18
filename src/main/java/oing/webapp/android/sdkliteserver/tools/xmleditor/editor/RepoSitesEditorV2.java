package oing.webapp.android.sdkliteserver.tools.xmleditor.editor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RepoSite;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RepoSiteType;
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

public class RepoSitesEditorV2 implements IRepoSitesEditor {
	private final String mStrXmlDirUrl;
	private Document mDocument;
	private static final BiMap<RepoSiteType, String> mBiMapRepoSiteTypeToXmlText;
	private static final RepoSiteType REPO_SITE_TYPE_DEFAULT = RepoSiteType.UNKNOWN;
	private static final String REPO_SITE_TYPE_DEFAULT_STRING = "unknown:unknownRepoSiteType";

	static {
		mBiMapRepoSiteTypeToXmlText = HashBiMap.create();
		mBiMapRepoSiteTypeToXmlText.put(REPO_SITE_TYPE_DEFAULT, REPO_SITE_TYPE_DEFAULT_STRING);
		mBiMapRepoSiteTypeToXmlText.put(RepoSiteType.ADDON_SITE, "sdk:addonSiteType");
		mBiMapRepoSiteTypeToXmlText.put(RepoSiteType.SYSTEM_IMAGE_SITE, "sdk:sysImgSiteType");
	}

	public RepoSitesEditorV2(String url, InputStream inputStreamXmlContent) throws IOException, DocumentException {
		this(url, inputStreamXmlContent, Charset.forName("UTF-8"));
	}

	public RepoSitesEditorV2(String url, InputStream inputStreamXmlContent, Charset charset) throws IOException, DocumentException {
		mStrXmlDirUrl = UrlTextUtil.getDir(url);
		mDocument = DocumentHelper.parseText(IOUtils.toString(inputStreamXmlContent, charset));
	}

	@Override
	public List<RepoSite> extractAll() {
		List<RepoSite> lListRepoSite = new LinkedList<>();
		List<Element> lListElement = mDocument.getRootElement().elements("site");
		final QName lQNameAttribute_type = createQNameAttribute_type();

		for (Element lElement : lListElement) {
			RepoSite lRepoSite = new RepoSite.Builder().sourceUrl(mStrXmlDirUrl)
					.type(mBiMapRepoSiteTypeToXmlText.inverse().getOrDefault(lElement.attributeValue(lQNameAttribute_type), REPO_SITE_TYPE_DEFAULT))
					.displayName(lElement.elementText("displayName"))
					.url(lElement.elementText("url")).build();
			lListRepoSite.add(lRepoSite);
		}
		return lListRepoSite;
	}

	public void rebuild(List<RepoSite> listRepoSite) {
		Element lElementRoot = mDocument.getRootElement();
		lElementRoot.elements().clear();
		final QName lQNameAttribute_type = createQNameAttribute_type();

		for (RepoSite repoSite : listRepoSite) {
			Element lElementSite = lElementRoot.addElement("site")
					.addAttribute(lQNameAttribute_type, mBiMapRepoSiteTypeToXmlText.getOrDefault(repoSite.getType(), REPO_SITE_TYPE_DEFAULT_STRING));
			lElementSite.addElement("displayName").setText(repoSite.getDisplayName());
			lElementSite.addElement("url").setText(repoSite.getUrl());
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
