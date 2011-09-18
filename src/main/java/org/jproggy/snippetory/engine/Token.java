package org.jproggy.snippetory.impl;

import java.util.LinkedHashMap;

import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.Syntax.TokenType;


public class Token {
	private final LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>();
	private final String name;
	private final String content;
	private final Syntax.TokenType type;
	private final int position;
	
	public Token(String name, String content, TokenType type, int position) {
		super();
		this.name = name;
		this.content = content;
		this.type = type;
		this.position = position;
	}

	public LinkedHashMap<String, String> getAttributes() {
		return attributes;
	}

	public String getName() {
		return name;
	}

	public Syntax.TokenType getType() {
		return type;
	}

	/**
	 *  as it's taken from template data 
	 */
	public String getContent() {
		return content;
	}

	/**
	 *  the position where the content starts within the entire template data.
	 *  It dosen't matter where this syntax started to be used.  
	 */
	public int getPosition() {
		return position;
	}
}
