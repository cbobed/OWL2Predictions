///////////////////////////////////////////////////////////////////////////////
// File: OWLClassExpressionComplexityEstimator.java 
// Author: Carlos Bobed
// Date: September 2016
// Comments:  
// Modifications: 
// 		October 2016: we add the complexities of the different ObjectProperties 
// 			and DataProperties for EL Profile
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics.ELProfile;


import java.util.HashSet;
import java.util.Hashtable;
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
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
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

public class OWLClassExpressionComplexityEstimatorEL implements OWLClassExpressionVisitor {

	int height; 
	int cost;
	int numConstructors;
	Stack<Integer> auxHeights; 
	Stack<Integer> auxCosts; 
	
	HashSet<OWLObject> signature; 
	
	Hashtable<String, Double> objectPropertiesComplexities = null;
	Hashtable<String, Double> dataPropertiesComplexities = null; 
	
	public OWLClassExpressionComplexityEstimatorEL (
			Hashtable<String, Double> objectPropertiesComplexities, 
			Hashtable<String, Double> dataPropertiesComplexities) {
		this.height = 0;
		this.cost = 0; 
		this.numConstructors = 0; 
		this.signature = new HashSet<>();
		this.auxHeights = new Stack<>(); 
		this.auxCosts = new Stack<>(); 
		
		this.objectPropertiesComplexities = objectPropertiesComplexities; 
		this.dataPropertiesComplexities = dataPropertiesComplexities; 
	}

