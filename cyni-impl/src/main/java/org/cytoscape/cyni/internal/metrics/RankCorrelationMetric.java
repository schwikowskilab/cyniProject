/*
  File: RankCorrelationMetric.java

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
public class RankCorrelationMetric extends AbstractCyniMetric {
	/**
	 * Creates a new  object.
	 */
	public RankCorrelationMetric() {
		super("RankCorrelation.cyni","Spearman Rank Correlation");
		addType(CyniMetricTypes.INPUT_NUMBERS.toString());
		addType(CyniMetricTypes.CORRELATION_METRIC.toString());
	}

	
	public Double getMetric(CyniTable table1, CyniTable table2, int indexBase, List<Integer> indexToCompare) { 
		double result = 0.0;
		double mean1 = 0.0;
		double mean2 = 0.0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		double rank1[], rank2[];
		int size = 0;
		int index2 = indexToCompare.get(0);
		
		
		rank1 = table1.getRank(indexBase);
		rank2 = table2.getRank(index2);
		
		mean1 = getMean(rank1);
		mean2 = getMean(rank2);
		
		size = Math.min(rank1.length, rank2.length);
		for (int i = 0; i < size; i++) {
			sum1 = sum1 + ((rank1[i] - mean1)*(rank2[i] - mean2));
			sum2 = sum2 + ((rank1[i] - mean1)*(rank1[i] - mean1));
			sum3 = sum3 + ((rank2[i] - mean2)*(rank2[i] - mean2));
			
		}
		
		result = sum1/(Math.sqrt(sum2)*Math.sqrt(sum3));
		
		
		return  result;
	}
	
	public double getMean(double data[])
	{
		int i, size;
		double mean = 0.0;
		
		size = data.length;
		
		for(i=0;i<size;i++)
		{
			mean = mean + data[i];
		}
		
		mean = mean / (double) size;
		
		return mean;
	}
	
}