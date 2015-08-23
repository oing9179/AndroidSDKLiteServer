package oing.webapp.android.sdkliteserver.dao.impl;

import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RepoXmlDaoImpl implements RepoXmlDao {
    @Autowired
    private SqlSession sqlSession;

    /**
     * SELECT all from table "repo_xml"
     */
    @Override
    public List<RepoXml> getAll() {
        return sqlSession.selectList("RepoXml.getAll");
    }
}
