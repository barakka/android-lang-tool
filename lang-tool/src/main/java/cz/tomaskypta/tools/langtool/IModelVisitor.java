package cz.tomaskypta.tools.langtool;

import cz.tomaskypta.tools.langtool.model.Plural;
import cz.tomaskypta.tools.langtool.model.StringArray;
import cz.tomaskypta.tools.langtool.model.Term;
import cz.tomaskypta.tools.langtool.model.TypedArray;

public interface IModelVisitor {

	public abstract void visitStringArray(StringArray stringArray);

	public abstract void visitPlural(Plural plural);

	public abstract void visitTerm(Term term);

	public abstract void visitTypedArray(TypedArray typedArray);

}
