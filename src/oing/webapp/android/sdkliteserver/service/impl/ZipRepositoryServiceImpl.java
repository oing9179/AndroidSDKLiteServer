package oing.webapp.android.sdkliteserver.service.impl;

import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZipRepositoryServiceImpl implements ZipRepositoryService {
	@Autowired
	private RepoZipDao repoZipDao;

	@Override
	public List<RepoZip> getDependsRepoXmlId(Long idRepoXml) {
		return repoZipDao.selectDependsOnRepoXmlId(idRepoXml);
	}
}
