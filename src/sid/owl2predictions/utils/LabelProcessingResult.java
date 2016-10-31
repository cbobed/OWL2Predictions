///////////////////////////////////////////////////////////////////////////////
// File: LabelProcessingResult.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Auxiliar class which stores the values calculated when processing 
// 		a property label in the ABox graph
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.utils;

public class LabelProcessingResult {
	boolean inverse; 
	boolean negative; 
	String IRI; 
	
	public LabelProcessingResult(String IRI, boolean inverse, boolean negative) {
		this.IRI = IRI; 
		this.inverse = inverse; 
		this.negative = negative; 
	}
	
	public LabelProcessingResult () {
		this.IRI = ""; 
		this.inverse = false; 
		this.negative = false; 
	}

	public boolean isInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	public String getIRI() {
		return IRI;
	}

	public void setIRI(String iRI) {
		IRI = iRI;
	}


	
	
}