///////////////////////////////////////////////////////////////////////////////
// File: OWLClassExpressionHarvester.java 
// Author: Carlos Bobed
// Date: August 2016
// Comments: Class to traverse a given ontology and retrieve all the anonymous 
// 		concept expressions that appear in the ontology regardless the kind 
// 		of axioms that they appear within. 
// 		To speed up the syntactical sieve we apply, we use an auxiliar hashtable 
// 		to keep track of the class expressions we have already visited. 
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.harvester;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

public class OWLClassExpressionHarvester implements OWLAxiomVisitor {

	public ArrayList<OWLClassExpression> getHarvestedClasses() {
		return harvestedClasses;
	}

	ArrayList<OWLClassExpression> harvestedClasses = null; 
	HashSet<Integer> auxIDSet = null; 
	
	public OWLClassExpressionHarvester () {
		this.harvestedClasses = new ArrayList<OWLClassExpression>();
		this.auxIDSet = new HashSet<>(); 
	}
	
	@Override
	public void visit(OWLAnnotationAssertionAxiom axiom) {
		return;
	}

	@Override
	public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		return;
	}

	@Override
	public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
		return;
	}

	@Override
	public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
		return;
	}
	
	@Override
	public void visit(OWLDifferentIndividualsAxiom axiom) {
		return;
	}

	@Override
	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		return;
	}

	@Override
	public void visit(SWRLRule rule) {
		return;
	}
	
	@Override
	public void visit(OWLSameIndividualAxiom axiom) {
		return;
	}
	
	@Override
	public void visit(OWLSubPropertyChainOfAxiom axiom) {
		return;
	}
	
	@Override
	public void visit(OWLDeclarationAxiom axiom) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(OWLSubClassOfAxiom axiom) { 		
		if (axiom.getSubClass().isAnonymous()) {
			checkAndAdd(axiom.getSubClass()); 
		}
		if (axiom.getSuperClass().isAnonymous()) {
			checkAndAdd(axiom.getSuperClass()); 
		}

	}

	@Override
	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDisjointClassesAxiom axiom) {		
		List<OWLClassExpression> operands = axiom.getClassExpressionsAsList();		
		for (OWLClassExpression ce: operands) {
			if (ce.isAnonymous()) {
				checkAndAdd(ce); 
			}
		}
	}
	

	@Override
	public void visit(OWLDisjointUnionAxiom axiom) {
		Set<OWLClassExpression> operands = axiom.getClassExpressions();
		for (OWLClassExpression ce: operands) {
			if (ce.isAnonymous()) {
				checkAndAdd(ce); 
			}
		}
	}
	
	@Override
	public void visit(OWLEquivalentClassesAxiom axiom) {	
		List<OWLClassExpression> operands = axiom.getClassExpressionsAsList();
		for (OWLClassExpression ce: operands) {
			if (ce.isAnonymous()) {
				checkAndAdd(ce); 
			}
		}
	}

	@Override
	public void visit(OWLDataPropertyDomainAxiom axiom) {
		OWLClassExpression domain = axiom.getDomain();
		if (domain.isAnonymous()) {
			checkAndAdd(domain); 
		}
	}

	@Override
	public void visit(OWLObjectPropertyDomainAxiom axiom) {	
		OWLClassExpression domain = axiom.getDomain();		
		if (domain.isAnonymous()) {
			checkAndAdd(domain); 
		}
	}

	@Override
	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		// TODO Auto-generated method stub

	}


	@Override
	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectPropertyRangeAxiom axiom) {		
		OWLClassExpression range = axiom.getRange();		
		if (range.isAnonymous()) {
			checkAndAdd(range); 
		}
	}

	@Override
	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLSubObjectPropertyOfAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataPropertyRangeAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLClassAssertionAxiom axiom) {
		return;
	}

	@Override
	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLSubDataPropertyOfAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLHasKeyAxiom axiom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDatatypeDefinitionAxiom axiom) {
		return;
	}
	
	/* Auxiliary methods */ 
	
	/* checks whether the class expression has been already inserted by using its hashcode to avoid 
	 * string comparison
	 */
	private void checkAndAdd (OWLClassExpression axiom) {
		Integer auxID; 			
		auxID = axiom.toString().hashCode(); 
		if (!auxIDSet.contains(auxID)) {
			harvestedClasses.add(axiom); 
			auxIDSet.add(auxID); 
		} 	
	}

}
