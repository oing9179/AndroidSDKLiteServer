package oing.webapp.android.sdkliteserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jodd.http.ProxyInfo;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.service.AutomaticAdditionEventListener;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryEditorService;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryListService;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/repository/xml/{repositoryName}/")
public class XmlRepositoryEditorController {
	private static Logger mLogger = LoggerFactory.getLogger(XmlRepositoryEditorController.class);
	@Autowired
	private XmlRepositoryListService xmlRepositoryListService;
	@Autowired
	private XmlRepositoryEditorService xmlRepositoryEditorService;

	@RequestMapping(method = RequestMethod.GET)
	public String _index(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName) {
		try {
			RepoXml lRepoXml = xmlRepositoryListService.getByName(repositoryName);
			modelMap.put("xmlRepository", lRepoXml);
			List<RepoXmlFile> lListRepoXmlFiles = xmlRepositoryEditorService.getFilesByRepoXmlId(lRepoXml.getId());
			modelMap.put("xmlFiles", lListRepoXmlFiles);
		} catch (IllegalArgumentException e) {
			modelMap.put("errorMessage", e);
		}
		return "repository/xml/repositoryName/index";
	}

	/**
	 * Navigate to automatic addition page, help users fill repository easily.
	 */
	@RequestMapping(value = "/automatic_addition.do", method = RequestMethod.GET)
	public String automatic_addition_view(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName) {
		modelMap.put("xmlRepository", xmlRepositoryListService.getByName(repositoryName));
		modelMap.put("url_addons_list_xml", ConfigurationUtil.get("url.addons_list_2_xml"));
		modelMap.put("url_repository_xml", ConfigurationUtil.get("url.repository_11_xml"));
		return "repository/xml/repositoryName/automatic_addition";
	}

