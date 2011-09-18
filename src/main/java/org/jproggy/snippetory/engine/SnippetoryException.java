package org.jproggy.snippetory.engine;

public class SnippetoryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SnippetoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public SnippetoryException(String message) {
		super(message);
	}

	public SnippetoryException(Throwable cause) {
		super(cause);
	}

}
