///////////////////////////////////////////////////////////////////////////////
// File: ObjectPropertyAssertionsMetrics.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Class which stores the values calculated for the ObjectPropertyAssertions
// 	axioms metrics
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions;

public class ObjectPropertyAssertionsMetrics {

	// total number of ObjectProperty assertions in the ontology
	int totalNumObjectPropertyAssertions; 
	// mean number of ObjectProperty assertions per concept expression in the ontology
	double meanNumObjectPropertyAssertions; 
	// max number of ObjectProperty assertions per concept expression in the ontology
	int maxNumObjectPropertyAssertions;
	// min number of ObjectProperty assertions per concept expression in the ontology
	int minNumObjectPropertyAssertions;
	// standard deviation of the mean
	double stdNumObjectPropertyAssertions;
	// entropy of the distribution of ObjectProperty assertions
	double entropyObjectPropertyAssertions;
	
	public ObjectPropertyAssertionsMetrics() {
		totalNumObjectPropertyAssertions = 0; 
		meanNumObjectPropertyAssertions = 0; 
		maxNumObjectPropertyAssertions = 0; 
		minNumObjectPropertyAssertions = 0; 
		stdNumObjectPropertyAssertions = 0.0; 
		entropyObjectPropertyAssertions = 0.0; 
	}
	
	// Getters && Setters 
	public int getTotalNumObjectPropertyAssertions() {
		return totalNumObjectPropertyAssertions;
	}

	public void setTotalNumObjectPropertyAssertions(
			int totalNumObjectPropertyAssertions) {
		this.totalNumObjectPropertyAssertions = totalNumObjectPropertyAssertions;
	}

	public double getMeanNumObjectPropertyAssertions() {
		return meanNumObjectPropertyAssertions;
	}

	public void setMeanNumObjectPropertyAssertions(
			double meanNumObjectPropertyAssertions) {
		this.meanNumObjectPropertyAssertions = meanNumObjectPropertyAssertions;
	}

	public int getMaxNumObjectPropertyAssertions() {
		return maxNumObjectPropertyAssertions;
	}

	public void setMaxNumObjectPropertyAssertions(int maxNumObjectPropertyAssertions) {
		this.maxNumObjectPropertyAssertions = maxNumObjectPropertyAssertions;
	}

	public int getMinNumObjectPropertyAssertions() {
		return minNumObjectPropertyAssertions;
	}

	public void setMinNumObjectPropertyAssertions(int minNumObjectPropertyAssertions) {
		this.minNumObjectPropertyAssertions = minNumObjectPropertyAssertions;
	}

	public double getStdNumObjectPropertyAssertions() {
		return stdNumObjectPropertyAssertions;
	}

	public void setStdNumObjectPropertyAssertions(
			double stdNumObjectPropertyAssertions) {
		this.stdNumObjectPropertyAssertions = stdNumObjectPropertyAssertions;
	}

	public double getEntropyObjectPropertyAssertions() {
		return entropyObjectPropertyAssertions;
	}

	public void setEntropyObjectPropertyAssertions(
			double entropyObjectPropertyAssertions) {
		this.entropyObjectPropertyAssertions = entropyObjectPropertyAssertions;
	}
	
	@Override
	public String toString() {
		
//		String result = "NCA: "+totalNumClassAssertions+"\n"; 
//		result += "MCA: "+meanNumClassAssertions+"\n"; 
//		result += "MaxCA: "+maxNumClassAssertions+"\n"; 
//		result += "MinCA: "+minNumClassAssertions+"\n"; 
//		result += "STD: "+stdNumClassAssertions+"\n"; 
//		result += "ENT: "+entropyClassAssertions+"\n"; 
		
		String result = ""+totalNumObjectPropertyAssertions+"\t"; 
		result += ""+meanNumObjectPropertyAssertions+"\t"; 
		result += ""+maxNumObjectPropertyAssertions+"\t"; 
		result += ""+minNumObjectPropertyAssertions+"\t"; 
		result += ""+stdNumObjectPropertyAssertions+"\t"; 
		result += ""+entropyObjectPropertyAssertions+"\t"; 
		
		return result;
	}

	
}
