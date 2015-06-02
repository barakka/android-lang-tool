package cz.tomaskypta.tools.langtool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cz.tomaskypta.tools.langtool.model.Plural;
import cz.tomaskypta.tools.langtool.model.Resource;
import cz.tomaskypta.tools.langtool.model.StringArray;
import cz.tomaskypta.tools.langtool.model.Term;
import cz.tomaskypta.tools.langtool.model.TypedArray;

public class ModelLoader {
	/* 2 = context + key + default = 0, 1, 2 */
	private static final int FIXED_COLUMNS = 2;
	public static final String DEFAULT_LANGUAGE = "default";
	public static final String DIR_VALUES = "values";
	public static final String[] POTENTIAL_RES_DIRS = new String[] { "res",
			"src/main/res" };

	private Map<String, Resource> resources;
	private Map<String, Integer> languageIndex;
	private DocumentBuilder builder;
	private Set<String> sAllowedFiles;

	public ModelLoader() throws ParserConfigurationException {
		this.resources = new HashMap<String, Resource>();

		sAllowedFiles = new HashSet<String>();
		sAllowedFiles.add("strings.xml");

		this.languageIndex = new HashMap<String, Integer>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		this.builder = dbf.newDocumentBuilder();
	}

	public void load(HSSFSheet sheet) {
		HSSFRow headerRow = sheet.getRow(0);

		// Build language index
		for (int i = FIXED_COLUMNS; i < headerRow.getLastCellNum(); i++) {
			String lang = headerRow.getCell(i).getStringCellValue();
			languageIndex.put(lang, i);
		}

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {			
			HSSFRow row = sheet.getRow(i);

			String context = row.getCell(0, Row.CREATE_NULL_AS_BLANK)
					.getStringCellValue();
			String key = row.getCell(1, Row.CREATE_NULL_AS_BLANK)
					.getStringCellValue();

			if (StringUtils.isNotEmpty(context) && StringUtils.isNotEmpty(key)) {

				Resource res = getResource(context);
				sAllowedFiles.add(res.getFileName());

				if (key.contains("[")) {
					String[] keyParts = key.split("[\\[\\]]");
					
					StringArray stringArray = res.getStringArray(keyParts[0]);					
					Term term = stringArray.getPosition(Integer.parseInt(keyParts[1]));
					addTranslationsForTerm(row, term);					
				} else if (key.contains("#")) {
					String[] keyParts = key.split("#");
					
					Plural plural = res.getPlural(keyParts[0]);
					Term term = plural.getQuantity(keyParts[1]);
					addTranslationsForTerm(row, term);					
				} else {
					Term term = res.getTerm(key);
					addTranslationsForTerm(row, term);
				}
			}
		}
	}

	private void addTranslationsForTerm(HSSFRow row, Term term) {
		for (Map.Entry<String, Integer> language : languageIndex
				.entrySet()) {
			String lang = language.getKey();
			String translation = row.getCell(language.getValue())
					.getStringCellValue();
			term.addTranslation(lang, translation);
		}
	}

	public void load(File project, Set<String> additionalResources)
			throws IOException, SAXException {
		File res = findResourceDir(project);
		if (res == null) {
			System.err.println("Cannot find resource directory.");
			return;
		}

		sAllowedFiles.addAll(additionalResources);

		// First build an index of the supported languages
		for (File dir : res.listFiles()) {
			if (isValuesDirectory(dir)) {
				String language = extractLanguage(dir);
				if (language != null) {
					languageIndex.put(language, languageIndex.size()
							+ FIXED_COLUMNS);
				}
			}
		}

		// Process each language
		for (File dir : res.listFiles()) {
			String lang = extractLanguage(dir);
			if (lang != null) {
				exportLang(lang, dir);
			}
		}
	}

	private String extractLanguage(File dir) {
		String dirName = dir.getName();
		if (dir.getName().equals(DIR_VALUES)) {
			return DEFAULT_LANGUAGE;
		} else {
			int index = dirName.indexOf('-');
			if (index != -1) {
				String lang = dirName.substring(index + 1);
				return lang;
			}
		}
		return null;
	}

