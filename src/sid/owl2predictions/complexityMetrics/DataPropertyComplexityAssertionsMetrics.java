///////////////////////////////////////////////////////////////////////////////
// File: ObjectPropertyComplexityAssertionsMetrics.java 
// Author: Carlos Bobed
// Date: October 2016
// Version: 0.01
// Comments: Class which stores the values calculated for the ObjectProperty
// 	axioms metrics
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics;

public class DataPropertyComplexityAssertionsMetrics extends ComplexityAssertionsMetrics {
	
	public String headers(){
		String result = "TDPCA\tAVG_DPCA\tMAX_DPCA\tMIN_DPCA\tSTD_DPCA\tENT_DPCA\t";
		result += "TWDPCA\tAVG_WDPCA\tMAX_WDPCA\tMIN_WDPCA\tSTD_WDPCA\tENT_WDPCA"; 
		return result; 
	}

}