	/**
	 * Fill xml repository automatically
	 *
	 * @param isPreferHttpsConnection Prefer HTTPS connection instead of HTTP.
	 * @param proxyInfo_type          Proxy type {Direct, HTTP, SOCKS4, SOCKS5}
	 * @param proxyInfo_address       Proxy address
	 * @param proxyInfo_port          Proxy port
	 * @param proxyInfo_userName      Username for proxy authorization
	 * @param proxyInfo_password      Password for proxy authorization
	 */
	@RequestMapping(value = "/automatic_addition.do", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void automatic_addition(HttpServletResponse response,
								   @PathVariable("repositoryName") String repositoryName,
								   @RequestParam(value = "isPreferHttpsConnection", required = false) boolean isPreferHttpsConnection,
								   @RequestParam("proxyInfo.type") String proxyInfo_type,
								   @RequestParam(value = "proxyInfo.address", required = false) String proxyInfo_address,
								   @RequestParam(value = "proxyInfo.port", required = false, defaultValue = "0") int proxyInfo_port,
								   @RequestParam(value = "proxyInfo.userName", required = false) String proxyInfo_userName,
								   @RequestParam(value = "proxyInfo.password", required = false) String proxyInfo_password)
			throws Exception {
		AutomaticAdditionEventListener listener = null;
		try {
			/**
			 * Set contentType of response to "text/html",
			 * so that browsers have ability to read the content what I flushed immediately.
			 */
			response.setContentType("text/html");
			response.flushBuffer();
			listener = new AutomaticAdditionEventListener() {
				private OutputStreamWriter mWriter = new OutputStreamWriter(response.getOutputStream(), "UTF8");

				/**
				 * Publish a message to client(browser).
				 */
				@Override
				public synchronized void onPublish(float progress, String message) {
					if (progress > 0) progress = (int) (progress * 100);
					JSONObject lJsonObj = new JSONObject();
					lJsonObj.put("progress", progress);
					lJsonObj.put("message", message);
					try {
						mWriter.write(lJsonObj.toString() + "\n");
						mWriter.flush();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			};
			// Form validation
			switch (proxyInfo_type) {
				case "direct":
				case "http":
				case "socks4":
				case "socks5":
					break;
				default:
					throw new IllegalArgumentException("Unknown proxy type: " + proxyInfo_type);
			}
			if (!proxyInfo_type.equals("direct")) {
				Validate.isTrue(proxyInfo_address.length() > 0, "Required: proxyInfo.address");
				Validate.isTrue(proxyInfo_port > 0 && proxyInfo_port < 65536, "Illegal proxyInfo.port: " + proxyInfo_port);
			}
			// Create ProxyInfo object
			ProxyInfo lProxyInfo;
			{
				ProxyInfo.ProxyType proxyType = ProxyInfo.ProxyType.NONE;
				switch (proxyInfo_type) {
					case "http":
						proxyType = ProxyInfo.ProxyType.HTTP;
						break;
					case "socks4":
						proxyType = ProxyInfo.ProxyType.SOCKS4;
						break;
					case "socks5":
						proxyType = ProxyInfo.ProxyType.SOCKS5;
						break;
				}
				lProxyInfo = new ProxyInfo(proxyType, proxyInfo_address, proxyInfo_port, proxyInfo_userName, proxyInfo_password);
			}
			xmlRepositoryEditorService
					.automaticAddition(repositoryName, isPreferHttpsConnection, lProxyInfo, listener);
		} catch (Exception e) {
			mLogger.error(e.toString(), e);
			try {
				//noinspection ConstantConditions
				listener.onPublish(-1/*Abortion*/, e.toString());
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * Navigate to manual addition page
	 */
	@RequestMapping(value = "/manual_addition.do", method = RequestMethod.GET)
	public String manual_addition_view(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName) {
		modelMap.put("xmlRepository", xmlRepositoryListService.getByName(repositoryName));
		return "repository/xml/repositoryName/manual_addition";
	}

	/**
	 * Fill or update xml repository manually
	 *
	 * @param multipartFiles XML files user uploaded
	 * @param urls           Where these xml files comes from
	 */
	@RequestMapping(value = "/manual_addition.do", method = RequestMethod.POST)
	public String manual_addition(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName,
								  @RequestParam("file") MultipartFile[] multipartFiles,
								  @RequestParam("url") String[] urls) {
		RepoXml lRepoXml = xmlRepositoryListService.getByName(repositoryName);
		try {
			Validate.isTrue(multipartFiles.length == urls.length, "Count of files and URLs are not equal.");
			xmlRepositoryEditorService.manualAddition(repositoryName, multipartFiles, urls);
		} catch (Exception e) {
			mLogger.error(e.toString(), e);
			modelMap.put("errorMessage", e.toString());
			modelMap.put("xmlRepository", lRepoXml);
		}
		return modelMap.containsKey("errorMessage") ?
				"repository/xml/repositoryName/manual_addition" :
				"redirect:/repository/xml/" + lRepoXml.getName() + "/";
	}

	/**
	 * Convert log comes from SDK Manager to XML URLs.
	 */
	@RequestMapping(value = "/parse_log_for_sdkmanager.do", method = RequestMethod.POST)
	@ResponseBody
	public JSONArray parse_log_for_sdkmanager(@RequestParam("log") String log) {
		HashSet<String> lHashSetUrls = new HashSet<>();
		Matcher lMatcher = Pattern.compile("(http|https)://\\S+.xml").matcher(log);
		while (lMatcher.find()) {
			lHashSetUrls.add(lMatcher.group());
		}
		return (JSONArray) JSON.toJSON(lHashSetUrls);
	}

	@RequestMapping(value = "/deletion.do", method = RequestMethod.GET)
	public String deletion_view(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName,
								@RequestParam("id") Long id) {
		try {
			RepoXml lRepoXml = xmlRepositoryListService.getByName(repositoryName);
			modelMap.put("xmlRepository", lRepoXml);
			modelMap.put("xmlFile", xmlRepositoryEditorService.getByIdDependsRepoXmlId(id, lRepoXml.getId()));
		} catch (Exception e) {
			mLogger.error(e.toString(), e);
			modelMap.put("errorMessage", e);
		}
		return "repository/xml/repositoryName/deletion";
	}

	@RequestMapping(value = "/deletion.do", method = RequestMethod.POST)
	public String deletion(ModelMap modelMap, @PathVariable("repositoryName") String repositoryName,
						   @RequestParam("id") Long id, @RequestParam("name") String name) {
		try {
			xmlRepositoryEditorService.delete(repositoryName, id, name);
		} catch (Exception e) {
			RepoXml lRepoXml = xmlRepositoryListService.getByName(repositoryName);
			modelMap.put("xmlRepository", lRepoXml);
			try {
				modelMap.put("xmlFile", xmlRepositoryEditorService.getByIdDependsRepoXmlId(id, lRepoXml.getId()));
			} catch (Exception ignore) {
			}
			modelMap.put("errorMessage", e);
		}
		return modelMap.containsKey("errorMessage") ?
				"repository/xml/repositoryName/deletion" :
				"redirect:/repository/xml/" + repositoryName + "/";
	}
}
