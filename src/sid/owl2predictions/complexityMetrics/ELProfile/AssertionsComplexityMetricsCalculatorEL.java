///////////////////////////////////////////////////////////////////////////////
// File: AssertionComplexityMetricsCalculator.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.02
// Comments: Class which calculates the metrics about the complexity of ABox assertions 
// Modifications: 
// 			October 2016: 
// 				* refactored to adapt to EL Profile-oriented classes 
// 				naming schema
// 				* adapted to capture the complexity of class assertions in 
// 				EL Profile
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics.ELProfile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import sid.owl2predictions.complexityMetrics.ClassComplexityAssertionsMetrics;
import sid.owl2predictions.complexityMetrics.ComplexityAssertionsMetrics;
import sid.owl2predictions.complexityMetrics.DataPropertyComplexityAssertionsMetrics;
import sid.owl2predictions.complexityMetrics.ObjectPropertyComplexityAssertionsMetrics;
import sid.owl2predictions.complexityMetrics.exceptions.NotYetRetrievedException;
import sid.owl2predictions.harvester.OWLClassExpressionHarvester;

public class AssertionsComplexityMetricsCalculatorEL {

	double NANO_TO_SEC = 1000000000; 
	
	OWLOntology ont = null; 
	
	ClassComplexityAssertionsMetrics classComplexities = null; 
	ClassComplexityAssertionsMetrics classComplexitiesWitoughGCIs = null; 
	ObjectPropertyComplexityAssertionsMetrics objectPropComplexities = null; 
	DataPropertyComplexityAssertionsMetrics dataPropComplexities = null; 
	
	public AssertionsComplexityMetricsCalculatorEL (OWLOntology ont) {
		this.ont = ont; 
	}
	
	public void calculateAllMetrics () throws NotYetRetrievedException {
		
		long startTime = 0; 
		// we first get all the information out from the ontology
		OWLObjectPropertyComplexityHarvesterEL objectPropComplexityHarvester = new OWLObjectPropertyComplexityHarvesterEL(ont);
		objectPropComplexityHarvester.retrieveComplexities(); 
		
		OWLDataPropertyComplexityHarvesterEL dataPropComplexityHarvester = new OWLDataPropertyComplexityHarvesterEL(ont);
		dataPropComplexityHarvester.retrieveComplexities();
		
		OWLClassExpressionComplexityHarvesterEL classComplexityHarvester = new OWLClassExpressionComplexityHarvesterEL(ont,
																objectPropComplexityHarvester.getObjectPropertyComplexities(), 
																dataPropComplexityHarvester.getDataPropertyComplexities());
		classComplexityHarvester.retrieveComplexitiesConsideringGCIs(); 
		
		Hashtable<String, Integer> witnessCountCE = new Hashtable<>();
		Hashtable<String, Integer> witnessCountOP = new Hashtable<>(); 
		Hashtable<String, Integer> witnessCountDP = new Hashtable<>(); 
		
		String auxIndex = ""; 
		Integer auxCount = -1; 
		
		// we initilialize all the expressions that appear in the ontology
		for (String id: classComplexityHarvester.getClassExpressionComplexitiesWithoutGCIs().keySet()) {
			witnessCountCE.put(id, 1); 
		}
		for (String id: objectPropComplexityHarvester.getObjectPropertyComplexities().keySet()){
			witnessCountOP.put(id, 1); 
		}
		for (String id: dataPropComplexityHarvester.getDataPropertyComplexities().keySet()){
			witnessCountDP.put(id, 1); 
		}
		
		// we now get the witnesses from class assertions 
		Set<OWLClassAssertionAxiom> classAssertionAxioms = ont.getAxioms(AxiomType.CLASS_ASSERTION); 
		for (OWLClassAssertionAxiom ax: classAssertionAxioms) {
			auxIndex = ax.getClassExpression().toString();
			checkAndUpdate(witnessCountCE, auxIndex, 1);
		}		
		
		// we now get the witnesses from objectProperty assertions
		Set<OWLObjectPropertyAssertionAxiom> objectPropAssertionsAxioms = ont.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION); 
		
		// we build a table with the domain and rage axioms
		// to speed up
		
		Hashtable<String, HashSet<String>> domainsObjProp = new Hashtable<>(); 
		Hashtable<String, HashSet<String>> rangesObjProp = new Hashtable<>(); 
		String id = null; 
		for (OWLObjectPropertyDomainAxiom ax: ont.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
			id = ax.getProperty().toString(); 
			if (!domainsObjProp.containsKey(id)){
				domainsObjProp.put(id, new HashSet<String>()); 
			}
			domainsObjProp.get(id).add(ax.getDomain().toString()); 
		}
		for (OWLObjectPropertyRangeAxiom ax: ont.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
			id = ax.getProperty().toString(); 
			if (!rangesObjProp.containsKey(id)){
				rangesObjProp.put(id, new HashSet<String>()); 
			}
			rangesObjProp.get(id).add(ax.getRange().toString()); 
		}
		
