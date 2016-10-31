///////////////////////////////////////////////////////////////////////////////
// File: SubGraphMetricsCalculator.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.02
// Comments: Class which calculates the metrics about subgraphs in the ABox
// Modifications: 
// 		- Added the axioms-based metrics 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions;

import java.util.HashSet;
import java.util.Hashtable;

import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

import sid.owl2predictions.utils.LabelProcessingResult;
import sid.owl2predictions.utils.SimpleABoxGraph;
import sid.owl2predictions.utils.SimpleABoxGraphNode;

public class SubGraphMetricsCalculator {

	OWLOntology ont = null; 
	SimpleABoxGraph graph = null; 
	
	public SubGraphMetricsCalculator (OWLOntology ont) {
		this.ont = ont; 
		this.graph = new SimpleABoxGraph(this.ont); 
	}
	
	public SubGraphMetrics calculateMetrics () {
		
		SubGraphMetrics results = new SubGraphMetrics(); 
		
		results.setCOA(ont.getABoxAxioms(true).size());
		results.setSOVA(ont.getIndividualsInSignature(true).size());
		
		// the rest of the metrics need the traversal
		long currentSubGraph = 0; 
		Hashtable<Long, Long> connectionsPerSubgraph = new Hashtable<>();
		Hashtable<Long, Long> nodesPerSubgraph = new Hashtable<>(); 
		long currentConnections = 0; 
		for (OWLIndividual ind: graph.getNodes().keySet()) {
			if (graph.getNodes().get(ind).getNumSubgraph() == -1) {
				currentConnections = traverseLabelling(graph, ind, currentSubGraph); 
				connectionsPerSubgraph.put(currentSubGraph, currentConnections);
				currentSubGraph++; 
			}
			// else 
			// it has been already assigned in a previous traversal
		}
		
		// we now count the nodes in the different subgraphs
		// TODO: this could be done during the traversal as well
		SimpleABoxGraphNode currentNode = null; 
		for (OWLIndividual ind:graph.getNodes().keySet()) {
			currentNode = graph.getNodes().get(ind); 
			if (!nodesPerSubgraph.containsKey(currentNode.getNumSubgraph())) {
				nodesPerSubgraph.put(currentNode.getNumSubgraph(), (long)0); 
			}
			// we increase the table value for such subgraph's count of nodes
			nodesPerSubgraph.put(currentNode.getNumSubgraph(), nodesPerSubgraph.get(currentNode.getNumSubgraph())+1); 			
		}
		
		// we now calculate the different metrics
		assert nodesPerSubgraph.size() == connectionsPerSubgraph.size();
		assert !connectionsPerSubgraph.contains(-1);
		assert !nodesPerSubgraph.contains(-1); 
		
		long numberOfNonOne = 0;  
		// there are max values for NonOne as they will might be different in extreme cases (multiple class assertions)
		long currentMinNodesNonOne = Integer.MAX_VALUE; 
		long currentMaxNodesNonOne = Integer.MIN_VALUE; 
		long currentMinEdgesNonOne = Integer.MAX_VALUE; 
		long currentMaxEdgesNonOne = Integer.MIN_VALUE; 
		double stdAccumulatorNodesNonOne = 0.0;
		double stdAccumulatorEdgesNonOne = 0.0; 
		
		long currentMinNodes= Integer.MAX_VALUE; 
		long currentMaxNodes= Integer.MIN_VALUE;
		long currentMinEdges= Integer.MAX_VALUE; 
		long currentMaxEdges= Integer.MIN_VALUE;		
		double stdAccumulatorNodes= 0.0;
		double stdAccumulatorEdges= 0.0;
		
		long totalNodes = 0; 
		long totalEdges = 0; 
		long totalNodesNonOne = 0; 
		long totalEdgesNonOne = 0; 
		
		for (Long subGraphId: connectionsPerSubgraph.keySet()){
			// first, the global values
			totalEdges += connectionsPerSubgraph.get(subGraphId); 
			totalNodes += nodesPerSubgraph.get(subGraphId); 
			
			if (nodesPerSubgraph.get(subGraphId) < currentMinNodes) {
				currentMinNodes = nodesPerSubgraph.get(subGraphId); 
			}
			if (nodesPerSubgraph.get(subGraphId) > currentMaxNodes) {
				currentMaxNodes = nodesPerSubgraph.get(subGraphId); 
			}
			// we update both max and min edges for NonOnegraphs
			if (connectionsPerSubgraph.get(subGraphId) < currentMinEdges) {
				currentMinEdges = connectionsPerSubgraph.get(subGraphId); 
			}
			if (connectionsPerSubgraph.get(subGraphId) > currentMaxEdges) {
				currentMaxEdges= connectionsPerSubgraph.get(subGraphId); 
			}
			
			// then NonOne
			if (nodesPerSubgraph.get(subGraphId)>1) {
				numberOfNonOne++;
				totalEdgesNonOne += connectionsPerSubgraph.get(subGraphId);
				totalNodesNonOne += nodesPerSubgraph.get(subGraphId); 
				// we update the min and max nodes for NonOne graphs
				if (nodesPerSubgraph.get(subGraphId) < currentMinNodesNonOne ) {
					currentMinNodesNonOne = nodesPerSubgraph.get(subGraphId); 
				}
				if (nodesPerSubgraph.get(subGraphId) > currentMaxNodesNonOne ) {
					currentMaxNodesNonOne = nodesPerSubgraph.get(subGraphId); 
				}				
				// we update the min and max edges for NonOnegraphs
				if (connectionsPerSubgraph.get(subGraphId) < currentMinEdgesNonOne ) {
					currentMinEdgesNonOne = connectionsPerSubgraph.get(subGraphId); 
				}
				if (connectionsPerSubgraph.get(subGraphId) > currentMaxEdgesNonOne ) {
					currentMaxEdgesNonOne = connectionsPerSubgraph.get(subGraphId); 
				}
			}
		}
		
		// we set the number of relations in the graph 
		results.setROA(totalEdges);
		
		// now we can calculate the averages to calculate the STDS
		results.setTOT_SG(connectionsPerSubgraph.size());
		results.setAVG_SG( (results.getTOT_SG() != 0? ((double) totalNodes / results.getTOT_SG() ): -1 ) );
		results.setMIN_SG( (currentMinNodes != Integer.MAX_VALUE? currentMinNodes:-1) );
		results.setMAX_SG( (currentMaxNodes != Integer.MIN_VALUE? currentMaxNodes:-1) );
		
		results.setAVG_RPA( (results.getTOT_SG() != 0? ( (double) totalEdges / results.getTOT_SG()): -1) );
		results.setMIN_RPA( (currentMinEdges != Integer.MAX_VALUE? currentMinEdges:-1) );
		results.setMAX_RPA( (currentMaxEdges != Integer.MIN_VALUE? currentMaxEdges:-1) );
		
		results.setN_TOT_SG(numberOfNonOne);
		results.setN_AVG_SG( (results.getN_TOT_SG() != 0? ((double) totalNodesNonOne / results.getN_TOT_SG()) : -1));
		results.setN_MIN_SG( (currentMinNodesNonOne != Integer.MAX_VALUE?currentMinNodesNonOne:-1) );
		results.setN_MAX_SG( (currentMaxNodesNonOne != Integer.MIN_VALUE?currentMaxNodesNonOne:-1) ); 
		
		results.setN_AVG_RPA( (results.getN_TOT_SG() != 0? ((double) totalEdgesNonOne / results.getN_TOT_SG()): -1));
		results.setN_MIN_RPA( (currentMinEdgesNonOne != Integer.MAX_VALUE?currentMinEdgesNonOne:-1) );
		results.setN_MAX_RPA( (currentMaxEdgesNonOne != Integer.MIN_VALUE?currentMaxEdgesNonOne:-1) ); 
		// there is no: results.setN_MAX_RPA 
		
		// and now the std deviations
		for (Long subGraphId: connectionsPerSubgraph.keySet()) {
			
			stdAccumulatorNodes += Math.pow( ( (double) nodesPerSubgraph.get(subGraphId) - results.getAVG_SG()) , 2.0); 
			stdAccumulatorEdges += Math.pow( ( (double) connectionsPerSubgraph.get(subGraphId) - results.getAVG_RPA()) , 2.0);
			
			if (nodesPerSubgraph.get(subGraphId) > 1){
				stdAccumulatorNodesNonOne += Math.pow( ( (double) nodesPerSubgraph.get(subGraphId) - results.getN_AVG_SG()) , 2.0); 
				stdAccumulatorEdgesNonOne += Math.pow( ( (double) connectionsPerSubgraph.get(subGraphId) - results.getN_AVG_RPA()) , 2.0);				
			}
		}
		
		results.setSTD_SG((results.getTOT_SG() != 0? (Math.sqrt(stdAccumulatorNodes / results.getTOT_SG()) ) :-1) );
		results.setSTD_RPA((results.getTOT_SG() != 0? (Math.sqrt(stdAccumulatorEdges / results.getTOT_SG())) :-1) );
		
		results.setN_STD_SG((results.getN_TOT_SG() != 0? (Math.sqrt(stdAccumulatorNodesNonOne/ results.getN_TOT_SG())) : -1));
		results.setN_STD_RPA((results.getN_TOT_SG() != 0? (Math.sqrt(stdAccumulatorEdgesNonOne/results.getN_TOT_SG())) : -1));
		
		// now we calculate the newly added metrics regarding the axioms
		// DONE IN A HURRY, SORRY FOR THE CODE :|
		System.err.println("Now the axiom metrics ..."); 
		Hashtable<Long, Long> axiomSubgraphCounts = new Hashtable<>();
		Hashtable<Long, Long> axiomNonOneSubgraphCounts = new Hashtable<>(); 
		OWLIndividual auxInd = null;
		Long auxGraphID = null;
		Long auxPrevValue = null; 
		SimpleABoxGraphNode  auxNode = null; 
		int axCount = -1; 
		for (OWLAxiom ax: ont.getABoxAxioms(true)) {
			switch (ax.getAxiomType().toString()) {
				case SimpleABoxGraph.CLASS_ASSERTION_STRING: 
					OWLClassAssertionAxiom cax = (OWLClassAssertionAxiom)ax;
					auxNode = graph.getNodes().get( cax.getIndividual() ); 
					if (auxNode != null) {
						auxGraphID = auxNode.getNumSubgraph(); 
						auxPrevValue = axiomSubgraphCounts.get(auxGraphID);
						if (auxPrevValue != null){
							axiomSubgraphCounts.put(auxGraphID, auxPrevValue+1);
						}
						else {
							axiomSubgraphCounts.put(auxGraphID, new Long(1));
						}
						
						if (nodesPerSubgraph.get(auxGraphID) == 1) {							
							auxPrevValue = axiomNonOneSubgraphCounts.get(auxGraphID);
							if (auxPrevValue != null){
								axiomNonOneSubgraphCounts.put(auxGraphID, auxPrevValue+1);
							}
							else {
								axiomNonOneSubgraphCounts.put(auxGraphID, new Long(1));
							}							
						}
					}
					break;
				case SimpleABoxGraph.SAME_INDIVIDUAL_STRING: 
					OWLSameIndividualAxiom sax = (OWLSameIndividualAxiom) ax;
					auxInd = sax.getIndividualsAsList().get(0); // they all belong to the same subgraph (or should :P)
					// they are in fact n-1 axioms
					axCount = sax.getIndividualsAsList().size() - 1;					
					auxNode = graph.getNodes().get(auxInd); 
					if (auxNode != null) {
						auxGraphID = auxNode.getNumSubgraph(); 
						auxPrevValue = axiomSubgraphCounts.get(auxGraphID);
						if (auxPrevValue != null){
							axiomSubgraphCounts.put(auxGraphID, auxPrevValue+axCount);
						}
						else {
							axiomSubgraphCounts.put(auxGraphID, new Long(axCount));
						}		
						// in fact, this should never happen ... 
						if (nodesPerSubgraph.get(auxGraphID) == 1) {							
							auxPrevValue = axiomNonOneSubgraphCounts.get(auxGraphID);
							if (auxPrevValue != null){
								axiomNonOneSubgraphCounts.put(auxGraphID, auxPrevValue+axCount);
							}
							else {
								axiomNonOneSubgraphCounts.put(auxGraphID, new Long(axCount));
							}							
						}
					}
					break;
				case SimpleABoxGraph.DIFFERENT_INDIVIDUALS_STRING: 
					OWLDifferentIndividualsAxiom dax = (OWLDifferentIndividualsAxiom) ax; 
					auxInd = dax.getIndividualsAsList().get(0); 
					// they are in fact (n*(n-1))/2 
					axCount = (dax.getIndividualsAsList().size() * (dax.getIndividualsAsList().size()-1)) / 2; 
					auxNode = graph.getNodes().get(auxInd); 
					if (auxNode != null) {
						auxGraphID = auxNode.getNumSubgraph(); 
						auxPrevValue = axiomSubgraphCounts.get(auxGraphID);
						if (auxPrevValue != null){
							axiomSubgraphCounts.put(auxGraphID, auxPrevValue+axCount);
						}
						else {
							axiomSubgraphCounts.put(auxGraphID, new Long(axCount));
						}
						// in fact, this should never happen ... 
						if (nodesPerSubgraph.get(auxGraphID) == 1) {							
							auxPrevValue = axiomNonOneSubgraphCounts.get(auxGraphID);
							if (auxPrevValue != null){
								axiomNonOneSubgraphCounts.put(auxGraphID, auxPrevValue+axCount);
							}
							else {
								axiomNonOneSubgraphCounts.put(auxGraphID, new Long(axCount));
							}							
						}						
					}
					break;
				case SimpleABoxGraph.OBJECT_PROPERTY_ASSERTION_STRING: 
					OWLObjectPropertyAssertionAxiom opax = (OWLObjectPropertyAssertionAxiom) ax; 
					auxInd = opax.getSubject(); // it should not matter which one 
					auxNode = graph.getNodes().get(auxInd); 
					if (auxNode != null) {
						auxGraphID = auxNode.getNumSubgraph(); 
						auxPrevValue = axiomSubgraphCounts.get(auxGraphID);
						if (auxPrevValue != null){
							axiomSubgraphCounts.put(auxGraphID, auxPrevValue+1);
						}
						else {
							axiomSubgraphCounts.put(auxGraphID, new Long(1));
						}
						// again, this should never happen
						if (nodesPerSubgraph.get(auxGraphID) == 1) {							
							auxPrevValue = axiomNonOneSubgraphCounts.get(auxGraphID);
							if (auxPrevValue != null){
								axiomNonOneSubgraphCounts.put(auxGraphID, auxPrevValue+1);
							}
							else {
								axiomNonOneSubgraphCounts.put(auxGraphID, new Long(1));
							}							
						}
					}
					break;
				case SimpleABoxGraph.NEGATIVE_OBJECT_PROPERTY_ASSERTION_STRING: 
					OWLNegativeObjectPropertyAssertionAxiom nopax = (OWLNegativeObjectPropertyAssertionAxiom) ax; 
					auxInd = nopax.getSubject(); // it should not matter which one 
					auxNode = graph.getNodes().get(auxInd); 
					if (auxNode != null) {
						auxGraphID = auxNode.getNumSubgraph(); 
						auxPrevValue = axiomSubgraphCounts.get(auxGraphID);
						if (auxPrevValue != null){
							axiomSubgraphCounts.put(auxGraphID, auxPrevValue+1);
						}
						else {
							axiomSubgraphCounts.put(auxGraphID, new Long(1));
						}
						if (nodesPerSubgraph.get(auxGraphID) == 1) {							
							auxPrevValue = axiomNonOneSubgraphCounts.get(auxGraphID);
							if (auxPrevValue != null){
								axiomNonOneSubgraphCounts.put(auxGraphID, auxPrevValue+1);
							}
							else {
								axiomNonOneSubgraphCounts.put(auxGraphID, new Long(1));
							}							
						}
					}
					
					break;
				case SimpleABoxGraph.DATA_PROPERTY_ASSERTION_STRING: 
					OWLDataPropertyAssertionAxiom dpax = (OWLDataPropertyAssertionAxiom) ax; 
					auxInd = dpax.getSubject();  
					auxNode = graph.getNodes().get(auxInd); 
					if (auxNode != null) {
						auxGraphID = auxNode.getNumSubgraph(); 
						auxPrevValue = axiomSubgraphCounts.get(auxGraphID);
						if (auxPrevValue != null){
							axiomSubgraphCounts.put(auxGraphID, auxPrevValue+1);
						}
						else {
							axiomSubgraphCounts.put(auxGraphID, new Long(1));
						}
						if (nodesPerSubgraph.get(auxGraphID) == 1) {							
							auxPrevValue = axiomNonOneSubgraphCounts.get(auxGraphID);
							if (auxPrevValue != null){
								axiomNonOneSubgraphCounts.put(auxGraphID, auxPrevValue+1);
							}
							else {
								axiomNonOneSubgraphCounts.put(auxGraphID, new Long(1));
							}							
						}
					}
					break;
				case SimpleABoxGraph.NEGATIVE_DATA_PROPERTY_ASSERTION_STRING:
					OWLNegativeDataPropertyAssertionAxiom ndpax = (OWLNegativeDataPropertyAssertionAxiom) ax; 
					auxInd = ndpax.getSubject();  
					auxNode = graph.getNodes().get(auxInd); 
					if (graph.getNodes().get( auxInd ) != null) {
						auxGraphID = auxNode.getNumSubgraph(); 
						auxPrevValue = axiomSubgraphCounts.get(auxGraphID);
						if (auxPrevValue != null){
							axiomSubgraphCounts.put(auxGraphID, auxPrevValue+1);
						}
						else {
							axiomSubgraphCounts.put(auxGraphID, new Long(1));
						}
						if (nodesPerSubgraph.get(auxGraphID) == 1) {							
							auxPrevValue = axiomNonOneSubgraphCounts.get(auxGraphID);
							if (auxPrevValue != null){
								axiomNonOneSubgraphCounts.put(auxGraphID, auxPrevValue+1);
							}
							else {
								axiomNonOneSubgraphCounts.put(auxGraphID, new Long(1));
							}							
						}						
					}
					break;
			}
		}		
		
		// we now calculate and set the values
		
		// long numberOfNonOne = 0;  
		// there are max values for NonOne as they will might be different in extreme cases (multiple class assertions)
		long currentMinAxiomsNonOne = Integer.MAX_VALUE; 
		long currentMaxAxiomsNonOne = Integer.MIN_VALUE; 
		double stdAccumulatorAxiomsNonOne = 0.0;
		
		long currentMinAxioms= Integer.MAX_VALUE; 
		long currentMaxAxioms= Integer.MIN_VALUE;
		double stdAccumulatorAxioms= 0.0;
		
		long totalAxioms= 0; 
		long totalAxiomsNonOne = 0; 
		
		// we get all the subgraphs
		assert axiomSubgraphCounts.size() >= axiomNonOneSubgraphCounts.size(); 
		for (Long subGraphId: axiomSubgraphCounts.keySet()){
			// first, the global values
			totalAxioms += axiomSubgraphCounts.get(subGraphId); 
			
			if (axiomSubgraphCounts.get(subGraphId) < currentMinAxioms) {
				currentMinAxioms = axiomSubgraphCounts.get(subGraphId); 
			}
			if (axiomSubgraphCounts.get(subGraphId) > currentMaxAxioms) {
				currentMaxAxioms = axiomSubgraphCounts.get(subGraphId); 
			}
			
			// then NonOne
			if (nodesPerSubgraph.get(subGraphId)>1) {
				// numberOfNonOne++; has already been calculated 
				totalAxiomsNonOne += axiomSubgraphCounts.get(subGraphId);
				// we update the min and max nodes for NonOne graphs
				if (axiomSubgraphCounts.get(subGraphId) < currentMinAxiomsNonOne ) {
					currentMinAxiomsNonOne = axiomSubgraphCounts.get(subGraphId); 
				}
				if (axiomSubgraphCounts.get(subGraphId) > currentMaxAxiomsNonOne ) {
					currentMaxAxiomsNonOne = axiomSubgraphCounts.get(subGraphId); 
				}								
			}
		}
		
		results.setTOT_AX(totalAxioms);
		results.setAVG_AX((results.getTOT_SG() != 0?((double)totalAxioms/results.getTOT_SG()):-1));
		results.setMAX_AX( (currentMaxAxioms != Integer.MIN_VALUE?currentMaxAxioms:-1));
		results.setMIN_AX( (currentMinAxioms != Integer.MAX_VALUE?currentMinAxioms:-1));
		
		results.setN_TOT_AX(totalAxiomsNonOne);
		results.setN_AVG_AX( (results.getN_TOT_SG() != 0?((double)totalAxiomsNonOne/results.getN_TOT_SG()):-1));
		results.setN_MAX_AX( (currentMaxAxioms != Integer.MIN_VALUE?currentMaxAxioms:-1)); 
		results.setN_MIN_AX( (currentMinAxioms != Integer.MAX_VALUE?currentMinAxioms:-1));
		
		// and now the std deviations
		for (Long subGraphId: axiomSubgraphCounts.keySet()) {			
			stdAccumulatorAxioms+= Math.pow( ( (double) axiomSubgraphCounts.get(subGraphId) - results.getAVG_AX()) , 2.0); 
			
			if (nodesPerSubgraph.get(subGraphId) > 1){
				stdAccumulatorAxiomsNonOne+= Math.pow( ( (double) axiomSubgraphCounts.get(subGraphId) - results.getN_AVG_AX()) , 2.0); 
								
			}
		}
		
		results.setSTD_AX((results.getTOT_SG() != 0? (Math.sqrt(stdAccumulatorAxioms / results.getTOT_SG()) ) :-1) );
		results.setN_STD_AX((results.getTOT_SG() != 0? (Math.sqrt(stdAccumulatorAxiomsNonOne / results.getTOT_SG())) :-1) );
		
				
		return results; 
	}
	
