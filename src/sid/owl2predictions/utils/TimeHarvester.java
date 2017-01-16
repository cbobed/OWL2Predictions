///////////////////////////////////////////////////////////////////////////////
// File: TimeHarvester.java 
// Author: Carlos Bobed
// Date: December 2016
// Version: 0.01
// Comments: 
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager; 
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import eu.trowl.owlapi3.rel.reasoner.el.RELReasoner;
import eu.trowl.owlapi3.rel.reasoner.el.RELReasonerFactory;
import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;
import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.jfact.JFactReasoner;

public class TimeHarvester {

	static String JFACT_REASONER = "JFACT"; 
	static String FACTPP_REASONER = "FACTPP"; 
	static String TROWL_REASONER = "TROWL"; 
	
	static String CSV_HEADER = "OntologyURI;ReasonerCreationTime(ns);MaterializingTime(ns)";; 
	
	// args[0] == ReasonerID
	// args[1] == Ontology IRI
	
	public static void main(String[] args) throws Exception{ 
		
		File outFile = new File(args[0]+"_Times.csv");
		PrintWriter out = null; 
		if (outFile.exists()) {
			out = new PrintWriter(new FileWriter(outFile, true));
		}
		else {
			out = new PrintWriter(new FileWriter(outFile));  
			out.println(CSV_HEADER);
		}
		
		File errorFile = new File(args[0]+"_TimesError.csv"); 
		PrintWriter errorOut = null; 
		if (errorFile.exists()) {
			errorOut = new PrintWriter(new FileWriter (errorFile, true)); 
		}
		else {
			errorOut = new PrintWriter(new FileWriter (errorFile)); 
		}
		
        long precomputingTime = -1;
        long creationTime = -1; 
        long start = -1;
        String reasonerID = args[0]; 
        String inputLine = args[1]; 
        
        try {
        	OWLOntology ont = null; 
        	OWLOntologyManager om = null; 
        	OWLReasoner reasoner = null; 
        	
        	System.err.println("Processing "+inputLine);
        
        	om = OWLManager.createOWLOntologyManager(); 
        	
        	// if the input is not a URI, we assume that is is a local path
        	if (!(inputLine.startsWith("http://") || inputLine.startsWith("file://"))) {
        		inputLine = "file://"+inputLine; 
        	}
        	
        	ont = om.loadOntology(IRI.create(inputLine));
	       	
	       	// we also measure the reasoner creation time
	       	// as the creation of the inner structures might affect 
	       	// the actual reasoning times 
	        start = System.nanoTime(); 	
        	if (JFACT_REASONER.equalsIgnoreCase(reasonerID)) {        		
        		reasoner = (new JFactFactory()).createReasoner(ont);
        		// We get a facade to access the JFact low level methods and configuration  
        		JFactReasoner jFactReasoner = (JFactReasoner) reasoner;        		
        		// We want to use the EL algorithm
        		jFactReasoner.getConfiguration().setUseELReasoner(true);        		
        	} else if (FACTPP_REASONER.equalsIgnoreCase(reasonerID)) {
        		
        		OWLReasonerFactory reasonerFactory = new uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory(); 
        		reasoner = reasonerFactory.createReasoner(ont);
        		
        	} else if (TROWL_REASONER.equalsIgnoreCase(reasonerID)) {
        		RELReasonerFactory relfactory = new RELReasonerFactory();
    			reasoner = relfactory.createReasoner(ont);	
        	}
	       	creationTime = System.nanoTime()-start; 
        	
	       	System.err.println("-->Materializing"); 
			// materializing the inferences
        	start = System.nanoTime(); 
    		reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS, 
    							InferenceType.CLASS_HIERARCHY, InferenceType.DATA_PROPERTY_ASSERTIONS, 
    							InferenceType.DATA_PROPERTY_HIERARCHY, InferenceType.DIFFERENT_INDIVIDUALS, 
    							InferenceType.DISJOINT_CLASSES, InferenceType.OBJECT_PROPERTY_ASSERTIONS, 
    							InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.SAME_INDIVIDUAL);
    		precomputingTime = System.nanoTime()-start; 
        }
        catch (Exception e) {
        	errorOut.println(inputLine+";"+e.getClass().getName()+";"+e.getMessage()); 
        	System.err.println(inputLine+" could not be processed"); 
        }
    
        out.println(inputLine+";"+creationTime+";"+precomputingTime);  
    	out.flush();
    	out.close(); 
        errorOut.flush(); 
        errorOut.close(); 
	}
	
}
