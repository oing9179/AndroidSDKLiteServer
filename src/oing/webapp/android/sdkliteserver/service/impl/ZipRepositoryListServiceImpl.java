package oing.webapp.android.sdkliteserver.service.impl;

import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.model.SdkArchive;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import oing.webapp.android.sdkliteserver.utils.RepositoryXmlParser;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class ZipRepositoryListServiceImpl implements ZipRepositoryListService {
	private static final Logger mLogger = LoggerFactory.getLogger(ZipRepositoryListServiceImpl.class);
	@Autowired
	private RepoZipDao repoZipDao;
	@Autowired
	private RepoXmlDao repoXmlDao;
	@Autowired
	private RepoXmlFileDao repoXmlFileDao;

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

	public void updateRepositoryDependency(String repositoryName, Long targetRepoId) {
		RepoZip lRepoZip = getByName(repositoryName);
		lRepoZip.setIdRepoXml(targetRepoId);
		repoZipDao.updateById(lRepoZip);
	}

	@Override
	public void delete(Long id, String name) throws IOException {
		RepoZip lRepoZip = getById(id);
		Validate.isTrue(lRepoZip.getName().equals(name),
				"Repository name doesn't match, desired: " + lRepoZip.getName() + ", give: " + name + ".");
		repoZipDao.deleteById(lRepoZip.getId());
		FileUtil.deleteDir(ConfigurationUtil.getZipRepositoryDir(lRepoZip.getName()));
	}

	@Override
	public List<SdkArchive> getAllSdkArchiveInfo(String repositoryName, boolean includeSysLinux, boolean includeSysMacOSX,
	                                             boolean includeSysWin, boolean includeObsoleteArchives) {
		RepoZip lRepoZip = repoZipDao.selectByName(repositoryName);
		RepoXml lRepoXml = repoXmlDao.selectById(lRepoZip.getIdRepoXml());
		File lFileXmlRepo = ConfigurationUtil.getXmlRepositoryDir(lRepoXml.getName());
		List<RepoXmlFile> lListRepoXmlFiles = repoXmlFileDao.selectDependsRepoXmlId(lRepoXml.getId());
		List<SdkArchive> lListSdkArchives = new ArrayList<>();

		try {
			for (RepoXmlFile item : lListRepoXmlFiles) {
				if (item.getFileName().startsWith("addons_list")) continue;
				RepositoryXmlParser repositoryXmlParser = new RepositoryXmlParser(item.getUrl(), new File(lFileXmlRepo, item.getFileName()));
				lListSdkArchives.addAll(repositoryXmlParser.getSdkArchives(true, true));
			}
		} catch (Exception e) {
			mLogger.warn(e.toString(), e);
			e.printStackTrace();
		}
		// Do filter
		for (int i = 0; i < lListSdkArchives.size(); i++) {
			SdkArchive sdkArchive = lListSdkArchives.get(i);
			String lStrHostOs = sdkArchive.getHostOs();
			if (lStrHostOs != null && ((!includeSysLinux && lStrHostOs.equalsIgnoreCase("linux")) ||
					(!includeSysMacOSX && lStrHostOs.equalsIgnoreCase("macosx")) ||
					(!includeSysWin && lStrHostOs.equalsIgnoreCase("windows")))) {
				lListSdkArchives.remove(i);
				i--;
				continue;
			}
			if (!includeObsoleteArchives && sdkArchive.isObsolete()) {
				lListSdkArchives.remove(i);
				i--;
			}
		}
		return lListSdkArchives;
	}
}
