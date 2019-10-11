package progistar.moddecoder.data;

public class Translator {

	/**
	 * frame is a start position. This is zero-base.
	 * 
	 * @param nucleotides
	 * @param frame
	 * @return
	 */
	public static String translation (String nucleotides, int frame) {
		StringBuilder peptides = new StringBuilder();
		for(int position=frame; position<nucleotides.length()-2; position+=3) {
			peptides.append(Codon.nuclToAmino(nucleotides.substring(position,position+3)));
		}
		return peptides.toString();
	}
	
	public static String reverseComplementTranslation (String nucleotides, int frame) {
		StringBuilder peptides = new StringBuilder();
		StringBuilder reverseComplementNTs = new StringBuilder(nucleotides);
		for(int i=0; i<nucleotides.length(); i++) {
			switch(reverseComplementNTs.charAt(i)) {
				case 'A': reverseComplementNTs.setCharAt(i, 'T'); break;
				case 'C': reverseComplementNTs.setCharAt(i, 'G'); break;
				case 'T': reverseComplementNTs.setCharAt(i, 'A'); break;
				case 'G': reverseComplementNTs.setCharAt(i, 'C'); break;
				default : break;
			}
		}
		
		nucleotides = reverseComplementNTs.reverse().toString();
		for(int position=frame; position<nucleotides.length()-2; position+=3) {
			peptides.append(Codon.nuclToAmino(nucleotides.substring(position,position+3)));
		}
		return peptides.toString();
	}
}
