///////////////////////////////////////////////////////////////////////////////
// File: SimpleGraphNode.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Simple graph to store the ABOX in order to retrieve the metrics
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.utils;

import java.util.HashSet;
import java.util.Hashtable;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;

public class SimpleABoxGraphNode {
	OWLObject object = null;
	// we will work with the names of the properties that 
	// bind the diffent individuals in the graph
	// I would have used OWLProperty, but we want 
	// to respresent differentFrom and classAssertions 
	// as well in the graph
	Hashtable<String, HashSet<OWLObject>> outLinks;
	
	// information for the metrics
	long numSubgraph; 
	
	public SimpleABoxGraphNode (OWLObject obj) {
		this.object = obj; 
		this.outLinks = new Hashtable<>();
		this.numSubgraph = -1; 
	}
	
	public Hashtable<String, HashSet<OWLObject>> getOutLinks() {
		return outLinks;
	}

	public void setOutLinks(Hashtable<String, HashSet<OWLObject>> outLinks) {
		this.outLinks = outLinks;
	}


	public OWLObject getObject() {
		return object;
	}


	public void setObject(OWLObject object) {
		this.object = object;
	}

	public HashSet<OWLObject> checkAndCreatePropertyValues (String prop) {
		if (!this.outLinks.containsKey(prop)) {
			this.outLinks.put(prop, new HashSet<OWLObject>()); 
		}
		return this.outLinks.get(prop); 
	}


	public long getNumSubgraph() {
		return numSubgraph;
	}


	public void setNumSubgraph(long numSubgraph) {
		this.numSubgraph = numSubgraph;
	}
	
}
