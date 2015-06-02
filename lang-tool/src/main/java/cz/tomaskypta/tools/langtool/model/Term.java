package cz.tomaskypta.tools.langtool.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cz.tomaskypta.tools.langtool.IModelVisitor;
import cz.tomaskypta.tools.langtool.util.EscapingUtils;

public class Term extends ResourceEntry {

	private Map<String, String> translations;
	
	public Term(String key) {
		super(key);
		this.translations = new HashMap<String, String>();
	}

	public void addTranslation(String language, String textContent) {		
		translations.put(language, unescape(textContent));		
	}
	
	private String unescape(String textContent) {
		String value = EscapingUtils.unescapeQuotes(textContent);
		
		value = value.replace("\\\"", "\"");
		value = value.replace("\\'", "'");
		
		return value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Key: " + this.getKey() + "\t");
		
		for (Map.Entry<String, String> translation : translations.entrySet()) {
			builder.append(translation.getValue()+"\t");
		}
		
		return builder.toString();
	}

	@Override
	public void visit(IModelVisitor exporter) {
		exporter.visitTerm(this);		
	}

	public String getTranslation(String language) {		
		return translations.get(language);
	}

	public boolean allTranslationsAreEmpty() {
		for (String  translation : translations.values()) {
			if (StringUtils.isNotEmpty(translation)){
				return false;
			}
		}
		return true;
	}

}
