package oing.webapp.android.sdkliteserver.service.impl;

import jodd.http.*;
import jodd.http.net.SocketHttpConnectionProvider;
import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.service.AutomaticAdditionEventListener;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryEditorService;
import oing.webapp.android.sdkliteserver.utils.AddonsListXmlParser;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlRepositoryEditorServiceImpl implements XmlRepositoryEditorService {
	@Autowired
	private RepoXmlDao repoXmlDao;
	@Autowired
	private RepoXmlFileDao repoXmlFileDao;

	@Override
	public RepoXml getByName(String name) throws IllegalArgumentException {
		return getRepoXmlByNameOrThrow(name);
	}

	@Override
	public List<RepoXmlFile> getFilesByRepoXmlId(Long id) {
		return repoXmlFileDao.selectDependsRepoXmlId(id);
	}

	@Override
	public void automaticAddition(String repositoryName, boolean isPreferHttpsConnection, ProxyInfo proxyInfo,
								  AutomaticAdditionEventListener listener) throws Exception {
		String URL_ADDONS_LIST_XML = ConfigurationUtil.get("url.addons_list_2_xml");
		final String lStrBaseUrl = URL_ADDONS_LIST_XML.substring(0, URL_ADDONS_LIST_XML.lastIndexOf('/'));
		ArrayList<String> lListStrUrls = new ArrayList<>();
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);

		// Disable HTTPS connection if proxy type is socks4 or socks5, cuz JoddHttp doesn't support it.
		switch (proxyInfo.getProxyType()) {
			case SOCKS4:
			case SOCKS5:
				isPreferHttpsConnection = false;
				break;
		}
		if (isPreferHttpsConnection) {
			URL_ADDONS_LIST_XML = UrlTextUtil.http2https(URL_ADDONS_LIST_XML);
		} else {
			URL_ADDONS_LIST_XML = UrlTextUtil.https2http(URL_ADDONS_LIST_XML);
		}
		// 1. Fetch addons_list-2.xml and parse it.
		{
			listener.onPublish(0, "Fetching " + URL_ADDONS_LIST_XML);
			HttpResponse lHttpResponse = HttpRequest.get(URL_ADDONS_LIST_XML)
					.open(createHttpConnectionProvider(proxyInfo)).send();
			if (lHttpResponse.bodyText().length() == 0) {
				throw new HttpException("Remote resource not found: " + URL_ADDONS_LIST_XML);
			}
			// Save RepoXmlFile to database
			RepoXmlFile lRepoXmlFile = new RepoXmlFile();
			lRepoXmlFile.setIdRepoXml(lRepoXml.getId());
			lRepoXmlFile.setFileName(UrlTextUtil.getFileName(URL_ADDONS_LIST_XML));
			lRepoXmlFile.setUrl(URL_ADDONS_LIST_XML);
			repoXmlFileDao.insert(lRepoXmlFile);
			// Save addons_list-2.xml to repository(aka hard disk).
			File lFileAddonsListXml = new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName),
					"/" + lRepoXmlFile.getFileName());
			FileUtil.writeString(lFileAddonsListXml, lHttpResponse.bodyText());
			listener.onPublish(0, "Saved " + lRepoXmlFile.getFileName());
			// Parse addons_list-2.xml
			List<String> lListStrXmlUrls = new AddonsListXmlParser(
					URL_ADDONS_LIST_XML.substring(0, URL_ADDONS_LIST_XML.lastIndexOf('/')),
					lFileAddonsListXml).getURLs();
			for (int i = 0, size = lListStrXmlUrls.size(); i < size; i++) {
				String lStrUrl = lListStrXmlUrls.get(i);
				// Prepend BaseURL before it.
				if (!lStrUrl.startsWith("http://") && !lStrUrl.startsWith("https://")) {
					lStrUrl = UrlTextUtil.concat(lStrBaseUrl, lStrUrl);
				}
				lListStrUrls.add(lStrUrl);
				// Update XML URLs for addons_list-2.xml, so it will downloadable after deployment.
				lStrUrl = lListStrXmlUrls.get(i);
				// Prepend index to prevent file name duplication.
				/**
				 * i + 1: See: NOTE_0 in Step3.
				 */
				lStrUrl = i + "_" + lStrUrl.substring(lStrUrl.lastIndexOf('/') + 1);
				lListStrXmlUrls.set(i, lStrUrl);
			}
			// Update XML URLs for addons_list-2.xml, so it will downloadable after deployment.
			{
				// Commit changes to addons_list-2.xml
				OutputStreamWriter lWriter = null;
				AddonsListXmlParser lParser = new AddonsListXmlParser(lStrBaseUrl, lFileAddonsListXml);
				lParser.updateURLs(lListStrXmlUrls);
				try {
					lWriter = new OutputStreamWriter(new FileOutputStream(lFileAddonsListXml));
					lParser.getDocument().write(lWriter);
				} catch (IOException e) {
					throw e;
				} finally {
					// Java do "finally" first before rethrow.
					IOUtils.closeQuietly(lWriter);
				}
			}
			listener.onPublish(0, lRepoXmlFile.getFileName() + " updated.");
		}
		// Step 2: Fetch repository-11.xml and save it.
		// Boring code, this is quite duplicated from Step3.
		{
			String lStrUrl = ConfigurationUtil.get("url.repository_11_xml");
			listener.onPublish(0, "Fetching " + lStrUrl);
			lStrUrl = isPreferHttpsConnection ? UrlTextUtil.http2https(lStrUrl) : UrlTextUtil.https2http(lStrUrl);
			HttpResponse lHttpResponse = HttpRequest.get(lStrUrl)
					.open(createHttpConnectionProvider(proxyInfo)).send();
			String lStrResponse = lHttpResponse.bodyText();
			if (lStrResponse.length() != 0) {
				RepoXmlFile lRepoXmlFile = new RepoXmlFile();
				lRepoXmlFile.setIdRepoXml(lRepoXml.getId());
				lRepoXmlFile.setFileName(UrlTextUtil.getFileName(lStrUrl));
				lRepoXmlFile.setUrl(lStrUrl);
				repoXmlFileDao.insert(lRepoXmlFile);
				File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName),
						"/" + lRepoXmlFile.getFileName());
				FileUtil.writeString(lFileXml, lStrResponse);
				listener.onPublish(0, "Saved " + lFileXml.getName());
			} else {
				listener.onPublish(0, "Remote resource not found: " + lStrUrl);
			}
		}
		// Step 3. Download all XMLs which parsed from addons_list-2.xml
		for (int i = 0, size = lListStrUrls.size(); i < size; i++) {
			float lnProgress = 1.0F * i / size;
			String lStrUrl = lListStrUrls.get(i);

			listener.onPublish(lnProgress, "Fetching " + lStrUrl);
			lStrUrl = isPreferHttpsConnection ? UrlTextUtil.http2https(lStrUrl) : UrlTextUtil.https2http(lStrUrl);
			lListStrUrls.set(i, lStrUrl);
			HttpResponse lHttpResponse = HttpRequest.get(lStrUrl).open(createHttpConnectionProvider(proxyInfo)).send();
			String lStrResponse = lHttpResponse.bodyText();
			if (lStrResponse.length() != 0) {
				RepoXmlFile lRepoXmlFile = new RepoXmlFile();
				lRepoXmlFile.setIdRepoXml(lRepoXml.getId());
				/**
				 * NOTE_0: File rename.
				 * File will be renamed to prevent name duplication, here is some file name after rename:
				 * 	0_addon.xml
				 * 	1_addon-6.xml
				 * 	etc.
				 * 	So the first file from addons_list-2.xml(aka addon.xml) will be rename to 0_addon.xml .
				 */
				lRepoXmlFile.setFileName(i + "_" + UrlTextUtil.getFileName(lStrUrl));
				lRepoXmlFile.setUrl(lStrUrl);
				repoXmlFileDao.insert(lRepoXmlFile);
				File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName),
						"/" + lRepoXmlFile.getFileName());
				FileUtil.writeString(lFileXml, lStrResponse);
				listener.onPublish(lnProgress, "Saved " + lFileXml.getName());
			} else {
				listener.onPublish(lnProgress, "Remote resource not found: " + lStrUrl);
			}
		}
		listener.onPublish(1, "Everything is completed.");
	}

	private RepoXml getRepoXmlByNameOrThrow(String repositoryName) {
		RepoXml lRepoXml = repoXmlDao.selectByName(repositoryName);
		if (lRepoXml != null) return lRepoXml;
		throw new IllegalArgumentException("XML repository not found: " + repositoryName);
	}

	private HttpConnectionProvider createHttpConnectionProvider(ProxyInfo proxyInfo) {
		if (proxyInfo == null) proxyInfo = ProxyInfo.directProxy();
		SocketHttpConnectionProvider provider = new SocketHttpConnectionProvider();
		provider.useProxy(proxyInfo);
		return provider;
	}
}
