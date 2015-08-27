package oing.webapp.android.sdkliteserver.service.impl;

import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.misc.ConfigurationUtil;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryService;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class XmlRepositoryServiceImpl implements XmlRepositoryService {
	@Autowired
	private RepoXmlDao repoXmlDao;

	@Override
	public List<RepoXml> getAll() {
		return repoXmlDao.selectAll();
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
			repoXmlDao.copyExistingXmlFilesIntoExistingXmlRepo(createFrom, lRepoXmlDst.getId());
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
}
