package oing.webapp.android.sdkliteserver.service.impl;

import jodd.http.*;
import jodd.http.net.SocketHttpConnectionProvider;
import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.model.SdkAddonSite;
import oing.webapp.android.sdkliteserver.model.SdkArchive;
import oing.webapp.android.sdkliteserver.service.AutomaticAdditionEventListener;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryEditorService;
import oing.webapp.android.sdkliteserver.utils.AddonsListXmlEditor;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import oing.webapp.android.sdkliteserver.utils.RepositoryXmlEditor;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import org.apache.commons.lang3.Validate;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class XmlRepositoryEditorServiceImpl implements XmlRepositoryEditorService {
	@Autowired
	private RepoXmlDao repoXmlDao;
	@Autowired
	private RepoXmlFileDao repoXmlFileDao;

	@Override
	public RepoXmlFile getByIdDependsRepoXmlId(Long id, Long repoXmlId) {
		RepoXmlFile lRepoXmlFile = repoXmlFileDao.selectByIdDependsRepoXmlId(id, repoXmlId);
		if (lRepoXmlFile != null) return lRepoXmlFile;
		throw new IllegalArgumentException("XML file not found: id=" + id);
	}

	@Override
	public List<RepoXmlFile> getFilesByRepoXmlId(Long id) {
		return repoXmlFileDao.selectDependsRepoXmlId(id);
	}

	@Override
	public void automaticAddition(String repositoryName, boolean isPreferHttpsConnection, ProxyInfo proxyInfo,
	                              AutomaticAdditionEventListener listener) throws Exception {
		String URL_ADDONS_LIST_XML = ConfigurationUtil.get("url.repo_sites");
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
			AddonsListXmlEditor lAddonsListXmlEditor = new AddonsListXmlEditor(
					URL_ADDONS_LIST_XML.substring(0, URL_ADDONS_LIST_XML.lastIndexOf('/')), lFileAddonsListXml);
			List<SdkAddonSite> lListSdkAddonSites = lAddonsListXmlEditor.getAll(true, isPreferHttpsConnection);
			for (int i = 0, size = lListSdkAddonSites.size(); i < size; i++) {
				SdkAddonSite lSdkAddonSite = lListSdkAddonSites.get(i);
				lListStrUrls.add(lSdkAddonSite.getUrl());
				// Prepend index to prevent file name duplication.
				/*
				 * i + 1: See: NOTE_0 in Step3.
				 */
				lSdkAddonSite.setUrl(i + "_" + UrlTextUtil.getFileName(lSdkAddonSite.getUrl()));
			}
			// Save addons_list-2.xml back to storage, so URLs inside that xml will accessible after deployment.
			lAddonsListXmlEditor.replaceAll(lListSdkAddonSites);
			lAddonsListXmlEditor.write();
			listener.onPublish(0, lRepoXmlFile.getFileName() + " updated.");
		}
		// Step 2: Fetch repository-11.xml and save it.
		// Boring code, this is quite duplicated from Step3.
		{
			String lStrUrl = ConfigurationUtil.get("url.repo_common");
			lStrUrl = isPreferHttpsConnection ? UrlTextUtil.http2https(lStrUrl) : UrlTextUtil.https2http(lStrUrl);
			listener.onPublish(0, "Fetching " + lStrUrl);
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

			lStrUrl = isPreferHttpsConnection ? UrlTextUtil.http2https(lStrUrl) : UrlTextUtil.https2http(lStrUrl);
			listener.onPublish(lnProgress, "Fetching " + lStrUrl);
			HttpResponse lHttpResponse = HttpRequest.get(lStrUrl).open(createHttpConnectionProvider(proxyInfo)).send();
			String lStrResponse = lHttpResponse.bodyText();
			if (lStrResponse.length() != 0) {
				RepoXmlFile lRepoXmlFile = new RepoXmlFile();
				lRepoXmlFile.setIdRepoXml(lRepoXml.getId());
				/*
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
		repoXmlDao.updateById(lRepoXml);//Update date of last modified automatically.
		listener.onPublish(1, "Almost done...");
		// Database will commit changes after this method finished, it will take a while.
	}

	public void manualAddition(String repositoryName, MultipartFile[] multipartFiles, String[] urls) throws IOException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);

		for (int i = 0; i < multipartFiles.length; i++) {
			MultipartFile lMultipartFile = multipartFiles[i];
			RepoXmlFile lRepoXmlFile = repoXmlFileDao
					.selectByFileNameDependsRepoXmlId(lRepoXml.getId(), lMultipartFile.getOriginalFilename());

			if (lRepoXmlFile == null) {
				lRepoXmlFile = new RepoXmlFile();
				lRepoXmlFile.setIdRepoXml(lRepoXml.getId());
				lRepoXmlFile.setFileName(lMultipartFile.getOriginalFilename());
				lRepoXmlFile.setUrl(urls[i]);
			} else {
				lRepoXmlFile.setUrl(urls[i]);
			}
			repoXmlFileDao.insertOrUpdate(lRepoXmlFile);
			File lFileTarget = new File(ConfigurationUtil.getXmlRepositoryDir(lRepoXml.getName()),
					"/" + lMultipartFile.getOriginalFilename());
			lMultipartFile.transferTo(lFileTarget);
		}
	}

	@Override
	public void delete(String repositoryName, Long id, String name) throws IOException {
		/*
		 * 1. Repository exists.
		 * 2. Found a RepoXmlFile by id.
		 * 3. Name from database equals to name from parameter.
		 * All conditions are true, then delete xml file.
		 */
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = repoXmlFileDao.selectByIdDependsRepoXmlId(id, lRepoXml.getId());
		Validate.notNull(lRepoXmlFile, "XML file not found: (id=" + id + ")" + name);
		Validate.isTrue(name.equals(lRepoXmlFile.getFileName()),
				"XML file name incorrect, desired: " + lRepoXmlFile.getFileName() + " give: " + name);
		repoXmlFileDao.deleteById(lRepoXmlFile.getId());
		FileUtil.deleteFile(new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName), lRepoXmlFile.getFileName()));
	}

	@Override
	public List<SdkAddonSite> getSdkAddonSitesById(String repositoryName, Long id) throws IOException, DocumentException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = repoXmlFileDao.selectByIdDependsRepoXmlId(id, lRepoXml.getId());
		File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName), lRepoXmlFile.getFileName());
		AddonsListXmlEditor lEditor = new AddonsListXmlEditor(lRepoXmlFile.getUrl(), lFileXml);
		return lEditor.getAll();
	}

	public List<SdkArchive> getSdkArchivesById(String repositoryName, Long id) throws IOException, DocumentException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = repoXmlFileDao.selectByIdDependsRepoXmlId(id, lRepoXml.getId());
		File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName), lRepoXmlFile.getFileName());
		RepositoryXmlEditor lParser = new RepositoryXmlEditor(lRepoXmlFile.getUrl(), lFileXml);
		return lParser.getSdkArchives();
	}

	@Override
	public void updateSdkArchiveURLs(String repositoryName, Long id, String[] urls) throws IOException, DocumentException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = repoXmlFileDao.selectByIdDependsRepoXmlId(id, lRepoXml.getId());
		File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName), lRepoXmlFile.getFileName());
		RepositoryXmlEditor lEditor = new RepositoryXmlEditor(lRepoXmlFile.getUrl(), lFileXml);
		lEditor.updateURLs(Arrays.asList(urls));
		// Save back to storage.
		lEditor.write();
	}

	@Override
	public void updateSdkAddonSiteURLs(String repositoryName, Long id, List<SdkAddonSite> sdkAddonSites) throws IOException, DocumentException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = repoXmlFileDao.selectByIdDependsRepoXmlId(id, lRepoXml.getId());
		File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName), lRepoXmlFile.getFileName());
		AddonsListXmlEditor lEditor = new AddonsListXmlEditor(lRepoXmlFile.getUrl(), lFileXml);
		lEditor.replaceAll(sdkAddonSites);
		lEditor.write();
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
