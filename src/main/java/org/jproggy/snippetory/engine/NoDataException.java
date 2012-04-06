package org.jproggy.snippetory.engine;

public class NoDataException extends SnippetoryException {
	private static final long serialVersionUID = 1L;

	public NoDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoDataException(String message) {
		super(message);
	}

	public NoDataException(Throwable cause) {
		super(cause);
	}
}
