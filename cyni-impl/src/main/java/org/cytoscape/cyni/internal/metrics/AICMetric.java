/*
  File: AICMetric.java

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
public class AICMetric extends AbstractCyniMetric {
	
	private EntropyMetric entropy;
	
	/**
	 * Creates a new  object.
	 */
	public AICMetric() {
		super("AIC.cyni","AIC Metric");
		addType(CyniMetricTypes.INPUT_STRINGS.toString());
		addType(CyniMetricTypes.LOCAL_METRIC_SCORE.toString());
		entropy = new EntropyMetric();
		
	}
	
	public void resetParameters()
	{
		entropy.resetParameters();
	}

	
	public Double getMetric(CyniTable table1, CyniTable table2, int indexBase,List<Integer> indexToCompare) { 
		double result = 0.0;
		int nCounts;
		int numValues ;


		result = entropy.getMetric(table1, table2, indexBase, indexToCompare);
		numValues = table1.getAttributeStringValues().size();
		nCounts  =  (int)Math.pow((double)numValues, (double)indexToCompare.size());
		
		result -=  nCounts * (numValues - 1) ;
		
		
		return  result;
	}
	
	
	
}