package progistar.moddecoder.starter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import progistar.moddecoder.data.BED;
import progistar.moddecoder.data.BEDRecord;
import progistar.moddecoder.data.ChrIndexConvertor;
import progistar.moddecoder.data.Gene;
import progistar.moddecoder.data.GenomicRegion;
import progistar.moddecoder.data.MODdecoder;
import progistar.moddecoder.data.SAM;
import progistar.moddecoder.data.SAMCord;
import progistar.moddecoder.data.Translator;

public class App {

	public static void main(String[] args) throws IOException {

		SAM sam = new SAM(args[1]);
		BED bed = new BED(args[2], 0);
		ArrayList<MODdecoder> modRes = readModRes(args[3]);
		linkBEDtoRES(bed, modRes);
		linkSAMtoBED(bed, sam);
		getMODReads(modRes);
	}
	
	public static void getMODReads (ArrayList<MODdecoder> modRes) {
		int size = modRes.size();
		for(int i=0; i<size; i++) {
			MODdecoder modDecoder = modRes.get(i);
			BEDRecord record = modDecoder.linkedBEDRecord;
			if(record == null) continue;
			String mutatedPeptide = modDecoder.mutatedPeptide;
			
			ArrayList<Gene> genes = record.genes;
			for(Gene gene : genes) {
				int totCount = 0;
				Hashtable[] sams = new Hashtable[gene.regions.size()];
				
				int index = 0;
				StringBuilder genomicRegions = new StringBuilder();
				for(GenomicRegion region : gene.regions) {
					if(genomicRegions.length() != 0) genomicRegions.append(";");
					genomicRegions.append(ChrIndexConvertor.indexToChr(region.getChrIndex())).append(":").append(region.getStartIndex()).append("-").append(region.getEndIndex());
					sams[index] = new Hashtable<String, ArrayList<SAMCord>>();
					for(SAMCord samc : region.matchedSAMs) {
						String qName = samc.qName;
						ArrayList<SAMCord> selectedSams = null;
						if(sams[index].get(qName) == null) selectedSams = new ArrayList<SAMCord>();
						else {
							selectedSams = (ArrayList<SAMCord>) sams[index].get(qName);
						}
						selectedSams.add(samc);
						
						sams[index].put(qName, selectedSams);
					}
					index++;
				}
				
				if(sams[0].size() != 0) {
					Iterator<String> keys = (Iterator<String>) sams[0].keys();
					while(keys.hasNext()) {
						String qName = keys.next();
						ArrayList<SAMCord> selectedSams = (ArrayList<SAMCord>)sams[0].get(qName);
						for(SAMCord samc : selectedSams) {
							GenomicRegion region = gene.regions.get(0);
							StringBuilder str = new StringBuilder(samc.getSequence(region.getStartIndex(), region.getEndIndex()));
							totCount += getMatchedSequence(qName, str, sams, gene.regions, 1, index, mutatedPeptide, gene.strand);
						}
					}
				}
				
				System.out.println(modDecoder.fullRecord+"\t"+gene.geneID+"\t"+genomicRegions.toString()+"\t"+totCount+"\t"+index);
			}
			
		}
	}
	
	public static int getMatchedSequence (String qName, StringBuilder str, Hashtable[] sams, ArrayList<GenomicRegion> regions, int curIndex, int maxIndex, String mutatedPeptide, boolean strand) {
		// check
		if(curIndex == maxIndex) {
			if(strand) {
				if(Translator.translation(str.toString(), 0).equalsIgnoreCase(mutatedPeptide)) return 1;
			}
			else if(!strand) {
				if(Translator.reverseComplementTranslation(str.toString(), 0).equalsIgnoreCase(mutatedPeptide)) return 1;
			}
			
			return 0;
		}
		
		int totCount = 0;
		
		if(sams[curIndex].get(qName) == null) return 0;
		
		ArrayList<SAMCord> selectedSams = (ArrayList<SAMCord>) sams[curIndex].get(qName);
		int curSeqPos = str.length();
		for(SAMCord samc : selectedSams) {
			GenomicRegion region = regions.get(curIndex);
			str.setLength(curSeqPos);
			str.append(samc.getSequence(region.getStartIndex(), region.getEndIndex()));
			totCount += getMatchedSequence(qName, str, sams, regions, curIndex+1, maxIndex, mutatedPeptide, strand);
		}
		
		return totCount;
		
	}
	
	public static void linkSAMtoBED (BED bed, SAM sam) {
		int size = bed.sizeOfRecords();
		for(int i=0; i<size; i++) {
			BEDRecord record = bed.getRecordByIndex(i);
			ArrayList<Gene> genes = record.genes;
			for(int j=0; j<genes.size(); j++) {
				Gene gene = genes.get(j);
				for(GenomicRegion region : gene.regions) {
					region.matchedSAMs = sam.getCordsByRegion(region.getChrIndex(), region.getStartIndex(), region.getEndIndex());
				}
			}
		}
	}
	
	public static void linkBEDtoRES (BED bed, ArrayList<MODdecoder> modRes) {
		// hashing BED by peptide
		Hashtable<String, BEDRecord> hashing = new Hashtable<String, BEDRecord>();
		int size = bed.sizeOfRecords();
		for(int i=0; i<size; i++) hashing.put(bed.getRecordByIndex(i).peptide, bed.getRecordByIndex(i));
		
		// mapping bed to modRes
		size = modRes.size();
		for(int i=0; i<size; i++) modRes.get(i).linkedBEDRecord = hashing.get(modRes.get(i).wildPeptide);	
	}	
	
	public static ArrayList<MODdecoder> readModRes (String fileName) throws IOException {
		ArrayList<MODdecoder> modRes = new ArrayList<MODdecoder>();
		BufferedReader BR = new BufferedReader(new FileReader(fileName));
		String line = null;
		
		BR.readLine(); // consume header
		while((line = BR.readLine()) != null) {
			String[] fields = line.split("\t");
			String peptide = fields[6].replaceAll("[+-0123456789.*]", "");
			String modInfo = fields[12];
			int modPos = Integer.parseInt(modInfo.split("\\[")[0].split("\\,")[1])-1;
			char modAA = modInfo.split("\\>")[1].charAt(0);
			StringBuilder mutatedPeptide = new StringBuilder(peptide);
			mutatedPeptide.setCharAt(modPos, modAA);
			
			MODdecoder mod = new MODdecoder();
			mod.modInfo = modInfo;
			mod.modPos = modPos;
			mod.mutatedPeptide = mutatedPeptide.toString();
			mod.wildPeptide = peptide;
			mod.fullRecord = line;
			
			modRes.add(mod);
		}
		
		
		BR.close();
		
		return modRes;
	}
}
