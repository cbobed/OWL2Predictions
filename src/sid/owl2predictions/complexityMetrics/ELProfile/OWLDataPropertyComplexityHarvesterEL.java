///////////////////////////////////////////////////////////////////////////////
// File: OWLDataPropertyExpressionComplexityHarvester_ELProfile.java 
// Author: Carlos Bobed
// Date: October 2016
// Comments:  
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics.ELProfile;

import java.util.ArrayList;
import java.util.Hashtable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

import sid.owl2predictions.complexityMetrics.exceptions.NotYetRetrievedException;

public class OWLDataPropertyComplexityHarvesterEL {
	
	Hashtable<String, Double> dataPropertyComplexities = null;
	OWLOntology ont = null; 
	
	public OWLDataPropertyComplexityHarvesterEL (OWLOntology ont) {
		this.ont = ont; 
		this.dataPropertyComplexities = null; 
	}

	public void retrieveComplexities() {
		dataPropertyComplexities = new Hashtable<>();
		double auxiliarComplexity = 0.0;
		ArrayList<String> auxIds = new ArrayList<>(); 
		for (OWLDataProperty dp: ont.getDataPropertiesInSignature(true)) {
			auxiliarComplexity = 1.0; 
			// we check the only properties that can be present in EL profile
			if (dp.isFunctional(ont)) {
				auxiliarComplexity += 1.0; 
			}
			dataPropertyComplexities.put(dp.toString(), auxiliarComplexity);
		}
		// we now check the RIAs, and equivalence axioms
		for (OWLSubDataPropertyOfAxiom subPropAx: ont.getAxioms(AxiomType.SUB_DATA_PROPERTY)) {			
			checkAndUpdate(subPropAx.getSubProperty().toString(), 1.0);
		}
		
		for (OWLEquivalentDataPropertiesAxiom equivPropAx: ont.getAxioms(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
			// we get the maximum of all the values
			auxiliarComplexity = -1.0;
			auxIds.clear(); 
			for (OWLDataPropertyExpression dp: equivPropAx.getProperties()) {
				auxIds.add(dp.toString()); 
				if (dataPropertyComplexities.containsKey(dp.toString())) {
					if (dataPropertyComplexities.get(dp.toString()) > auxiliarComplexity) {
						auxiliarComplexity = dataPropertyComplexities.get(dp.toString()); 
					}
				}
			}
			for (String id: auxIds) {
				dataPropertyComplexities.put(id, auxiliarComplexity); 
			}
		}
		
	}
	
	public Hashtable<String, Double> getDataPropertyComplexities() throws NotYetRetrievedException {
		if (dataPropertyComplexities == null) throw new NotYetRetrievedException(); 
		return dataPropertyComplexities;
	}

	public void checkAndUpdate(String id, double value) {
		double aux = 0.0; 
		if (dataPropertyComplexities.containsKey(id)) {
			aux = dataPropertyComplexities.get(id); 
			dataPropertyComplexities.put(id, aux+value); 
		}
	}
}
