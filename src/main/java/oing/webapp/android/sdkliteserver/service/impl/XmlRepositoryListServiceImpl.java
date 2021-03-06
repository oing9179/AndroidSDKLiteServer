package oing.webapp.android.sdkliteserver.service.impl;

import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryListService;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class XmlRepositoryListServiceImpl implements XmlRepositoryListService {
	@Autowired
	private RepoXmlDao repoXmlDao;
	@Autowired
	private RepoXmlFileDao repoXmlFileDao;
	@Autowired
	private RepoZipDao repoZipDao;

	@Override
	public List<RepoXml> getAll() {
		return repoXmlDao.selectAll();
	}

	@Override
	public RepoXml getById(Long id) {
		RepoXml lRepoXml = repoXmlDao.selectById(id);
		Validate.notNull(lRepoXml, "Repository not found: id=" + id);
		return lRepoXml;
	}

	@Override
	public RepoXml getByName(String name) {
		RepoXml lRepoXml = repoXmlDao.selectByName(name);
		Validate.notNull(lRepoXml, "Repository not found: " + name);
		return lRepoXml;
	}

	@Override
	public void create(String name, Long createFrom) throws IOException {
		RepoXml lRepoXml_new, lRepoXml_existed = null;
		File lFileDirNewRepo, lFileDirExistedRepo;
		// Reject if new repository name already exist.
		Validate.isTrue(!repoXmlDao.existsByName(name), "Name of new repository already exist: " + name);
		// Check if source RepoXml(aka createFrom) does not found in database.
		if (createFrom != null) {
			lRepoXml_existed = repoXmlDao.selectById(createFrom);
			Validate.notNull(lRepoXml_existed, "The source xml repository were not found.");
		}

		// 1. Add record for new xml repository to table
		lRepoXml_new = new RepoXml();
		lRepoXml_new.setName(name);
		lRepoXml_new.setDateCreation(new Date());
		repoXmlDao.insert(lRepoXml_new);
		// 2. Copy data from a table to another table.
		if (lRepoXml_existed != null) {
			// If source repository exists, do copy data.
			lRepoXml_new = repoXmlDao.selectByName(lRepoXml_new.getName());
			repoXmlFileDao.copyExistingRecordsForNewXmlRepo(createFrom, lRepoXml_new.getId());
		}
		// 3. Create a folder for new repository.
		lFileDirNewRepo = ConfigurationUtil.getXmlRepositoryDir(lRepoXml_new.getName());
		lFileDirNewRepo.mkdirs();
		// 4. Copy folder content from existing repository to new repository.
		if (lRepoXml_existed != null) {
			// Assign variable "lFileDirNewRepo" is not needed cause it was already assigned.
			lFileDirExistedRepo = ConfigurationUtil.getXmlRepositoryDir(lRepoXml_existed.getName());
			FileUtil.copyDir(lFileDirExistedRepo, lFileDirNewRepo);
		}
	}

	@Override
	public void delete(Long id, String name) throws IOException {
		RepoXml lRepoXml = getById(id);
		// 1. If repositoryName have no matches in database, reject.
		Validate.isTrue(lRepoXml.getName().equals(name),
				"Repository name doesn't match, desired: " + lRepoXml.getName() + ", give: " + name + ".");
		// 2. Delete record from table xml_repo_file.
		repoXmlFileDao.deleteDependsRepoXmlId(lRepoXml.getId());
		// 3. Clear dependency for zip repository who depends on this.
		repoZipDao.updateClearDependencyFromRepoXml(lRepoXml.getId());
		// 4. Delete files from this repository.
		File lFileDirTargetXmlRepo = ConfigurationUtil.getXmlRepositoryDir(lRepoXml.getName());
		try {
			FileUtil.deleteDir(lFileDirTargetXmlRepo);
		} catch (FileNotFoundException ignore) {
		}
		// 5. Delete record of xml_repo.
		repoXmlDao.deleteById(lRepoXml.getId());
	}
}
