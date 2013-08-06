/*
 * #%L
 * Cyni Implementation (cyni-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.cytoscape.cyni.internal.metrics;


import java.util.*;

import org.cytoscape.cyni.*;



/**
 * The BasicInduction provides a very simple Induction, suitable as
 * the default Induction for Cytoscape data readers.
 */
public class KendallTauCorrelationMetric extends AbstractCyniMetric {
	/**
	 * Creates a new  object.
	 */
	public KendallTauCorrelationMetric() {
		super("Kendall.cyni","Kendall Tau Correlation");
		addTag(CyniMetricTags.INPUT_NUMBERS.toString());
		addTag(CyniMetricTags.CORRELATION_METRIC.toString());
	}

	
	public Double getMetric(CyniTable table1, CyniTable table2, int indexBase, List<Integer> indexToCompare) { 
		int j,k,n2=0,n1=0,n;
		double result = 0.0;
		double aa = 0.0;
		double a2 = 0.0;
		double a1 = 0.0;
		double is = 0.0;
		int index2 = indexToCompare.get(0);
		
		n = Math.min(table1.nColumns(), table2.nColumns());
		
		for (j = 0; j < (n-1); j++) {
			for (k = (j+1); k< n; k++) {
				a1 = 0.0;
				a2 = 0.0;
				if (table1.hasValue(indexBase, j) && table1.hasValue(indexBase, k) ) {
					a1 = table1.doubleValue(indexBase, j) - table1.doubleValue(indexBase, k);
					
				}
				if (table2.hasValue(index2, j) && table2.hasValue(index2, k) ) {
					a2 = table2.doubleValue(index2, j) - table2.doubleValue(index2, k);
					
				}
				
				aa = a1*a2;
				if(aa != 0.0)
				{
					++n1;
					++n2;
					if(aa > 0.0)
					{
						++is;
					}
					else
					{
						--is;
					}
				}
				else
				{
					if(a1 != 0.0) ++n1;
					if(a2 != 0.0) ++n2;
				}
			}
		}
		
		result = is/(Math.sqrt((double)n1)*Math.sqrt((double)n2));
		
		
		return  result;
	}
	
	public void setParameters(Map<String,Object> params){
		
	}
	
}