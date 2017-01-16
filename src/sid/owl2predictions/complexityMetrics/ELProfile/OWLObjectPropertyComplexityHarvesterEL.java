///////////////////////////////////////////////////////////////////////////////
// File: OWLObjectPropertyExpressionComplexityHarvester_ELProfile.java 
// Author: Carlos Bobed
// Date: October 2016
// Comments:  
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics.ELProfile;

import java.util.ArrayList;
import java.util.Hashtable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import sid.owl2predictions.complexityMetrics.exceptions.NotYetRetrievedException;

public class OWLObjectPropertyComplexityHarvesterEL {
	
	Hashtable<String, Double> objectPropertyComplexities = null;
	OWLOntology ont = null; 
	
	public OWLObjectPropertyComplexityHarvesterEL (OWLOntology ont) {
		this.ont = ont; 
		this.objectPropertyComplexities = null; 
	}

	public void retrieveComplexities() {
		objectPropertyComplexities = new Hashtable<>();
		double auxiliarComplexity = 0.0;
		ArrayList<String> auxIds = new ArrayList<>(); 
		for (OWLObjectProperty op: ont.getObjectPropertiesInSignature(true)) {
			auxiliarComplexity = 1.0; 
			// we check the only properties that can be present in EL profile
			if (op.isTransitive(ont)) {
				auxiliarComplexity += 2.0; 
			}
			if (op.isReflexive(ont)) {
				auxiliarComplexity += 1.0; 
			}
			objectPropertyComplexities.put(op.toString(), auxiliarComplexity);
		}
		
		// we now check the RIAs, subobjectPropertychinOfs and equivalence axioms
		for (OWLSubObjectPropertyOfAxiom subPropAx: ont.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) {			
			checkAndUpdate(subPropAx.getSubProperty().toString(), 1.0);
		}
		
		for (OWLSubPropertyChainOfAxiom subPropChainAx: ont.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)) {			
			if (!subPropChainAx.isEncodingOfTransitiveProperty()) {
				for (OWLObjectPropertyExpression op: subPropChainAx.getPropertyChain()) {
					checkAndUpdate(op.toString(), 1.0);
				}	
			}
			else {
				// if the subpropertyChain is encoding transitivity then we have to process it in a different way
				// the superObjectProperty would be transitive
				// TO DO: check whether we are counting twice the transitivity
				// 		  the assumption is that isTransitive is just for explicit axioms
				// 			and this axioms are implicit ones
				checkAndUpdate (subPropChainAx.getSuperProperty().toString(), 2.0); 
			}
		}
		
		for (OWLEquivalentObjectPropertiesAxiom equivPropAx: ont.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
			// we get the maximum of all the values
			auxiliarComplexity = 1.0;
			auxIds.clear(); 
			for (OWLObjectPropertyExpression op: equivPropAx.getProperties()) {
				auxIds.add(op.toString()); 
				if (objectPropertyComplexities.containsKey(op.toString())) {
					if (objectPropertyComplexities.get(op.toString()) > auxiliarComplexity) {
						auxiliarComplexity = objectPropertyComplexities.get(op.toString()); 
					}
				}
			}
			for (String id: auxIds) {
				objectPropertyComplexities.put(id, auxiliarComplexity); 
			}
		}
		
	}
	
	public Hashtable<String, Double> getObjectPropertyComplexities() throws NotYetRetrievedException {
		if (objectPropertyComplexities == null) throw new NotYetRetrievedException(); 
		return objectPropertyComplexities;
	}

	public void checkAndUpdate(String id, double value) {
		double aux = 0.0; 
		if (objectPropertyComplexities.containsKey(id)) {
			aux = objectPropertyComplexities.get(id); 
			objectPropertyComplexities.put(id, aux+value); 
		}
	}
}
