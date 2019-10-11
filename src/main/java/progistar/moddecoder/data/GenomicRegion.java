package progistar.moddecoder.data;

import java.util.ArrayList;

public class GenomicRegion extends Region{

	private int chrIndex = 0;
	public ArrayList<SAMCord> matchedSAMs = new ArrayList<SAMCord>();
	
	public GenomicRegion(String chr, int startLocus, int endLocus, int baseIndex, boolean isStartInclusive,
			boolean isEndInclusive) {
		super(startLocus, endLocus, baseIndex, isStartInclusive, isEndInclusive);
		this.chrIndex = ChrIndexConvertor.chrToIndex(chr);
	}
	
	public void setChrIndex (String chr) {
		this.chrIndex = ChrIndexConvertor.chrToIndex(chr);
	}
	
	public int getChrIndex () {
		return chrIndex;
	}
}
