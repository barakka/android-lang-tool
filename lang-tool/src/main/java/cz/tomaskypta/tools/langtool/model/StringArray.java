package cz.tomaskypta.tools.langtool.model;

import java.util.Arrays;

import cz.tomaskypta.tools.langtool.IModelVisitor;

public class StringArray extends ResourceEntry {

	private Term[] terms;
	
	public StringArray(String key) {
		super(key);
		terms = new Term[10];
	}

	public Term getPosition(int position) {
		
		if (position>=terms.length){
			terms = Arrays.copyOf(terms, position*2);
		}
		
		if (terms[position]==null){			
			terms[position] = new Term(Integer.toString(position));						
		} 
		
		return terms[position];
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Key: " + this.getKey() + "\n");
		
		for (Term t : terms) {
			if (t!=null){
				builder.append("\t" + t.toString() + "\n");
			}
		}
		
		return builder.toString();
	}

	@Override
	public void visit(IModelVisitor exporter) {		
		exporter.visitStringArray(this);
	}

	public Term[] getTerms() {		
		return terms;
	}
}
