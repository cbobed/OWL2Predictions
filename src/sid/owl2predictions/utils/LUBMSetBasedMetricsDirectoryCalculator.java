///////////////////////////////////////////////////////////////////////////////
// File: LUBMSetBasedMetricsDirectoryCalculator .java 
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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import sid.owl2predictions.ClassAssertionsMetrics;
import sid.owl2predictions.ClassAssertionsMetricsCalculator;
import sid.owl2predictions.DataPropertyAssertionsMetrics;
import sid.owl2predictions.DataPropertyAssertionsMetricsCalculator;
import sid.owl2predictions.ObjectPropertyAssertionsMetrics;
import sid.owl2predictions.ObjectPropertyAssertionsMetricsCalculator;
import sid.owl2predictions.SubGraphMetrics;
import sid.owl2predictions.SubGraphMetricsCalculator;
import sid.owl2predictions.utils.LUBMGraphBasedMetricsDirectoryCalculator.UniversityFileFilter;

public class LUBMSetBasedMetricsDirectoryCalculator {

	public static void main(String[] args) {
		File directory = new File(args[0]);
		int maxNumberUniversities = Integer.valueOf(args[1]); 
		String LUBM_URL = "http://swat.cse.lehigh.edu/onto/univ-bench.owl"; 
		
		if (directory.isDirectory()) {
			File metricResults = new File(directory.toString()+File.separator+args[2]);
			
			try (PrintWriter outResults = new PrintWriter(metricResults)) {
				printHeader(outResults); 	
				ArrayList<String> wrongProcessedFiles = new ArrayList<>();
				for (int i=0; i<maxNumberUniversities; i++) {
					System.out.println("Processing "+i+" universities ..."); 
					OWLOntologyManager om = OWLManager.createOWLOntologyManager(); 
					OWLOntology currentOntology = om.loadOntology(IRI.create(LUBM_URL));
					OWLOntologyMerger merger = null; 
					
					ClassAssertionsMetrics caMetrics = null; 
					ObjectPropertyAssertionsMetrics opaMetrics = null;
					DataPropertyAssertionsMetrics dpaMetrics = null;
					
					ClassAssertionsMetricsCalculator caCalculator = null;  
					ObjectPropertyAssertionsMetricsCalculator opaCalculator = null; 
					DataPropertyAssertionsMetricsCalculator dpaCalculator = null;
					
					OWLOntology mergedOntology = null; 
					// we have to include all the universities up to i
					for (int j=0; j<=i; j++) {
						// first, we get all the university filenames we have to include 
						// in the execution
						File[] universityList = directory.listFiles(new UniversityFileFilter(j));
						try {
							for (File currentUniversityFile: universityList) {
								System.out.println("--> Loading "+currentUniversityFile); 
								om.loadOntologyFromOntologyDocument(currentUniversityFile);
							}
						}
						catch(Exception e) {
							wrongProcessedFiles.add(LUBM_URL.replace(".owl","")+"-"+i+".owl"); 
						}
					}
					// at this point om has all the ontologies parsed and read
					merger = new OWLOntologyMerger(om); 
					mergedOntology = merger.createMergedOntology(om, IRI.create(LUBM_URL.replace(".owl", "")+"-"+i+".owl"));
					System.out.println(mergedOntology.getAxiomCount()+" axioms in the merged ontology"); 
					outResults.print(LUBM_URL.replace(".owl","")+"-"+i+".owl\t"); 
					
					caCalculator = new ClassAssertionsMetricsCalculator(mergedOntology);
					// first the local
					caMetrics = caCalculator.calculateLocalMetrics(); 
					outResults.print(caMetrics.toString());
					// then the global
					caMetrics = caCalculator.calculateGlobalMetrics(); 
					outResults.print(caMetrics.toString());
					
					opaCalculator = new ObjectPropertyAssertionsMetricsCalculator(mergedOntology); 
					opaMetrics = opaCalculator.calculateLocalMetrics(); 
					outResults.print(opaMetrics.toString());
					opaMetrics = opaCalculator.calculateGlobalMetrics(); 
					outResults.print(opaMetrics.toString()); 
					
					dpaCalculator = new DataPropertyAssertionsMetricsCalculator(mergedOntology); 
					dpaMetrics = dpaCalculator.calculateLocalMetrics(); 
					outResults.print(dpaMetrics.toString());
					dpaMetrics = dpaCalculator.calculateGlobalMetrics(); 
					outResults.println(dpaMetrics.toString());
					
					outResults.flush(); 
					
					System.gc();																				
				}
				File resultStats = new File(directory.toString()+File.separator+"resultStats.csv"); 
				if (!wrongProcessedFiles.isEmpty()) {
					try (PrintWriter out = new PrintWriter(resultStats)) {
						out.println("There were "+wrongProcessedFiles.size()+" ontologies which were wrong processed"); 
						for (String f: wrongProcessedFiles) {
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
	
	public static class UniversityFileFilter implements FileFilter {
		int universityNumber = -1; 
		public UniversityFileFilter(int number) {
			this.universityNumber = number; 
		}
		public boolean accept(File pathname) {
			return pathname.getName().startsWith("University"+universityNumber+"_");
		}
	}
}
