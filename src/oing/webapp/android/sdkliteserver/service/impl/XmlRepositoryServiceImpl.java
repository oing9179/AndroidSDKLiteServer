package oing.webapp.android.sdkliteserver.service.impl;

import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.misc.ConfigurationUtil;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryService;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Service
public class XmlRepositoryServiceImpl implements XmlRepositoryService {
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
		return repoXmlDao.selectById(id);
	}

	@Override
	public void repositoryCreate_step1(String name, Long createFrom) throws IOException {
		// Check if source RepoXml(aka createFrom) does not found in database.
		RepoXml lRepoXmlSrc;
		if (createFrom != null) {
			lRepoXmlSrc = repoXmlDao.selectById(createFrom);
			Validate.notNull(lRepoXmlSrc, "The source xml repository were not found.");
		}

		// 1. Add record to table
		if (repoXmlDao.existsByName(name)) {
			throw new IllegalArgumentException("Repository name already exist.");
		}
		RepoXml lRepoXmlDst = new RepoXml();// In the next, it will be a destination xml repository in file system.
		lRepoXmlDst.setName(name);
		lRepoXmlDst.setDateCreate(new Date());
		repoXmlDao.insert(lRepoXmlDst);
		// 2. Copy data from a table to another table.
		// Find out what I've insert, it should always not-null.
		if (createFrom != null) {
			lRepoXmlDst = repoXmlDao.selectByName(lRepoXmlDst.getName());
			repoXmlDao.copyExistingRecordsForNewXmlRepo(createFrom, lRepoXmlDst.getId());
		}
		// 3. Create a folder that stores xml files
		new File(ConfigurationUtil.getDataRepositoryDir(), "/xml/" + lRepoXmlDst.getName()).mkdirs();
	}

	@Override
	public void repositoryCreate_step2(String name, Long createFrom) throws IOException {
		if (createFrom == null) return;
		// 4. Copy folder from existing repository to new repository.
		RepoXml lRepoXmlSrc = repoXmlDao.selectById(createFrom);
		RepoXml lRepoXmlDst = repoXmlDao.selectByName(name);
		File lFileDirDataRepository, lFileDirRepoSrc, lFileDirRepoDst;
		lFileDirDataRepository = ConfigurationUtil.getDataRepositoryDir();
		lFileDirRepoSrc = new File(lFileDirDataRepository, "/xml/" + lRepoXmlSrc.getName());
		lFileDirRepoDst = new File(lFileDirDataRepository, "/xml/" + lRepoXmlDst.getName());
		// Do copy folder
		lFileDirRepoDst.mkdirs();
		FileUtil.copyDir(lFileDirRepoSrc, lFileDirRepoDst);
	}

	@Override
	public void repositoryDelete(Long id, String name) throws IOException {
		RepoXml lRepoXml = repoXmlDao.selectById(id);
		// 1. If repositoryName have no matches in database, reject.
		if (!lRepoXml.getName().equals(name)) {
			String lStrMessage;
			lStrMessage = MessageFormat.format("The repository name does not match, desired: {0}, give: {1}", lRepoXml.getName(), name);
			throw new IllegalArgumentException(lStrMessage);
		}
		// 2. Delete record from table xml_repo_file.
		repoXmlFileDao.deleteDependsRepoXmlId(lRepoXml.getId());
		// 3. Clear dependency for zip repository who depends on this.
		repoZipDao.updateClearDependencyFromRepoXml(lRepoXml.getId());
		// 4. Delete files from this repository.
		File lFileDirTargetXmlRepo = new File(ConfigurationUtil.getDataRepositoryDir(), "/xml/" + lRepoXml.getName());
		FileUtil.deleteDir(lFileDirTargetXmlRepo);
		// 5. Delete record of xml_repo.
		repoXmlDao.deleteById(lRepoXml.getId());
	}
}
