///////////////////////////////////////////////////////////////////////////////
// File: DirectoryOntologyChopper.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.01
// Comments: Runs the OntologyChopper on every ontology in the given directory 
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

public class DirectoryOntologyChopper {

	public static void main(String[] args) {
		
		File directory = new File(args[0]); 
		
		if (directory.isDirectory()) {
			// we assume that every single file ending in .owl that is in the directory is an ontology 
			// to be chopped 
			File[] ontologyList = directory.listFiles(new OWLFileFilter());
			OWLOntologyManager om = null;
			OWLOntology currentOntology = null; 
			ArrayList<File> wrongProcessedFiles = new ArrayList<>();
			OntologyChopper ontChopper = null; 
			for (File currentOntologyFile: ontologyList) {
				try {
					om = OWLManager.createOWLOntologyManager(); 
					currentOntology = om.loadOntologyFromOntologyDocument(currentOntologyFile);
					ontChopper = new OntologyChopper(currentOntology, 10, 5.0); 
					ontChopper.chopOntology(currentOntologyFile.toString());
				}
				catch (Exception e) {
					wrongProcessedFiles.add(currentOntologyFile); 
				}
			}
			
			File resultStats = new File(directory.toString()+File.separator+"resultStats.csv"); 
			if (!wrongProcessedFiles.isEmpty()) {
				try {
					PrintWriter out = new PrintWriter(resultStats);
					out.println("There were "+wrongProcessedFiles.size()+" ontologies which were wrong processed"); 
					for (File f: wrongProcessedFiles) {
						out.println(f.toString()); 
					}
					out.flush();
					out.close();
				}
				catch (Exception e) {
					e.printStackTrace(); 
				}
			}
			else {
				// everything went OK
				try {
					ontologyList = directory.listFiles(new OWLFileFilter()); 
					StatisticsHarvester.collectStatistics(ontologyList, resultStats);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
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
