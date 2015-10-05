package oing.webapp.android.sdkliteserver.controller;

import oing.webapp.android.sdkliteserver.service.XmlRepositoryListService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/repository/zip/{repositoryName}/")
public class ZipRepositoryEditorController {
	private static Logger mLogger = LoggerFactory.getLogger(ZipRepositoryEditorController.class);
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;
	@Autowired
	private XmlRepositoryListService xmlRepositoryListService;

	@RequestMapping(method = RequestMethod.GET)
	public String _index(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName) {
		try {
			modelMap.put("zipRepository", zipRepositoryListService.getByName(repositoryName));
			modelMap.put("xmlRepositories", xmlRepositoryListService.getAll());
		} catch (Exception e) {
			mLogger.info(e.toString(), e);
			modelMap.put("objException", e);
		}
		return "repository/zip/repositoryName/index";
	}

	@RequestMapping(value = "/update_repository_dependency.do", method = RequestMethod.POST)
	public String update_repository_dependency(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName,
											   @RequestParam("xmlRepository.id") Long id) {
		try {
			zipRepositoryListService.updateRepositoryDependency(repositoryName, id);
		} catch (Exception e) {
			mLogger.info(e.toString(), e);
			String lStrUrl = _index(modelMap, repositoryName);
			modelMap.put("objException", e);
			return lStrUrl;
		}
		return "redirect:/repository/zip/{repositoryName}/";
	}
}
