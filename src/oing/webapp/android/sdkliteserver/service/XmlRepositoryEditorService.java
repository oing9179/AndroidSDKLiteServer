package oing.webapp.android.sdkliteserver.service;

import jodd.http.ProxyInfo;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface XmlRepositoryEditorService {
	/**
	 * Select from repo_xml by name
	 *
	 * @throws IllegalArgumentException
	 */
	RepoXml getByName(String name) throws IllegalArgumentException;

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
}
