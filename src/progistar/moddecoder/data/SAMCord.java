package progistar.moddecoder.data;

public class SAMCord {

	public String chr = null;
	public String cigar = null;
	public String sequence = null;
	public int startPos = 0;
	public int endPos = 0;
	public String qName = null;
	public int index = 0;
	public boolean isDeprecated = false; // is discarded?
	
	public void init() {

		
		int charIndex = 0;
		int refConsume = 0;
		int readConsume = 0;
		this.endPos = this.startPos;
		int length = cigar.length();
		
		if(cigar.equalsIgnoreCase("*")) return; // discard this read information.
		
		for(int i=0; i<length; i++) {
			
			// If the character is a marker?
			if(!Character.isDigit(cigar.charAt(i))) {
				int markerSize = Integer.parseInt(cigar.substring(charIndex, i));
				
				switch(cigar.charAt(i)) {
				case 'M': refConsume += markerSize; readConsume += markerSize; break;
				case 'I': readConsume += markerSize; isDeprecated = true; break;
				case 'D': refConsume += markerSize; isDeprecated = true; break;
				case 'H': break; // nothing to do.
				case 'S': this.sequence = sequence.substring(0, readConsume) + this.sequence.substring(readConsume+markerSize); break;
				default: 
					System.err.println("Occurred Exception Case during parsing cigar string.");
					System.err.println(cigar);
				}
				
				charIndex = i+1;
			}
		}
		
		this.endPos += refConsume;
	}
	
	/**
	 * 
	 * [start, end) - 1based
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean isCoveredGivenRegion (int start, int end) {
		// out of range check
		if(isDeprecated) return false;
		if(!(this.startPos <= start && (this.startPos + this.sequence.length()) >= end)) return false;
		return true;
		
	}
	
	public String getSequence (int start, int end) {
		if(!isCoveredGivenRegion(start, end)) return null;
		int startIndex = start - this.startPos;
		int endIndex = end - this.startPos;
		
		return this.sequence.substring(startIndex, endIndex);
	}
}

