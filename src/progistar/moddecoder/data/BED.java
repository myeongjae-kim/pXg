package progistar.moddecoder.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class BED {
	
	private enum FieldIndex {
		chr(0), start(1), end(2), label(3), score(4), 
		strand(5), thickStart(6), thickEnd(7), itemRGB(8);
		
		public int value = 0;
		
		private FieldIndex (int value) {
			this.value = value;
			
		}
	}

	private ArrayList<BEDRecord> records = null;
	private StringBuilder header = null;
	
	
	public BED (String fileName, int skipLine) {
		this.records = new ArrayList<BEDRecord>();
		this.header = new StringBuilder();
		
		try {
			BufferedReader BR = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = BR.readLine()) != null) {
				if(skipLine <= 0) {
					String[] field = line.split("\t");
					String chr = field[FieldIndex.chr.value];
					int start = Integer.parseInt(field[FieldIndex.start.value]);
					int end = Integer.parseInt(field[FieldIndex.end.value]);
					String label = field[FieldIndex.label.value];
					String peptide = label.split("\\_")[0];
					String geneID = label.split("\\_")[1];
					boolean strand = label.split("_")[2].equalsIgnoreCase("+") ? true : false;
					
					BEDRecord record = new BEDRecord();
					record.peptide = peptide;
					
					Gene gene = new Gene();
					gene.geneID = geneID;
					gene.strand = strand;
					
					GenomicRegion region = new GenomicRegion(chr, start, end, 0, true, false);
					
					gene.regions.add(region);
					record.genes.add(gene);
					
					this.records.add(record);
					
				} else {
					this.header.append(line).append("\n");
					skipLine --;
				}
			}
			BR.close();
			System.out.println(records.size());
			// multiple exons: aggregate peptide with the same gene id
			Hashtable<String, BEDRecord> aggregation = new Hashtable<String, BEDRecord>();
			for(int i=0; i<records.size(); i++) {
				String peptide = records.get(i).peptide;
				String geneID = records.get(i).genes.get(0).geneID;
				String key = peptide+"_"+geneID;
				
				if(peptide.length() == (records.get(i).genes.get(0).regions.get(0).getEndIndex()-records.get(i).genes.get(0).regions.get(0).getStartIndex())/3) continue;
				
				BEDRecord record = aggregation.get(key);
				if(record == null) record = records.get(i);
				else {
					record.genes.get(0).regions.addAll(records.get(i).genes.get(0).regions);
					records.remove(i--);
				}
				aggregation.put(key, record);
			}
			System.out.println(records.size());
			
			// multiply-mapped peptide: aggregate peptide
			aggregation = new Hashtable<String, BEDRecord>();
			for(int i=0; i<records.size(); i++) {
				String peptide = records.get(i).peptide;
				String key = peptide;
				
				BEDRecord record = aggregation.get(key);
				if(record == null) record = records.get(i);
				else {
					record.genes.addAll(records.get(i).genes);
					records.remove(i--);
				}
				aggregation.put(key, record);
			}
			System.out.println(records.size());
		}catch (IOException ioe) {
			
		}
	}
	
	public BEDRecord getRecordByIndex (int index) {
		return this.records.get(index);
	}
	
	public int sizeOfRecords () {
		return this.records.size();
	}
	
	public void write (String fileName) {
		try {
			BufferedWriter BW = new BufferedWriter(new FileWriter(fileName));
			int size = sizeOfRecords();
			
			// added header if available.
			if(this.header.length() != 0) BW.append(this.header.toString());
			
			for(int i=0; i<size; i++) {
				BW.append(this.records.get(i).toString());
				BW.newLine();
			}
			
			BW.close();
		}catch (IOException ioe) {}
	}
 }
