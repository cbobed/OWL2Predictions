///////////////////////////////////////////////////////////////////////////////
// File: SimpleGraph.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Simple graph to store the ABOX in order to retrieve the metrics
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.utils;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

public class SimpleABoxGraph {
// Taken from OWLAPI 3.5.4 source code: 
//  public static final Set<AxiomType<?>> ABoxAxiomTypes = new HashSet<AxiomType<?>>(
//    Arrays.asList(CLASS_ASSERTION, SAME_INDIVIDUAL,
//            DIFFERENT_INDIVIDUALS, OBJECT_PROPERTY_ASSERTION,
//            NEGATIVE_OBJECT_PROPERTY_ASSERTION,
//            DATA_PROPERTY_ASSERTION, NEGATIVE_DATA_PROPERTY_ASSERTION));
	
//    public static final AxiomType<OWLClassAssertionAxiom> CLASS_ASSERTION = getInstance(
//            5, "ClassAssertion", false, false, true);
//    /** SameIndividual */
//    public static final AxiomType<OWLSameIndividualAxiom> SAME_INDIVIDUAL = getInstance(
//            6, "SameIndividual", false, false, true);
//    /** DifferentIndividuals */
//    public static final AxiomType<OWLDifferentIndividualsAxiom> DIFFERENT_INDIVIDUALS = getInstance(
//            7, "DifferentIndividuals", false, false, true);
//    /** ObjectPropertyAssertion */
//    public static final AxiomType<OWLObjectPropertyAssertionAxiom> OBJECT_PROPERTY_ASSERTION = getInstance(
//            8, "ObjectPropertyAssertion", false, false, true);
//    /** NegativeObjectPropertyAssertion */
//    public static final AxiomType<OWLNegativeObjectPropertyAssertionAxiom> NEGATIVE_OBJECT_PROPERTY_ASSERTION = getInstance(
//            9, "NegativeObjectPropertyAssertion", true, false, true);
//    /** DataPropertyAssertion */
//    public static final AxiomType<OWLDataPropertyAssertionAxiom> DATA_PROPERTY_ASSERTION = getInstance(
//            10, "DataPropertyAssertion", false, false, true);
//    /** NegativeDataPropertyAssertion */
//    public static final AxiomType<OWLNegativeDataPropertyAssertionAxiom> NEGATIVE_DATA_PROPERTY_ASSERTION = getInstance(
//            11, "NegativeDataPropertyAssertion", true, false, true);
	
	public static final String CLASS_ASSERTION_STRING = "ClassAssertion"; 
	public static final String SAME_INDIVIDUAL_STRING = "SameIndividual"; 
	public static final String DIFFERENT_INDIVIDUALS_STRING = "DifferentIndividuals"; 
	public static final String OBJECT_PROPERTY_ASSERTION_STRING = "ObjectPropertyAssertion"; 
	public static final String NEGATIVE_OBJECT_PROPERTY_ASSERTION_STRING = "NegativeObjectPropertyAssertion"; 
	public static final String DATA_PROPERTY_ASSERTION_STRING = "DataPropertyAssertion"; 
	public static final String NEGATIVE_DATA_PROPERTY_ASSERTION_STRING = "NegativeDataPropertyAssertion"; 
	
	public static final String INVERSE_STRING = "-Inverse"; 
	public static final String NEGATIVE_STRING = "-Negative"; 
	
	// we could speed up a little more the lookups by using the 
	// hashcodes of the namedIndividuals instead of the objects 
	// directly, but is is more readable this way
	Hashtable<OWLIndividual, SimpleABoxGraphNode> nodes = null;
	
	long classAssertionsAxioms; 
	long sameIndividualAxioms; 
	long differentIndividualAxioms; 
	long objectPropertyAxioms; 
	long negativeObjectPropertyAxioms;
	long dataPropertyAssertionAxioms; 
	long negativeDataPropertyAssertionAxioms; 
	
