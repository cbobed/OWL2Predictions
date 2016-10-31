///////////////////////////////////////////////////////////////////////////////
// File: SubGraphMetrics.java 
// Author: Isa Guclu / Carlos Bobed
// Date: August 2016
// Version: 0.02
// Comments: Class which stores the values calculated for the SubGraph
// 	axioms metrics. Developed by CBL based on Isa's code.
// 	The metrics have been extended as the previous ones were quite confusing
// Modifications: 
// 		- Added the axiom-based metrics
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions;

public class SubGraphMetrics {
	long COA;	 	// Metric 00 - (COA) # of ALL ABoxes	
	long SOVA; 		// Metric 01 - (SOVA) # of Named Individuals
	long ROA; 		// Metric 02 - # of relationships in the ABox graph
	
	// for NonOne SubGraph Metrics
	long N_TOT_SG; 		// # of SubGraps that have more than 1 node (NonOne)
	double N_AVG_SG; 	// average size (nodes) of these SubGraphs
	double N_MIN_SG; 	// min size (nodes) of these subgraphs
	double N_MAX_SG; 
	double N_STD_SG; 	// STDDEV. of size (nodes) in these subgraphs
	double N_AVG_RPA; 	// AVG. number of edges/relationships in these graphs ObjectPropertyAssertion in NonOne SubGraphs
	long N_MIN_RPA; 	// MIN. number of edges/relationships in NonOne SubGraphs
	long N_MAX_RPA; 
	double N_STD_RPA; 	// STDDEv of edges/relationships in SubGraphs	
	
	// Global SubGraph Metrics
	// same meaning as the previous ones, but taking into account all the subgraphs
	long TOT_SG; 		
	double AVG_SG; 	
	double MIN_SG; 
	double MAX_SG;
	double STD_SG; 	
	double AVG_RPA; 
	long MAX_RPA; 
	long MIN_RPA; 	
	double STD_RPA; 	
	
	// Axiom-based NonOneSubGraphMetrics
	double N_TOT_AX; 
	double N_AVG_AX; 
	double N_MAX_AX; 
	double N_MIN_AX; 
	double N_STD_AX; 
	
	double TOT_AX; 
	double AVG_AX; 
	double MAX_AX; 
	double MIN_AX; 
	double STD_AX; 
	
	
	public SubGraphMetrics () {
		this.COA = 0;	
		this.SOVA = 0; 
		this.ROA =0; 
		
		// for NonOne SubGraph Metrics
		this.N_TOT_SG = 0; 		// # of SubGraps that have more than 1 node (NonOne)
		this.N_AVG_SG = 0; 	// average size (nodes) of these SubGraphs
		this.N_MIN_SG = 0; 	// min size (nodes) of these subgraphs
		this.N_MAX_SG = 0;  // max size (nodes) of these subgraphs
		this.N_STD_SG = 0; 	// STDDEV. of size (nodes) in these subgraphs
		this.N_AVG_RPA = 0; 	// AVG. number of edges/relationships in these graphs ObjectPropertyAssertion in NonOne SubGraphs
		this.N_MIN_RPA = 0; 	// MIN. number of edges/relationships in NonOne SubGraphs
								// There is no max, as the max is going to be the same as for Global metrics
		this.N_MAX_RPA = 0; 
		this.N_STD_RPA = 0; 	// STDDEv of edges/relationships in SubGraphs		
		
		// Global SubGraph Metrics
		this.TOT_SG = 0; 		
		this.AVG_SG = 0; 	
		this.MIN_SG = 0; 
		this.MAX_SG = 0;
		this.STD_SG = 0; 	
		this.AVG_RPA = 0; 
		this.MAX_RPA = 0; 
		this.MIN_RPA = 0; 	
		this.STD_RPA = 0; 	
		
		// Axiom metrics
		this.N_TOT_AX = 0; 
		this.N_AVG_AX = 0; 
		this.N_MAX_AX = 0; 
		this.N_MIN_AX = 0; 
		this.N_STD_AX = 0; 
		
		this.TOT_AX = 0; 
		this.AVG_AX = 0; 
		this.MAX_AX = 0; 
		this.MIN_AX = 0; 
		this.STD_AX = 0; 
	}

	
	public String toString(){
		String result = COA+"\t"; 
		result += SOVA+"\t";
		result += ROA+"\t"; 
		result +=N_TOT_SG+"\t"; 		// # of SubGraps that have more than 1 node (NonOne)
		result +=N_AVG_SG +"\t"; 	// average size (nodes) of these SubGraphs
		result +=N_MIN_SG +"\t"; 	// min size (nodes) of these subgraphs
		result +=N_MAX_SG +"\t"; 	// min size (nodes) of these subgraphs
		result +=N_STD_SG +"\t"; 	// STDDEV. of size (nodes) in these subgraphs
		result +=N_AVG_RPA +"\t"; 	// AVG. number of edges/relationships in these graphs ObjectPropertyAssertion in NonOne SubGraphs
		result +=N_MIN_RPA +"\t"; 	// MIN. number of edges/relationships in NonOne SubGraphs
		result +=N_MAX_RPA +"\t"; 	// MIN. number of edges/relationships in NonOne SubGraphs
		result +=N_STD_RPA +"\t"; 	// STDDEv of edges/relationships in SubGraphs		
		
		// Global SubGraph Metrics
		result +=TOT_SG +"\t"; 		
		result +=AVG_SG +"\t"; 	
		result +=MIN_SG +"\t"; 
		result +=MAX_SG +"\t";
		result +=STD_SG +"\t"; 	
		result +=AVG_RPA +"\t"; 
		result +=MAX_RPA +"\t"; 
		result +=MIN_RPA +"\t"; 	
		result +=STD_RPA +"\t"; 	
		
		// Axiom metrics
		result +=N_TOT_AX +"\t"; 
		result +=N_AVG_AX +"\t"; 
		result +=N_MAX_AX +"\t"; 
		result +=N_MIN_AX +"\t"; 
		result +=N_STD_AX +"\t"; 
		
		result +=TOT_AX +"\t"; 
		result +=AVG_AX +"\t"; 
		result +=MAX_AX +"\t"; 
		result +=MIN_AX +"\t"; 
		result +=STD_AX +"\t"; 
		
		return result; 
	}
	
