///////////////////////////////////////////////////////////////////////////////
// File: StatisticsHarvester.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.01
// Comments: Obtains the axiom counts and ratios of all the ontologies in 
// 		a list of files
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class StatisticsHarvester {
	
	
	public static synchronized void writeMessage (PrintWriter out, String str) {
		out.println(str); 
		out.flush(); 
	}
	
	public static void collectStatistics (File[] ontologyFiles, File outFile) throws Exception {
		PrintWriter out = new PrintWriter(outFile);
		out.println("ontFilename;#Axioms;#LogicalAxioms;#TBoxAxioms;#ABoxAxioms;Ratio");
		out.flush(); 

		Stream<File> ontFiles = Arrays.stream(ontologyFiles);
		
		ontFiles.parallel().forEach( ontFile -> {
			System.out.println("Processing "+ontFile+"...");
			OWLOntologyManager om = OWLManager.createOWLOntologyManager();
			OWLOntology ont = null; 
			try {
				om = OWLManager.createOWLOntologyManager(); 
				ont = om.loadOntologyFromOntologyDocument(ontFile);
				double TBoxAxioms = 0; 
				double ABoxAxioms = 0;
				TBoxAxioms = ont.getTBoxAxioms(true).size(); 
				ABoxAxioms = ont.getABoxAxioms(true).size(); 
				writeMessage(out, ontFile.toString()+";"+ont.getAxiomCount()+";"+
						ont.getLogicalAxiomCount()+";"+
						TBoxAxioms+";"
						+ABoxAxioms+";"+
						(ABoxAxioms/TBoxAxioms));
			}
			catch (Exception e) {
				e.printStackTrace(); 
			}
		});
		
		out.flush(); 
		out.close(); 
	}
}
