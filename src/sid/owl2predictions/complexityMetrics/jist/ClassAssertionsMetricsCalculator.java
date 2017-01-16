///////////////////////////////////////////////////////////////////////////////
// File: ClassAssertionsMetricsCalculator.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.01
// Comments: Class which calculates the metrics about class assertions 
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics.jist;

import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import sid.owl2predictions.harvester.OWLClassExpressionHarvester;

public class ClassAssertionsMetricsCalculator {

	OWLOntology ont = null; 
	
	public ClassAssertionsMetricsCalculator (OWLOntology ont) {
		this.ont = ont; 
	}
	
	// only concept expressions which have instances are considered to perform the calculations 
	public ClassAssertionsMetrics calculateLocalMetrics () {
		
		// we first retrieve the set of ClassAssertionAxioms
		Set<OWLClassAssertionAxiom> axioms = ont.getAxioms(AxiomType.CLASS_ASSERTION); 
		
		// we work with the hashCode representation of the 
		// concept expressions to speedup the process and comparisons
		Hashtable<String, Integer> axiomsCount = new Hashtable<>(); 
		String auxIndex = ""; 
		Integer auxCount = -1; 
		// I assume that duplicated axioms have been already 
		// disposed of by the parsing 
		for (OWLClassAssertionAxiom ax: axioms) {
			auxIndex = ax.getClassExpression().toString(); 
			auxCount = axiomsCount.get(auxIndex); 
			if (auxCount != null) {
				axiomsCount.put(auxIndex, auxCount+1); 
			}
			else {
				axiomsCount.put(auxIndex, 1); 
			}
		}
		
		// total number of metric
		int NCA = axioms.size();
		// mean number 
		double MCA = (double) NCA  / axiomsCount.keySet().size();
		
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
			stdAccumulator += Math.pow( ( (double) axiomsCount.get(idx) - MCA ) , 2.0); 
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)axiomsCount.get(idx) / NCA; 
			ENT -= auxProb * ( Math.log (auxProb) / Math.log(2) ); 
		}
		
		// we have to finish calculating STD 
		STD = Math.sqrt( stdAccumulator / axiomsCount.keySet().size() ); 
		
		ClassAssertionsMetrics result = new ClassAssertionsMetrics(); 
		result.setTotalNumClassAssertions(NCA);
		result.setMeanNumClassAssertions((NCA != 0)?MCA:-1);
		result.setMaxNumClassAssertions((NCA != 0)?currentMax:-1);
		result.setMinNumClassAssertions((NCA != 0)?currentMin:-1);
		result.setStdNumClassAssertions((NCA != 0)?STD:-1);
		result.setEntropyClassAssertions((NCA != 0)?ENT:-1);
		
		return result; 
	}
	
	// all the concept expressions are considered to perform the calculations
	// in order to soften those concept expressions which do not appear, we apply a +1 softener
	// as in NLP techniques to include them (Laplace add_one smoothing)
	
	// if the numbers become too small, work with their logarithm values
	public ClassAssertionsMetrics calculateGlobalMetrics() {
		
		
		// we first retrieve the set of ClassAssertionAxioms
		Set<OWLClassAssertionAxiom> axioms = ont.getAxioms(AxiomType.CLASS_ASSERTION); 
		
		// we work with the hashCode representation of the 
		// concept expressions to speedup the process and comparisons
		Hashtable<String, Integer> axiomsCount = new Hashtable<>(); 
		String auxIndex = ""; 
		Integer auxCount = -1; 
		
		// we take all the anonymous ClassExpressions that appear in the ontology
//		OWLClassExpressionHarvester axiomVisitor = new OWLClassExpressionHarvester();
//		for (OWLAxiom ax: ont.getTBoxAxioms(true)) {
//			 ax.accept(axiomVisitor); 
//		}
		
		// for all of them, we insert them in the count list 
		// as we may have seen at least one time
//		for (OWLClassExpression ce: axiomVisitor.getHarvestedClasses()){
//			axiomsCount.put(ce.toString().hashCode(), 1); 
//		}
//		
		// we have also to add the named concepts
		for (OWLClass ce: ont.getClassesInSignature(true)) {
			axiomsCount.put(ce.toString(), 1); 
		}
		
		// we now do as in the local, but if any CE is newly inserted
		// we have to consider that we might have had viewed it before 
		// and therefore put a 2 
		
		// I assume that duplicated axioms have been already 
		// disposed of by the parsing 
		for (OWLClassAssertionAxiom ax: axioms) {
			auxIndex = ax.getClassExpression().toString(); 
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
		int NCA = axioms.size() + axiomsCount.keySet().size();
		// mean number 
		double MCA = (double) NCA / axiomsCount.keySet().size();
		
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
			stdAccumulator += Math.pow( ( (double) axiomsCount.get(idx) - MCA ) , 2.0); 
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)axiomsCount.get(idx) / NCA; 
			ENT -= auxProb * ( Math.log (auxProb) / Math.log(2) ); 
		}
		
		// we have to finish calculating STD 
		STD = Math.sqrt( stdAccumulator / axiomsCount.keySet().size() ); 
		
		ClassAssertionsMetrics result = new ClassAssertionsMetrics(); 
		result.setTotalNumClassAssertions(NCA);
		result.setMeanNumClassAssertions((NCA != 0)?MCA:-1);
		result.setMaxNumClassAssertions((NCA != 0)?currentMax:-1);
		result.setMinNumClassAssertions((NCA != 0)?currentMin:-1);
		result.setStdNumClassAssertions((NCA != 0)?STD:-1);
		result.setEntropyClassAssertions((NCA != 0)?ENT:-1);
		
		return result; 
	}
}
