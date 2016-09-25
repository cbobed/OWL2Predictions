///////////////////////////////////////////////////////////////////////////////
// File: OWLClassExpressionComplexityEstimator.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Class to calculate different parameters of the complexity of a given 
// 		concept expression. It is a first and naive version to be improved.  
// 		Tested with OWLAPI 3.4.10 and 3.5.4
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions;

import java.util.HashSet;
import java.util.Stack;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

public class OWLClassExpressionComplexityEstimator implements OWLClassExpressionVisitor {

	int height; 
	int numConstructors;
	// due to the nature of the visitor interface, 
	// we use a Stack to keep track and calculate the height of the expression 
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
		
		if (!signature.contains(arg0)) {
			signature.add(arg0); 
		}
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
	}

	@Override
	public void visit(OWLObjectIntersectionOf arg0) {
		this.numConstructors++; 
		// we use the -1 as delimiter to know which 
		// stacked heights correspond to this particular operator
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
	}

	@Override
	public void visit(OWLObjectUnionOf arg0) {
		this.numConstructors++; 

		// we use the -1 as delimiter to know which 
		// stacked heights correspond to this particular operator
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
		
	}

	@Override
	public void visit(OWLObjectComplementOf arg0) {
		
		this.numConstructors++;  
		arg0.getOperand().accept(this); 
		this.auxHeights.push(this.auxHeights.pop()+1); 
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		
	}

	@Override
	public void visit(OWLObjectSomeValuesFrom arg0) {
		
		this.numConstructors++; 
		arg0.getFiller().accept(this); 		
		this.auxHeights.push(this.auxHeights.pop()+1); 
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		
	}

	@Override
	public void visit(OWLObjectAllValuesFrom arg0) {
		
		this.numConstructors++; 
		arg0.getFiller().accept(this); 		
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		
	}

	@Override
	public void visit(OWLObjectHasValue arg0) {
		
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
	}

	@Override
	public void visit(OWLObjectMinCardinality arg0) {
		
		this.numConstructors++; 
		arg0.getFiller().accept(this); 	
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		
	}

	@Override
	public void visit(OWLObjectExactCardinality arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++; 
		arg0.getFiller().accept(this);
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		
	}

	@Override
	public void visit(OWLObjectMaxCardinality arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++; 
		arg0.getFiller().accept(this);
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		
	}

	@Override
	public void visit(OWLObjectHasSelf arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
	}

	@Override
	public void visit(OWLObjectOneOf arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++;  	
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
	}

	@Override
	public void visit(OWLDataSomeValuesFrom arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
	}

	@Override
	public void visit(OWLDataAllValuesFrom arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
	}

	@Override
	public void visit(OWLDataHasValue arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
	}

	@Override
	public void visit(OWLDataMinCardinality arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
	}

	@Override
	public void visit(OWLDataExactCardinality arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
	}

	@Override
	public void visit(OWLDataMaxCardinality arg0) {
		
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		
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
