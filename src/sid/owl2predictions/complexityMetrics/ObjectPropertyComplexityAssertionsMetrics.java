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

public class ObjectPropertyComplexityAssertionsMetrics extends ClassComplexityAssertionsMetrics{

	public String headers(){
		String result = "TOPCA\tAVG_OPCA\tMAX_OPCA\tMIN_OPCA\tSTD_OPCA\tENT_OPCA\t";
		result += "TWOPCA\tAVG_WOPCA\tMAX_WOPCA\tMIN_WOPCA\tSTD_WOPCA\tENT_WOPCA"; 
		return result; 
	}

}
