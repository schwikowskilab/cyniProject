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
		if(rank1 == null)
			return 0.0;
		if(rank2 == null)
			return 0.0;
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
	
	public void setParameters(Map<String,Object> params){
		
	}
	
}