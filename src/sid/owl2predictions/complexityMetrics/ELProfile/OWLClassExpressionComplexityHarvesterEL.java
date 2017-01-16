///////////////////////////////////////////////////////////////////////////////
// File: OWLClassExpressionComplexityHarvester_ELProfiler.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.02
// Comments: Class which calculates the metrics about the complexity of class assertions 
// Modifications: 
// 			October 2016: 
// 				* refactored to adapt to EL Profile-oriented classes 
// 				naming schema
// 				* adapted to capture the complexity of class assertions in 
// 				EL Profile
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics.ELProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import sid.owl2predictions.complexityMetrics.ClassComplexityAssertionsMetrics;
import sid.owl2predictions.complexityMetrics.exceptions.NotYetRetrievedException;
import sid.owl2predictions.harvester.OWLClassExpressionHarvester;

public class OWLClassExpressionComplexityHarvesterEL {

	OWLOntology ont = null; 
	Hashtable<String, OWLClassExpressionComplexityEstimatorEL> classExpressionComplexitiesWithoutGCIs = null;
	Hashtable<String, OWLClassExpressionComplexityEstimatorEL> GCIsCosts = null;  
	Hashtable<String, Double> classExpressionComplexitiesWithGCIs = null; 
	Hashtable<String, Double> objectPropertyComplexities = null; 
	Hashtable<String, Double> dataPropertyComplexities = null; 
	
	OWLClassExpressionHarvester axiomVisitor = null; 
	
	public OWLClassExpressionComplexityHarvesterEL (OWLOntology ont, 
														Hashtable<String, Double> objectPropertyComplexities, 
														Hashtable<String, Double> dataPropertyComplexities) {
		this.ont = ont; 
		this.classExpressionComplexitiesWithoutGCIs = null; 
		this.objectPropertyComplexities = objectPropertyComplexities; 
		this.dataPropertyComplexities = dataPropertyComplexities; 
	}
	
