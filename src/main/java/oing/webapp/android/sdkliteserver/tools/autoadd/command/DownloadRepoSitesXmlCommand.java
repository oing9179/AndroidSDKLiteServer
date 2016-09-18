package oing.webapp.android.sdkliteserver.tools.autoadd.command;

import jodd.http.HttpException;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.tools.autoadd.executor.CommandListAware;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RepoSite;
import oing.webapp.android.sdkliteserver.tools.xmleditor.editor.IRepoSitesEditor;
import oing.webapp.android.sdkliteserver.tools.xmleditor.editor.RepoXmlEditorFactory;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;

/**
 * This class does following things:<br/>
 * 1. Download XML from given URL.<br/>
 * 2. Save XML file info to database.<br/>
 * 3. Parse XML file.<br/>
 * 4. Create {@link DownloadRepoCommonXmlCommand} from step 3 then put it into List&lt;Command%gt;({@link CommandListAware#setCommandList(List)}).
 * 5. Save XML file to local storage.
 */
public class DownloadRepoSitesXmlCommand implements Command<Void>, CommandListAware {
	private RepoXmlFileDao mRepoXmlFileDao = null;
	private RepoXml mRepoXml = null;
	private String mStrDownloadUrl = null;
	private List<Command> mListCommands = null;

	/**
	 * @param repoXmlFileDao To access database.
	 * @param repoXml        Where all XML files belongs to it.
	 * @param url            Where XML from.
	 */
	public DownloadRepoSitesXmlCommand(RepoXmlFileDao repoXmlFileDao, RepoXml repoXml, String url) {
		mRepoXmlFileDao = repoXmlFileDao;
		mRepoXml = repoXml;
		mStrDownloadUrl = url;
	}

	@Override
	public String getDescription() {
		return "Download " + mStrDownloadUrl;
	}

	@Override
	public Void execute() throws Exception {
		HttpResponse lHttpResponse = HttpRequest.get(mStrDownloadUrl).send();
		if (lHttpResponse.statusCode() != 200) { // HTTP_OK = 200
			throw new HttpException(String.format("Unwanted http status %d from URL: %s", lHttpResponse.statusCode(), mStrDownloadUrl));
		}
		// Save xml file info to database.
		RepoXmlFile lRepoXmlFile = new RepoXmlFile.Builder()
				.idRepoXml(mRepoXml.getId())
				.fileName(UrlTextUtil.getFileName(mStrDownloadUrl))
				.url(mStrDownloadUrl)
				.build();
		mRepoXmlFileDao.insert(lRepoXmlFile);
		// Save xml file to local storage.
		File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(mRepoXml.getName()),
				File.separator + lRepoXmlFile.getFileName());
		FileUtil.writeString(lFileXml, lHttpResponse.bodyText());
		// Parse xml
		IRepoSitesEditor lEditor;
		{
			InputStream inputStream = IOUtils.toInputStream(lHttpResponse.bodyText(), "UTF-8");
			lEditor = RepoXmlEditorFactory.createRepoSitesEditor(mStrDownloadUrl, inputStream);
			IOUtils.closeQuietly(inputStream);
		}
		List<RepoSite> lListRepoSites = lEditor.extractAll();
		for (int i = 0, length = lListRepoSites.size(); i < length; i++) {
			RepoSite lRepoSite = lListRepoSites.get(i);
			String lStrFileName = UrlTextUtil.getFileName(lRepoSite.getUrl());
			// Avoid file name conflict by perform string formatting, an example after string formatting "addon2-1-x86_2.xml".
			lStrFileName = String.format("%s_%d.%s",
					lStrFileName.substring(0, lStrFileName.indexOf('.')),
					i,
					lStrFileName.substring(lStrFileName.lastIndexOf('.') + 1)
			);
			mListCommands.add(new DownloadRepoCommonXmlCommand(mRepoXmlFileDao, mRepoXml, lRepoSite.getAbsoluteUrl(), lStrFileName));
			lRepoSite.setUrl(lStrFileName);
		}
		// Commit changes back to xml file itself.
		lEditor.rebuild(lListRepoSites);
		{
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(lFileXml));
			lEditor.write(outputStream);
			IOUtils.closeQuietly(outputStream);
		}
		return null;
	}

	@Override
	public void setCommandList(List<Command> listCommand) {
		mListCommands = listCommand;
	}
}
