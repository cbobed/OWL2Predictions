///////////////////////////////////////////////////////////////////////////////
// File: ObjectPropertyAssertionsMetricsCalculator.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Class which calculates the metrics about ObjectProperty assertions 
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics.jist;

import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;


public class ObjectPropertyAssertionsMetricsCalculator {

	OWLOntology ont = null; 
	
	public ObjectPropertyAssertionsMetricsCalculator (OWLOntology ont) {
		this.ont = ont; 
	}
	
	// only concept expressions which have instances are considered to perform the calculations 
	public ObjectPropertyAssertionsMetrics calculateLocalMetrics () {
		
		// we first retrieve the set of ClassAssertionAxioms
		Set<OWLObjectPropertyAssertionAxiom> axioms = ont.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION); 
		
		// we work with the hashCode representation of the 
		// concept expressions to speedup the process and comparisons
		Hashtable<String, Integer> axiomsCount = new Hashtable<>(); 
		String auxIndex = ""; 
		Integer auxCount = -1; 
		// I assume that duplicated axioms have been already 
		// disposed of by the parsing 
		for (OWLObjectPropertyAssertionAxiom ax: axioms) {
			auxIndex = ax.getProperty().toString(); 
			auxCount = axiomsCount.get(auxIndex); 
			if (auxCount != null) {
				axiomsCount.put(auxIndex, auxCount+1); 
			}
			else {
				axiomsCount.put(auxIndex, 1); 
			}
		}
		
		// total number of metric
		int NPA = axioms.size();
		// mean number 
		double MPA = (double) NPA  / axiomsCount.keySet().size();
		
		int currentMin = Integer.MAX_VALUE; 
		int currentMax = Integer.MIN_VALUE;
		double stdAccumulator = 0.0; 
		double STD = 0.0; 
		double ENT = 0.0; 
		double auxProb = 0.0;
		
		for (String idx: axiomsCount.keySet()) {
			//update max and min if appropriate
			if (axiomsCount.get(idx) < currentMin ) {
				currentMin = axiomsCount.get(idx); 
			}
			if (axiomsCount.get(idx) > currentMax)  {
				currentMax = axiomsCount.get(idx); 
			}
			// calculate the contributions of this class expression to the other
			// two metrics
			// std
			stdAccumulator += Math.pow( ( (double) axiomsCount.get(idx) - MPA ) , 2.0); 
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)axiomsCount.get(idx) / NPA; 
			ENT -= auxProb * ( Math.log (auxProb) / Math.log(2) ); 
		}
		
		// we have to finish calculating STD 
		STD = Math.sqrt( stdAccumulator / axiomsCount.keySet().size() ); 
		
		ObjectPropertyAssertionsMetrics result = new ObjectPropertyAssertionsMetrics(); 
		result.setTotalNumObjectPropertyAssertions(NPA);
		result.setMeanNumObjectPropertyAssertions((NPA!=0)?MPA:-1);
		result.setMaxNumObjectPropertyAssertions((NPA!=0)?currentMax:-1);
		result.setMinNumObjectPropertyAssertions((NPA!=0)?currentMin:-1);
		result.setStdNumObjectPropertyAssertions((NPA!=0)?STD:-1);
		result.setEntropyObjectPropertyAssertions((NPA!=0)?ENT:-1);
		
		return result; 
	}
	
	// all the concept expressions are considered to perform the calculations
	// in order to soften those concept expressions which do not appear, we apply a +1 softener
	// as in NLP techniques to include them (Laplace add_one smoothing)
	
	// if the numbers become too small, work with their logarithm values
	public ObjectPropertyAssertionsMetrics calculateGlobalMetrics() {
		
		
		// we first retrieve the set of ClassAssertionAxioms
		Set<OWLObjectPropertyAssertionAxiom> axioms = ont.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION); 
		
		// we work with the hashCode representation of the 
		// concept expressions to speedup the process and comparisons
		Hashtable<String, Integer> axiomsCount = new Hashtable<>(); 
		String auxIndex = ""; 
		Integer auxCount = -1; 
		
		// we have also to add the ObjectProperty 
		for (OWLObjectProperty op: ont.getObjectPropertiesInSignature(true)) {
			axiomsCount.put(op.toString(), 1); 
		}
		
		// we now do as in the local, but if any CE is newly inserted
		// we have to consider that we might have had viewed it before 
		// and therefore put a 2 
		
		// I assume that duplicated axioms have been already 
		// disposed of by the parsing 
		for (OWLObjectPropertyAssertionAxiom ax: axioms) {
			auxIndex = ax.getProperty().toString(); 
			auxCount = axiomsCount.get(auxIndex); 
			if (auxCount != null) {
				axiomsCount.put(auxIndex, auxCount+1); 
			}
			else {
				axiomsCount.put(auxIndex, 2); 
			}
		}
		
		// total number of metric 
		// now it is increased by the size of vocabulary
		// we have added one class Assertion to each CE in the ontology
		int NPA = axioms.size() + axiomsCount.keySet().size();
		// mean number 
		double MPA = (double) NPA / axiomsCount.keySet().size();
		
		int currentMin = Integer.MAX_VALUE; 
		int currentMax = Integer.MIN_VALUE;
		double stdAccumulator = 0.0; 
		double STD = 0.0; 
		double ENT = 0.0; 
		double auxProb = 0.0;
		
		for (String idx: axiomsCount.keySet()) {
			//update max and min if appropriate
			if (axiomsCount.get(idx) < currentMin ) {
				currentMin = axiomsCount.get(idx); 
			}
			if (axiomsCount.get(idx) > currentMax)  {
				currentMax = axiomsCount.get(idx); 
			}
			// calculate the contributions of this class expression to the other
			// two metrics
			// std
			stdAccumulator += Math.pow( ( (double) axiomsCount.get(idx) - MPA ) , 2.0); 
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)axiomsCount.get(idx) / NPA; 
			ENT -= auxProb * ( Math.log (auxProb) / Math.log(2) ); 
		}
		
		// we have to finish calculating STD 
		STD = Math.sqrt( stdAccumulator / axiomsCount.keySet().size() ); 
		
		ObjectPropertyAssertionsMetrics result = new ObjectPropertyAssertionsMetrics(); 
		result.setTotalNumObjectPropertyAssertions(NPA);
		result.setMeanNumObjectPropertyAssertions((NPA!=0)?MPA:-1);
		result.setMaxNumObjectPropertyAssertions((NPA!=0)?currentMax:-1);
		result.setMinNumObjectPropertyAssertions((NPA!=0)?currentMin:-1);
		result.setStdNumObjectPropertyAssertions((NPA!=0)?STD:-1);
		result.setEntropyObjectPropertyAssertions((NPA!=0)?ENT:-1);
		
		return result; 
	}
}
