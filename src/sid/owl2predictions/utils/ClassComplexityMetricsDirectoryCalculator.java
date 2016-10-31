///////////////////////////////////////////////////////////////////////////////
// File: ClassComplexityMetricsDirectoryCalculator .java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Obtains the classComplexityMetrics metrics for all the ontologies in a given 
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
import sid.owl2predictions.ClassComplexityAssertionsMetrics;
import sid.owl2predictions.ClassComplexityAssertionsMetricsCalculator;
import sid.owl2predictions.DataPropertyAssertionsMetrics;
import sid.owl2predictions.DataPropertyAssertionsMetricsCalculator;
import sid.owl2predictions.ObjectPropertyAssertionsMetrics;
import sid.owl2predictions.ObjectPropertyAssertionsMetricsCalculator;

public class ClassComplexityMetricsDirectoryCalculator {

	public static void main(String[] args) {
		File directory = new File(args[0]);
		if (directory.isDirectory()) {
			// we assume that every single file ending in .owl that is in the directory is an ontology 
			// to be chopped 
			File[] ontologyList = directory.listFiles(new OWLFileFilter());
			OWLOntologyManager om = null;
			OWLOntology currentOntology = null; 			
			ArrayList<File> wrongProcessedFiles = new ArrayList<>();
			
			ClassComplexityAssertionsMetrics complexityMetrics = null; 
			ClassComplexityAssertionsMetricsCalculator compCalculator = null; 
			
			File metricResults = new File(directory.toString()+File.separator+args[1]); 
			
			try (PrintWriter outResults = new PrintWriter(metricResults)) {
				printHeader(outResults); 
				int counter=0; 
				for (File currentOntologyFile: ontologyList) {
					if (counter%10 == 0)  {
						System.out.println(counter +" of "+ontologyList.length+" ontologies processed."); 
					}
					counter++; 
					try {
						om = OWLManager.createOWLOntologyManager(); 
						currentOntology = om.loadOntologyFromOntologyDocument(currentOntologyFile);
						outResults.print(currentOntologyFile.getName()+"\t"); 

						compCalculator = new ClassComplexityAssertionsMetricsCalculator(currentOntology);
						complexityMetrics = compCalculator.calculateClassComplexityMetrics(); 
						outResults.println(complexityMetrics.toString());
							
						outResults.flush(); 
					}
					catch (Exception e) {
						wrongProcessedFiles.add(currentOntologyFile); 
					}
				}
				
				File resultStats = new File(directory.toString()+File.separator+"resultStats.csv"); 
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
		out.println("Ontology\t"+ClassComplexityAssertionsMetrics.headers()); 
	}
	
	public static class OWLFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.toString().endsWith(".owl"); 
		}
	}
}