	public String toStringEyeCandy(){
		String result = "COA: "+COA+"\n"; 
		result += "SOVA: "+SOVA+"\n";
		result += "ROA: "+ROA+"\n\n"; 
		result += "N_TOT_SG: "+N_TOT_SG+"\n"; 		// # of SubGraps that have more than 1 node (NonOne)
		result += "N_AVG_SG: "+N_AVG_SG +"\n"; 	// average size (nodes) of these SubGraphs
		result += "N_MIN_SG: "+N_MIN_SG +"\n"; 	// min size (nodes) of these subgraphs
		result += "N_MAX_SG: "+N_MAX_SG +"\n"; 	// min size (nodes) of these subgraphs
		result += "N_STD_SG: "+N_STD_SG +"\n"; 	// STDDEV. of size (nodes) in these subgraphs
		result += "N_AVG_RPA: "+N_AVG_RPA +"\n"; 	// AVG. number of edges/relationships in these graphs ObjectPropertyAssertion in NonOne SubGraphs
		result += "N_MIN_RPA: "+N_MIN_RPA+"\n"; 	// MIN. number of edges/relationships in NonOne SubGraphs
		result += "N_MAX_RPA: "+N_MAX_RPA+"\n"; 	// MIN. number of edges/relationships in NonOne SubGraphs
		result += "N_STD_RPA: "+N_STD_RPA +"\n\n"; 	// STDDEv of edges/relationships in SubGraphs		
		
		// Global SubGraph Metrics
		result +="TOT_SG: "+TOT_SG +"\n"; 		
		result +="AVG_SG: "+AVG_SG +"\n"; 	
		result +="MIN_SG: "+MIN_SG +"\n"; 
		result +="MAX_SG: "+MAX_SG +"\n";
		result +="STD_SG: "+STD_SG +"\n"; 	
		result +="AVG_RPA: "+AVG_RPA +"\n"; 
		result +="MIN_RPA: "+MIN_RPA +"\n"; 	
		result +="MAX_RPA: "+MAX_RPA +"\n"; 
		result +="STD_RPA: "+STD_RPA +"\n\n"; 	
		
		// Axiom Metrics
		
		result +="N_TOT_AX: "+ N_TOT_AX +"\n"; 
		result +="N_AVG_AX: "+N_AVG_AX +"\n"; 
		result +="N_MAX_AX: "+N_MAX_AX +"\n"; 
		result +="N_MIN_AX: "+N_MIN_AX +"\n"; 
		result +="N_STD_AX: "+N_STD_AX +"\n\n"; 
		
		result +="TOT_AX: "+TOT_AX +"\n"; 
		result +="AVG_AX: "+AVG_AX +"\n"; 
		result +="MAX_AX: "+MAX_AX +"\n"; 
		result +="MIN_AX: "+MIN_AX +"\n"; 
		result +="STD_AX: "+STD_AX +"\n"; 
		
		return result; 
	}	


	public long getCOA() {
		return COA;
	}


	public void setCOA(long cOA) {
		COA = cOA;
	}


	public long getSOVA() {
		return SOVA;
	}


	public void setSOVA(long sOVA) {
		SOVA = sOVA;
	}


	public long getN_TOT_SG() {
		return N_TOT_SG;
	}


	public void setN_TOT_SG(long n_TOT_SG) {
		N_TOT_SG = n_TOT_SG;
	}


	public double getN_AVG_SG() {
		return N_AVG_SG;
	}


	public void setN_AVG_SG(double n_AVG_SG) {
		N_AVG_SG = n_AVG_SG;
	}


