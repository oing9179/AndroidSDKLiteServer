package oing.webapp.android.sdkliteserver.service.impl;

import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class ZipRepositoryListServiceImpl implements ZipRepositoryListService {
	@Autowired
	private RepoZipDao repoZipDao;

	public List<RepoZip> getAll() {
		return repoZipDao.selectAll();
	}

	public RepoZip getById(Long id) {
		RepoZip lRepoZip = repoZipDao.selectById(id);
		Validate.notNull(lRepoZip, "Repository not found: id=" + id);
		return lRepoZip;
	}

	@Override
	public RepoZip getByName(String name) {
		RepoZip lRepoZip = repoZipDao.selectByName(name);
		Validate.notNull(lRepoZip, "Repository not found: " + name);
		return lRepoZip;
	}

	@Override
	public List<RepoZip> getDependsRepoXmlId(Long idRepoXml) {
		return repoZipDao.selectDependsOnRepoXmlId(idRepoXml);
	}

	@Override
	public void create(String name) {
		// create database record
		RepoZip lRepoZip = new RepoZip();
		lRepoZip.setName(name);
		lRepoZip.setDateCreation(new Date());
		repoZipDao.insert(lRepoZip);
		// Create a folder
		ConfigurationUtil.getZipRepositoryDir(lRepoZip.getName()).mkdirs();
	}

	@Override
	public void delete(Long id, String name) throws IOException {
		RepoZip lRepoZip = getById(id);
		Validate.isTrue(lRepoZip.getName().equals(name),
				"Repository name doesn't match, desired: " + lRepoZip.getName() + ", give: " + name + ".");
		repoZipDao.deleteById(lRepoZip.getId());
		FileUtil.deleteDir(ConfigurationUtil.getZipRepositoryDir(lRepoZip.getName()));
	}
}
