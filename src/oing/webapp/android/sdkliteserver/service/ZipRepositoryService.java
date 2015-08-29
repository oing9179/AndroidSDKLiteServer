package oing.webapp.android.sdkliteserver.service;

import oing.webapp.android.sdkliteserver.model.RepoZip;

import java.util.List;

public interface ZipRepositoryService {
	/**
	 * Select from repo_zip where id_repo_xml=repo_xml.id
	 */
	List<RepoZip> getDependsRepoXmlId(Long idRepoXml);
}
