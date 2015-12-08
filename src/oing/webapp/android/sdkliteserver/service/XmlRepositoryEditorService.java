package oing.webapp.android.sdkliteserver.service;

import jodd.http.ProxyInfo;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.model.SdkArchive;
import org.dom4j.DocumentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface XmlRepositoryEditorService {
	/**
	 * Get RepoXmlFile by id depends repo_xml.id.
	 *
	 * @param id        ID for repo_xml_file
	 * @param repoXmlId ID for repo_xml
	 */
	RepoXmlFile getByIdDependsRepoXmlId(Long id, Long repoXmlId);

	/**
	 * Get all files from specific repository
	 *
	 * @param id The id of repository
	 */
	List<RepoXmlFile> getFilesByRepoXmlId(Long id);

	/**
	 * Fill a xml repository automatically.
	 *
	 * @param repositoryName          Repository name
	 * @param isPreferHttpsConnection If true, all urls will change to HTTPS except proxy type is socks(4 or 5).
	 * @param proxyInfo               Use proxy to access web resources.
	 * @param listener                The event listener let controller knows how many jobs completed in this service.
	 */
	void automaticAddition(String repositoryName, boolean isPreferHttpsConnection, ProxyInfo proxyInfo,
						   AutomaticAdditionEventListener listener) throws Exception;

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
	 * Parse a xml file from specific repository, then return all of the SdkArchives.
	 * @param repositoryName XML repository name where xml file lives in.
	 * @param id XML file id in database, we will find xml file only from {@code repositoryName}.
	 */
	List<SdkArchive> getSdkArchivesById(String repositoryName, Long id) throws IOException, DocumentException;

	/**
	 * Update URLs to xml file
	 * @param repositoryName XML repository name where xml file lives in.
	 * @param id XML file id in database, we will find xml file only from {@code repositoryName}.
	 * @param urls URLs that will update into xml file.
	 */
	void updateXmlURLs(String repositoryName, Long id, String[] urls) throws IOException, DocumentException;
}
