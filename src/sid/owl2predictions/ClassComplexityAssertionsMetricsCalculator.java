///////////////////////////////////////////////////////////////////////////////
// File: ClassComplexityAssertionsMetricsCalculator.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.01
// Comments: Class which calculates the metrics about the complexity of class assertions 
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions;

import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import sid.owl2predictions.harvester.OWLClassExpressionHarvester;

public class ClassComplexityAssertionsMetricsCalculator {

	OWLOntology ont = null; 
	
	public ClassComplexityAssertionsMetricsCalculator (OWLOntology ont) {
		this.ont = ont; 
	}
	
	public ClassComplexityAssertionsMetrics calculateClassComplexityMetrics () {
		Hashtable<String, Integer> witnessCount = new Hashtable<>(); 
		Hashtable<String, OWLClassExpressionComplexityEstimator> complexities = new Hashtable<>(); 
		String auxIndex = ""; 
		Integer auxCount = -1; 
		// we take all the anonymous ClassExpressions that appear in the ontology
		OWLClassExpressionHarvester axiomVisitor = new OWLClassExpressionHarvester();
		for (OWLAxiom ax: ont.getTBoxAxioms(true)) {
			 ax.accept(axiomVisitor); 
		}
		
		// for all of them, we insert them in the count list 
		// as we may have seen at least one time
		for (OWLClassExpression ce: axiomVisitor.getHarvestedClasses()){			
			witnessCount.put(ce.toString(), 1);
			OWLClassExpressionComplexityEstimator estimator = new OWLClassExpressionComplexityEstimator(); 
			ce.accept(estimator);
			complexities.put(ce.toString(), estimator); 
		}
		for (OWLClass ce: ont.getClassesInSignature(true)) {
			witnessCount.put(ce.toString(), 1);
			OWLClassExpressionComplexityEstimator estimator = new OWLClassExpressionComplexityEstimator(); 
			ce.accept(estimator);
			complexities.put(ce.toString(), estimator);
		}
		
		// we now get the witnesses 
		Set<OWLClassAssertionAxiom> axioms = ont.getAxioms(AxiomType.CLASS_ASSERTION); 
		
		for (OWLClassAssertionAxiom ax: axioms) {
			auxIndex = ax.getClassExpression().toString(); 
			auxCount = witnessCount.get(auxIndex); 
			if (auxCount != null) {
				witnessCount.put(auxIndex, auxCount+1); 
			}
			else {
				witnessCount.put(auxIndex, 2); 
			}
		}
		
		assert (complexities.size() == witnessCount.size()); 
		
		// we now have the complexity of each CE estimated 
		// as well as the number of witnesses for each CE 
		// total number of metric 
		// now it is increased by the size of vocabulary
		// we have added one class Assertion to each CE in the ontology
		
		double TCCA = 0; 
		double MAX_CCA = Double.MIN_VALUE; 
		double MIN_CCA = Double.MAX_VALUE; 
		double stdAccumulator = 0.0; 
		double STD = 0.0; 
		double ENT = 0.0; 
		double auxProb = 0.0;
		double auxEstimation = 0.0; 
		for (String id: complexities.keySet()) {
			TCCA += complexities.get(id).estimation(); 
		}
		double AVG_CCA = TCCA / (complexities.keySet().size());
		
		for (String idx: complexities.keySet()) {
			//update max and min if appropriate
			auxEstimation = complexities.get(idx).estimation(); 
			if (auxEstimation < MIN_CCA) {
				MIN_CCA = auxEstimation;  
			}
			if (auxEstimation > MAX_CCA)  {
				MAX_CCA = auxEstimation; 
			}
			// calculate the contributions of this class expression to the other
			// two metrics
			// std
			stdAccumulator += Math.pow( ( (double) auxEstimation - AVG_CCA) , 2.0); 
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)auxEstimation/ TCCA; 
			ENT -= auxProb * ( Math.log (auxProb) / Math.log(2) ); 
		}
		STD = Math.sqrt( stdAccumulator / witnessCount.keySet().size() ); 
		
		ClassComplexityAssertionsMetrics result = new ClassComplexityAssertionsMetrics(); 
		result.setTotalClassComplexityAssertions(TCCA);
		result.setMeanClassComplexityAssertions((TCCA != 0)?AVG_CCA:0);
		result.setMaxClassComplexityAssertions((TCCA != 0)?MAX_CCA:0);
		result.setMinClassComplexityAssertions((TCCA != 0)?MIN_CCA:0);
		result.setStdClassComplexityAssertions((TCCA != 0)?STD:0);
		result.setEntropyClassComplexityAssertions((TCCA != 0)?ENT:0);

//		totalWitnessedClassComplexity = 0; 
//		meanWitnessedClassComplexity = 0;
//		maxWitnessedClassComplexity = 0; 
//		minWitnessedClassComplexity = 0; 
//		stdWitnessedClassComplexity = 0.0; 
//		entropyWitnessedClassComplexity =0.0; 

		double TWCCA = 0; 
		double MAX_WCCA = Double.MIN_VALUE; 
		double MIN_WCCA = Double.MAX_VALUE; 
		stdAccumulator = 0.0; 
		STD = 0.0; 
		ENT = 0.0; 
		auxProb = 0.0;
		auxEstimation = 0.0; 
		for (String id: complexities.keySet()) {
			TWCCA += complexities.get(id).estimation()*witnessCount.get(id); 
		}
		double AVG_WCCA = TWCCA / (witnessCount.size());
		
		for (String idx: complexities.keySet()) {
			//update max and min if appropriate
			auxEstimation = complexities.get(idx).estimation()*witnessCount.get(idx); 
			if (auxEstimation < MIN_WCCA) {
				MIN_WCCA = auxEstimation;  
			}
			if (auxEstimation > MAX_WCCA)  {
				MAX_WCCA = auxEstimation; 
			}
			// calculate the contributions of this class expression to the other
			// two metrics
			// std
			stdAccumulator += Math.pow( ( (double) auxEstimation - AVG_WCCA) , 2.0); 
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)auxEstimation/ TWCCA; 
			ENT -= auxProb * ( Math.log (auxProb) / Math.log(2) ); 
		}
		STD = Math.sqrt( stdAccumulator / witnessCount.keySet().size() ); 
		 
		result.setTotalWitnessedClassComplexity(TWCCA); 
		result.setMeanWitnessedClassComplexity((TWCCA != 0)?AVG_WCCA:0);
		result.setMaxWitnessedClassComplexity((TWCCA != 0)?MAX_WCCA:0);
		result.setMinWitnessedClassComplexity((TWCCA != 0)?MIN_WCCA:0);
		result.setStdWitnessedClassComplexity((TWCCA != 0)?STD:0);
		result.setEntropyWitnessedClassComplexity((TWCCA != 0)?ENT:0);
		
		return result; 
		
	}
	

	
	
}
