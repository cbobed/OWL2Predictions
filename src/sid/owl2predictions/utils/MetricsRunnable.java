package sid.owl2predictions.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import sid.owl2predictions.complexityMetrics.ELProfile.AssertionsComplexityMetricsCalculatorEL;

public class MetricsRunnable implements Runnable {
	
	public static Integer numOnts = 0; 
	public static Integer id = 0; 
	public static Object writeLock = new Object(); 
	public static ArrayList<String> wrongProcessedOntologies = new ArrayList<>(); 
	
	File ontologyFile = null; 
	PrintWriter out = null; 
	
	public MetricsRunnable (File ontFile, PrintWriter out) {
		this.ontologyFile = ontFile; 
		this.out = out; 
	}
	
	public void run() {
		String results = ""; 
		
		synchronized (id) {
			id ++; 
			if (id%10 == 0)  {
				System.out.println("Processing "+id +"th of "+numOnts+" ontologies."); 
			}
		}
		try {
			
			OWLOntologyManager om = OWLManager.createOWLOntologyManager(); 
		
			OWLOntology currentOntology = om.loadOntologyFromOntologyDocument(ontologyFile);
			results = ontologyFile.getName()+"\t"; 
			AssertionsComplexityMetricsCalculatorEL compCalculator = new AssertionsComplexityMetricsCalculatorEL(currentOntology);
			compCalculator.calculateAllMetrics();
			results += compCalculator.getClassComplexities().toString()+
					compCalculator.getClassComplexitiesWitoughGCIs()+
					compCalculator.getObjectPropComplexities()+
					compCalculator.getDataPropComplexities();
			synchronized(writeLock) {
				out.println(results); 
				out.flush();
			}
			System.gc();
		}
		catch (Exception e) {
			synchronized (wrongProcessedOntologies) {
				wrongProcessedOntologies.add(ontologyFile.getName());
			}
		}
	}
}
