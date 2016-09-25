///////////////////////////////////////////////////////////////////////////////
// File: ClassComplexityAssertionsMetrics.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Class which stores the values calculated for the ClassComplexityAssertions
// 	axioms metrics
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions;

public class ClassComplexityAssertionsMetrics {

	// total class complexity value
	double totalClassComplexityAssertions; 
	// mean class complexity per concept expression in the ontology
	double meanClassComplexityAssertions; 
	// max class complexity per concept expression in the ontology
	double maxClassComplexityAssertions;
	// min class complexity per concept expression in the ontology
	double minClassComplexityAssertions;
	// standard deviation of the mean
	double stdClassComplexityAssertions;
	// entropy of the distribution of class assertions
	double entropyClassComplexityAssertions;
	
	double totalWitnessedClassComplexity; 
	double meanWitnessedClassComplexity;
	double maxWitnessedClassComplexity; 
	double minWitnessedClassComplexity; 
	double stdWitnessedClassComplexity; 
	double entropyWitnessedClassComplexity; 
	
	
	public ClassComplexityAssertionsMetrics() {
		totalClassComplexityAssertions = 0; 
		meanClassComplexityAssertions = 0; 
		maxClassComplexityAssertions = 0; 
		minClassComplexityAssertions = 0; 
		stdClassComplexityAssertions = 0.0; 
		entropyClassComplexityAssertions = 0.0; 
		
		totalWitnessedClassComplexity = 0; 
		meanWitnessedClassComplexity = 0;
		maxWitnessedClassComplexity = 0; 
		minWitnessedClassComplexity = 0; 
		stdWitnessedClassComplexity = 0.0; 
		entropyWitnessedClassComplexity =0.0; 
	}
	
	// Getters && Setters 
	public double getTotalClassComplexityAssertions() {
		return totalClassComplexityAssertions;
	}

	public void setTotalClassComplexityAssertions(double totalClassComplexityAssertions) {
		this.totalClassComplexityAssertions = totalClassComplexityAssertions;
	}

	public double getMeanClassComplexityAssertions() {
		return meanClassComplexityAssertions;
	}

	public void setMeanClassComplexityAssertions(
			double meanClassComplexityAssertions) {
		this.meanClassComplexityAssertions = meanClassComplexityAssertions;
	}

	public double getMaxClassComplexityAssertions() {
		return maxClassComplexityAssertions;
	}

	public void setMaxClassComplexityAssertions(double maxClassComplexityAssertions) {
		this.maxClassComplexityAssertions = maxClassComplexityAssertions;
	}

	public double getMinClassComplexityAssertions() {
		return minClassComplexityAssertions;
	}

	public void setMinClassComplexityAssertions(double minClassComplexityAssertions) {
		this.minClassComplexityAssertions = minClassComplexityAssertions;
	}

	public double getStdClassComplexityAssertions() {
		return stdClassComplexityAssertions;
	}

	public void setStdClassComplexityAssertions(double stdClassComplexityAssertions) {
		this.stdClassComplexityAssertions = stdClassComplexityAssertions;
	}

	public double getEntropyClassComplexityAssertions() {
		return entropyClassComplexityAssertions;
	}

	public void setEntropyClassComplexityAssertions(
			double entropyClassComplexityAssertions) {
		this.entropyClassComplexityAssertions = entropyClassComplexityAssertions;
	}
	
	public double getTotalWitnessedClassComplexity() {
		return totalWitnessedClassComplexity;
	}

	public void setTotalWitnessedClassComplexity(double totalWitnessedClassComplexity) {
		this.totalWitnessedClassComplexity = totalWitnessedClassComplexity;
	}

	public double getMeanWitnessedClassComplexity() {
		return meanWitnessedClassComplexity;
	}

	public void setMeanWitnessedClassComplexity(double meanWitnessedClassComplexity) {
		this.meanWitnessedClassComplexity = meanWitnessedClassComplexity;
	}

	public double getMaxWitnessedClassComplexity() {
		return maxWitnessedClassComplexity;
	}

	public void setMaxWitnessedClassComplexity(double maxWitnessedClassComplexity) {
		this.maxWitnessedClassComplexity = maxWitnessedClassComplexity;
	}

	public double getMinWitnessedClassComplexity() {
		return minWitnessedClassComplexity;
	}

	public void setMinWitnessedClassComplexity(double minWitnessedClassComplexity) {
		this.minWitnessedClassComplexity = minWitnessedClassComplexity;
	}


	public double getEntropyWitnessedClassComplexity() {
		return entropyWitnessedClassComplexity;
	}

	public void setEntropyWitnessedClassComplexity(
			double entropyWitnessedClassComplexity) {
		this.entropyWitnessedClassComplexity = entropyWitnessedClassComplexity;
	}
	
	
	public double getStdWitnessedClassComplexity() {
		return stdWitnessedClassComplexity;
	}

	public void setStdWitnessedClassComplexity(double stdWitnessedClassComplexity) {
		this.stdWitnessedClassComplexity = stdWitnessedClassComplexity;
	}

	
	public static String headers(){
		String result = "TCCA\tAVG_CCA\tMAX_CCA\tMIN_CCA\tSTD_CCA\tENT_CCA\t";
		result += "TWCCA\tAVG_WCCA\tMAX_WCCA\tMIN_WCCA\tSTD_WCCA\tENT_WCCA"; 
		return result; 
	}
	
	public String toString() {
		
		String result = totalClassComplexityAssertions+"\t"; 
		result += meanClassComplexityAssertions+"\t"; 
		result += maxClassComplexityAssertions+"\t"; 
		result += minClassComplexityAssertions+"\t"; 
		result += stdClassComplexityAssertions+"\t"; 
		result += entropyClassComplexityAssertions+"\t"; 
		
		result += totalWitnessedClassComplexity+"\t"; 
		result += meanWitnessedClassComplexity+"\t"; 
		result += maxWitnessedClassComplexity+"\t"; 
		result += minWitnessedClassComplexity+"\t"; 
		result += stdWitnessedClassComplexity+"\t"; 
		result += entropyWitnessedClassComplexity+"\t"; 
		return result;
	}


	


}
