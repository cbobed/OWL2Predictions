///////////////////////////////////////////////////////////////////////////////
// File: DataPropertyAssertionsMetricsCalculator.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Class which calculates the metrics about dataProperty assertions 
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions;

import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import sid.owl2predictions.harvester.OWLClassExpressionHarvester;

public class DataPropertyAssertionsMetricsCalculator {

	OWLOntology ont = null; 
	
	public DataPropertyAssertionsMetricsCalculator (OWLOntology ont) {
		this.ont = ont; 
	}
	
	// only concept expressions which have instances are considered to perform the calculations 
	public DataPropertyAssertionsMetrics calculateLocalMetrics () {
		
		// we first retrieve the set of ClassAssertionAxioms
		Set<OWLDataPropertyAssertionAxiom> axioms = ont.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION); 
		
		// we work with the hashCode representation of the 
		// dataProperty to speedup the process and comparisons
		Hashtable<String, Integer> axiomsCount = new Hashtable<>(); 
		String auxIndex = ""; 
		Integer auxCount = -1; 
		// I assume that duplicated axioms have been already 
		// disposed of by the parsing 
		for (OWLDataPropertyAssertionAxiom ax: axioms) {
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
		int NDA = axioms.size();
		// mean number 
		double MDA = (double) NDA  / axiomsCount.keySet().size();
		
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
			stdAccumulator += Math.pow( ( (double) axiomsCount.get(idx) - MDA ) , 2.0); 
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)axiomsCount.get(idx) / NDA; 
			ENT -= auxProb * ( Math.log (auxProb) / Math.log(2) ); 
		}
		
		// we have to finish calculating STD 
		STD = Math.sqrt( stdAccumulator / axiomsCount.keySet().size() ); 
		
		DataPropertyAssertionsMetrics result = new DataPropertyAssertionsMetrics(); 
		result.setTotalNumDataPropertyAssertions(NDA);
		result.setMeanNumDataPropertyAssertions((NDA!=0)?MDA:-1);
		result.setMaxNumDataPropertyAssertions((NDA!=0)?currentMax:-1);
		result.setMinNumDataPropertyAssertions((NDA!=0)?currentMin:-1);
		result.setStdNumDataPropertyAssertions((NDA!=0)?STD:-1);
		result.setEntropyDataPropertyAssertions((NDA!=0)?ENT:-1);
		
		return result; 
	}
	
	// all the concept expressions are considered to perform the calculations
	// in order to soften those concept expressions which do not appear, we apply a +1 softener
	// as in NLP techniques to include them (Laplace add_one smoothing)
	
	// if the numbers become too small, work with their logarithm values
	public DataPropertyAssertionsMetrics calculateGlobalMetrics() {
		
		// we first retrieve the set of ClassAssertionAxioms
		Set<OWLDataPropertyAssertionAxiom> axioms = ont.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION); 
		
		// we work with the hashCode representation of the 
		// concept expressions to speedup the process and comparisons
		Hashtable<String, Integer> axiomsCount = new Hashtable<>(); 
		String auxIndex = ""; 
		Integer auxCount = -1; 
		
		// we have to add all the dataProperties
		for (OWLDataProperty dp: ont.getDataPropertiesInSignature(true)) {
			axiomsCount.put(dp.toString(), 1); 
		}
		
		// we now do as in the local, but if any DP is newly inserted
		// we have to consider that we might have had viewed it before 
		// and therefore put a 2 
		
		// I assume that duplicated axioms have been already 
		// disposed of by the parsing 
		for (OWLDataPropertyAssertionAxiom ax: axioms) {
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
		// we have added one class Assertion to each DP in the ontology
		int NDA = axioms.size() + axiomsCount.keySet().size();
		// mean number 
		double MDA = (double) NDA / axiomsCount.keySet().size();
		
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
			stdAccumulator += Math.pow( ( (double) axiomsCount.get(idx) - MDA ) , 2.0); 
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)axiomsCount.get(idx) / NDA; 
			ENT -= auxProb * ( Math.log (auxProb) / Math.log(2) ); 
		}
		
		// we have to finish calculating STD 
		STD = Math.sqrt( stdAccumulator / axiomsCount.keySet().size() ); 
		
		DataPropertyAssertionsMetrics result = new DataPropertyAssertionsMetrics(); 
		result.setTotalNumDataPropertyAssertions(NDA);
		result.setMeanNumDataPropertyAssertions((NDA!=0)?MDA:-1);
		result.setMaxNumDataPropertyAssertions((NDA!=0)?currentMax:-1);
		result.setMinNumDataPropertyAssertions((NDA!=0)?currentMin:-1);
		result.setStdNumDataPropertyAssertions((NDA!=0)?STD:-1);
		result.setEntropyDataPropertyAssertions((NDA!=0)?ENT:-1);
		
		return result; 
	}
}
