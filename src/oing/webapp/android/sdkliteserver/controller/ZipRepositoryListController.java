package oing.webapp.android.sdkliteserver.controller;

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

@Controller
@RequestMapping("/repository/zip/")
public class ZipRepositoryListController {
	private static Logger mLogger = LoggerFactory.getLogger(ZipRepositoryListController.class);
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;

	@RequestMapping(method = RequestMethod.GET)
	public String _index(ModelMap modelMap) {
		modelMap.put("zipRepositories", zipRepositoryListService.getAll());
		return "repository/zip/index";
	}

	@RequestMapping(value = "/creation.do", method = RequestMethod.GET)
	public String creation_view() {
		return "repository/zip/creation";
	}

	@RequestMapping(value = "/creation.do", method = RequestMethod.POST)
	public String creation(ModelMap modelMap, @RequestParam("name") String name) {
		try {
			Validate.matchesPattern(name, "^\\w{6,32}$",
					"Validation failed: repository name: At least 6 chars, at most 32 chars, alphabets numbers and underscores are allowed.");
			zipRepositoryListService.create(name);
		} catch (Exception e) {
			mLogger.info(e.toString(), e);
			modelMap.put("errorMessage", e);
		}
		return modelMap.containsKey("errorMessage") ?
				"repository/zip/creation" : "redirect:/repository/zip/";
	}
}