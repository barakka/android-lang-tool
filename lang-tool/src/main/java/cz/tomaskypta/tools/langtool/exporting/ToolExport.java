package cz.tomaskypta.tools.langtool.exporting;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import cz.tomaskypta.tools.langtool.ModelLoader;

public class ToolExport {
		
	public static void run(ExportConfig config) throws SAXException,
			IOException, ParserConfigurationException {
						
		
		if (StringUtils.isEmpty(config.inputExportProject)) {
			System.out.println("Cannot export, missing config");
			return;
		}

		File project = new File(config.inputExportProject);

		if (StringUtils.isEmpty(config.outputFile)) {
			config.outputFile = "exported_strings_"
					+ System.currentTimeMillis() + ".xls";
		}
		
		System.out.println("Loading model.");
		
		ModelLoader modelLoader = new ModelLoader();		
		modelLoader.load(project, config.additionalResources);
		
		System.out.println("Model loaded. Found " + modelLoader.getResources().size() + " resources.");		
			
		System.out.println("Exporting to " + config.outputFile);
		Exporter exporter = new Exporter(project.getName(), config.outputFile, modelLoader.getLanguageIndex());
		exporter.export(modelLoader.getResources());
		System.out.println("Export completed.");
	}
	
}
