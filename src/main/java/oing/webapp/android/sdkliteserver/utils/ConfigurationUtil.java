package oing.webapp.android.sdkliteserver.utils;

import org.apache.commons.io.IOUtils;
import org.dom4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ConfigurationUtil implements ServletContextAware {
	private static final String ELEMENT_NAME_PROPERTY = "property";
	private static ServletContext mServletContext;
	private static Document mDocument;

	private ConfigurationUtil() {
	}

	public static void load(File configFile) throws IOException, DocumentException {
		InputStream lInputStreamXml = new BufferedInputStream(new FileInputStream(configFile));
		String lStrXmlContent = IOUtils.toString(lInputStreamXml, "UTF-8");
		IOUtils.closeQuietly(lInputStreamXml);
		mDocument = DocumentHelper.parseText(lStrXmlContent);
	}

	public static void save(File configFile) throws IOException {
		OutputStreamWriter lOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(configFile));
		mDocument.write(lOutputStreamWriter);
		IOUtils.closeQuietly(lOutputStreamWriter);
	}

	public static String get(String key) {
		Element lElement = element(key);
		if (lElement == null) return null;
		return lElement.getText();
	}

	public static List<String> getList(String key) {
		Element lElement = element(key);
		if (lElement == null) return null;
		List<Element> lListElementsValue = lElement.element("list").elements("value");
		List<String> lListStrValues = new ArrayList<>(lListElementsValue.size());
		for (Element element : lListElementsValue) {
			lListStrValues.add(element.getText());
		}
		return lListStrValues;
	}

	public static void put(String key, String value) {
		Element lElement = element(key);
		if (lElement == null) {
			lElement = mDocument.getRootElement().addElement(ELEMENT_NAME_PROPERTY);
		}
		lElement.addAttribute("key", key).setText(value);
	}

	public static void put(String key, List<String> values) {
		Element lElement = element(key);
		if (lElement == null) {
			lElement = mDocument.getRootElement().addElement(ELEMENT_NAME_PROPERTY);
			lElement = lElement.addElement("list");
		} else {
			lElement.element("list");
		}
		lElement.clearContent();
		for (String value : values) {
			lElement.addElement("value").setText(value);
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		mServletContext = servletContext;
	}

	private static Element element(String key) {
		List<Element> lListElementsProperty = mDocument.getRootElement().elements(ELEMENT_NAME_PROPERTY);
		for (Element element : lListElementsProperty) {
			Attribute lAttribute = element.attribute("key");
			if (lAttribute != null && lAttribute.getValue().equals(key)) return element;
		}
		return null;
	}

	// ---------- Quick access methods ----------
	public static File getWebappRootDir() {
		return new File(mServletContext.getRealPath("/"));
	}

	private static final String TEXT_ABSOLUTE_COLON = "absolute:";

	public static File getDataRepositoryDir() {
		String lStrLocation = get("data_repository.location");
		if (lStrLocation.startsWith(TEXT_ABSOLUTE_COLON)) {
			return new File(lStrLocation.substring(TEXT_ABSOLUTE_COLON.length() + 1));
		} else {
			return new File(getWebappRootDir(), "/" + lStrLocation);
		}
	}

	public static File getXmlRepositoryDir(String repositoryName) {
		return new File(getDataRepositoryDir(), "/xml/" + repositoryName);
	}

	public static File getZipRepositoryDir(String repositoryName) {
		return new File(getDataRepositoryDir(), "/zip/" + repositoryName);
	}
}