	public double getN_MIN_SG() {
		return N_MIN_SG;
	}


	public void setN_MIN_SG(double n_MIN_SG) {
		N_MIN_SG = n_MIN_SG;
	}

	public double getN_STD_SG() {
		return N_STD_SG;
	}


	public void setN_STD_SG(double n_STD_SG) {
		N_STD_SG = n_STD_SG;
	}


	public double getN_AVG_RPA() {
		return N_AVG_RPA;
	}


	public void setN_AVG_RPA(double n_AVG_RPA) {
		N_AVG_RPA = n_AVG_RPA;
	}


	public long getN_MIN_RPA() {
		return N_MIN_RPA;
	}


	public void setN_MIN_RPA(long n_MIN_RPA) {
		N_MIN_RPA = n_MIN_RPA;
	}

	public long getTOT_SG() {
		return TOT_SG;
	}


	public void setTOT_SG(long tOT_SG) {
		TOT_SG = tOT_SG;
	}


	public double getAVG_SG() {
		return AVG_SG;
	}


	public void setAVG_SG(double aVG_SG) {
		AVG_SG = aVG_SG;
	}


	public double getMIN_SG() {
		return MIN_SG;
	}


	public void setMIN_SG(double mIN_SG) {
		MIN_SG = mIN_SG;
	}


	public double getMAX_SG() {
		return MAX_SG;
	}


	public void setMAX_SG(double mAX_SG) {
		MAX_SG = mAX_SG;
	}


	public double getSTD_SG() {
		return STD_SG;
	}


	public void setSTD_SG(double sTD_SG) {
		STD_SG = sTD_SG;
	}


	public double getN_STD_RPA() {
		return N_STD_RPA;
	}


	public void setN_STD_RPA(double n_STD_RPA) {
		N_STD_RPA = n_STD_RPA;
	}


	public double getAVG_RPA() {
		return AVG_RPA;
	}


	public void setAVG_RPA(double aVG_RPA) {
		AVG_RPA = aVG_RPA;
	}


	public long getMAX_RPA() {
		return MAX_RPA;
	}


	public void setMAX_RPA(long mAX_RPA) {
		MAX_RPA = mAX_RPA;
	}


	public long getMIN_RPA() {
		return MIN_RPA;
	}


	public void setMIN_RPA(long mIN_RPA) {
		MIN_RPA = mIN_RPA;
	}


	public double getSTD_RPA() {
		return STD_RPA;
	}


	public void setSTD_RPA(double sTD_RPA) {
		STD_RPA = sTD_RPA;
	}


	public long getROA() {
		return ROA;
	}


	public void setROA(long rOA) {
		ROA = rOA;
	}


	public double getN_MAX_SG() {
		return N_MAX_SG;
	}


	public void setN_MAX_SG(double n_MAX_SG) {
		N_MAX_SG = n_MAX_SG;
	}


	public long getN_MAX_RPA() {
		return N_MAX_RPA;
	}


	public void setN_MAX_RPA(long n_MAX_RPA) {
		N_MAX_RPA = n_MAX_RPA;
	}


	public double getN_TOT_AX() {
		return N_TOT_AX;
	}


	public void setN_TOT_AX(double n_TOT_AX) {
		N_TOT_AX = n_TOT_AX;
	}


	public double getN_AVG_AX() {
		return N_AVG_AX;
	}


	public void setN_AVG_AX(double n_AVG_AX) {
		N_AVG_AX = n_AVG_AX;
	}


	public double getN_MAX_AX() {
		return N_MAX_AX;
	}


	public void setN_MAX_AX(double n_MAX_AX) {
		N_MAX_AX = n_MAX_AX;
	}


	public double getN_MIN_AX() {
		return N_MIN_AX;
	}


	public void setN_MIN_AX(double n_MIN_AX) {
		N_MIN_AX = n_MIN_AX;
	}


	public double getN_STD_AX() {
		return N_STD_AX;
	}


	public void setN_STD_AX(double n_STD_AX) {
		N_STD_AX = n_STD_AX;
	}


	public double getTOT_AX() {
		return TOT_AX;
	}


	public void setTOT_AX(double tOT_AX) {
		TOT_AX = tOT_AX;
	}


	public double getAVG_AX() {
		return AVG_AX;
	}


	public void setAVG_AX(double aVG_AX) {
		AVG_AX = aVG_AX;
	}


	public double getMAX_AX() {
		return MAX_AX;
	}


	public void setMAX_AX(double mAX_AX) {
		MAX_AX = mAX_AX;
	}


	public double getMIN_AX() {
		return MIN_AX;
	}


	public void setMIN_AX(double mIN_AX) {
		MIN_AX = mIN_AX;
	}


	public double getSTD_AX() {
		return STD_AX;
	}


	public void setSTD_AX(double sTD_AX) {
		STD_AX = sTD_AX;
	}
	
}
