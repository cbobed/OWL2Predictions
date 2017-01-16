///////////////////////////////////////////////////////////////////////////////
// File: ClassComplexityAssertionsMetrics.java 
// Author: Carlos Bobed
// Date: September 2016
// Version: 0.01
// Comments: Class which stores the values calculated for the ClassComplexityAssertions
// 	axioms metrics
// Modifications: 
///////////////////////////////////////////////////////////////////////////////

package sid.owl2predictions.complexityMetrics;

public class ClassComplexityAssertionsMetrics extends ComplexityAssertionsMetrics{
	
	public String headers(){
		String result = "TCCA\tAVG_CCA\tMAX_CCA\tMIN_CCA\tSTD_CCA\tENT_CCA\t";
		result += "TWCCA\tAVG_WCCA\tMAX_WCCA\tMIN_WCCA\tSTD_WCCA\tENT_WCCA"; 
		return result; 
	}
	
	public String headersWO(){
		String result = "TCCA_WO\tAVG_CCA_WO\tMAX_CCA_WO\tMIN_CCA_WO\tSTD_CCA_WO\tENT_CCA_WO\t";
		result += "TWCCA_WO\tAVG_WCCA_WO\tMAX_WCCA_WO\tMIN_WCCA_WO\tSTD_WCCA_WO\tENT_WCCA_WO"; 
		return result; 
	}
	
}