	private boolean isValuesDirectory(File dir) {
		if (dir.isDirectory() && dir.getName().startsWith(DIR_VALUES)) {
			File strings = new File(dir, "strings.xml");
			return strings.exists();
		}
		return false;
	}

	private File findResourceDir(File project) {
		List<File> availableResDirs = new LinkedList<File>();
		for (String potentialResDir : POTENTIAL_RES_DIRS) {
			File res = new File(project, potentialResDir);
			if (res.exists()) {
				availableResDirs.add(res);
			}
		}
		if (!availableResDirs.isEmpty()) {
			return availableResDirs.get(0);
		}
		return null;
	}

	private void exportLang(String lang, File valueDir) throws IOException,
			SAXException {
		for (String fileName : sAllowedFiles) {
			File stringFile = new File(valueDir, fileName);
			if (stringFile.exists()) {
				Resource res = getResource(fileName);
				exportLangToExcel(lang, stringFile, getStrings(stringFile), res);
			}
		}
	}

	private Resource getResource(String fileName) {
		Resource res = resources.get(fileName);
		if (res == null) {
			res = new Resource(fileName);
			resources.put(fileName, res);
		}
		return res;
	}

	private NodeList getStrings(File f) throws SAXException, IOException {
		Document dom = builder.parse(f);
		return dom.getDocumentElement().getChildNodes();
	}

	private void exportLangToExcel(String lang, File src, NodeList strings,
			Resource res) throws FileNotFoundException, IOException {

		for (int i = 0; i < strings.getLength(); i++) {
			Node item = strings.item(i);

			if ("string".equals(item.getNodeName())) {
				Node translatable = item.getAttributes().getNamedItem(
						"translatable");
				if (translatable != null
						&& "false".equals(translatable.getNodeValue())) {
					continue;
				}
				String key = item.getAttributes().getNamedItem("name")
						.getNodeValue();

				Term term = res.getTerm(key);
				term.addTranslation(lang, item.getTextContent());

			} else if ("plurals".equals(item.getNodeName())) {
				String key = item.getAttributes().getNamedItem("name")
						.getNodeValue();
				Plural plural = res.getPlural(key);

				NodeList items = item.getChildNodes();
				for (int j = 0; j < items.getLength(); j++) {
					Node plurarItem = items.item(j);
					if ("item".equals(plurarItem.getNodeName())) {

						Term term = plural.getQuantity(plurarItem
								.getAttributes().getNamedItem("quantity")
								.getNodeValue());
						term.addTranslation(lang, plurarItem.getTextContent());
					}
				}
			} else if ("string-array".equals(item.getNodeName())) {
				String key = item.getAttributes().getNamedItem("name")
						.getNodeValue();

				StringArray stringArray = res.getStringArray(key);

				NodeList arrayItems = item.getChildNodes();
				for (int j = 0, k = 0; j < arrayItems.getLength(); j++) {
					Node arrayItem = arrayItems.item(j);
					if ("item".equals(arrayItem.getNodeName())) {
						Term term = stringArray.getPosition(k++);
						term.addTranslation(lang, arrayItem.getTextContent());
					}
				}
			} else if ("array".equals(item.getNodeName())) {
				String key = item.getAttributes().getNamedItem("name")
						.getNodeValue();

				TypedArray typedArray = res.getTypedArray(key);

				NodeList arrayItems = item.getChildNodes();
				for (int j = 0, k = 0; j < arrayItems.getLength(); j++) {
					Node arrayItem = arrayItems.item(j);
					if ("item".equals(arrayItem.getNodeName())) {
						Term term = typedArray.getPosition(k++);
						term.addTranslation(lang, arrayItem.getTextContent());
					}
				}
			}
		}

	}

	public Map<String, Integer> getLanguageIndex() {
		return languageIndex;
	}

	public Map<String, Resource> getResources() {
		return resources;
	}

}
