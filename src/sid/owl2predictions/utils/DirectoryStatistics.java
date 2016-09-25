///////////////////////////////////////////////////////////////////////////////
// File: DirectoryStatistics.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.01
// Comments:  
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class DirectoryStatistics {

	public static void main(String[] args) {
		
		File directory = new File(args[0]); 
		
		if (directory.isDirectory()) {
			// we assume that every single file ending in .owl that is in the directory is an ontology 
			// to be chopped
			try {
				File[] ontologyList = directory.listFiles(new OWLFileFilter());
				
				File resultStats = new File(directory.toString()+File.separator+"resultStats.csv");
				
				StatisticsHarvester.collectStatistics(ontologyList, resultStats);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
		}
		else {
			System.out.println("Not a directory ... leaving"); 
		}
		
	}
	
	public static class OWLFileFilter implements FileFilter {
		
		public boolean accept(File pathname) {
			return pathname.toString().endsWith(".owl"); 
		}
	}

}
