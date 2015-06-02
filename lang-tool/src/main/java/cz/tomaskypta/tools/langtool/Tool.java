package cz.tomaskypta.tools.langtool;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import cz.tomaskypta.tools.langtool.exporting.ExportConfig;
import cz.tomaskypta.tools.langtool.exporting.ToolExport;
import cz.tomaskypta.tools.langtool.importing.ToolImport;

public class Tool {

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        if (args == null || args.length == 0) {
            printHelp();
            return;
        }

        CommandlineArguments parsedArgs = new CommandlineArguments();
        try {
            new JCommander(parsedArgs, args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            printHelp();
            return;
        }

        if (parsedArgs.outputFile != null) {
            // exporting
            ExportConfig config = new ExportConfig(parsedArgs);
            ToolExport.run(config);
        } else if (parsedArgs.importFile != null) {            
            ToolImport.run(parsedArgs);            
        } else {
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("commands format:");
        System.out.println("\texport: -e <project dir> [-o <output file>] [--additional-resources <colon separated " +
            "list of additional resources>]");
        System.out.println("\timport: -i <input file>");
    }
}
