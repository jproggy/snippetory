package org.jproggy.snippetory.engine;

public class TextPosition {
	private final int line;
	private final int position;
	public TextPosition(int line, int position) {
		super();
		this.line = line;
		this.position = position;
	}
	public int getLine() {
		return line;
	}
	public int getPosition() {
		return position;
	}
}
