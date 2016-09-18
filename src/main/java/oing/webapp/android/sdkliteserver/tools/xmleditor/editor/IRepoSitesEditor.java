package oing.webapp.android.sdkliteserver.tools.xmleditor.editor;

import oing.webapp.android.sdkliteserver.tools.xmleditor.RepoSite;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface IRepoSitesEditor {
	/**
	 * Extract all {@link RepoSite}s from given xml document.
	 */
	List<RepoSite> extractAll();

	/**
	 * Update URLs into xml document.
	 *
	 * @param listRepoSite URLs will update into xml document.
	 */
	void rebuild(List<RepoSite> listRepoSite);

	/**
	 * Save XML file to another destination.
	 */
	void write(OutputStream out) throws IOException;
}
