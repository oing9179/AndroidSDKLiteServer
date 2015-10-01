package oing.webapp.android.sdkliteserver.service.impl;

import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ZipRepositoryListServiceImpl implements ZipRepositoryListService {
	@Autowired
	private RepoZipDao repoZipDao;

	public List<RepoZip> getAll() {
		return repoZipDao.selectAll();
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
		lRepoZip.setDateCreate(new Date());
		lRepoZip.setDateLastModified(lRepoZip.getDateCreate());
		repoZipDao.insert(lRepoZip);
		// Create a folder
		ConfigurationUtil.getZipRepositoryDir(lRepoZip.getName()).mkdirs();
	}
}
