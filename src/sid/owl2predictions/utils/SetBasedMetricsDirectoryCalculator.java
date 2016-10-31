///////////////////////////////////////////////////////////////////////////////
// File: SetBasedMetricsDirectoryCalculator .java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Obtains the set-based metrics for all the ontologies in a given 
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

public class SetBasedMetricsDirectoryCalculator {

	public static void main(String[] args) {
		File directory = new File(args[0]);
		if (directory.isDirectory()) {
			// we assume that every single file ending in .owl that is in the directory is an ontology 
			// to be chopped 
			File[] ontologyList = directory.listFiles(new OWLFileFilter());
			OWLOntologyManager om = null;
			OWLOntology currentOntology = null; 			
			ArrayList<File> wrongProcessedFiles = new ArrayList<>();
			
			ClassAssertionsMetrics caMetrics = null; 
			ObjectPropertyAssertionsMetrics opaMetrics = null;
			DataPropertyAssertionsMetrics dpaMetrics = null;
			
			ClassAssertionsMetricsCalculator caCalculator = null;  
			ObjectPropertyAssertionsMetricsCalculator opaCalculator = null; 
			DataPropertyAssertionsMetricsCalculator dpaCalculator = null;
			
			File metricResults = new File(directory.toString()+File.separator+args[1]); 
			
			try (PrintWriter outResults = new PrintWriter(metricResults)) {
				printHeader(outResults); 
				int count = 0; 
				for (File currentOntologyFile: ontologyList) {
					count++; 
					if (count%10==0) System.out.println("Processing "+count+"th of "+ontologyList.length); 
					try {
						om = OWLManager.createOWLOntologyManager(); 
						currentOntology = om.loadOntologyFromOntologyDocument(currentOntologyFile);
						outResults.print(currentOntologyFile.getName()+"\t"); 

						caCalculator = new ClassAssertionsMetricsCalculator(currentOntology);
						// first the local
						caMetrics = caCalculator.calculateLocalMetrics(); 
						outResults.print(caMetrics.toString());
						// then the global
						caMetrics = caCalculator.calculateGlobalMetrics(); 
						outResults.print(caMetrics.toString());
						
						opaCalculator = new ObjectPropertyAssertionsMetricsCalculator(currentOntology); 
						opaMetrics = opaCalculator.calculateLocalMetrics(); 
						outResults.print(opaMetrics.toString());
						opaMetrics = opaCalculator.calculateGlobalMetrics(); 
						outResults.print(opaMetrics.toString()); 
						
						dpaCalculator = new DataPropertyAssertionsMetricsCalculator(currentOntology); 
						dpaMetrics = dpaCalculator.calculateLocalMetrics(); 
						outResults.print(dpaMetrics.toString());
						dpaMetrics = dpaCalculator.calculateGlobalMetrics(); 
						outResults.println(dpaMetrics.toString());
						
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
		// first the localValues for CA
		String header = "Ontology\tNCA\tMCA\tMaxCA\tMinCA\tSTD_CA\tENT_CA\t";
		// the global values for CA
		header+="G_NCA\tG_MCA\tG_MaxCA\tG_MinCA\tG_STD_CA\tG_ENT_CA\t";
		// the local values for ObjectPropAssertions
		header+="NOPA\tMOPA\tMaxOPA\tMinOPA\tSTD_OPA\tENT_OPA\t";
		// the global values for ObjectPropAssertions
		header+="G_NOPA\tG_MOPA\tG_MaxOPA\tG_MinOPA\tG_STD_OPA\tG_ENT_OPA\t";
		// the local values for DataProperties
		header+="NDPA\tMDPA\tMaxDPA\tMinDPA\tSTD_DPA\tENT_DPA\t";
		// the global values for DataProperties
		header+="G_NDPA\tG_MDPA\tG_MaxDPA\tG_MinDPA\tG_STD_DPA\tG_ENT_DPA\t";
		out.println(header); 
	}
	
	public static class OWLFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.toString().endsWith(".owl"); 
		}
	}
}
