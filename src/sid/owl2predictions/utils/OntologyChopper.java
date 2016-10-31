///////////////////////////////////////////////////////////////////////////////
// File: OntologyChopper.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.02
// Comments: Class which chops a given ontology keeping the minimum ABox count 
// 		and the desired ABOX/TBOX ratio 
// Modifications: 
// 		* CBL: Changed the removing axioms method to enhance performance 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyChopper {

	/** 
	 * The ontology to be choppeed
	 */
	OWLOntology ont = null; 
	double desiredRatio = -1.0; 
	long bucketSize = -1; 
	
	public OntologyChopper (OWLOntology ont, double desiredRatio, long bucketSize) {
		this.ont = ont; 
		// [0..infinite] with 1.0 == 100%
		this.desiredRatio = desiredRatio; 
		this.bucketSize = bucketSize; 	
	}
	
	// forced to change from long to double by Java due to the signature of the method
	public OntologyChopper (OWLOntology ont, double desiredRatio, double initialChopNumber) {
		this.ont = ont; 
		// [0..infinite] with 1.0 == 100%
		this.desiredRatio = desiredRatio; 
		this.bucketSize = (long) Math.floor(( ont.getABoxAxioms(true).size() - (this.desiredRatio * ont.getTBoxAxioms(true).size()) ) / initialChopNumber); 
	}
	
	public void chopOntology (String outFilename) {
		OWLOntologyManager om = ont.getOWLOntologyManager();
		
		double TBoxAxiomsCount = ont.getTBoxAxioms(true).size(); 
		double ABoxAxiomsCount = ont.getABoxAxioms(true).size();
		
		Set<OWLAxiom> currentABoxSet = null; 
		int step = 1; 
		double minimumABoxAxiomCount = -1;
		long currentStepSize = -1; 
		long currentCount = -1; 
		OWLOntology currentOntology = ont; 
		OWLOntology newOntology = null; 
		
		// the minimum size of the ABOX that would hold the desiredRatio
		minimumABoxAxiomCount = Math.floor(TBoxAxiomsCount*desiredRatio); 
		
		System.out.println("TBox: "+TBoxAxiomsCount+" ABox:"+ABoxAxiomsCount+" Min:"+minimumABoxAxiomCount); 
		
		// if we have a ratio higher than the desired one
		// and we have more than the minimum (given by the bucketSize)
		if ((ABoxAxiomsCount/TBoxAxiomsCount) >= desiredRatio && 
				ABoxAxiomsCount > bucketSize) {
		
			// we can remove axioms as we work with the different 
			// traversals
			currentStepSize = bucketSize;
			
			while (minimumABoxAxiomCount < (ABoxAxiomsCount - currentStepSize) ) {
				// we have to chop the ontology in several chunks
				// the current chunkSize to be removed
				currentStepSize = step*bucketSize; 
				// the current number of ABoxAxioms in the current ontology 
				// initialized taking into account the Abox axioms in the original ontology
				currentCount = (long)ABoxAxiomsCount - currentStepSize;
				// we restart the chopping process
				currentOntology = ont; 
				
				// we keep all the original axioms but the ABox Ones 
				Set<OWLAxiom> baseAxioms = currentOntology.getAxioms(); 
				baseAxioms.removeAll(currentOntology.getABoxAxioms(true)); 
				currentABoxSet = currentOntology.getABoxAxioms(true); 
				
				// we copy and remove axioms randomly
				// we only enter the loop if we have enough axioms to be removed (currentCount)
				while (currentCount > minimumABoxAxiomCount) {
					try{
						// we now have the ontologyBase in baseAxioms
						// create a new version of the ontology in the ontology manager
						assert currentOntology != null; 
						// we create a new OntologyManager to avoid ontologyID clashes
						om = OWLManager.createOWLOntologyManager(); 
						System.out.println("processing "+step+"  "+currentStepSize+"  "+currentCount); 
						newOntology = om.createOntology(new OWLOntologyID( currentOntology.getOntologyID().getOntologyIRI(), 
														IRI.create(currentOntology.getOntologyID().getOntologyIRI().toString()
																+"_"+step+"_"+currentStepSize+"_"+currentCount)));
						// we assert every axiom of the current ontology
						// into the newly created one
						om.addAxioms(newOntology, baseAxioms); 
						
						// before adding the ABox axioms, we get rid of 
						// #stepSize axioms randomly
						
						List<OWLAxiom> axiomList = new ArrayList<OWLAxiom> (currentABoxSet); 
						List<OWLAxiom> axiomsToRemove = null; 
						Collections.shuffle(axiomList);
						axiomsToRemove = axiomList.subList(0, (int)currentStepSize); 
						// we remove before asserting
						currentABoxSet.removeAll(axiomsToRemove); 
						// we only assert the previous ABox - randomlySelectedOnes
						om.addAxioms(newOntology, currentABoxSet); 
						
						// we save the ontology annotating its current iteration, stepSize, and AboxSize
						om.saveOntology(newOntology ,new FileOutputStream(new File(outFilename+"_"+step
								+"_"+currentStepSize+"_"+currentCount+".owl")));
						
						
						// we update the count
						currentCount -= currentStepSize; 
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
				// we increase the currentStepSize 
				step++; 
				currentStepSize = step*bucketSize; 
			}
			
		}
	}
	
}