	@Override
	public void visit(OWLClass arg0) {
		// printDebugInfo(arg0);
		if (!signature.contains(arg0)) {
			signature.add(arg0); 
		}
		this.auxHeights.push(1);
		this.auxCosts.push(1); 
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost < 1) {
			this.cost = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectIntersectionOf arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++; 	
		this.auxHeights.push(-1);
		this.auxCosts.push(-1); 
		for (OWLClassExpression exp: arg0.getOperands()) {
			exp.accept(this);
		}
		// we get the maximum of the top of the stack 
		// until we get a -1 
		// this is done to track the traversal
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
		
		// we sum the values of the cost of the 
		// stack of costs
		currentValue = this.auxCosts.pop(); 
		int costSum = 0;  
		while (currentValue != -1) {
			costSum+=currentValue; 
			currentValue = this.auxCosts.pop(); 
		}
		this.auxCosts.push(costSum); 
		
		if (this.height <= maxHeight){
			this.height = maxHeight; 
		}
		if (this.cost <= costSum) {
			this.cost = costSum; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectUnionOf arg0) {
		// in the EL Profile this constructor must not appear 
		// printDebugInfo(arg0);
		this.numConstructors++; 
		this.auxHeights.push(-1); 
		this.auxCosts.push(-1); 
		for (OWLClassExpression exp: arg0.getOperands()) {
			exp.accept(this);
		}
		
		// heights 
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
		
		// we sum the values of the cost of the 
		// stack of costs
		currentValue = this.auxCosts.pop(); 
		int costSum = 0;  
		while (currentValue != -1) {
			costSum+=currentValue; 
			currentValue = this.auxCosts.pop(); 
		}
		this.auxCosts.push(costSum); 
		
		if (this.height <= maxHeight){
			this.height = maxHeight; 
		}
		if (this.cost <= costSum) {
			this.cost = costSum; 
		}
		
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectComplementOf arg0) {
		// printDebugInfo(arg0);
		// in the EL Profile this constructor must not appear 
		
		this.numConstructors++;  
		arg0.getOperand().accept(this); 
		this.auxHeights.push(this.auxHeights.pop()+1); 
		this.auxCosts.push(this.auxCosts.pop()+1); 
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		if (this.cost <= this.auxCosts.peek()) {
			this.cost = this.auxCosts.peek(); 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectSomeValuesFrom arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++; 
	
		int costSum = 0; 
		arg0.getFiller().accept(this);
		// the accept must leave a cost value
		costSum = this.auxCosts.pop();
		if (objectPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			costSum += objectPropertiesComplexities.get(arg0.getProperty().toString()); 
		}
		this.auxCosts.push(costSum); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(this.auxHeights.pop()+1); 
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		
		if (this.cost <= this.auxCosts.peek()) {
			this.cost = this.auxCosts.peek(); 
		}
		
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectAllValuesFrom arg0) {
		// not in EL Profile
		// printDebugInfo(arg0);
		this.numConstructors++;
		int costSum = 0; 
		arg0.getFiller().accept(this);
		costSum = this.auxCosts.pop(); 
		
		if (objectPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			costSum += objectPropertiesComplexities.get(arg0.getProperty().toString()); 
		}
		this.auxCosts.push(costSum); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		if (this.cost <= this.auxHeights.peek()) {
			this.cost = this.auxCosts.peek(); 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectHasValue arg0) {
		// printDebugInfo(arg0);
		this.numConstructors++; 
		this.auxHeights.push(1);
		
		int cost = 1; 
		if (objectPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			cost = (int) Math.round(objectPropertiesComplexities.get(arg0.getProperty().toString())); 
		}
		this.auxCosts.push(cost); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		// we have into account for the signature: concepts, roles, namedIndividuals
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		if (!signature.contains(arg0.getValue())) {
			signature.add(arg0.getValue()); 
		}
		
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost < cost) {
			this.cost = cost; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectMinCardinality arg0) {
		// not in EL profile
		// printDebugInfo(arg0);
		this.numConstructors++;
		
		int costSum = 0; 
		arg0.getFiller().accept(this);
		costSum = this.auxCosts.pop(); 
		if (objectPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			costSum += objectPropertiesComplexities.get(arg0.getProperty().toString()); 
		}
		this.auxCosts.push(costSum);  

		// we have to take into account the properties in the signature of the 
		// class expression
		// we just have into account for the signature: concepts and roles
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		if (this.cost <= this.auxCosts.peek()) {
			this.cost = this.auxCosts.peek(); 
		}
		
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectExactCardinality arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		
		int costSum = 0; 
		arg0.getFiller().accept(this);
		costSum = this.auxCosts.pop(); 
		
		if (objectPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			costSum += objectPropertiesComplexities.get(arg0.getProperty().toString()); 
		}
		this.auxCosts.push(costSum); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		// we just have into account for the signature: concepts and roles
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		if (this.cost <= this.auxCosts.peek()) {
			this.cost = this.auxCosts.peek(); 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectMaxCardinality arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		
		int costSum = 0; 
		arg0.getFiller().accept(this);
		costSum = this.auxCosts.pop(); 
		
		if (objectPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			costSum += objectPropertiesComplexities.get(arg0.getProperty().toString()); 
		}
		this.auxCosts.push(costSum); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		// we just have into account for the signature: concepts and roles
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(this.auxHeights.pop()+1);
		if (this.height <= this.auxHeights.peek() ) {
			this.height = this.auxHeights.peek(); 
		}
		if (this.cost <= this.auxCosts.peek()) {
			this.cost = this.auxCosts.peek(); 
		}
		// printDebugInfo(arg0);
	}
	
	@Override
	public void visit(OWLObjectHasSelf arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		
		int cost = 1; 
		if (objectPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			cost = (int) Math.round(objectPropertiesComplexities.get(arg0.getProperty().toString())); 
		}
		this.auxCosts.push(cost); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		// we just have into account for the signature: concepts and roles
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost < cost) {
			this.cost = cost; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLObjectOneOf arg0) {
		// EL profile is restricted to safe nominals
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++;  	
		
		this.auxHeights.push(1);
		this.auxCosts.push(1);
		
		for (OWLIndividual ind: arg0.getIndividuals()) {
			if (!signature.contains(ind)) {
				signature.add(ind); 
			}
		}
		
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost < 1) {
			this.cost = 1; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataSomeValuesFrom arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		
		int cost = 1; 
		if (dataPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			cost = (int) Math.round(dataPropertiesComplexities.get(arg0.getProperty().toString())); 
		}
		this.auxCosts.push(cost); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost <cost) {
			this.cost = cost; 
		}
		
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataAllValuesFrom arg0) {
		// not in EL Profile
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++;
		
		int cost = 1; 
		if (dataPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			cost = (int) Math.round(dataPropertiesComplexities.get(arg0.getProperty().toString())); 
		}
		this.auxCosts.push(cost); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost < cost) {
			this.cost = cost; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataHasValue arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		this.auxHeights.push(1);
		
		int cost = 1; 
		if (dataPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			cost = (int) Math.round(dataPropertiesComplexities.get(arg0.getProperty().toString())); 
		}
		this.auxCosts.push(cost); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost < cost) {
			this.cost = cost; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataMinCardinality arg0) {
		// not in EL Profile
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		
		int cost = 1; 
		if (dataPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			cost = (int) Math.round(dataPropertiesComplexities.get(arg0.getProperty().toString())); 
		}
		this.auxCosts.push(cost); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost < cost) {
			this.cost = cost; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataExactCardinality arg0) {
		//not in el profile 
		
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		
		int cost = 1; 
		if (dataPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			cost = (int) Math.round(dataPropertiesComplexities.get(arg0.getProperty().toString())); 
		}
		this.auxCosts.push(cost); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost < cost) {
			this.cost = cost; 
		}
		// printDebugInfo(arg0);
	}

	@Override
	public void visit(OWLDataMaxCardinality arg0) {
		// printDebugInfo(arg0);
		// TODO Auto-generated method stub
		this.numConstructors++; 
		
		int cost = 1; 
		if (dataPropertiesComplexities.containsKey(arg0.getProperty().toString())) {
			cost = (int) Math.round(dataPropertiesComplexities.get(arg0.getProperty().toString())); 
		}
		this.auxCosts.push(cost); 
		
		// we have to take into account the properties in the signature of the 
		// class expression
		if (!signature.contains(arg0.getProperty())) {
			signature.add(arg0.getProperty()); 
		}
		
		this.auxHeights.push(1);
		if (this.height < 1) {
			this.height = 1; 
		}
		if (this.cost < cost) {
			this.cost = cost; 
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

	public HashSet<OWLObject> getSignature() {
		return signature;
	}

	public void setSignature(HashSet<OWLObject> signature) {
		this.signature = signature;
	}
	
	public double estimation () {
		return ((double)this.height+this.signature.size()+this.cost)/3.0; 
	}
	
	public void  printDebugInfo(OWLClassExpression arg0) {
		System.out.println("-----------------------------"); 
		System.out.println(arg0); 
		System.out.println("Height: "+this.height); 
		System.out.println("Cost: "+this.cost);
		System.out.println("Signature: "+this.signature.size());
		System.out.println("\t"+this.signature); 
		System.out.println("#Constructors: "+this.numConstructors); 
		System.out.println(this.auxHeights); 
		System.out.println(this.auxCosts); 
		
	}
	
}
