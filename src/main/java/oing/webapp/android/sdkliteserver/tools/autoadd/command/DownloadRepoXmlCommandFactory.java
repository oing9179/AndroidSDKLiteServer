package oing.webapp.android.sdkliteserver.tools.autoadd.command;

import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;

public class DownloadRepoXmlCommandFactory {
	public static Command createCommand(RepoXmlFileDao repoXmlFileDao, RepoXml repoXml, String url) {
		String lStrFileName = UrlTextUtil.getFileName(url);
		if (lStrFileName.startsWith("addons_list")) {
			return new DownloadRepoSitesXmlCommand(repoXmlFileDao, repoXml, url);
		} else {
			return new DownloadRepoCommonXmlCommand(repoXmlFileDao, repoXml, url);
		}
	}
}
