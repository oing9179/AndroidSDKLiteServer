package oing.webapp.android.sdkliteserver.service.impl;

import jodd.http.ProxyInfo;
import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.service.AutomaticAdditionEventListener;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryEditorService;
import oing.webapp.android.sdkliteserver.tools.autoadd.command.Command;
import oing.webapp.android.sdkliteserver.tools.autoadd.command.DownloadRepoCommonXmlCommand;
import oing.webapp.android.sdkliteserver.tools.autoadd.command.DownloadRepoSitesXmlCommand;
import oing.webapp.android.sdkliteserver.tools.autoadd.executor.CommandExecutionListener;
import oing.webapp.android.sdkliteserver.tools.autoadd.executor.CommandExecutor;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RemotePackage;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RepoSite;
import oing.webapp.android.sdkliteserver.tools.xmleditor.editor.IRepoCommonEditor;
import oing.webapp.android.sdkliteserver.tools.xmleditor.editor.IRepoSitesEditor;
import oing.webapp.android.sdkliteserver.tools.xmleditor.editor.RepoXmlEditorFactory;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class XmlRepositoryEditorServiceImpl implements XmlRepositoryEditorService {
	@Autowired
	private RepoXmlDao repoXmlDao;
	@Autowired
	private RepoXmlFileDao repoXmlFileDao;

	@Override
	public RepoXmlFile getByIdDependsRepoXmlIdOrThrow(Long id, Long repoXmlId) {
		RepoXmlFile lRepoXmlFile = repoXmlFileDao.selectByIdDependsRepoXmlId(id, repoXmlId);
		if (lRepoXmlFile != null) return lRepoXmlFile;
		throw new IllegalArgumentException("XML file not found: id=" + id);
	}

	@Override
	public List<RepoXmlFile> getFilesByRepoXmlId(Long id) {
		return repoXmlFileDao.selectDependsRepoXmlId(id);
	}

	@Override
	public void automaticAddition(String repositoryName, boolean isPreferHttpsConnection, ProxyInfo proxyInfo, AutomaticAdditionEventListener listener) throws Exception {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		List<Command> lListCommands = new ArrayList<>();

		lListCommands.add(new DownloadRepoSitesXmlCommand(repoXmlFileDao, lRepoXml, ConfigurationUtil.get("url.repo_sites_v3")));
		lListCommands.add(new DownloadRepoSitesXmlCommand(repoXmlFileDao, lRepoXml, ConfigurationUtil.get("url.repo_sites_v2")));
		lListCommands.add(new DownloadRepoCommonXmlCommand(repoXmlFileDao, lRepoXml, ConfigurationUtil.get("url.repo_common_v2")));
		lListCommands.add(new DownloadRepoCommonXmlCommand(repoXmlFileDao, lRepoXml, ConfigurationUtil.get("url.repo_common_v1")));
		new CommandExecutor(lListCommands, new CommandExecutionListenerImpl(listener)).execute();
	}

	@Override
	public void manualAddition(String repositoryName, MultipartFile[] multipartFiles, String[] urls) throws IOException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);

		for (int i = 0; i < multipartFiles.length; i++) {
			MultipartFile lMultipartFile = multipartFiles[i];
			RepoXmlFile lRepoXmlFile = repoXmlFileDao
					.selectByFileNameDependsRepoXmlId(lRepoXml.getId(), lMultipartFile.getOriginalFilename());

			if (lRepoXmlFile == null) {
				lRepoXmlFile = new RepoXmlFile();
				lRepoXmlFile.setIdRepoXml(lRepoXml.getId());
				lRepoXmlFile.setFileName(lMultipartFile.getOriginalFilename());
				lRepoXmlFile.setUrl(urls[i]);
			} else {
				lRepoXmlFile.setUrl(urls[i]);
			}
			repoXmlFileDao.insertOrUpdate(lRepoXmlFile);
			File lFileTarget = new File(ConfigurationUtil.getXmlRepositoryDir(lRepoXml.getName()),
					"/" + lMultipartFile.getOriginalFilename());
			lMultipartFile.transferTo(lFileTarget);
		}
	}

	@Override
	public void delete(String repositoryName, Long id, String name) throws IOException {
		/*
		 * 1. Repository exists.
		 * 2. Found a RepoXmlFile by id.
		 * 3. Name from database equals to name from parameter.
		 * All conditions are true, then delete xml file.
		 */
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = repoXmlFileDao.selectByIdDependsRepoXmlId(id, lRepoXml.getId());
		Validate.notNull(lRepoXmlFile, "XML file not found: (id=" + id + ")" + name);
		Validate.isTrue(name.equals(lRepoXmlFile.getFileName()),
				"XML file name incorrect, desired: " + lRepoXmlFile.getFileName() + " give: " + name);
		repoXmlFileDao.deleteById(lRepoXmlFile.getId());
		try {
			FileUtil.deleteFile(new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName), lRepoXmlFile.getFileName()));
		} catch (FileNotFoundException ignore) {
		}
	}

	@Override
	public List<RepoSite> getRepoSitesById(String repositoryName, Long id) throws IOException, DocumentException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = getByIdDependsRepoXmlIdOrThrow(id, lRepoXml.getId());
		InputStream lInputStreamXml = new BufferedInputStream(new FileInputStream(new File(
				ConfigurationUtil.getXmlRepositoryDir(repositoryName),
				lRepoXmlFile.getFileName()
		)));
		IRepoSitesEditor lEditor = RepoXmlEditorFactory.createRepoSitesEditor(lRepoXmlFile.getUrl(), lInputStreamXml);
		IOUtils.closeQuietly(lInputStreamXml);
		return lEditor.extractAll();
	}

	@Override
	public List<RemotePackage> getRemotePackagesById(String repositoryName, Long id) throws IOException, DocumentException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = getByIdDependsRepoXmlIdOrThrow(id, lRepoXml.getId());
		InputStream lInputStreamXml = new BufferedInputStream(new FileInputStream(new File(
				ConfigurationUtil.getXmlRepositoryDir(repositoryName),
				lRepoXmlFile.getFileName()
		)));
		IRepoCommonEditor lEditor = RepoXmlEditorFactory.createRepoCommonEditor(lRepoXmlFile.getUrl(), lInputStreamXml);
		IOUtils.closeQuietly(lInputStreamXml);
		return lEditor.extractAll();
	}

	@Override
	public void updateRepoSite(String repositoryName, Long id, List<RepoSite> repoSites) throws IOException, DocumentException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = getByIdDependsRepoXmlIdOrThrow(id, lRepoXml.getId());
		File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName), lRepoXmlFile.getFileName());
		// Load XML file
		InputStream lInputStreamXml = new BufferedInputStream(new FileInputStream(lFileXml));
		IRepoSitesEditor lEditor = RepoXmlEditorFactory.createRepoSitesEditor(lRepoXmlFile.getUrl(), lInputStreamXml);
		IOUtils.closeQuietly(lInputStreamXml);
		// Update XML file
		lEditor.rebuild(repoSites);
		// Save XML file
		OutputStream lOutputStreamXml = new BufferedOutputStream(new FileOutputStream(lFileXml));
		lEditor.write(lOutputStreamXml);
		IOUtils.closeQuietly(lOutputStreamXml);
	}

	@Override
	public void updateArchiveURLs(String repositoryName, Long id, String[] urls) throws IOException, DocumentException {
		RepoXml lRepoXml = getRepoXmlByNameOrThrow(repositoryName);
		RepoXmlFile lRepoXmlFile = getByIdDependsRepoXmlIdOrThrow(id, lRepoXml.getId());
		File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(repositoryName), lRepoXmlFile.getFileName());
		// Load XML file
		InputStream lInputStreamXml = new BufferedInputStream(new FileInputStream(lFileXml));
		IRepoCommonEditor lEditor = RepoXmlEditorFactory.createRepoCommonEditor(lRepoXmlFile.getUrl(), lInputStreamXml);
		IOUtils.closeQuietly(lInputStreamXml);
		// Update XML file
		lEditor.updateArchivesUrl(Arrays.asList(urls));
		// Save XML file
		OutputStream lOutputStreamXml = new BufferedOutputStream(new FileOutputStream(lFileXml));
		lEditor.write(lOutputStreamXml);
		IOUtils.closeQuietly(lOutputStreamXml);
	}

	private RepoXml getRepoXmlByNameOrThrow(String repositoryName) {
		RepoXml lRepoXml = repoXmlDao.selectByName(repositoryName);
		if (lRepoXml != null) return lRepoXml;
		throw new IllegalArgumentException("XML repository not found: " + repositoryName);
	}

	private class CommandExecutionListenerImpl implements CommandExecutionListener {
		private AutomaticAdditionEventListener mListener;

		CommandExecutionListenerImpl(AutomaticAdditionEventListener listener) {
			this.mListener = listener;
		}

		@Override
		public void onPrepare() {
			mListener.onPublish(0, "Starting automatic addition...");
		}

		@Override
		public void onPreExecute(int totalTasks, int currentIndex, Command command) {
			mListener.onPublish(1.0F * currentIndex / totalTasks, command.getDescription());
		}

		@Override
		public void onPostExecute(int totalTasks, int currentIndex, Command command) {
			mListener.onPublish(1.0F * currentIndex / totalTasks, "Done: " + command.getDescription());
		}

		@Override
		public void onFinalize() {
			mListener.onPublish(0.99F, "Almost done...");
			// Database will commit changes after this method finished, it will take a while.
		}
	}
}
