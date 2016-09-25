///////////////////////////////////////////////////////////////////////////////
// File: DataPropertyAssertionMetrics.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Class which stores the values calculated for the DataProperty
// 	axioms metrics
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions;

public class DataPropertyAssertionsMetrics {

	// total number of dataProperty assertions in the ontology
	int totalNumDataPropertyAssertions; 
	// mean number of dataProperty per dataProperty expression in the ontology
	double meanNumDataPropertyAssertions; 
	// max number of dataProperty assertions per dataProperty expression in the ontology
	int maxNumDataPropertyAssertions;
	// min number of dataProperty assertions per dataProperty expression in the ontology
	int minNumDataPropertyAssertions;
	// standard deviation of the mean
	double stdNumDataPropertyAssertions;
	// entropy of the distribution of class assertions
	double entropyDataPropertyAssertions;
	
	public DataPropertyAssertionsMetrics() {
		totalNumDataPropertyAssertions = 0; 
		meanNumDataPropertyAssertions = 0; 
		maxNumDataPropertyAssertions = 0; 
		minNumDataPropertyAssertions = 0; 
		stdNumDataPropertyAssertions = 0.0; 
		entropyDataPropertyAssertions = 0.0; 
	}
	
	// Getters && Setters 
	
	public int getTotalNumDataPropertyAssertions() {
		return totalNumDataPropertyAssertions;
	}

	public void setTotalNumDataPropertyAssertions(int totalNumDataPropertyAssertions) {
		this.totalNumDataPropertyAssertions = totalNumDataPropertyAssertions;
	}

	public double getMeanNumDataPropertyAssertions() {
		return meanNumDataPropertyAssertions;
	}

	public void setMeanNumDataPropertyAssertions(
			double meanNumDataPropertyAssertions) {
		this.meanNumDataPropertyAssertions = meanNumDataPropertyAssertions;
	}

	public int getMaxNumDataPropertyAssertions() {
		return maxNumDataPropertyAssertions;
	}

	public void setMaxNumDataPropertyAssertions(int maxNumDataPropertyAssertions) {
		this.maxNumDataPropertyAssertions = maxNumDataPropertyAssertions;
	}

	public int getMinNumDataPropertyAssertions() {
		return minNumDataPropertyAssertions;
	}

	public void setMinNumDataPropertyAssertions(int minNumDataPropertyAssertions) {
		this.minNumDataPropertyAssertions = minNumDataPropertyAssertions;
	}

	public double getStdNumDataPropertyAssertions() {
		return stdNumDataPropertyAssertions;
	}

	public void setStdNumDataPropertyAssertions(double stdNumDataPropertyAssertions) {
		this.stdNumDataPropertyAssertions = stdNumDataPropertyAssertions;
	}

	public double getEntropyDataPropertyAssertions() {
		return entropyDataPropertyAssertions;
	}

	public void setEntropyDataPropertyAssertions(
			double entropyDataPropertyAssertions) {
		this.entropyDataPropertyAssertions = entropyDataPropertyAssertions;
	}
	
	@Override
	public String toString() {
		
		String result = ""+totalNumDataPropertyAssertions+"\t"; 
		result += ""+meanNumDataPropertyAssertions+"\t"; 
		result += ""+maxNumDataPropertyAssertions+"\t"; 
		result += ""+minNumDataPropertyAssertions+"\t"; 
		result += ""+stdNumDataPropertyAssertions+"\t"; 
		result += ""+entropyDataPropertyAssertions+"\t"; 
		
		return result;
	}

	
}
