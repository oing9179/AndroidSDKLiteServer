package oing.webapp.android.sdkliteserver.controller;

import com.alibaba.fastjson.JSON;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/repository/xml/")
public class XmlRepositoryController {
    @Autowired
    private XmlRepositoryService xmlRepositoryService;

    /**
     * Show all xml repository
     */
    @RequestMapping(method = RequestMethod.GET)
    public String _index(ModelMap modelMap) {
        List<RepoXml> lListRepoXml = xmlRepositoryService.getAll();

        modelMap.put("data", JSON.toJSONString(lListRepoXml));
        return "repository/xml/index";
    }

    @RequestMapping(value = "/creation.do", method = RequestMethod.GET)
    public String creation() {
        return "repository/xml/creation";
    }

    @RequestMapping(value = "/creation.do", method = RequestMethod.POST)
    public String creation_post() {
        return "repository/xml/";
    }
}
