package de.jproggy.snippetory;

import de.jproggy.snippetory.spi.SyntaxID;

public enum Syntaxes implements SyntaxID {
	XML_ALIKE,
	HIDDEN_BLOCKS;
	
	public String getName() { return name(); }
}