package oing.webapp.android.sdkliteserver.service.impl;

import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XmlRepositoryServiceImpl implements XmlRepositoryService {
    @Autowired
    private RepoXmlDao repoXmlDao;

    /**
     * Get all xml repository information as a list.
     */
    @Override
    public List<RepoXml> getAll() {
        return repoXmlDao.getAll();
    }
}
