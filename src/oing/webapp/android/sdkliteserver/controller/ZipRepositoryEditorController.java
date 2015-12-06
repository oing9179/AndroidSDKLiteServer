package oing.webapp.android.sdkliteserver.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.model.SdkArchive;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryListService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

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

	@RequestMapping(value = "/file_completion.do", method = RequestMethod.GET)
	public String file_completion_view(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName) {
		RepoZip lRepoZip = zipRepositoryListService.getByName(repositoryName);
		modelMap.put("zipRepository", lRepoZip);
		try {
			modelMap.put("xmlRepository", xmlRepositoryListService.getById(lRepoZip.getIdRepoXml()));
		} catch (Exception ignore) {
		}
		return "repository/zip/repositoryName/file_completion";
	}

	@RequestMapping(value = "/get_all_archives.do", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject get_all_archives(@PathVariable("repositoryName") String repositoryName, @RequestParam HashMap<String, String> params,
	                                   @RequestParam(value = "isIncludeSysLinux", required = false, defaultValue = "false") Boolean isIncludeSysLinux,
	                                   @RequestParam(value = "isIncludeSysOSX", required = false, defaultValue = "false") Boolean isIncludeSysOSX,
	                                   @RequestParam(value = "isIncludeSysWin", required = false, defaultValue = "false") Boolean isIncludeSysWin,
	                                   @RequestParam(value = "isIncludeObsoleted", required = false, defaultValue = "false") Boolean isIncludeObsoleted,
	                                   @RequestParam(value = "isIncludeExisted", required = false, defaultValue = "false") Boolean isIncludeExisted) {
		JSONObject lJsonObjResponse = new JSONObject();
		lJsonObjResponse.put("linux", isIncludeSysLinux);
		lJsonObjResponse.put("osx", isIncludeSysOSX);
		lJsonObjResponse.put("win", isIncludeSysWin);
		lJsonObjResponse.put("obsoleted", isIncludeObsoleted);
		lJsonObjResponse.put("existed", isIncludeExisted);
		{
			List<SdkArchive> lListSdkArchives = zipRepositoryListService.getAllSdkArchiveInfo(repositoryName,
					isIncludeSysLinux, isIncludeSysOSX, isIncludeSysWin, isIncludeObsoleted, isIncludeExisted);
			JSONObject lJsonObj = new JSONObject();
			/**
			 * The final json content would like this:
			 * {
			 *     // API Level as key.
			 *     "23":[
			 *          {"description":"Android SDK Platform 6.0", ...}, ...
			 *     ],
			 *     // "null" stands for it have no API Level.
			 *     "null":[
			 *          {"description":"Android NDK", ...}, ...
			 *     ]
			 * }
			 */
			for (SdkArchive sdkArchive : lListSdkArchives) {
				String lStrKey = sdkArchive.getApiLevel() + "";
				JSONArray lJsonArr = lJsonObj.getJSONArray(lStrKey);
				if (lJsonArr == null) {
					lJsonArr = new JSONArray();
					lJsonObj.put(lStrKey, lJsonArr);
				}
				lJsonArr.add(sdkArchive);
			}
			lJsonObjResponse.put("data", lJsonObj);
		}
		return lJsonObjResponse;
	}

	@RequestMapping(value = "/redundancy_cleanup.do", method = RequestMethod.GET)
	public String redundancy_cleanup_view(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName) {
		RepoZip lRepoZip = zipRepositoryListService.getByName(repositoryName);
		modelMap.put("zipRepository", lRepoZip);
		try {
			modelMap.put("xmlRepository", xmlRepositoryListService.getById(lRepoZip.getIdRepoXml()));
		} catch (Exception ignore) {
		}
		return "repository/zip/repositoryName/redundancy_cleanup";
	}

	@RequestMapping(value = "/get_no_longer_needed_archives.do", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject get_no_longer_needed_archives(@PathVariable("repositoryName") String repositoryName,
	                                     @RequestParam(value = "isAbandonObsoleted", required = false, defaultValue = "false") boolean isAbandonObsoleted,
	                                     @RequestParam(value = "inAbandonNotExisted", required = false, defaultValue = "false") boolean isAbandonNotExisted) {
		JSONObject lJsonObjResponse = new JSONObject();
		lJsonObjResponse.put("data", zipRepositoryListService.getNoLongerNeededArchives(
				repositoryName, isAbandonObsoleted, isAbandonNotExisted));
		return lJsonObjResponse;
	}

	@RequestMapping(value = "/redundancy_cleanup.do", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject redundancy_cleanup(@PathVariable("repositoryName") String repositoryName,
	                                     @RequestParam("fileNames") String[] fileNames) {
		JSONObject lJsonObjResponse = new JSONObject();
		try {
			zipRepositoryListService.doRedundancyCleanup(repositoryName, fileNames);
			lJsonObjResponse.put("success", true);
		} catch (Exception e){
			mLogger.warn(e.toString(), e);
			lJsonObjResponse.put("success", false);
			lJsonObjResponse.put("message", e.toString());
		}
		return lJsonObjResponse;
	}
}
