package cz.tomaskypta.tools.langtool.exporting;

import java.util.HashSet;
import java.util.Set;

import cz.tomaskypta.tools.langtool.CommandlineArguments;

/**
 * Created by tomas on 03.10.14.
 */
public class ExportConfig  {

    public String inputExportProject;
    public String outputFile;
    public Set<String> additionalResources;

    public ExportConfig() {
        super();
        this.additionalResources = new HashSet<String>();
    }

    public ExportConfig(ExportConfig other) {
        this.inputExportProject = other.inputExportProject;
        this.outputFile = other.outputFile;
        this.additionalResources = new HashSet<String>(other.additionalResources);
    }

    public ExportConfig(CommandlineArguments args) {        
        this.inputExportProject = args.getExportProject();
        this.outputFile = args.getOutputFile();
        this.additionalResources = new HashSet<String>();
        addAdditionalResources(args.getAdditionalResources());
    }

    private void addAdditionalResources(String additionalResourcesRaw) {
        if (additionalResourcesRaw == null) {
            return;
        }
        for (String resName : additionalResourcesRaw.split(":")) {
            if (!resName.endsWith(".xml")) {
                resName = resName + ".xml";
            }
            additionalResources.add(resName);
        }
    }
}
