package oing.webapp.android.sdkliteserver.dao;

import oing.webapp.android.sdkliteserver.model.RepoXmlFile;

import java.util.List;

public interface RepoXmlFileDao {
	/**
	 * Get RepoXmlFile by id depends repo_xml.id.
	 *
	 * @param id ID for repo_xml_file
	 * @param repoXmlId ID for repo_xml
	 */
	RepoXmlFile selectByIdDependsRepoXmlId(Long id, Long repoXmlId);

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
	 * Copy existing records for new xml repository in table "repo_xml_file"
	 * copyExistingRecordsForNewXmlRepo
	 *
	 * @param fromId The source xml repository id.
	 * @param toId   The destination xml repository id.
	 */
	int copyExistingRecordsForNewXmlRepo(Long fromId, Long toId);

	/**
	 * Delete from repo_xml_file where id_repo_xml=repo_xml.id
	 */
	int deleteDependsRepoXmlId(Long idRepoXml);

	/**
	 * Delete from repo_xml_file by id
	 */
	int deleteById(Long id);
}
