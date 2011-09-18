package org.jproggy.snippetory.impl;

public class ParseError extends SnippetoryException {
	private static final long serialVersionUID = 1L;

	public ParseError(String message, Token at) {
		super(message + "  " + toMessage(at));
	}

	public ParseError(Throwable cause, Token at) {
		super(toMessage(at), cause);
	}

	private static String toMessage(Token at) {
		return "Error while parsing " + at.getContent() + " at position " + at.getPosition();
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
