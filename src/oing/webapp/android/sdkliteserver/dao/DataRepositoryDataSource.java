package oing.webapp.android.sdkliteserver.dao;

import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.springframework.jdbc.datasource.AbstractDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataRepositoryDataSource extends AbstractDataSource {
	private String databaseLocation;

	/**
	 * Stores SQLite3 jdbc driver is loaded or not.
	 */
	private boolean mzJdbcDriverLoaded = false;
	private String mStrJdbcUrlCache = null;

	@Override
	public Connection getConnection() throws SQLException {
		try {
			if (!mzJdbcDriverLoaded) Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new SQLException("SQLite3 jdbc driver class not found.", e);
		}
		if (mStrJdbcUrlCache == null) {
			mStrJdbcUrlCache = "jdbc:sqlite:" + new File(ConfigurationUtil.getDataRepositoryDir(), getDatabaseLocation()).getAbsolutePath();
		}
		return DriverManager.getConnection(mStrJdbcUrlCache);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		// Username and password is not required in SQLite database.
		return getConnection();
	}

	public String getDatabaseLocation() {
		return databaseLocation;
	}

	public void setDatabaseLocation(String databaseLocation) {
		this.databaseLocation = databaseLocation;
	}
}
