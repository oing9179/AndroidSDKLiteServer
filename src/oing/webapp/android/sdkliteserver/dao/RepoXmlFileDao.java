package oing.webapp.android.sdkliteserver.dao;

import oing.webapp.android.sdkliteserver.model.RepoXmlFile;

import java.util.List;

public interface RepoXmlFileDao {
	/**
	 * Select from repo_xml_file by name
	 */
	RepoXmlFile selectByFileName(String name);

	/**
	 * Select from repo_xml_file depends on repo_xml.id
	 */
	List<RepoXmlFile> selectDependsRepoXmlId(Long idRepoXml);

	/**
	 * Add a RepoXmlFile to database.
	 */
	int insert(RepoXmlFile repoXmlFile);

	/**
	 * Insert or Update a repo_xml_file
	 */
	int insertOrUpdate(RepoXmlFile repoXmlFile);

	/**
	 * Delete from repo_xml_file where id_repo_xml=repo_xml.id
	 */
	int deleteDependsRepoXmlId(Long idRepoXml);
}
