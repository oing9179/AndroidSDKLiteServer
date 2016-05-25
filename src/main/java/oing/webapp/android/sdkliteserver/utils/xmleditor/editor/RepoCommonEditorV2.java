package oing.webapp.android.sdkliteserver.utils.xmleditor.editor;

import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import oing.webapp.android.sdkliteserver.utils.xmleditor.RemotePackage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RepoCommonEditorV2 implements IRepoCommonEditor {
	private final String mStrXmlDirUrl;
	private File mFileXml;
	private Document mDocument;

	RepoCommonEditorV2(String url, File xmlFile) throws IOException, DocumentException {
		mStrXmlDirUrl = UrlTextUtil.getDir(url);
		mFileXml = xmlFile;
		mDocument = DocumentHelper.parseText(FileUtils.readFileToString(mFileXml));
	}

	@Override
	public List<RemotePackage> getAllRemotePackages() {
		throw new NotImplementedException("unimplemented");
	}

	@Override
	public void updateArchivesUrl(List<String> archives) {
		throw new NotImplementedException("unimplemented");
	}

	@Override
	public void save() throws IOException {
		throw new NotImplementedException("unimplemented");
	}

	@Override
	public void save(File target) throws IOException {
		throw new NotImplementedException("unimplemented");
	}
}
