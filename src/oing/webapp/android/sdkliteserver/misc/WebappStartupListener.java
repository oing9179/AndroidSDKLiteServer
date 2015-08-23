package oing.webapp.android.sdkliteserver.misc;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class WebappStartupListener implements ApplicationContextAware, ServletContextAware {
    private ApplicationContext applicationContext;
    private ServletContext servletContext;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    public void onStartup() throws IOException, SQLException {
        File lFileDb = new File(servletContext.getRealPath("/DataRepository/main.sqlite3"));
        if (lFileDb.exists()) return;

        System.out.println("/DataRepository/main.sqlite3 does not exist, create it.");
        lFileDb = lFileDb.getParentFile();
        lFileDb.mkdirs();
        File lFileSqlScript = new File(servletContext.getRealPath("/WEB-INF/init_sqlite3_database.sql"));
        ScriptRunner lScriptRunner = new ScriptRunner(sqlSessionFactory.openSession().getConnection());
        InputStreamReader lInputStreamReader = new InputStreamReader(new FileInputStream(lFileSqlScript), "UTF8");

        lScriptRunner.setEscapeProcessing(false);
        lScriptRunner.runScript(lInputStreamReader);
        lScriptRunner.closeConnection();
        lInputStreamReader.close();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
}
