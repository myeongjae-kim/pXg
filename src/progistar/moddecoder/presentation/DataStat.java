package progistar.moddecoder.presentation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class DataStat {

	public static void main(String[] args) throws IOException {
		printPSMs("C:\\Users\\progi\\Desktop\\MODDecoder\\PDAC_SAAV.tsv");
	}
	
	public static void printPSMs (String fileName) throws IOException {
		BufferedReader BR = new BufferedReader(new FileReader(fileName));
		String line = null;
		Hashtable<String, ArrayList<String>> result = new Hashtable<String, ArrayList<String>>();
		Hashtable<String, Hashtable<String, String>> peptides = new Hashtable<String, Hashtable<String, String>>();
		Hashtable<String, String> totPepts = new Hashtable<String, String>();
		while((line = BR.readLine()) != null) {
			String[] field = line.split("\t");
			String set = field[2];
			String peptide = field[6].replaceAll("[+-0123456789.*]", "");
			ArrayList<String> psms = result.get(set);
			if(psms == null) psms = new ArrayList<String>();
			psms.add(field[3]+"\t"+field[4]+"\t"+field[5]+"\t"+field[6]);
			result.put(set, psms);
			
			Hashtable<String, String> pepts = peptides.get(set);
			if(pepts == null) pepts = new Hashtable<String, String>();
			pepts.put(peptide, "");
			peptides.put(set, pepts);
			totPepts.put(peptide,"");
		}
		BR.close();
		
		System.out.println("Total of peptides: "+totPepts.size());
		Iterator<String> sets = (Iterator<String>)result.keys();
		System.out.println("PSMs per Set");
		while(sets.hasNext()) {
			String set = sets.next();
			System.out.println(set+"\t"+result.get(set).size()+"\t"+peptides.get(set).size());
		}
		
	}
}
