package oing.webapp.android.sdkliteserver.controller;

import com.alibaba.fastjson.JSONObject;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryListService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryEditorService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/repository/zip/{repositoryName}/")
public class ZipRepositoryEditorController {
	private static Logger mLogger = LoggerFactory.getLogger(ZipRepositoryEditorController.class);
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;
	@Autowired
	private ZipRepositoryEditorService zipRepositoryEditorService;
	@Autowired
	private XmlRepositoryListService xmlRepositoryListService;

	@RequestMapping(method = RequestMethod.GET)
	public String _index(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName) {
		try {
			modelMap.put("zipRepository", zipRepositoryListService.getByNameOrThrow(repositoryName));
			modelMap.put("xmlRepositories", xmlRepositoryListService.getAll());
		} catch (Exception e) {
			mLogger.info(e.toString(), e);
			modelMap.put("objException", e);
		}

		return "admin/repository/zip/repositoryName/index";
	}

	@RequestMapping(value = "/update_repository_dependency.do", method = RequestMethod.POST)
	public String update_repository_dependency(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName,
	                                           @RequestParam("xmlRepository.id") Long id) {
		try {
			zipRepositoryEditorService.updateRepositoryDependency(repositoryName, id);
		} catch (Exception e) {
			mLogger.info(e.toString(), e);
			String lStrUrl = _index(modelMap, repositoryName);
			modelMap.put("objException", e);
			return lStrUrl;
		}
		return "redirect:/admin/repository/zip/{repositoryName}/";
	}

	@RequestMapping(value = "/file_completion.do", method = RequestMethod.GET)
	public String file_completion_view(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName) {
		RepoZip lRepoZip = zipRepositoryListService.getByNameOrThrow(repositoryName);
		modelMap.put("zipRepository", lRepoZip);
		try {
			modelMap.put("xmlRepository", xmlRepositoryListService.getById(lRepoZip.getIdRepoXml()));
		} catch (Exception ignore) {
		}
		return "admin/repository/zip/repositoryName/file_completion";
	}

	@RequestMapping(value = "/get_all_archives.do", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject get_all_archives(@PathVariable("repositoryName") String repositoryName,
	                                   @RequestParam(value = "isIncludeSysLinux", required = false, defaultValue = "false") Boolean isIncludeSysLinux,
	                                   @RequestParam(value = "isIncludeSysOSX", required = false, defaultValue = "false") Boolean isIncludeSysOSX,
	                                   @RequestParam(value = "isIncludeSysWin", required = false, defaultValue = "false") Boolean isIncludeSysWin,
	                                   @RequestParam(value = "isIncludeObsoleted", required = false, defaultValue = "false") Boolean isIncludeObsoleted,
	                                   @RequestParam(value = "isIncludeExisted", required = false, defaultValue = "false") Boolean isIncludeExisted)
			throws IOException, DocumentException {
		JSONObject lJsonObjResponse = new JSONObject();
		{
			JSONObject lListRemotePackages = new JSONObject();
			lListRemotePackages.put("includeSysLinux", isIncludeSysLinux);
			lListRemotePackages.put("includeSysOSX", isIncludeSysOSX);
			lListRemotePackages.put("includeSysWin", isIncludeSysWin);
			lListRemotePackages.put("includeObsoleted", isIncludeObsoleted);
			lListRemotePackages.put("includeExisted", isIncludeExisted);
			lJsonObjResponse.put("options", lListRemotePackages);
		}
		List lListRemotePackages = zipRepositoryEditorService.getAllRemotePackages(repositoryName,
				isIncludeSysLinux, isIncludeSysOSX, isIncludeSysWin, isIncludeObsoleted, isIncludeExisted);
		lJsonObjResponse.put("data", lListRemotePackages);
		return lJsonObjResponse;
	}

	@RequestMapping(value = "/redundancy_cleanup.do", method = RequestMethod.GET)
	public String redundancy_cleanup_view(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName) {
		RepoZip lRepoZip = zipRepositoryListService.getByNameOrThrow(repositoryName);
		modelMap.put("zipRepository", lRepoZip);
		try {
			modelMap.put("xmlRepository", xmlRepositoryListService.getById(lRepoZip.getIdRepoXml()));
		} catch (Exception ignore) {
		}
		return "admin/repository/zip/repositoryName/redundancy_cleanup";
	}

	@RequestMapping(value = "/get_no_longer_needed_archives.do", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject get_no_longer_needed_archives(@PathVariable("repositoryName") String repositoryName,
	                                                @RequestParam(value = "isIncludeObsoleted", required = false, defaultValue = "false") boolean isIncludeObsoleted,
	                                                @RequestParam(value = "isIncludeNotInRepo", required = false, defaultValue = "false") boolean isIncludeNotInRepo)
			throws IOException, DocumentException {
		JSONObject lJsonObjResponse = new JSONObject();
		lJsonObjResponse.put("data", zipRepositoryEditorService.getNoLongerNeededArchives(repositoryName, isIncludeObsoleted, isIncludeNotInRepo));
		return lJsonObjResponse;
	}

	@RequestMapping(value = "/redundancy_cleanup.do", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject redundancy_cleanup(@PathVariable("repositoryName") String repositoryName,
	                                     @RequestParam("fileNames") String[] fileNames) {
		JSONObject lJsonObjResponse = new JSONObject();
		try {
			zipRepositoryEditorService.doRedundancyCleanup(repositoryName, fileNames);
			lJsonObjResponse.put("success", true);
		} catch (Exception e) {
			mLogger.warn(e.toString(), e);
			lJsonObjResponse.put("success", false);
			lJsonObjResponse.put("message", e.toString());
		}
		return lJsonObjResponse;
	}
}

