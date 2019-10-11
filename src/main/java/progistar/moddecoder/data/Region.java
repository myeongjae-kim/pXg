package progistar.moddecoder.data;

public abstract class Region {

	public static final int BASE_INDEX = 1;
	private int startIndex = -1;
	private int endIndex = -1;
	private int baseIndex = 0;
	
	/**
	 * Global rules.<br>
	 * 1. baseIndex is parsed to one-based.<br>
	 * if you put a non one-based, it automatically changes it. <br>
	 * 2. [start,end) rule.<br>
	 * if you put a [start, end], it automatically changes it. <br>
	 * 
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @param baseIndex
	 * @param isStartInclusive
	 * @param isEndInclusive
	 */
	public Region (int startIndex, int endIndex, int baseIndex, boolean isStartInclusive, boolean isEndInclusive) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.baseIndex = baseIndex;
		
		// inclusive change
		if(!isStartInclusive) this.startIndex += 1;
		if(isEndInclusive) this.endIndex += 1;
		if(this.baseIndex != BASE_INDEX) {
			this.startIndex -= (this.baseIndex - BASE_INDEX);
			this.endIndex -= (this.baseIndex - BASE_INDEX);
		}
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndLocus(int endLocus) {
		this.endIndex = endLocus;
	}

	
	
	
}
