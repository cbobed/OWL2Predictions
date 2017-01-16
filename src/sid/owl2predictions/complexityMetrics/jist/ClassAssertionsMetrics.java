///////////////////////////////////////////////////////////////////////////////
// File: ClassAssertionsMetrics.java 
// Author: Carlos Bobed
// Date: August 2016
// Version: 0.01
// Comments: Class which stores the values calculated for the ClassAssertions
// 	axioms metrics
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics.jist;

public class ClassAssertionsMetrics {

	// total number of class assertions in the ontology
	int totalNumClassAssertions; 
	// mean number of class assertions per concept expression in the ontology
	double meanNumClassAssertions; 
	// max number of class assertions per concept expression in the ontology
	int maxNumClassAssertions;
	// min number of class assertions per concept expression in the ontology
	int minNumClassAssertions;
	// standard deviation of the mean
	double stdNumClassAssertions;
	// entropy of the distribution of class assertions
	double entropyClassAssertions;
	
	public ClassAssertionsMetrics() {
		totalNumClassAssertions = 0; 
		meanNumClassAssertions = 0; 
		maxNumClassAssertions = 0; 
		minNumClassAssertions = 0; 
		stdNumClassAssertions = 0.0; 
		entropyClassAssertions = 0.0; 
	}
	
	// Getters && Setters 
	public int getTotalNumClassAssertions() {
		return totalNumClassAssertions;
	}
	public void setTotalNumClassAssertions(int totalNumClassAssertions) {
		this.totalNumClassAssertions = totalNumClassAssertions;
	}
	public double getMeanNumClassAssertions() {
		return meanNumClassAssertions;
	}
	public void setMeanNumClassAssertions(double meanNumClassAssertions) {
		this.meanNumClassAssertions = meanNumClassAssertions;
	}
	public int getMaxNumClassAssertions() {
		return maxNumClassAssertions;
	}
	public void setMaxNumClassAssertions(int maxNumClassAssertions) {
		this.maxNumClassAssertions = maxNumClassAssertions;
	}
	public int getMinNumClassAssertions() {
		return minNumClassAssertions;
	}
	public void setMinNumClassAssertions(int minNumClassAssertions) {
		this.minNumClassAssertions = minNumClassAssertions;
	}
	public double getStdNumClassAssertions() {
		return stdNumClassAssertions;
	}
	public void setStdNumClassAssertions(double stdNumClassAssertions) {
		this.stdNumClassAssertions = stdNumClassAssertions;
	}
	public double getEntropyClassAssertions() {
		return entropyClassAssertions;
	}
	public void setEntropyClassAssertions(double entropyClassAssertions) {
		this.entropyClassAssertions = entropyClassAssertions;
	} 
	
	@Override
	public String toString() {
		
//		String result = "NCA: "+totalNumClassAssertions+"\n"; 
//		result += "MCA: "+meanNumClassAssertions+"\n"; 
//		result += "MaxCA: "+maxNumClassAssertions+"\n"; 
//		result += "MinCA: "+minNumClassAssertions+"\n"; 
//		result += "STD: "+stdNumClassAssertions+"\n"; 
//		result += "ENT: "+entropyClassAssertions+"\n"; 
		
		String result = ""+totalNumClassAssertions+"\t"; 
		result += ""+meanNumClassAssertions+"\t"; 
		result += ""+maxNumClassAssertions+"\t"; 
		result += ""+minNumClassAssertions+"\t"; 
		result += ""+stdNumClassAssertions+"\t"; 
		result += ""+entropyClassAssertions+"\t"; 
		
		return result;
	}
}
