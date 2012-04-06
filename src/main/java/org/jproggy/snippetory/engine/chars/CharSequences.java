package org.jproggy.snippetory.engine.chars;


public abstract class CharSequences implements CharSequence {
	private CharSequence recentCS = null;
	private int csIndex = -1;
	private int recentStart = 0;
	
	protected abstract int partCount();
	
	protected abstract CharSequence part(int index);
	
	@Override
	public char charAt(int index) {
		if (index < recentStart) {
			recentStart = 0;
			recentCS = null;
			csIndex = -1;
		}
		while ((recentCS == null || (index - recentStart) >= recentCS.length()) && csIndex + 1 < partCount()) {
			if (recentCS != null) {
				recentStart += recentCS.length();
			}
			csIndex++;
			recentCS = part(csIndex);
		}
		return recentCS.charAt(index - recentStart);
	}

	@Override
	public int length() {
		int l = 0;
		for (int i = 0; i < partCount(); i++) l += part(i).length(); 
		return l;
	}
	
	@Override
	public CharSequence subSequence(int start, int end) {
		return new MyCharSeq(start, end);
	}
	
	private class MyCharSeq implements CharSequence {
		private final int start, end;
		public MyCharSeq(int start, int end) {
			this.start = start;
			this.end = end;
		}
		@Override
		public char charAt(int index) {
			return CharSequences.this.charAt(index + start);
		}
		
		@Override
		public int length() {
			return end - start;
		}
		
		@Override
		public CharSequence subSequence(int start, int end) {
			return new MyCharSeq(start + this.start, end + this.start);
		}
	}
}
