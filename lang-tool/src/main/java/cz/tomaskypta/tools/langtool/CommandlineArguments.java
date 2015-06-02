package cz.tomaskypta.tools.langtool;

import com.beust.jcommander.Parameter;

/**
* Created by Tomáš Kypta on 03.10.14.
*/
public class CommandlineArguments {

    @Parameter(names = "-e", description = "Export project dir")
    String exportProject;
    @Parameter(names = "-o", description = "Output file")
    String outputFile;
    @Parameter(names = "--additional-resources", description = "Colon separated list of additional resource files" +
        " to export")
    String additionalResources;
    @Parameter(names = "-i", description = "Import xls file")
    String importFile;
    
    public String getExportProject() {
        return exportProject;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getAdditionalResources() {
        return additionalResources;
    }

    public String getImportFile() {
        return importFile;
    }
    
}
