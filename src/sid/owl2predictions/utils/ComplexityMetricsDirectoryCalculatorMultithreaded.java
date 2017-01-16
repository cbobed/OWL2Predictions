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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import sid.owl2predictions.complexityMetrics.ClassComplexityAssertionsMetrics;
import sid.owl2predictions.complexityMetrics.DataPropertyComplexityAssertionsMetrics;
import sid.owl2predictions.complexityMetrics.ObjectPropertyComplexityAssertionsMetrics;
import sid.owl2predictions.complexityMetrics.ELProfile.AssertionsComplexityMetricsCalculatorEL;


public class ComplexityMetricsDirectoryCalculatorMultithreaded {

	static int numThreads = 8; 
	public static void main(String[] args) {
		File directory = new File(args[0]);
		if (directory.isDirectory()) {
			// we assume that every single file ending in .owl that is in the directory is an ontology 
			// to be chopped 
			File[] ontologyList = directory.listFiles(new OWLFileFilter());
			OWLOntologyManager om = null;		
			
			ExecutorService executor = Executors.newFixedThreadPool(numThreads);  
			
			File metricResults = new File(directory.toString()+File.separator+args[1]); 
			MetricsRunnable runnable = null; 
			try (PrintWriter outResults = new PrintWriter(metricResults)) {
				printHeader(outResults); 
				int counter=0; 
				for (File currentOntologyFile: ontologyList) {
					if (counter%10 == 0)  {
						System.out.println(counter +" of "+ontologyList.length+" ontologies scheduled."); 
					}
					counter++; 
					runnable = new MetricsRunnable(currentOntologyFile, outResults); 
					executor.execute(runnable);
				}
			
				executor.shutdown();
				try{
					executor.awaitTermination(6000, TimeUnit.MINUTES); 
				}
				catch (InterruptedException e) {
					System.err.println("You should not have waken up the beast!!"); 
				}
				
				File resultStats = new File(directory.toString()+File.separator+"errorResultStats.csv"); 
				if (!MetricsRunnable.wrongProcessedOntologies.isEmpty()) {
					try (PrintWriter out = new PrintWriter(resultStats)) {
						out.println("There were "+MetricsRunnable.wrongProcessedOntologies.size()+" ontologies which were wrong processed"); 
						for (String f: MetricsRunnable.wrongProcessedOntologies) {
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
		ClassComplexityAssertionsMetrics aux = new ClassComplexityAssertionsMetrics();  
		out.println("Ontology\t"+aux.headers()+"\t"+aux.headersWO()+"\t"+
					(new ObjectPropertyComplexityAssertionsMetrics()).headers()+"\t"+
					(new DataPropertyComplexityAssertionsMetrics()).headers()); 
	}
	
	public static class OWLFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.toString().endsWith(".owl"); 
		}
	}
}
