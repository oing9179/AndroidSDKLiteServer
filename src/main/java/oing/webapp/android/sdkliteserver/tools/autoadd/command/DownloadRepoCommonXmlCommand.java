package oing.webapp.android.sdkliteserver.tools.autoadd.command;

import jodd.http.HttpException;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;

import java.io.File;

/**
 * This class does following things:<br/>
 * 1. Download XML from given URL.<br/>
 * 2. Save XML file info to database.
 * 3. Save XML file to local storage.
 */
public class DownloadRepoCommonXmlCommand implements Command<Void> {
	private RepoXmlFileDao mRepoXmlFileDao = null;
	private RepoXml mRepoXml = null;
	private String mStrDownloadUrl = null;
	private String mStrFileNameNew = null;

	/**
	 * @param repoXmlFileDao To access database.
	 * @param repoXml        Where all XML files belongs to it.
	 * @param url            Where XMl from.
	 */
	public DownloadRepoCommonXmlCommand(RepoXmlFileDao repoXmlFileDao, RepoXml repoXml, String url) {
		this(repoXmlFileDao, repoXml, url, UrlTextUtil.getFileName(url));
	}

	/**
	 * @param repoXmlFileDao To access database.
	 * @param repoXml        Where all XML files belongs to it.
	 * @param url            Where XMl from.
	 * @param fileNameNew    A new file name for XML to avoid file name conflict.
	 */
	public DownloadRepoCommonXmlCommand(RepoXmlFileDao repoXmlFileDao, RepoXml repoXml, String url, String fileNameNew) {
		mRepoXmlFileDao = repoXmlFileDao;
		mRepoXml = repoXml;
		mStrDownloadUrl = url;
		mStrFileNameNew = fileNameNew;
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
				.fileName(mStrFileNameNew)
				.url(mStrDownloadUrl)
				.build();
		mRepoXmlFileDao.insert(lRepoXmlFile);
		// Save xml file to local storage.
		File lFileXml = new File(ConfigurationUtil.getXmlRepositoryDir(mRepoXml.getName()),
				File.pathSeparator + lRepoXmlFile.getFileName());
		FileUtil.writeString(lFileXml, lHttpResponse.bodyText());
		return null;
	}
}