	OWLOntology ont = null; 
	
	public SimpleABoxGraph (OWLOntology ont ) {
		SimpleABoxGraphNode entry = null; 
		List<OWLIndividual> indList = null; 
		OWLIndividual ind1 = null; 
		OWLIndividual ind2 = null; 
		OWLLiteral value = null; 
		String auxPropertyIRI = null; 
		
		this.ont = ont; 
		
		this.classAssertionsAxioms = 0; 
		this.sameIndividualAxioms = 0; 
		this.differentIndividualAxioms = 0; 
		this.objectPropertyAxioms = 0; 
		this.negativeObjectPropertyAxioms = 0;
		this.dataPropertyAssertionAxioms = 0; 
		this.negativeDataPropertyAssertionAxioms = 0;
		
		this.nodes = new Hashtable<>();
		
		for (OWLAxiom axiom: ont.getABoxAxioms(true)) {
			switch (axiom.getAxiomType().toString()){
				case CLASS_ASSERTION_STRING:
					this.classAssertionsAxioms++; 
					OWLClassAssertionAxiom cAxiom = (OWLClassAssertionAxiom) axiom;
					// if it is not in the graph structure yet, 
					// we include it
					entry = checkAndCreateIndividualEntry(cAxiom.getIndividual()); 
					// we check whether there is already a hashset created
					entry.checkAndCreatePropertyValues(CLASS_ASSERTION_STRING).add(cAxiom.getClassExpression()); 
					break; 
				case SAME_INDIVIDUAL_STRING: 
					OWLSameIndividualAxiom sameAxiom = (OWLSameIndividualAxiom) axiom;
					// to avoid problems, we include them pairwisely
					// we include all the edges 
					this.sameIndividualAxioms += ((sameAxiom.getIndividuals().size())*(sameAxiom.getIndividuals().size()-1))/2; 
					indList = sameAxiom.getIndividualsAsList(); 					
					for (int i=0; i<indList.size()-1;i++) {
						for (int j=i+1; j<indList.size(); j++) {
							ind1 = indList.get(i); 
							ind2 = indList.get(j); 							
							// we insert both entries
							entry = checkAndCreateIndividualEntry(ind1);
							entry.checkAndCreatePropertyValues(SAME_INDIVIDUAL_STRING).add(ind2); 							
							entry = checkAndCreateIndividualEntry(ind2); 
							entry.checkAndCreatePropertyValues(SAME_INDIVIDUAL_STRING).add(ind1); 
						}
					}
					break; 
				case DIFFERENT_INDIVIDUALS_STRING:
					OWLDifferentIndividualsAxiom diffAxiom = (OWLDifferentIndividualsAxiom) axiom;
					this.differentIndividualAxioms += (diffAxiom.getIndividuals().size()*(diffAxiom.getIndividuals().size()-1))/2; 
					indList = diffAxiom.getIndividualsAsList(); 
					for (int i=0; i<indList.size()-1;i++) {
						for (int j=i+1; j<indList.size(); j++) {
							ind1 = indList.get(i); 
							ind2 = indList.get(j); 							
							// we insert both entries
							entry = checkAndCreateIndividualEntry(ind1);
							entry.checkAndCreatePropertyValues(DIFFERENT_INDIVIDUALS_STRING).add(ind2); 
							entry = checkAndCreateIndividualEntry(ind2); 
							entry.checkAndCreatePropertyValues(DIFFERENT_INDIVIDUALS_STRING).add(ind1); 
						}
					}
					break; 
				case OBJECT_PROPERTY_ASSERTION_STRING: 
					OWLObjectPropertyAssertionAxiom objPropAxiom = (OWLObjectPropertyAssertionAxiom) axiom; 
					this.objectPropertyAxioms++; 
					ind1 = objPropAxiom.getSubject(); 
					ind2 = objPropAxiom.getObject();
					// we assume that all role assertions are simple OWLObjectProperties 
					auxPropertyIRI = objPropAxiom.getProperty().asOWLObjectProperty().getIRI().toQuotedString(); 
					entry = checkAndCreateIndividualEntry(ind1);
					entry.checkAndCreatePropertyValues(auxPropertyIRI).add(ind2);
					// we differentiate the inverse situation
					entry = checkAndCreateIndividualEntry(ind2); 
					entry.checkAndCreatePropertyValues(auxPropertyIRI+INVERSE_STRING).add(ind1); 	
					break;
				case NEGATIVE_OBJECT_PROPERTY_ASSERTION_STRING:
					this.negativeObjectPropertyAxioms++; 
					OWLNegativeObjectPropertyAssertionAxiom negObjPropAxiom = (OWLNegativeObjectPropertyAssertionAxiom) axiom; 
					ind1 = negObjPropAxiom.getSubject(); 
					ind2 = negObjPropAxiom.getObject(); 
					auxPropertyIRI = negObjPropAxiom.getProperty().asOWLObjectProperty().getIRI().toQuotedString(); 
					entry = checkAndCreateIndividualEntry(ind1);
					entry.checkAndCreatePropertyValues(auxPropertyIRI+NEGATIVE_STRING).add(ind2);
					// we differentiate the inverse situation
					entry = checkAndCreateIndividualEntry(ind2); 
					entry.checkAndCreatePropertyValues(auxPropertyIRI+NEGATIVE_STRING+INVERSE_STRING).add(ind1); 
					break;
				case DATA_PROPERTY_ASSERTION_STRING:
					this.dataPropertyAssertionAxioms++; 
					OWLDataPropertyAssertionAxiom dataPropAxiom = (OWLDataPropertyAssertionAxiom) axiom; 
					ind1 = dataPropAxiom.getSubject(); 
					value = dataPropAxiom.getObject();   
					entry = checkAndCreateIndividualEntry(ind1);
					auxPropertyIRI = dataPropAxiom.getProperty().asOWLDataProperty().getIRI().toQuotedString(); 
					entry.checkAndCreatePropertyValues(auxPropertyIRI).add(value);
					break;
				case NEGATIVE_DATA_PROPERTY_ASSERTION_STRING:
					this.negativeDataPropertyAssertionAxioms++; 
					OWLNegativeDataPropertyAssertionAxiom negDataPropAxiom = (OWLNegativeDataPropertyAssertionAxiom) axiom; 
					ind1 = negDataPropAxiom.getSubject(); 
					value = negDataPropAxiom.getObject();   
					entry = checkAndCreateIndividualEntry(ind1);
					auxPropertyIRI = negDataPropAxiom.getProperty().asOWLDataProperty().getIRI().toQuotedString(); 
					entry.checkAndCreatePropertyValues(negDataPropAxiom.getProperty().toString()+NEGATIVE_STRING).add(value);
					break;
				default: 
					// this should never happen
					break; 
			}
		}
	}
	
