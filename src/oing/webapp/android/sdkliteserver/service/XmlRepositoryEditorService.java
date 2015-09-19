package oing.webapp.android.sdkliteserver.service;

import jodd.http.ProxyInfo;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;

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
}
