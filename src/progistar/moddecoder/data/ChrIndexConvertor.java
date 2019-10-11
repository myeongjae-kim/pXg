package progistar.moddecoder.data;

public class ChrIndexConvertor {

	public static final String[] CHR_INDEX_LIST = {"1", "2", "3", "4", "5", "6", "7", "8", "9", 
													"10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
													"20", "21", "22", "X", "Y", "MT"};
	
	/**
	 * Supported index: chr1, chr2, ..., chr22, chrX, chrY, chrMT. <br>
	 * chr1 to 0, chr2 to 1, ..., chrX to 22, chrY to 23, chrMT to 24. <br>
	 * If you give an unsupported index, it will return -1.
	 * 
	 * @param chr
	 * @return
	 */
	public static int chrToIndex (String chr) {
		int indexValue = -1;
		try {
			chr = chr.toLowerCase();
			if(chr.startsWith("chr")) chr = chr.substring(3);
			for(int index = 0; index < CHR_INDEX_LIST.length; index++) {
				if(chr.equalsIgnoreCase(CHR_INDEX_LIST[index])) {
					indexValue = index; break;
				}
			}
		} catch (Exception e) {
			System.err.println("chrIndexConversion: "+chr+" is not supported index value. return -1.");
		}
		return indexValue;
	}
	
	public static String indexToChr (int index) {
		return "chr"+CHR_INDEX_LIST[index];
	}
}