	public long traverseLabelling (SimpleABoxGraph gr, 
									OWLIndividual currentIndividual, 
									long currentId) {
		long result = 0;
		SimpleABoxGraphNode currentNode = null; 
		currentNode = gr.getNodes().get(currentIndividual); 
		HashSet<OWLIndividual> nextNodes = new HashSet<>(); 
		if (currentNode.getNumSubgraph() == -1) {
			currentNode.setNumSubgraph(currentId);
			// now we treat slightly different each of the outGoing edges 
			for (String key: currentNode.getOutLinks().keySet()) {
				switch (key) {
					case SimpleABoxGraph.CLASS_ASSERTION_STRING:						
						result+=currentNode.getOutLinks().get(key).size(); 
						break;						
					case SimpleABoxGraph.SAME_INDIVIDUAL_STRING: 
						for (OWLObject ind: currentNode.getOutLinks().get(key) ) {
							// we only count the edges that have not been 
							// traversed yet
							if (graph.getNodes().get((OWLIndividual)ind).getNumSubgraph() == -1) {
								result++; 
								// we include it to be traversed when we 
								// end processing this node
								nextNodes.add((OWLIndividual)ind);  
							}
						}
						break; 
					case SimpleABoxGraph.DIFFERENT_INDIVIDUALS_STRING:
						// we apply the same as strategy
						// - first calculate the edges that have not been processed yet
						// - store the nodes to be traversed
						for (OWLObject ind: currentNode.getOutLinks().get(key)) {
							if (graph.getNodes().get((OWLIndividual)ind).getNumSubgraph() == -1) {
								result++; 
								nextNodes.add((OWLIndividual)ind); 
							}
						}
						break; 
					default:
						// here we have 4 different edges, which can be bi-directional (-inverse)
						// we first check which kind of edge it is and the IRI
						// neg vs nonNeg axiom 
						// inv vs nonInv (this is introduced because we have 
						LabelProcessingResult label = processLabel(key); 
						IRI iri = IRI.create(label.getIRI()); 
						OWLEntity aux = (ont.getEntitiesInSignature(iri)).iterator().next();
						if (aux != null) {
							if (aux instanceof OWLObjectPropertyExpression) {
								// we have to check all the edges which belong to this property
								for (OWLObject ind: currentNode.getOutLinks().get(key)){
									if (graph.getNodes().get((OWLIndividual)ind).getNumSubgraph() == -1) {
										result++; 
										nextNodes.add((OWLIndividual) ind); 
										// as we introduce all the information (the inverse edges) in the 
										// graph, we can process everything locally from each node
									}
									else {
										// we have to check reflexive relationships
										// we have included two relationships, we skip the inverse
										if ( ((OWLIndividual)currentNode.getObject()).asOWLNamedIndividual().getIRI().equals( 
															((OWLIndividual)ind).asOWLNamedIndividual().getIRI()) && !label.isInverse() ) {
											result++; 
										}
										// else we don't do anything
									}
								}
							}
							else if (aux instanceof OWLDataPropertyExpression) {
								// the dataPropertyAxioms are treated as class 
								// assertions in the graph
								// we differentiate here in the code 
								// just in case we will need to differentiate the 
								// kind of edges afterwards								
								if (label.isNegative()) {
									result+= currentNode.getOutLinks().get(key).size(); 
								}
								else {
									result+= currentNode.getOutLinks().get(key).size(); 
								}
							}
						}
						break; 			
				}				
			}
			// before returning 
			// we traverse the rest of reachable nodes
			// accumulating the new edges traversed in each call
			for (OWLIndividual ind: nextNodes) {
				result+=traverseLabelling(gr, ind, currentId); 
			}
		}
		// else 
		// we have already visited such 
		return result;
	}
	
