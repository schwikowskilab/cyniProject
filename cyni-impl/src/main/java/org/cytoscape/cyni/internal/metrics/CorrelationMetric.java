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


import org.cytoscape.cyni.*;

import java.util.*;


/**
 * The BasicInduction provides a very simple Induction, suitable as
 * the default Induction for Cytoscape data readers.
 */
public class CorrelationMetric extends AbstractCyniMetric {
	/**
	 * Creates a new  object.
	 */
	public CorrelationMetric() {
		super("Correlation.cyni","Correlation");
		addType(CyniMetricTypes.INPUT_NUMBERS.toString());
		addType(CyniMetricTypes.CORRELATION_METRIC.toString());
		
	}

	
	public Double getMetric(CyniTable table1, CyniTable table2, int indexBase, List<Integer> indexToCompare) { 
		double result = 0.0;
		double mean1 = 0.0;
		double mean2 = 0.0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double elem1 = 0.0;
		double elem2 = 0.0;
		double elem3 = 0.0;
		int times = 0;
		int index2 = indexToCompare.get(0);
		
		for (int i = 0; i < table1.nColumns(); i++) {
			if (table1.hasValue(indexBase, i)) {
				sum1 = sum1 + table1.doubleValue(indexBase, i);
				times++;
			}
		}
		
		mean1 = sum1 / times;
		
		times = 0;
		for (int i = 0; i < table2.nColumns(); i++) {
			if (table2.hasValue(index2, i) ) {
				sum2 = sum2 + table1.doubleValue(index2, i);
				times++;
			}
		}
		mean2 = sum2 / times;
		sum1 = 0.0;
		sum2 = 0.0;
		
		for (int i = 0; i < table1.nColumns(); i++) {
			if (table1.hasValue(indexBase, i) && table2.hasValue(index2, i)) {
				double term1 = table1.doubleValue(indexBase, i);
				double term2 = table2.doubleValue(index2, i);
				
				elem1 += (term1 - mean1)*(term2 - mean2);
				elem2 += (term1 - mean1)*(term1 - mean1);
				elem3 += (term2 - mean2)*(term2 - mean2);
				
			}
		}
		
		result = elem1 / Math.sqrt(elem2*elem3);
		
		return  result;
	}
	
}