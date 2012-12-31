/*
  File: KendallTauCorrelationMetric.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
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
		addType(CyniMetricTypes.INPUT_NUMBERS.toString());
		addType(CyniMetricTypes.CORRELATION_METRIC.toString());
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
	
}