package oing.webapp.android.sdkliteserver.service;

import jodd.http.ProxyInfo;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RemotePackage;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RepoSite;
import org.dom4j.DocumentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface XmlRepositoryEditorService {
	/**
	 * Get RepoXmlFile by id depends repo_xml.id.
	 *
	 * @param id        ID of repo_xml_file
	 * @param repoXmlId ID of repo_xml
	 */
	RepoXmlFile getById(Long id, Long repoXmlId);

	/**
	 * Find RepoXmlFile by its file name depends repo_xml.id.
	 * @param fileName Its file name.
	 * @param repoXmlId ID of RepoXml.
	 */
	RepoXmlFile getByName(String fileName, Long repoXmlId) throws FileNotFoundException;

	/**
	 * Get all files from specific repository
	 *
	 * @param id The id of repository
	 */
	List<RepoXmlFile> getByRepoXmlId(Long id);

	/**
	 * Fill a xml repository automatically.
	 *
	 * @param repositoryName          Repository name
	 * @param isPreferHttpsConnection If true, all urls will change to HTTPS except proxy type is socks(4 or 5).
	 * @param proxyInfo               Use proxy to access web resources.
	 * @param listener                The event listener let controller knows how many jobs completed in this service.
	 */
	void automaticAddition(String repositoryName, boolean isPreferHttpsConnection, ProxyInfo proxyInfo,
	                       String[] xmlDownloadUrls, AutomaticAdditionEventListener listener) throws Exception;

	/**
	 * Fill(or update) a xml repository manually.
	 *
	 * @param repositoryName Repository name
	 * @param multipartFiles XML file user uploaded. File will be replaced if a file exist in database,
	 *                       otherwise create a new record in database.
	 * @param urls           Where are these xml files comes from.
	 */
	void manualAddition(String repositoryName, MultipartFile[] multipartFiles, String[] urls) throws IOException;

	/**
	 * Delete a xml file from repository
	 *
	 * @param repositoryName Repository name where xml file lives in
	 * @param id             Database repo_xml_file.id
	 * @param name           XML file name
	 */
	void delete(String repositoryName, Long id, String name) throws IOException;

	/**
	 * Parse "addons_list-3.xml" from a specific repository, then return all of the {@link RepoSite}s.
	 *
	 * @param repositoryName XML repository name where xml file lives in.
	 * @param id             XML file id in database, we will find xml file only from {@code repositoryName}.
	 */
	List<RepoSite> getRepoSitesById(String repositoryName, Long id) throws IOException, DocumentException;

	/**
	 * Parse a xml file from specific repository, then return all of the {@link RemotePackage}.
	 *
	 * @param repositoryName XML repository name where xml file lives in.
	 * @param id             XML file id in database, we will find xml file only from {@code repositoryName}.
	 */
	List<RemotePackage> getRemotePackagesById(String repositoryName, Long id) throws IOException, DocumentException;

	/**
	 * Update URLs to "addons_list-3.xml" specially.
	 * NOTE: All elements from "addons_list-3.xml" will REMOVED, then fill {@code repoSites} into xml file.
	 *
	 * @param repositoryName XML repository name where xml file lives in.
	 * @param id             XML file id in database, we will find xml file only from {@code repositoryName}.
	 * @param repoSites      The {@link RepoSite}s will replace into "addons_list-2.xml".
	 */
	void updateRepoSite(String repositoryName, Long id, List<RepoSite> repoSites) throws IOException, DocumentException;

	/**
	 * Update URLs to xml file
	 *
	 * @param repositoryName XML repository name where xml file lives in.
	 * @param id             XML file id in database from given {@code repositoryName}.
	 * @param urls           URLs that will update into xml file.
	 */
	void updateRepoCommon(String repositoryName, Long id, String zipSubDirectory, String[] urls) throws IOException, DocumentException;
}
