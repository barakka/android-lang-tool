package cz.tomaskypta.tools.langtool.importing;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cz.tomaskypta.tools.langtool.IModelVisitor;
import cz.tomaskypta.tools.langtool.ModelLoader;
import cz.tomaskypta.tools.langtool.model.Plural;
import cz.tomaskypta.tools.langtool.model.Resource;
import cz.tomaskypta.tools.langtool.model.ResourceEntry;
import cz.tomaskypta.tools.langtool.model.StringArray;
import cz.tomaskypta.tools.langtool.model.Term;
import cz.tomaskypta.tools.langtool.model.TypedArray;

public class Importer implements IModelVisitor{

	private DocumentBuilder builder;
	private Element root;
	private Document dom;
	private String lang;
	private String lastKey;

	public Importer() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		builder = dbf.newDocumentBuilder();
	}

	public void importResource(Resource res, File valuesDir, String language) throws TransformerException, TransformerFactoryConfigurationError, IOException {
		dom = builder.newDocument();
		root = dom.createElement("resources");
		dom.appendChild(root);
		lang = language;
		lastKey="";

		for (ResourceEntry entry : res.getSortedEntries()) {
			entry.visit(this);
		}
		
		saveDocument(res, valuesDir, dom);
	}

	private void saveDocument(Resource res, File valuesDir, Document dom)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException, IOException {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");		
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");

		File target = new File(valuesDir,res.getFileName());
		
//		if (target.exists()){
//			Files.copy(target.toPath(), new File(target.getAbsolutePath() + ".tmp").toPath(), StandardCopyOption.REPLACE_EXISTING);
//		}
		
		DOMSource source = new DOMSource(dom);
		StreamResult result = new StreamResult(target);

		transformer.transform(source, result);
	}

	public void visitStringArray(StringArray stringArray) {
		addEmptyLine();
		
		Element node = dom.createElement("string-array");		
		node.setAttribute("name", stringArray.getKey());
		
		for (Term term : stringArray.getTerms()) {
			if (term!=null){
				String translation = term.getTranslation(lang);
				if (StringUtils.isNotEmpty(translation)){
					Element item = dom.createElement("item");							
					item.setTextContent(escape(translation));
					node.appendChild(item);
				}
			}
		}
		
		if (node.getChildNodes().getLength()!=0){
			root.appendChild(node);
		}
	}
	
	public void visitTypedArray(TypedArray typedArray) {
		addEmptyLine();
		
		Element node = dom.createElement("array");		
		node.setAttribute("name", typedArray.getKey());
		
		for (Term term : typedArray.getTerms()) {
			if (term!=null){
				String translation = term.getTranslation(lang);
				if (StringUtils.isNotEmpty(translation)){
					Element item = dom.createElement("item");							
					item.setTextContent(translation);
					node.appendChild(item);
				}
			}
		}
		
		if (ModelLoader.DEFAULT_LANGUAGE.equals(lang) || node.getChildNodes().getLength()!=0 ){
			root.appendChild(node);
		}
		
	}

	private void addEmptyLine() {
		root.appendChild(dom.createTextNode(""));
	}

	public void visitPlural(Plural plural) {			
		addEmptyLine();
		Element node = dom.createElement("plurals");		
		node.setAttribute("name", plural.getKey());
		
		for (Map.Entry<String, Term> term : plural.getTerms().entrySet()) {			
			String translation = term.getValue().getTranslation(lang);
			if (StringUtils.isNotEmpty(translation)){
				Element item = dom.createElement("item");	
				item.setAttribute("quantity", term.getKey());
				item.setTextContent(escape(translation));
				node.appendChild(item);
			}			
		}
		
		root.appendChild(node);
		
	}

	public void visitTerm(Term term) {	
		addLetterCommentIfNeeded(term);
		
		String translation = term.getTranslation(lang);
		if (StringUtils.isNotEmpty(translation)){
			createTermNode(term, translation);
		} else {
			if (term.allTranslationsAreEmpty() && ModelLoader.DEFAULT_LANGUAGE.equals(lang)){
				createTermNode(term,"");
			}
		}
	}

	private void createTermNode(Term term, String translation) {
		Element node = dom.createElement("string");		
		node.setAttribute("name", term.getKey());			
		
		node.setTextContent(escape(translation));
		root.appendChild(node);
	}

	private String escape(String translation) {
		String value = translation.replace("'", "\\'");
		value = value.replace("\"", "\\\"");
		
		return value;
	}

	private void addLetterCommentIfNeeded(ResourceEntry entry) {
		String firstKeyLetter = entry.getKey().substring(0,1).toUpperCase();
		if (!firstKeyLetter.equals(lastKey)){
			addEmptyLine();			
			root.appendChild(dom.createComment(firstKeyLetter));
			addEmptyLine();	
		}
		
		lastKey = firstKeyLetter;
	}	
		
}