	public OWLClassExpressionComplexityHarvesterEL (OWLOntology ont) {
		this.ont = ont; 
		this.classExpressionComplexitiesWithoutGCIs = null; 
		
		// we initialize the tables of complexities
		OWLObjectPropertyComplexityHarvesterEL OPHarvester = new OWLObjectPropertyComplexityHarvesterEL(ont); 
		OWLDataPropertyComplexityHarvesterEL DPHarvester = new OWLDataPropertyComplexityHarvesterEL(ont);
		
		OPHarvester.retrieveComplexities();
		DPHarvester.retrieveComplexities();
		try {
			this.dataPropertyComplexities = DPHarvester.getDataPropertyComplexities(); 
			this.objectPropertyComplexities = OPHarvester.getObjectPropertyComplexities();
		}
		catch (NotYetRetrievedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void retrieveComplexitiesWithoutGCIs () {
		
		this.classExpressionComplexitiesWithoutGCIs = new Hashtable<>(); 

		// we take all the anonymous ClassExpressions that appear in the ontology
		this.axiomVisitor = new OWLClassExpressionHarvester();
		
		int aux = 0; 
		System.out.println(aux+" out of "+ont.getTBoxAxioms(true).size()); 
		for (OWLAxiom ax: ont.getTBoxAxioms(true)) {
			 ax.accept(axiomVisitor);
			 aux++; 
			 if (aux % 1000 == 0) System.out.println(aux+" out of "+ont.getTBoxAxioms(true).size()); 
		}
		
		// for all of them, we insert them in the count list 
		// as we may have seen at least one time
		for (OWLClassExpression ce: axiomVisitor.getHarvestedClasses()){			
			OWLClassExpressionComplexityEstimatorEL estimator = 
					new OWLClassExpressionComplexityEstimatorEL(
															objectPropertyComplexities, 
															dataPropertyComplexities); 
			ce.accept(estimator);
			classExpressionComplexitiesWithoutGCIs.put(ce.toString(), estimator); 
		}
		for (OWLClass ce: ont.getClassesInSignature(true)) {
			OWLClassExpressionComplexityEstimatorEL estimator = 
					new OWLClassExpressionComplexityEstimatorEL(
															objectPropertyComplexities, 
															dataPropertyComplexities); 
			ce.accept(estimator);
			classExpressionComplexitiesWithoutGCIs.put(ce.toString(), estimator);
		}
		
		// we have to add OWLThing and OWLNothing
		
		OWLClassExpression thing = ont.getOWLOntologyManager().getOWLDataFactory().getOWLThing();
		OWLClassExpressionComplexityEstimatorEL thingEstimator = new OWLClassExpressionComplexityEstimatorEL(
																				objectPropertyComplexities, 
																				dataPropertyComplexities); 
		thing.accept(thingEstimator);
		classExpressionComplexitiesWithoutGCIs.put(thing.toString(), thingEstimator );
		
		OWLClassExpression nothing = ont.getOWLOntologyManager().getOWLDataFactory().getOWLNothing(); 
		OWLClassExpressionComplexityEstimatorEL nothingEstimator = new OWLClassExpressionComplexityEstimatorEL(
																				objectPropertyComplexities, 
																				dataPropertyComplexities); 
		nothing.accept(nothingEstimator);
		classExpressionComplexitiesWithoutGCIs.put(nothing.toString(), nothingEstimator ); 
		
	}
	public void retrieveComplexitiesConsideringGCIs() {
		/* first, retrieve the complexitiesWithoutGCIs */ 
		retrieveComplexitiesWithoutGCIs();
		
		Hashtable<String, HashSet<OWLClassExpression>> GCIsTable = new Hashtable<>(); 
		String subClassString = ""; 
		// we group all the GCIs sharing the leftside part 
		// and calculate the complexity of the AND of the rightside part
		for (OWLSubClassOfAxiom gciAxiom: ont.getAxioms(AxiomType.SUBCLASS_OF)) {
			subClassString = gciAxiom.getSubClass().toString(); 
			if (!GCIsTable.containsKey(subClassString)) {
				GCIsTable.put(subClassString, new HashSet<OWLClassExpression>()); 			
			}
			GCIsTable.get(subClassString).add(gciAxiom.getSuperClass());
		}
		
		// we calculate the cost of the conjunctions
		// and we apply it to the class expressions costs
		OWLDataFactory dataFactory = ont.getOWLOntologyManager().getOWLDataFactory();
		OWLClassExpression auxClassExpression = null;
		
		this.GCIsCosts = new Hashtable<>();
		this.classExpressionComplexitiesWithGCIs = new Hashtable<>(); 
		
		for (String id: GCIsTable.keySet()) {
			if (GCIsTable.get(id).size()>1) {
				auxClassExpression = dataFactory.getOWLObjectIntersectionOf(GCIsTable.get(id));
				OWLClassExpressionComplexityEstimatorEL estimator = 
						new OWLClassExpressionComplexityEstimatorEL(
																objectPropertyComplexities, 
																dataPropertyComplexities); 
				auxClassExpression.accept(estimator);
				GCIsCosts.put(id, estimator); 
			}
			else{
				// there should only be just one classExpression
				// it should be already processed in the global table
				assert classExpressionComplexitiesWithoutGCIs.get(id) != null; 
				try{
					GCIsCosts.put(id, classExpressionComplexitiesWithoutGCIs.get(id));
				}
				catch (NullPointerException e) {
					System.err.println(id); 
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
		// we have calculated the GCIs costs with the static costs of the expressions
		// now we apply the value just by adding the cost
		double auxCost = -1; 
		for (String ceString: classExpressionComplexitiesWithoutGCIs.keySet()) {
			auxCost = classExpressionComplexitiesWithoutGCIs.get(ceString).estimation(); 
			if (GCIsCosts.containsKey(ceString)) {
				auxCost += GCIsCosts.get(ceString).estimation(); 
			}
			classExpressionComplexitiesWithGCIs.put(ceString, auxCost); 
		}
		
	}

	public Hashtable<String, OWLClassExpressionComplexityEstimatorEL> getClassExpressionComplexitiesWithoutGCIs() 
												throws NotYetRetrievedException{
		if (classExpressionComplexitiesWithoutGCIs == null) throw new NotYetRetrievedException(); 
		return classExpressionComplexitiesWithoutGCIs;
	}

	public Hashtable<String, Double> getClassExpressionComplexitiesWithGCIs() throws NotYetRetrievedException{
		if (classExpressionComplexitiesWithGCIs == null) throw new NotYetRetrievedException(); 
		return classExpressionComplexitiesWithGCIs;
	}

	public Hashtable<String, Double> getObjectPropertyComplexities() throws NotYetRetrievedException {
		if (objectPropertyComplexities == null) throw new NotYetRetrievedException(); 
		return objectPropertyComplexities;
	}

	public Hashtable<String, Double> getDataPropertyComplexities() throws NotYetRetrievedException {
		if (dataPropertyComplexities == null) throw new NotYetRetrievedException(); 
		return dataPropertyComplexities;
	}
}
