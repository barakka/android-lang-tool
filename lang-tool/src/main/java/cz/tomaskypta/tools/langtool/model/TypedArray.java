package cz.tomaskypta.tools.langtool.model;

import cz.tomaskypta.tools.langtool.IModelVisitor;

public class TypedArray extends StringArray{

	public TypedArray(String key) {
		super(key);		
	}

	@Override
	public void visit(IModelVisitor exporter) {
		exporter.visitTypedArray(this);		
	}

}
