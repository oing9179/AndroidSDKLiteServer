package oing.webapp.android.sdkliteserver.dao;

import oing.webapp.android.sdkliteserver.model.RepoZip;

import java.util.List;

public interface RepoZipDao {
	/**
	 * Select all from repo_zip
	 */
	List<RepoZip> selectAll();

	/**
	 * Select from repo_zip by id
	 */
	RepoZip selectById(Long id);

	/**
	 * Select from repo_zip by name
	 */
	RepoZip selectByName(String name);

	/**
	 * Select from repo_zip where id_repo_xml=repo_xml.id
	 */
	List<RepoZip> selectDependsOnRepoXmlId(Long idRepoXml);

	/**
	 * Add a RepoZip to database
	 */
	int insert(RepoZip repoZip);

	/**
	 * Update RepoZip by id
	 */
	int updateById(RepoZip repoZip);

	/**
	 * Clear dependency from repo_xml, zip repositories that updated will depends nothing.
	 *
	 * @param idRepoXml The repo_xml.id
	 * @return Number of rows affected.
	 */
	int updateClearDependencyFromRepoXml(Long idRepoXml);

	/**
	 * Delete a record by id
	 */
	int deleteById(Long id);
}
