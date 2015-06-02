package cz.tomaskypta.tools.langtool.model;

import java.util.HashMap;
import java.util.Map;

import cz.tomaskypta.tools.langtool.IModelVisitor;

public class Plural extends ResourceEntry {

	private Map<String, Term> terms;
	
	public Plural(String key) {
		super(key);
		this.terms = new HashMap<String, Term>();
	}

	public Term getQuantity(String quantity) {
		Term term = terms.get(quantity);
		if (term==null){
			term = new Term(quantity);
			terms.put(quantity, term);
		}
		return term;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Key: " + this.getKey() + "\n");
		
		for (Map.Entry<String, Term> plural : terms.entrySet()) {
			builder.append("\t" + plural.getKey() + "\t" + plural.getValue().toString() + "\n");
		}
		
		return builder.toString();
	}

	@Override
	public void visit(IModelVisitor exporter) {	
		exporter.visitPlural(this);
	}

	public Map<String, Term> getTerms() {		
		return terms;
	}
}
