package progistar.moddecoder.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class SAM {

	public TreeMap<Integer, ArrayList<SAMCord>> locatedMapper = null;
	
	private enum FieldIndex {
		qname(0), chr(2), startPos(3), cigar(5), sequence(9);
		
		public int value = 0;
		
		private FieldIndex (int value) {
			this.value = value;
			
		}
	}
	
	private String sampleID = null;
	
	
	public SAM (String fileName) {
		try {
			this.locatedMapper = new TreeMap<Integer, ArrayList<SAMCord>>();
			parse(fileName);
		}catch (IOException ioe) {System.err.println("Failed to read SAM file.");}
	}
	
	private void parse (String fileName) throws IOException {
		File file = new File(fileName);
		// we assumed that the fileName "PDACXXXX"-"XXXXX".
		// "-" is a delimiter.
		this.sampleID = file.getName().split("\\-")[0];
		BufferedReader BR = new BufferedReader(new FileReader(file));
		String line = null;
		
		Hashtable<String, Integer> qNameCount = new Hashtable<String, Integer>();
		int index = 0;
		while((line = BR.readLine()) != null) {
			if(line.length() == 0) continue;
			
			String[] field = line.split("\t");
			
			// parsing field
			SAMCord record = new SAMCord();
			record.chr = field[FieldIndex.chr.value];
			record.cigar = field[FieldIndex.cigar.value];
			record.sequence = field[FieldIndex.sequence.value];
			record.startPos = Integer.parseInt(field[FieldIndex.startPos.value]);
			record.qName = field[FieldIndex.qname.value];
			record.index = index++;
			record.init();
			
			// add record
			ArrayList<SAMCord> sams = this.locatedMapper.get(record.startPos);
			if(sams == null) sams = new ArrayList<SAMCord>();
			sams.add(record);
			this.locatedMapper.put(record.startPos, sams);
			
			sams = this.locatedMapper.get(record.endPos);
			if(sams == null) sams = new ArrayList<SAMCord>();
			sams.add(record);
			this.locatedMapper.put(record.endPos, sams);
			
			Integer count = qNameCount.get(record.qName);
			if(count == null) count = 0;
			count ++;
			qNameCount.put(record.qName, count);
		}
		
		// max count
		Iterator<String> counts = (Iterator<String>)qNameCount.keys();
		int max = 0;
		while(counts.hasNext()) {
			max = Math.max(max, qNameCount.get(counts.next()));
		}
		System.out.println("maximum number of duplicated qNames:\t"+max);
		
		BR.close();
	}
	
	public ArrayList<SAMCord> getCordsByRegion (int chrIndex, int start, int end) {
		int RNA_SEQ_READ_LENGTH = 100; // the value depends on the genomic/transcriptomic experiments.
		SortedMap<Integer, ArrayList<SAMCord>> samcords = this.locatedMapper.subMap(start-RNA_SEQ_READ_LENGTH, end+RNA_SEQ_READ_LENGTH);
		ArrayList<SAMCord> cords = new ArrayList<SAMCord>();
		
		Hashtable<Integer, Boolean> indexCheck = new Hashtable<Integer, Boolean>();
		if(samcords != null) {
			Iterator<Integer> keys = (Iterator<Integer>)samcords.keySet().iterator();
			while(keys.hasNext()) {
				ArrayList<SAMCord> candidates = samcords.get(keys.next());
				for(SAMCord candidate : candidates) {
					if(indexCheck.get(candidate.index) != null) continue;
					indexCheck.put(candidate.index, true);
					int index = ChrIndexConvertor.chrToIndex(candidate.chr);
					if(index != chrIndex) continue;
					if(candidate.isCoveredGivenRegion(start, end)) cords.add(candidate);
				}
			}
		}
		
		return cords;
	}
	
}
