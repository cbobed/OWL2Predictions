///////////////////////////////////////////////////////////////////////////////
// File: OWLClassExpressionComplexityEstimator.java 
// Author: Carlos Bobed
// Date: September 2016
// Comments:  
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
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
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
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

public class OWLClassExpressionComplexityEstimator implements OWLClassExpressionVisitor {

	int height; 
	int numConstructors;
	Stack<Integer> auxHeights; 
	
	HashSet<OWLClass> signature; 
	
	public OWLClassExpressionComplexityEstimator () {
		this.height = 0; 
		this.numConstructors = 0; 
		this.signature = new HashSet<>();
		this.auxHeights = new Stack<>(); 
	}

	@Override
	public void visit(OWLClass arg0) {
		// printDebugInfo(arg0);
		if (!signature.contains(arg0)) {
			signature.add(arg0); 
		}
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectIntersectionOf arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++; 	
		this.auxHeights.push(-1); 
		for (OWLClassExpression exp: arg0.getOperands()) {
			exp.accept(this);
		}
		int currentValue = this.auxHeights.pop(); 
		int maxHeight = Integer.MIN_VALUE; 
		while (currentValue != -1) {
			if (currentValue>=maxHeight) { 
				maxHeight = currentValue; 
			}
			currentValue = this.auxHeights.pop(); 			
		}
		maxHeight++; 
		this.auxHeights.push(maxHeight); 
		if (this.height <= maxHeight){
			this.height = maxHeight; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectUnionOf arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++; 
		this.auxHeights.push(-1); 
		for (OWLClassExpression exp: arg0.getOperands()) {
			exp.accept(this);
		}
		int currentValue = this.auxHeights.pop(); 
		int maxHeight = Integer.MIN_VALUE; 
		while (currentValue != -1) {
			if (currentValue>=maxHeight) { 
				maxHeight = currentValue; 
			}
			currentValue = this.auxHeights.pop(); 			
		}
		maxHeight++; 
		this.auxHeights.push(maxHeight); 
		if (this.height <= maxHeight){
			this.height = maxHeight; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectComplementOf arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++;  
		arg0.getOperand().accept(this); 
		this.auxHeights.push(this.auxHeights.pop()+1); 
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectSomeValuesFrom arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++; 
		arg0.getFiller().accept(this); 		
		this.auxHeights.push(this.auxHeights.pop()+1); 
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectAllValuesFrom arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++; 
		arg0.getFiller().accept(this); 		
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectHasValue arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectMinCardinality arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++; 
		arg0.getFiller().accept(this); 	
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectExactCardinality arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		arg0.getFiller().accept(this);
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectMaxCardinality arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		arg0.getFiller().accept(this);
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectHasSelf arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectOneOf arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++;  	
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataSomeValuesFrom arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataAllValuesFrom arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataHasValue arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataMinCardinality arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataExactCardinality arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataMaxCardinality arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		// printDebugInfo(arg0);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getNumConstructors() {
		return numConstructors;
	}

	public void setNumConstructors(int numConstructors) {
		this.numConstructors = numConstructors;
	}

	public HashSet<OWLClass> getSignature() {
		return signature;
	}

	public void setSignature(HashSet<OWLClass> signature) {
		this.signature = signature;
	}
	
	public double estimation () {
		return ((double)this.height+this.signature.size()+this.numConstructors)/3.0; 
	}
	
	public void  printDebugInfo(OWLClassExpression arg0) {
		System.out.println("-----------------------------"); 
		System.out.println(arg0); 
		System.out.println("Height: "+this.height); 
		System.out.println("Signature: "+this.signature.size());
		System.out.println("\t"+this.signature); 
		System.out.println("#Constructors: "+this.numConstructors); 
		System.out.println(this.auxHeights); 
		
	}
	
}
