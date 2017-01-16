///////////////////////////////////////////////////////////////////////////////
// File: ComplexityAssertionsMetrics.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Class which stores the values calculated for the different entity
// 		assertions
// 	axioms metrics
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics;

public abstract class ComplexityAssertionsMetrics {

	// total complexity value
	double totalComplexityAssertions; 
	// mean complexity per entity in the ontology
	double meanComplexityAssertions; 
	// max complexity per entity in the ontology
	double maxComplexityAssertions;
	// min complexity per entity in the ontology
	double minComplexityAssertions;
	// standard deviation of the mean
	double stdComplexityAssertions;
	// entropy of the distribution of assertions
	double entropyComplexityAssertions;
	
	double totalWitnessedComplexity; 
	double meanWitnessedComplexity;
	double maxWitnessedComplexity; 
	double minWitnessedComplexity; 
	double stdWitnessedComplexity; 
	double entropyWitnessedComplexity; 
	
	
	public ComplexityAssertionsMetrics() {
		totalComplexityAssertions = 0; 
		meanComplexityAssertions = 0; 
		maxComplexityAssertions = 0; 
		minComplexityAssertions = 0; 
		stdComplexityAssertions = 0.0; 
		entropyComplexityAssertions = 0.0; 
		
		totalWitnessedComplexity = 0; 
		meanWitnessedComplexity = 0;
		maxWitnessedComplexity = 0; 
		minWitnessedComplexity = 0; 
		stdWitnessedComplexity = 0.0; 
		entropyWitnessedComplexity =0.0; 
	}
	
	// Getters && Setters 

	public double getTotalComplexityAssertions() {
		return totalComplexityAssertions;
	}

	public void setTotalComplexityAssertions(double totalComplexityAssertions) {
		this.totalComplexityAssertions = totalComplexityAssertions;
	}

	public double getMeanComplexityAssertions() {
		return meanComplexityAssertions;
	}

	public void setMeanComplexityAssertions(double meanComplexityAssertions) {
		this.meanComplexityAssertions = meanComplexityAssertions;
	}

	public double getMaxComplexityAssertions() {
		return maxComplexityAssertions;
	}

	public void setMaxComplexityAssertions(double maxComplexityAssertions) {
		this.maxComplexityAssertions = maxComplexityAssertions;
	}

	public double getMinComplexityAssertions() {
		return minComplexityAssertions;
	}

	public void setMinComplexityAssertions(double minComplexityAssertions) {
		this.minComplexityAssertions = minComplexityAssertions;
	}

	public double getStdComplexityAssertions() {
		return stdComplexityAssertions;
	}

	public void setStdComplexityAssertions(double stdComplexityAssertions) {
		this.stdComplexityAssertions = stdComplexityAssertions;
	}

	public double getEntropyComplexityAssertions() {
		return entropyComplexityAssertions;
	}

	public void setEntropyComplexityAssertions(double entropyComplexityAssertions) {
		this.entropyComplexityAssertions = entropyComplexityAssertions;
	}

	public double getTotalWitnessedComplexity() {
		return totalWitnessedComplexity;
	}

	public void setTotalWitnessedComplexity(double totalWitnessedComplexity) {
		this.totalWitnessedComplexity = totalWitnessedComplexity;
	}

	public double getMeanWitnessedComplexity() {
		return meanWitnessedComplexity;
	}

	public void setMeanWitnessedComplexity(double meanWitnessedComplexity) {
		this.meanWitnessedComplexity = meanWitnessedComplexity;
	}

	public double getMaxWitnessedComplexity() {
		return maxWitnessedComplexity;
	}

	public void setMaxWitnessedComplexity(double maxWitnessedComplexity) {
		this.maxWitnessedComplexity = maxWitnessedComplexity;
	}

	public double getMinWitnessedComplexity() {
		return minWitnessedComplexity;
	}

	public void setMinWitnessedComplexity(double minWitnessedComplexity) {
		this.minWitnessedComplexity = minWitnessedComplexity;
	}

	public double getStdWitnessedComplexity() {
		return stdWitnessedComplexity;
	}

	public void setStdWitnessedComplexity(double stdWitnessedComplexity) {
		this.stdWitnessedComplexity = stdWitnessedComplexity;
	}

	public double getEntropyWitnessedComplexity() {
		return entropyWitnessedComplexity;
	}

	public void setEntropyWitnessedComplexity(double entropyWitnessedComplexity) {
		this.entropyWitnessedComplexity = entropyWitnessedComplexity;
	}

	public abstract String headers(); 
	
	public String toString() {
		
		String result = totalComplexityAssertions+"\t"; 
		result += meanComplexityAssertions+"\t"; 
		result += maxComplexityAssertions+"\t"; 
		result += minComplexityAssertions+"\t"; 
		result += stdComplexityAssertions+"\t"; 
		result += entropyComplexityAssertions+"\t"; 
		
		result += totalWitnessedComplexity+"\t"; 
		result += meanWitnessedComplexity+"\t"; 
		result += maxWitnessedComplexity+"\t"; 
		result += minWitnessedComplexity+"\t"; 
		result += stdWitnessedComplexity+"\t"; 
		result += entropyWitnessedComplexity+"\t"; 
		return result;
	}

}
