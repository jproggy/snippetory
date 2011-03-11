package de.jproggy.templa.impl;

public class TemplaException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TemplaException() {
		super();
	}

	public TemplaException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplaException(String message) {
		super(message);
	}

	public TemplaException(Throwable cause) {
		super(cause);
	}

}
