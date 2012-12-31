/*
  File: BayesDirichletEquivalentMetric.java

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
public class BayesDirichletEquivalentMetric extends AbstractCyniMetric {
	
	private static Map<String,Integer> mapStringValues;
	/**
	 * Creates a new  object.
	 */
	public BayesDirichletEquivalentMetric() {
		super("BDE.cyni","Bayesian Dirichlet Equivalent(BDe) Metric");
		addType(CyniMetricTypes.INPUT_STRINGS.toString());
		addType(CyniMetricTypes.LOCAL_METRIC_SCORE.toString());
		mapStringValues =  new HashMap<String,Integer>();
	}
	
	public void resetParameters()
	{
		if(!mapStringValues.isEmpty())
			mapStringValues.clear();
	}

	
	public Double getMetric(CyniTable table1, CyniTable table2, int indexBase, List<Integer> indexToCompare) { 
		double result = 0.0;
		int i = 0;
		int ncols,col;
		int count = 0;
		int numValues ;

		
		if(mapStringValues.size() != table1.getAttributeStringValues().size())
		{
			i=0;
			mapStringValues.clear();
			for(String name : table1.getAttributeStringValues())
			{
				mapStringValues.put(name, i);
				i++;
				System.out.println("attribute: " + name);
			}
		}
		
		if(indexToCompare.size() == 0)
			return result;
		
		numValues =  mapStringValues.size();
		
		int[] nCounts = new int[ (int)Math.pow((double)numValues, (double)(indexToCompare.size()+1))];
		int[] nodes ;
			
		
		ncols = table1.nColumns();
		if(indexToCompare.size() == 1)
		{
			if(indexToCompare.get(0) == indexBase)
				nodes = new int[indexToCompare.size()];
			else
				nodes = new int[indexToCompare.size()+1];
		}
		else
			nodes = new int[indexToCompare.size()+1];

		i=0;
		for(int ele : indexToCompare)
		{
			nodes[i] = ele;
			i++;
		}
		if(indexToCompare.size() < nodes.length)
			nodes[i] = indexBase;
		
		for(col = 0; col<ncols;col++ )
		{
			count = 0;
			for(i=0;i<nodes.length;i++)
			{
				if(table1.hasValue(nodes[i], col))
					count = numValues*count + mapStringValues.get(table1.stringValue(nodes[i], col));
			}
			nCounts[count]++;
		}
		
		result = getScoreWithCounts(nodes,nCounts );
		
		
		return  result;
	}
	
	private double getScoreWithCounts(int[] nodes,int[] nCounts )
	{
		double result = 1;
		int combinations;
		int i,j;
		int numValues =  mapStringValues.size();
		int numTimes;
		double temp1,temp2;
		
		combinations = (int)Math.pow((double)mapStringValues.size(),(double)(nodes.length-1));
		temp1 = gammaln((double)1.0/(combinations*numValues));
		temp2 = gammaln((double)1.0/combinations);
		for(i=0;i<combinations;i++)
		{
			numTimes = 0;
			for(j=0;j<numValues;j++)
			{
				result += gammaln(1.0/(combinations*numValues)+(double)nCounts[i*numValues+j]);
				result -=  temp1;
				numTimes += nCounts[i*numValues+j];
			}
			result += temp2;
			result -= gammaln(1.0/combinations+(double)numTimes);
		}
		
		return result;// Math.exp(result);
	}
	
	private double gammaln(double xx)
	{
		double x,y,tmp,ser;
		int j;
		final double[] cof = {57.1562356658629235,-59.5979603554754912,
				14.1360979747417471,-0.491913816097620199,.339946499848118887e-4,
				.465236289270485756e-4,-.983744753048795646e-4,.158088703224912494e-3,
				-.210264441724104883e-3,.217439618115212643e-3,-.164318106536763890e-3,
				.844182239838527433e-4,-.261908384015814087e-4,.368991826595316234e-5};
				
		y=x=xx;
		tmp = x+5.24218750000000000;
		tmp = (x+0.5)*Math.log(tmp)-tmp;
		ser = 0.999999999999997092;
		for (j=0;j<14;j++) ser += cof[j]/++y;
		return tmp+Math.log(2.5066282746310005*ser/x);

	}

	
}