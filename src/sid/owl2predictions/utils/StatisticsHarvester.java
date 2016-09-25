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

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class StatisticsHarvester {
	
	public static void collectStatistics (File[] ontologyFiles, File outFile) throws Exception {
		OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null; 
		PrintWriter out = new PrintWriter(outFile);
		out.println("ontFilename;#Axioms;#TBoxAxioms;#ABoxAxioms;Ratio"); 
		for (File ontFile: ontologyFiles) {
			om = OWLManager.createOWLOntologyManager(); 
			ont = om.loadOntologyFromOntologyDocument(ontFile); 
			out.println(ontFile.toString()+";"+ont.getAxiomCount()+";"+ont.getTBoxAxioms(true).size()+";"
					+ont.getABoxAxioms(true).size()+";"+((double)ont.getABoxAxioms(true).size()/ont.getTBoxAxioms(true).size())); 			
		}
		out.flush(); 
		out.close(); 
	}
}
