package oing.webapp.android.sdkliteserver.utils.xmleditor.editor;

import oing.webapp.android.sdkliteserver.utils.xmleditor.RemotePackage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IRepoCommonEditor {
	List<RemotePackage> getAllRemotePackages();

	void updateArchivesUrl(List<String> urls);

	void save() throws IOException;

	void save(File target) throws IOException;
}
