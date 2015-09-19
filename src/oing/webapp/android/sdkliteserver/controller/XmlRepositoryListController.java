package oing.webapp.android.sdkliteserver.controller;

import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryListService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/repository/xml/")
public class XmlRepositoryListController {
	private static Logger mLogger = LoggerFactory.getLogger(XmlRepositoryListController.class);
	@Autowired
	private XmlRepositoryListService xmlRepositoryListService;
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;

	/**
	 * Show all xml repository
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String _index(ModelMap modelMap) {
		List<RepoXml> lListRepoXml = xmlRepositoryListService.getAll();

		modelMap.put("xmlRepositories", lListRepoXml);
		return "repository/xml/index";
	}

	@RequestMapping(value = "/creation.do", method = RequestMethod.GET)
	public String creation_view(ModelMap modelMap) {
		modelMap.put("xmlRepositories", xmlRepositoryListService.getAll());
		return "repository/xml/creation";
	}

	@RequestMapping(value = "/creation.do", method = RequestMethod.POST)
	public String creation(ModelMap modelMap,
						   @RequestParam("name") String name,
						   @RequestParam(value = "createFrom", required = false) Long createFrom) {
		/**
		 * Validate:
		 * 1. At least 6 characters.
		 * 2. At most 32 characters.
		 * 3. Alphabets, numbers and underscores are allowed.
		 */
		try {
			Validate.matchesPattern(name, "^\\w{6,32}$",
					"Validation failed: repository name: At least 6 chars, at most 32 chars, alphabets numbers and underscores are allowed.");
		} catch (IllegalArgumentException e) {
			mLogger.info(e.toString(), e);
			modelMap.put("errorMessage", e);
		}
		if (modelMap.containsKey("errorMessage")) return "/repository/xml/creation";
		// After validation complete, create xml repository.
		try {
			xmlRepositoryListService.create(name, createFrom);
		} catch (Exception e) {
			mLogger.info(e.toString(), e);
			modelMap.put("xmlRepositories", xmlRepositoryListService.getAll());
			modelMap.put("errorMessage", e);
		}
		if (modelMap.containsKey("errorMessage")) return "/repository/xml/creation";
		return "redirect:/repository/xml/";
	}

	@RequestMapping(value = "/deletion.do", method = RequestMethod.GET)
	public String deletion_view(ModelMap modelMap, @RequestParam("id") Long id) {
		modelMap.put("xmlRepository", xmlRepositoryListService.getById(id));
		modelMap.put("zipRepositories", zipRepositoryListService.getDependsRepoXmlId(id));
		return "repository/xml/deletion";
	}

	@RequestMapping(value = "/deletion.do", method = RequestMethod.POST)
	public String deletion(ModelMap modelMap, @RequestParam("id") Long id, @RequestParam("name") String name) {
		try {
			xmlRepositoryListService.delete(id, name);
		} catch (Exception e) {
			mLogger.error(e.toString(), e);
			modelMap.put("errorMessage", e);
		}
		if (modelMap.containsKey("errorMessage")) {
			modelMap.put("xmlRepository", xmlRepositoryListService.getById(id));
			modelMap.put("zipRepositories", zipRepositoryListService.getDependsRepoXmlId(id));
			return "repository/xml/deletion";
		}
		return "redirect:/repository/xml/";
	}
}
