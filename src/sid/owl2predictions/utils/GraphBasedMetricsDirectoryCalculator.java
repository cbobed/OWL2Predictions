///////////////////////////////////////////////////////////////////////////////
// File: GraphBasedMetricsDirectoryCalculator .java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Obtains the graph-based metrics for all the ontologies in a given 
// 			directory  
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

import sid.owl2predictions.ClassAssertionsMetrics;
import sid.owl2predictions.ClassAssertionsMetricsCalculator;
import sid.owl2predictions.DataPropertyAssertionsMetrics;
import sid.owl2predictions.DataPropertyAssertionsMetricsCalculator;
import sid.owl2predictions.ObjectPropertyAssertionsMetrics;
import sid.owl2predictions.ObjectPropertyAssertionsMetricsCalculator;
import sid.owl2predictions.SubGraphMetrics;
import sid.owl2predictions.SubGraphMetricsCalculator;

public class GraphBasedMetricsDirectoryCalculator {

	public static void main(String[] args) {
		File directory = new File(args[0]);
		if (directory.isDirectory()) {
			// we assume that every single file ending in .owl that is in the directory is an ontology 
			// to be chopped 
			File[] ontologyList = directory.listFiles(new OWLFileFilter());
			OWLOntologyManager om = null;
			OWLOntology currentOntology = null; 			
			ArrayList<File> wrongProcessedFiles = new ArrayList<>();
			
			SubGraphMetrics sgMetrics = null;
			SubGraphMetricsCalculator sgCalculator = null; 
			
			File metricResults = new File(directory.toString()+File.separator+args[1]); 
			
			try (PrintWriter outResults = new PrintWriter(metricResults)) {
				printHeader(outResults); 
				for (File currentOntologyFile: ontologyList) {
					try {
						om = OWLManager.createOWLOntologyManager(); 
						currentOntology = om.loadOntologyFromOntologyDocument(currentOntologyFile);
						outResults.print(currentOntologyFile.getName()+"\t"); 
						
						sgCalculator = new SubGraphMetricsCalculator(currentOntology); 
						sgMetrics = sgCalculator.calculateMetrics(); 
						outResults.println(sgMetrics.toString());
					
						outResults.flush(); 
					}
					catch (Exception e) {
						wrongProcessedFiles.add(currentOntologyFile); 
					}
				}
				
				File resultStats = new File(directory.toString()+File.separator+"resultGraphBasedErrors.csv"); 
				if (!wrongProcessedFiles.isEmpty()) {
					try (PrintWriter out = new PrintWriter(resultStats)) {
						out.println("There were "+wrongProcessedFiles.size()+" ontologies which were wrong processed"); 
						for (File f: wrongProcessedFiles) {
							out.println(f.toString()); 
						}
						out.flush();
					}
					catch (Exception e) {
						e.printStackTrace(); 
					}
				}		
			}
			catch (Exception e) {
				System.out.println("Problems with the metricsResults file"); 
				System.exit(-1);
			}
		}
		else {
			System.out.println("Not a directory ... leaving"); 
		}
	}
	
	public static void printHeader (PrintWriter out) {		
		String header = "Ontology\tCOA\tSOVA\tROA\t"; 
		header += "N_TOT_SG\tN_AVG_SG\tN_MIN_SG\tN_MAX_SG\tN_STD_SG\tN_AVG_RPA\tN_MIN_RPA\tN_MAX_RPA\tN_STD_RPA\t";
		header += "TOT_SG\tAVG_SG\tMIN_SG\tMAX_SG\tSTD_SG\tAVG_RPA\tMAX_RPA\tMIN_RPA\tSTD_RPA";
		out.println(header); 
	}
	
	public static class OWLFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.toString().endsWith(".owl"); 
		}
	}
}
