package org.jproggy.snippetory.spi;

/**
 * SyntaxID is typically implemented by an enum used to identify a named {@link Syntax}. 
 * However, to be able to use a Syntax it has to be registered via the 
 * {@link Syntax.Registry#register(SyntaxID, Syntax) Syntax.REGISTRY.register} method.
 * 
 * @author B. Ebertz
 */
public interface SyntaxID {
	/**
	 * @return a name identifying the syntax uniquely. 
	 */
	String getName();
}
