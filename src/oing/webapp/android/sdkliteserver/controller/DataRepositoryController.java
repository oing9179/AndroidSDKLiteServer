package oing.webapp.android.sdkliteserver.controller;

import com.alibaba.fastjson.JSON;
import oing.webapp.android.sdkliteserver.io.LimitedBandwidthInputStream;
import oing.webapp.android.sdkliteserver.io.RangeFileInputStream;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryListService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/android/repository/")
public class DataRepositoryController {
	@Autowired
	private XmlRepositoryListService xmlRepositoryListService;
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;

	@RequestMapping("/*.*")
	public ResponseEntity _index(HttpServletRequest request, HttpServletResponse response)
			throws IOException, HttpSessionRequiredException {
		System.out.println(request.getRequestURI() + JSON.toJSONString(getHeaders(request), true));
		if (request.getRequestURI().endsWith(".xml")) {
			return acceptXmlDownload(request, response);
		} else {
			return acceptAnythingDownload(request, response);
		}
	}

	private ResponseEntity acceptXmlDownload(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
		File lFileXml;
		{
			Long lLongRepoXmlId = getRepositoryXmlId(request.getServletContext());
			Validate.notNull(lLongRepoXmlId, "You have not deploy an XML Repository.");
			RepoXml lRepoXml = xmlRepositoryListService.getById(lLongRepoXmlId);
			lFileXml = ConfigurationUtil.getXmlRepositoryDir(lRepoXml.getName());
			String lStrFileName = request.getRequestURI();
			lStrFileName = lStrFileName.substring(lStrFileName.lastIndexOf('/') + 1);
			lFileXml = new File(lFileXml, lStrFileName);
		}
		if (!lFileXml.exists()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		HttpHeaders lHttpHeaders = new HttpHeaders();
		lHttpHeaders.setContentType(new MediaType("text", "xml"));
		lHttpHeaders.setContentLength(lFileXml.length());
		InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(lFileXml));
		return new ResponseEntity<>(inputStreamResource, lHttpHeaders, HttpStatus.OK);
	}

	private ResponseEntity acceptAnythingDownload(HttpServletRequest request, HttpServletResponse response)
			throws IOException, HttpSessionRequiredException {
		final HashMap<String, String> lMapRequestHeaders = getHeaders(request);
		File lFileZip;
		{
			Long lLongRepoZipId = getRepositoryZipId(request.getServletContext());
			Validate.notNull(lLongRepoZipId, "You have not deploy an ZIP Repository.");
			RepoZip lRepoZip = zipRepositoryListService.getById(lLongRepoZipId);
			lFileZip = ConfigurationUtil.getZipRepositoryDir(lRepoZip.getName());
			String lStrFileName = request.getRequestURI();
			lStrFileName = lStrFileName.substring(lStrFileName.lastIndexOf('/') + 1);
			lFileZip = new File(lFileZip, lStrFileName);
		}
		if (!lFileZip.exists()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		Long ljBandwidthLimit;
		{
			ljBandwidthLimit = (Long) request.getServletContext().getAttribute(ApplicationConstants.KEY_UPSTREAM_SPEED_LIMIT);
			if (ljBandwidthLimit == null) ljBandwidthLimit = Long.MAX_VALUE;
		}
		// HTTP header "Range" support
		HttpRange lHttpRange = null;
		{
			List<HttpRange> lListHttpRanges = HttpRange.parseRanges(lMapRequestHeaders.get("range"));
			// After I read the source code of HttpRange, this list can't be null, so we don't need null-check.
			if (lListHttpRanges.size() > 0) {
				lHttpRange = lListHttpRanges.get(0);
			} else {
				lHttpRange = HttpRange.createByteRange(0);
			}
		}
		InputStreamResource inputStreamResource = new InputStreamResource(new LimitedBandwidthInputStream(
				new RangeFileInputStream(lFileZip, lHttpRange), request.getSession().getId(), ljBandwidthLimit));
		HttpHeaders lHttpHeaders = new HttpHeaders();
		lHttpHeaders.setContentType(new MediaType("application", "octet-stream"));
		lHttpHeaders.set("content-description", "attachment; filename=" + lFileZip.getName());
		{
			long length = lFileZip.length();
			length = lHttpRange.getRangeEnd(length) - lHttpRange.getRangeStart(length) + 1;
			lHttpHeaders.setContentLength(length);
		}
		{
			final long length = lFileZip.length();
			String lStrContentRange = lHttpRange.getRangeStart(length) + "-" +
					(lHttpRange.getRangeEnd(length)) + "/" + length;
			lHttpHeaders.set("content-range", lStrContentRange);
		}
		if (lMapRequestHeaders.containsKey("range")) {
			return new ResponseEntity<>(inputStreamResource, lHttpHeaders, HttpStatus.PARTIAL_CONTENT);
		}
		return new ResponseEntity<>(inputStreamResource, lHttpHeaders, HttpStatus.OK);
	}

	private Long getRepositoryXmlId(ServletContext servletContext) {
		return (Long) servletContext.getAttribute(ApplicationConstants.KEY_REPOSITORY_XML_ID);
	}

	private Long getRepositoryZipId(ServletContext servletContext) {
		return (Long) servletContext.getAttribute(ApplicationConstants.KEY_REPOSITORY_ZIP_ID);
	}

	private HashMap<String, String> getHeaders(HttpServletRequest request) {
		HashMap<String, String> lMapHeaders = new HashMap<>();
		Enumeration<String> lEnumHeaders = request.getHeaderNames();
		while (lEnumHeaders.hasMoreElements()) {
			String lStrHeader = lEnumHeaders.nextElement();
			lMapHeaders.put(lStrHeader.toLowerCase(), request.getHeader(lStrHeader));
		}
		return lMapHeaders;
	}

	public static void main(String[] args) {
		List<HttpRange> lListHttpRanges = HttpRange.parseRanges("bytes=0-14,-29");
		System.out.println(JSON.toJSONString(lListHttpRanges, true));
	}
}
