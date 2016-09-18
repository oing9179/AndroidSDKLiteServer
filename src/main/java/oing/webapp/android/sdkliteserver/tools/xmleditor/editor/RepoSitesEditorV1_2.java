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

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * repo-sites xml file editor for version 1.2.
 */
public class RepoSitesEditorV1_2 implements IRepoSitesEditor {
	private final String mStrXmlDirUrl;
	private Document mDocument;
	private static final BiMap<RepoSiteType, String> mBiMapRepoSiteTypeToXmlText;
	private static final RepoSiteType REPO_SITE_TYPE_DEFAULT = RepoSiteType.UNKNOWN;
	private static final String REPO_SITE_TYPE_DEFAULT_STRING = "unknown-site";

	static {
		mBiMapRepoSiteTypeToXmlText = HashBiMap.create();
		mBiMapRepoSiteTypeToXmlText.put(REPO_SITE_TYPE_DEFAULT, REPO_SITE_TYPE_DEFAULT_STRING);
		mBiMapRepoSiteTypeToXmlText.put(RepoSiteType.ADDON_SITE, "addon-site");
		mBiMapRepoSiteTypeToXmlText.put(RepoSiteType.SYSTEM_IMAGE_SITE, "sys-img-site");
	}

	public RepoSitesEditorV1_2(String url, InputStream inputStreamXmlContent) throws IOException, DocumentException {
		this(url, inputStreamXmlContent, Charset.forName("UTF-8"));
	}

	public RepoSitesEditorV1_2(String url, InputStream inputStreamXmlContent, Charset charset) throws IOException, DocumentException {
		mStrXmlDirUrl = UrlTextUtil.getDir(url);
		mDocument = DocumentHelper.parseText(IOUtils.toString(inputStreamXmlContent, charset));
	}

	@Override
	public List<RepoSite> extractAll() {
		List<RepoSite> lListRepoSites = new LinkedList<>();
		List<Element> lListElements = mDocument.getRootElement().elements();
		Namespace lNamespaceSdk = createXmlNamespace_sdk();
		final QName lQNameName = new QName("name", lNamespaceSdk);
		final QName lQNameUrl = new QName("url", lNamespaceSdk);
		for (Element lElementSite : lListElements) {
			RepoSite lRepoSite = new RepoSite.Builder().sourceUrl(mStrXmlDirUrl)
					.type(mBiMapRepoSiteTypeToXmlText.inverse().getOrDefault(lElementSite.getName(), REPO_SITE_TYPE_DEFAULT))
					.displayName(lElementSite.elementText(lQNameName))
					.url(lElementSite.elementText(lQNameUrl)).build();
			lListRepoSites.add(lRepoSite);
		}
		return lListRepoSites;
	}

	@Override
	public void rebuild(List<RepoSite> listRepoSite) {
		Element lElementRoot = mDocument.getRootElement();
		lElementRoot.elements().clear();
		for (RepoSite repoSite : listRepoSite) {
			Element lElementSite = lElementRoot.addElement(new QName(
					mBiMapRepoSiteTypeToXmlText.getOrDefault(repoSite.getType(), REPO_SITE_TYPE_DEFAULT_STRING),
					createXmlNamespace_sdk()
			));
			lElementSite.addElement(new QName("name", createXmlNamespace_sdk())).setText(repoSite.getDisplayName());
			lElementSite.addElement(new QName("url", createXmlNamespace_sdk())).setText(repoSite.getUrl());
		}
	}

	@Override
	public void write(OutputStream out) throws IOException {
		XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
		writer.write(mDocument);
	}

	private Namespace createXmlNamespace_sdk() {
		return mDocument.getRootElement().getNamespaceForPrefix("sdk");
	}

	public static void main(String[] args) throws Exception {
		File lFile = new File("./out/artifacts/AndroidSDKLiteServer_war_exploded/DataRepository/xml/xml_20160916_orig/addons_list-2.xml");
		InputStream inputStream = new BufferedInputStream(new FileInputStream(lFile));
		RepoSitesEditorV1_2 lEditor = new RepoSitesEditorV1_2("https://dl.google.com/android/repository/addons_list-2.xml", inputStream);
		IOUtils.closeQuietly(inputStream);
		List<RepoSite> lListRepoSites = new ArrayList<>();
		lListRepoSites.add(new RepoSite.Builder()
				.sourceUrl("https://dl.google.com/android/repository/addon-test.xml")
				.type(RepoSiteType.ADDON_SITE)
				.displayName("addon test")
				.url("addon-test.xml").build()
		);
		lListRepoSites.add(new RepoSite.Builder()
				.sourceUrl("https://dl.google.com/android/repository/sysimg-test.xml")
				.type(RepoSiteType.SYSTEM_IMAGE_SITE)
				.displayName("sysimg test")
				.url("sysimg-test.xml").build()
		);
		lEditor.rebuild(lListRepoSites);
		System.out.println(lEditor.mDocument.asXML());
	}
}