		for (OWLObjectPropertyAssertionAxiom ax: objectPropAssertionsAxioms) {
			auxIndex = ax.getProperty().toString(); 
			checkAndUpdate(witnessCountOP, auxIndex,1); 
			//* we can get the domain and range (or at least try to do it) 
			// we calculate it using the prefetched tables
			if (domainsObjProp.containsKey(auxIndex)) {
				for (String dom: domainsObjProp.get(auxIndex)) {
					checkAndUpdate(witnessCountCE, dom, 1); 
				}
			}
			if (rangesObjProp.containsKey(auxIndex)) {
				for (String rng: rangesObjProp.get(auxIndex)) {
					checkAndUpdate(witnessCountCE, rng, 1);
				}
			}
		}
		
		// we now get the witnesses from dataProperty assertions
		Set<OWLDataPropertyAssertionAxiom> dataPropAssertionsAxioms = ont.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION); 
		
		// we build a table with the domain and rage axioms
		// to speed up
				
		Hashtable<String, HashSet<String>> domainsDataProp = new Hashtable<>(); 
		id = null;  
		for (OWLDataPropertyDomainAxiom ax: ont.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
			id = ax.getProperty().toString(); 
			if (!domainsDataProp.containsKey(id)){
				domainsDataProp.put(id, new HashSet<String>()); 
			}
			domainsDataProp.get(id).add(ax.getDomain().toString()); 
		}
		
		for (OWLDataPropertyAssertionAxiom ax: dataPropAssertionsAxioms) {
			auxIndex = ax.getProperty().toString(); 
			checkAndUpdate(witnessCountDP, auxIndex,1); 
			//* we can get the domain (or at least try to do it)
			if (domainsDataProp.containsKey(auxIndex)){
				for (String dom: domainsDataProp.get(auxIndex)){
					checkAndUpdate(witnessCountCE, dom, 1);
				}
			}
		}
		
		assert (classComplexityHarvester.getClassExpressionComplexitiesWithGCIs().size() == witnessCountCE.size());
		assert (classComplexityHarvester.getClassExpressionComplexitiesWithoutGCIs().size() == witnessCountCE.size()) ;
		assert (classComplexityHarvester.getClassExpressionComplexitiesWithGCIs().size() == 
					classComplexityHarvester.getClassExpressionComplexitiesWithGCIs().size()); 
		assert (objectPropComplexityHarvester.getObjectPropertyComplexities().size() == witnessCountOP.size());
		assert (dataPropComplexityHarvester.getDataPropertyComplexities().size() == witnessCountDP.size());
		
		// we now have the complexity of each CE estimated 
		// as well as the number of witnesses for each CE 
		// total number of metric 
		// now it is increased by the size of vocabulary
		// we have added one class Assertion to each CE in the ontology
		
		this.classComplexities = new ClassComplexityAssertionsMetrics(); 
		this.classComplexitiesWitoughGCIs = new ClassComplexityAssertionsMetrics(); 
		
		calculateSingleMetricsAdapter(classComplexityHarvester.getClassExpressionComplexitiesWithoutGCIs(), 
													witnessCountCE, classComplexitiesWitoughGCIs);
		
		calculateSingleMetrics(classComplexityHarvester.getClassExpressionComplexitiesWithGCIs(), 
													witnessCountCE, classComplexities);
		
		this.objectPropComplexities = new ObjectPropertyComplexityAssertionsMetrics(); 
		this.dataPropComplexities = new DataPropertyComplexityAssertionsMetrics(); 
		
		
		
		calculateSingleMetrics(objectPropComplexityHarvester.getObjectPropertyComplexities(), 
													witnessCountOP, objectPropComplexities);
		calculateSingleMetrics(dataPropComplexityHarvester.getDataPropertyComplexities(), 
													witnessCountDP, dataPropComplexities); 
		
	}

	public ClassComplexityAssertionsMetrics getClassComplexities() throws NotYetRetrievedException{
		if (classComplexities == null) throw new NotYetRetrievedException(); 
		return classComplexities;
	}
	
	public ClassComplexityAssertionsMetrics getClassComplexitiesWitoughGCIs() throws NotYetRetrievedException {
		if (classComplexitiesWitoughGCIs == null) throw new NotYetRetrievedException(); 
		return classComplexitiesWitoughGCIs;
	}

	public ObjectPropertyComplexityAssertionsMetrics getObjectPropComplexities() throws NotYetRetrievedException{
		if (objectPropComplexities == null) throw new NotYetRetrievedException(); 
		return objectPropComplexities;
	}

	public DataPropertyComplexityAssertionsMetrics getDataPropComplexities() throws NotYetRetrievedException {
		if (dataPropComplexities == null) throw new NotYetRetrievedException(); 
		return dataPropComplexities;
	}
	
	public void checkAndUpdate (Hashtable<String, Integer> table, String id, int value) {
		Integer auxCount = table.get(id);  
		if (auxCount != null) {
			table.put(id, auxCount+1); 
		}
		else {
			table.put(id, 2); 
		}
	}

	
	private void calculateSingleMetrics (Hashtable <String, Double> complexityTable, 
										Hashtable <String, Integer> witnessesTable, 
										ComplexityAssertionsMetrics complexities) {
		
		double TOT_C = 0;
		double MAX_C = Double.MIN_VALUE;
		double MIN_C = Double.MAX_VALUE; 
		double stdAccumulator = 0.0; 
		double STD_C = 0.0; 
		double ENT_C = 0.0;
		
		double auxProb = 0.0;
		double auxEstimation = 0.0;

		// First the totals and averages for both With and Without GCIs 
		for (String id: complexityTable.keySet()) {
			TOT_C += complexityTable.get(id); 
		}
		double AVG_C = TOT_C / (complexityTable.keySet().size());
		
		for (String idx: complexityTable.keySet()) {
			//update max and min if appropriate
			auxEstimation = complexityTable.get(idx); 
			if (auxEstimation < MIN_C) {
				MIN_C = auxEstimation;  
			}
			if (auxEstimation > MAX_C)  {
				MAX_C = auxEstimation; 
			}
			
			// calculate the contributions of this class expression to the other
			// two metrics
			// std
			stdAccumulator += Math.pow( ( (double) auxEstimation - AVG_C) , 2.0);
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)auxEstimation/ TOT_C;
			ENT_C -= auxProb * ( Math.log (auxProb) / Math.log(2) );
		}
		STD_C = Math.sqrt( stdAccumulator / complexityTable.keySet().size() );
		
		
		complexities.setTotalComplexityAssertions(TOT_C);
		complexities.setMeanComplexityAssertions((TOT_C != 0)?AVG_C:0);
		complexities.setMaxComplexityAssertions((TOT_C != 0)?MAX_C:0);
		complexities.setMinComplexityAssertions((TOT_C != 0)?MIN_C:0);
		complexities.setStdComplexityAssertions((TOT_C != 0)?STD_C:0);
		complexities.setEntropyComplexityAssertions((TOT_C != 0)?ENT_C:0);

		double TOT_W = 0; 
		double MAX_W = Double.MIN_VALUE; 
		double MIN_W = Double.MAX_VALUE; 
		stdAccumulator = 0.0; 
		double STD_W = 0.0; 
		double ENT_W = 0.0; 
		auxProb = 0.0;
		auxEstimation = 0.0; 
		for (String id: complexityTable.keySet()) {
			TOT_W += complexityTable.get(id)*witnessesTable.get(id); 
		}
		double AVG_W = TOT_W / (witnessesTable.size());
		
		for (String idx: complexityTable.keySet()) {
			//update max and min if appropriate
			auxEstimation = complexityTable.get(idx)*witnessesTable.get(idx); 
			if (auxEstimation < MIN_W) {
				MIN_W = auxEstimation;  
			}
			if (auxEstimation > MAX_W)  {
				MAX_W = auxEstimation; 
			}
			// calculate the contributions of this class expression to the other
			// two metrics
			// std
			stdAccumulator += Math.pow( ( (double) auxEstimation - AVG_W) , 2.0); 
			// entropy
			// partial result: p(x_i) * log_2 ( p(x_i) ) 
			auxProb = (double)auxEstimation/ TOT_W; 
			ENT_W -= auxProb * ( Math.log (auxProb) / Math.log(2) ); 
		}
		STD_W = Math.sqrt( stdAccumulator / witnessesTable.keySet().size() ); 
		 
		complexities.setTotalWitnessedComplexity(TOT_W); 
		complexities.setMeanWitnessedComplexity((TOT_W != 0)?AVG_W:0);
		complexities.setMaxWitnessedComplexity((TOT_W != 0)?MAX_W:0);
		complexities.setMinWitnessedComplexity((TOT_W != 0)?MIN_W:0);
		complexities.setStdWitnessedComplexity((TOT_W != 0)?STD_W:0);
		complexities.setEntropyWitnessedComplexity((TOT_W != 0)?ENT_W:0);
	}
	
	private void calculateSingleMetricsAdapter (Hashtable <String, OWLClassExpressionComplexityEstimatorEL> complexityTable, 
																	Hashtable <String, Integer> witnessesTable, 
																	ComplexityAssertionsMetrics complexities) {
		Hashtable<String, Double> auxComplexity = new Hashtable<>(); 
		for (String id: complexityTable.keySet()) {
			auxComplexity.put(id, complexityTable.get(id).estimation()); 
		}
		calculateSingleMetrics(auxComplexity, witnessesTable, complexities); 
	}

	
}
