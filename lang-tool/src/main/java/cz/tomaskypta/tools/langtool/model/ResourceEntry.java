package cz.tomaskypta.tools.langtool.model;

import java.util.Map;

import cz.tomaskypta.tools.langtool.IModelVisitor;

public abstract class ResourceEntry {	

	private String key;
	
	public ResourceEntry(String key) {
		this.key = key;
	}

	public String getKey() {		
		return key;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Key: " + key);
		
		return builder.toString();
	}

	public abstract void visit(IModelVisitor exporter) ;
}
