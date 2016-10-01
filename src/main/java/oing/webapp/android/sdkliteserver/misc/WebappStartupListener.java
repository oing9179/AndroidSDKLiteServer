package oing.webapp.android.sdkliteserver.misc;

import oing.webapp.android.sdkliteserver.controller.interceptor.HttpSessionListenerImpl;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class WebappStartupListener {
	@Autowired
	private ServletContext servletContext;
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	private Logger mLogger = LoggerFactory.getLogger(WebappStartupListener.class);

	public void onStartup() throws IOException, SQLException, DocumentException {
		loadConfiguration();
		createDataRepositoryDirsIfNotExist();
		createDatabaseIfNotExist();
		servletContext.addListener(new HttpSessionListenerImpl());
	}

	private void loadConfiguration() throws IOException, DocumentException {
		ConfigurationUtil.load(new File(servletContext.getRealPath(ApplicationConstants.FILE_PATH_CONFIG)));
	}

	private void createDatabaseIfNotExist() throws IOException, SQLException {
		File lFileDb = new File(ConfigurationUtil.getDataRepositoryDir(), ConfigurationUtil.get("database.location"));
		if (lFileDb.exists()) return;

		mLogger.info("The SQLite database does not exist, create it. (location: " + lFileDb + ")");
		if (!lFileDb.getParentFile().mkdirs()) {
			throw new IOException("Failed to create directories: " + lFileDb.getAbsolutePath());
		}
		File lFileSqlScript = new File(servletContext.getRealPath("/WEB-INF/init_sqlite3_database.sql"));
		ScriptRunner lScriptRunner = new ScriptRunner(sqlSessionFactory.openSession().getConnection());
		InputStreamReader lInputStreamReader = new InputStreamReader(new FileInputStream(lFileSqlScript), "UTF8");

		lScriptRunner.setEscapeProcessing(false);// If true, we will get a Exception caused by unknown.
		lScriptRunner.runScript(lInputStreamReader);
		lScriptRunner.closeConnection();
		lInputStreamReader.close();
		mLogger.info("SQLite database initialized.");
	}

	private void createDataRepositoryDirsIfNotExist() {
		File lFileDirDataRepository = ConfigurationUtil.getDataRepositoryDir();

		File lFile = new File(lFileDirDataRepository, "/xml/");
		if (!lFile.exists()) lFile.mkdirs();
		lFile = new File(lFileDirDataRepository, "/zip/");
		if (!lFile.exists()) lFile.mkdirs();
	}
}