	public LabelProcessingResult processLabel (String label) {
		// here we have 4 different edges, which can be bi-directional (-inverse)
		String resultIRI = label;
		LabelProcessingResult result = new LabelProcessingResult(); 
		if (label.endsWith(SimpleABoxGraph.INVERSE_STRING)) {
			result.setInverse(true);
			resultIRI = label.substring(0, resultIRI.lastIndexOf(SimpleABoxGraph.INVERSE_STRING));
			if (resultIRI.endsWith(SimpleABoxGraph.NEGATIVE_STRING)) {
				result.setNegative(true); 
				resultIRI = resultIRI.substring(0,resultIRI.lastIndexOf(SimpleABoxGraph.NEGATIVE_STRING));
			}
// 			Not needed as it initializes to false
//			else{
//				result.setNegative(false); 
//			}
		}
		else {
			// inverse = false;
			// result.setInverse(false) not needed as it initializes
			if (resultIRI.endsWith(SimpleABoxGraph.NEGATIVE_STRING)) {
				result.setNegative(true); 
				resultIRI = resultIRI.substring(0,resultIRI.lastIndexOf(SimpleABoxGraph.NEGATIVE_STRING)); 								
			}
// 			Not needed as it initializes to false 						
//			else{
//				negative = false; 					
//			}
		}
		// I found some problems when the IRIs are complete (noNamespaces)
		if (resultIRI.startsWith("<") && resultIRI.endsWith(">")) 
			resultIRI = resultIRI.substring(1, resultIRI.length()-1);
		
		result.setIRI(resultIRI);
		return result; 
	}
	
}
