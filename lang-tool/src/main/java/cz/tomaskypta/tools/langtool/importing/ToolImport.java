package cz.tomaskypta.tools.langtool.importing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.xml.sax.SAXException;

import cz.tomaskypta.tools.langtool.CommandlineArguments;
import cz.tomaskypta.tools.langtool.ModelLoader;
import cz.tomaskypta.tools.langtool.model.Resource;

public class ToolImport {

	public static void run(CommandlineArguments args)
			throws FileNotFoundException, IOException,
			ParserConfigurationException, SAXException, TransformerException {
		
		if (StringUtils.isEmpty(args.getImportFile())) {
			System.err.println("Cannot import, missing input file name");
			return;
		}

		File projectFile = new File(args.getExportProject());
		if (!projectFile.exists()) {
			System.err.println("Cannot import, project directory not set.");
			return;
		}

		ModelLoader loader = new ModelLoader();

		System.out.println("Loading model from project file.");
		loader.load(projectFile,
				parseAdditionalResources(args.getAdditionalResources()));

		File inputFile = new File(args.getImportFile());
		if (!inputFile.exists()) {
			System.err.println("Cannot import, input file does not exist: " + inputFile.getAbsolutePath());
			return;
		}
		
		System.out.println("Loading model from input excel file: " + inputFile.getAbsolutePath());
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(inputFile));
		HSSFSheet sheet = wb.getSheetAt(0);
		loader.load(sheet);

		for (String path : ModelLoader.POTENTIAL_RES_DIRS) {
			File resDir = projectFile.toPath().resolve(path).toFile();
			if (resDir.exists()) {
				for (String language : loader.getLanguageIndex().keySet()) {
					File valuesDir = resDir
							.toPath()
							.resolve(
									ModelLoader.DIR_VALUES
											+ getLanguageExtension(language))
							.toFile();
					if (valuesDir.exists()) {
						for (Resource res : loader.getResources().values()) {
							importResource(res, valuesDir, language);
						}
					}
				}
			}
		}

	}

	private static Set<String> parseAdditionalResources(
			String additionalResources) {
		if (additionalResources == null) {
			return Collections.emptySet();
		}

		Set<String> resources = new HashSet<String>();

		for (String resName : additionalResources.split(":")) {
			if (!resName.endsWith(".xml")) {
				resName = resName + ".xml";
			}
			resources.add(resName);
		}

		return resources;
	}

	private static void importResource(Resource res, File valuesDir,
			String language) throws TransformerException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			IOException {
		Importer importer = new Importer();
		importer.importResource(res, valuesDir, language);
	}

	private static String getLanguageExtension(String language) {
		if (!ModelLoader.DEFAULT_LANGUAGE.equals(language)) {
			return "-" + language;
		}
		return "";
	}
}