	private SimpleABoxGraphNode checkAndCreateIndividualEntry (OWLIndividual ind) {
		if (!nodes.containsKey(ind)){
			nodes.put(ind, new SimpleABoxGraphNode(ind)); 
		}
		return nodes.get(ind); 
	}
	
	public void printValues () {
		OWLDataFactory dataFactory = ont.getOWLOntologyManager().getOWLDataFactory(); 
		System.out.println("Individuals: "+nodes.size());
		long edges = 0; 
		long ca = 0; 
		long sa = 0; 
		long df = 0; 
		long op = 0; 
		long nop = 0; 
		long dp = 0; 
		long ndp = 0; 
		String auxIRI = ""; 
		boolean inverse = false; 
		boolean negative = false; 
		for (OWLIndividual ind: nodes.keySet()){
			for (String key: nodes.get(ind).getOutLinks().keySet()) {
				edges+=nodes.get(ind).getOutLinks().get(key).size(); 
				switch (key) {
					case CLASS_ASSERTION_STRING:
						ca+=nodes.get(ind).getOutLinks().get(key).size(); 
						break;
					case SAME_INDIVIDUAL_STRING: 
						sa+=nodes.get(ind).getOutLinks().get(key).size(); 
						break; 
					case DIFFERENT_INDIVIDUALS_STRING: 
						df+=nodes.get(ind).getOutLinks().get(key).size(); 
						break; 
					default:
						if (key.endsWith(INVERSE_STRING)) {
							inverse = true;
							auxIRI = key.substring(0, key.lastIndexOf(INVERSE_STRING));
							if (auxIRI.endsWith(NEGATIVE_STRING)) {
								negative = true; 
								auxIRI = auxIRI.substring(0,auxIRI.lastIndexOf(NEGATIVE_STRING)); 
							}
							else{
								negative = false; 
							}
							auxIRI = auxIRI.substring(1, auxIRI.length()-1); 
						}
						else {
							inverse = false; 
							auxIRI = key; 
							if (auxIRI.endsWith(NEGATIVE_STRING)) {
								negative = true; 
								auxIRI = auxIRI.substring(0,auxIRI.lastIndexOf(NEGATIVE_STRING)); 								
							}
							else{
								negative = false; 					
							}
							auxIRI = auxIRI.substring(1, auxIRI.length()-1);
						}
						IRI iri = IRI.create(auxIRI); 
						OWLEntity aux = (ont.getEntitiesInSignature(iri)).iterator().next();
						if (aux != null) {
							if (aux instanceof OWLObjectPropertyExpression) {
								if (negative) {
									nop += nodes.get(ind).getOutLinks().get(key).size(); 
								}
								else {
									op += nodes.get(ind).getOutLinks().get(key).size(); 
								}
							}
							else if (aux instanceof OWLDataPropertyExpression) {
								if (negative) {
									ndp += nodes.get(ind).getOutLinks().get(key).size(); 
								}
								else {
									dp += nodes.get(ind).getOutLinks().get(key).size(); 
								}
							}
						}
						break; 			
				}				
			}
		}
		System.out.println("Edges: "+edges); 
		System.out.println("Axioms"); 
		System.out.println("\tclassAssertions: "+this.classAssertionsAxioms + " vs "+ca); 
		System.out.println("\tsameAs: "+this.sameIndividualAxioms+ " vs "+sa); 
		System.out.println("\tdifferentFrom: "+this.differentIndividualAxioms+ " vs "+df); 
		System.out.println("\tobjProp: "+this.objectPropertyAxioms+ " vs "+op); 
		System.out.println("\tnegObjProp: "+this.negativeObjectPropertyAxioms+ " vs "+nop);
		System.out.println("\tdataProp: "+this.dataPropertyAssertionAxioms+ " vs "+dp);
		System.out.println("\tnegDataProp: "+this.negativeDataPropertyAssertionAxioms+ " vs "+ndp);
		
		System.out.println(ca+sa+df+op+nop+dp+ndp); 
		System.out.println(classAssertionsAxioms+sameIndividualAxioms*2+differentIndividualAxioms*2+objectPropertyAxioms*2+negativeDataPropertyAssertionAxioms*2+dataPropertyAssertionAxioms+negativeDataPropertyAssertionAxioms); 
		System.out.println("Total ... "+
				(this.classAssertionsAxioms +
						this.sameIndividualAxioms * 2 + 
						this.differentIndividualAxioms * 2 + 
						this.objectPropertyAxioms * 2 + 
						this.negativeObjectPropertyAxioms * 2 + 
						this.dataPropertyAssertionAxioms + 
						this.negativeDataPropertyAssertionAxioms) ); 	
	}

	public Hashtable<OWLIndividual, SimpleABoxGraphNode> getNodes() {
		return nodes;
	}

	public void setNodes(Hashtable<OWLIndividual, SimpleABoxGraphNode> nodes) {
		this.nodes = nodes;
	}
	
}
