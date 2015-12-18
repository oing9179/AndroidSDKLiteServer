package oing.webapp.android.sdkliteserver.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Properties;

/**
 * A global configuration utility
 *
 * @author oing9179
 */
@Component
public class ConfigurationUtil implements ServletContextAware {
	private static ServletContext servletContext;
	private static Properties mProperties;
	private static File mFileLastLoaded;

	public static void load(File file) throws IOException {
		mProperties = new Properties();
		mProperties.load(new InputStreamReader(new FileInputStream(file), "UTF8"));
		mFileLastLoaded = file;
	}

	public static void save() throws IOException {
		OutputStreamWriter lWriter = new OutputStreamWriter(new FileOutputStream(mFileLastLoaded), "UTF8");
		mProperties.store(lWriter, "File encoded as UTF-8\nDO NOT DO ANYTHING to this file if you DON'T know what are you doing.");
	}

	public static String get(String key) {
		return mProperties.getProperty(key);
	}

	public static void put(String key, String value) {
		mProperties.put(key, value);
	}

	// ---------- Quick access methods ----------
	public static File getWebappRootDir() {
		return new File(servletContext.getRealPath("/"));
	}

	private static final String TEXT_ABSOLUTE_COLON = "absolute:";

	public static File getDataRepositoryDir() {
		String lStrLocation = get("data_repository.location");
		if (lStrLocation.startsWith(TEXT_ABSOLUTE_COLON)) {
			return new File(lStrLocation.substring(TEXT_ABSOLUTE_COLON.length() + 1));
		} else {
			return new File(getWebappRootDir(), "/" + lStrLocation);
		}
	}

	public static File getXmlRepositoryDir(String repositoryName) {
		return new File(getDataRepositoryDir(), "/xml/" + repositoryName);
	}

	public static File getZipRepositoryDir(String repositoryName) {
		return new File(getDataRepositoryDir(), "/zip/" + repositoryName);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		ConfigurationUtil.servletContext = servletContext;
	}
}
