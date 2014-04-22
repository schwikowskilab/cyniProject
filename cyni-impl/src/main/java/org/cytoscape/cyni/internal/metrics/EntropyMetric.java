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
public class EntropyMetric extends AbstractCyniMetric {
	
	private static Map<String,Integer> mapStringValues;
	private boolean conditional = false;
	private  GeneralLog log;
	private  GeneralLog log2;
	private  GeneralLog log10;
	private  GeneralLog loge;
	/**
	 * Creates a new  object.
	 */
	public EntropyMetric() {
		super("Entropy.cyni","Entropy Metric");
		addTag(CyniMetricTags.INPUT_STRINGS.toString());
		addTag(CyniMetricTags.LOCAL_METRIC_SCORE.toString());
		addTag(CyniMetricTags.DISCRETE_VALUES.toString());
		addTag(CyniMetricTags.INFORMATION_THEORY.toString());
		addTag(CyniMetricTags.K2_METRIC.toString());
		addTag(CyniMetricTags.HILL_CLIMBING_METRIC.toString());
		addTag(CyniMetricTags.LOW_METRIC.toString());
		mapStringValues =  new HashMap<String,Integer>();
		log2 = new LogBase2();
		log10 = new LogBase10();
		loge = new LogBaseE();
		log = log2;
		conditional = false;		
	}
	
	public void initMetric()
	{
		if(!mapStringValues.isEmpty())
			mapStringValues.clear();
		
	}

	
	public Double getMetric(CyniTable table1, CyniTable table2, int indexBase,List<Integer> indexToCompare) { 
		double result = 0.0;
		int i = 0;
		int ncols,col;
		int count = 0;
		int numValues ;
		boolean equalTables;

		equalTables = table1.equals(table2);
		
		if(mapStringValues.size() != table1.getAttributeStringValues().size())
		{
			i=0;
			mapStringValues.clear();
			for(String name : table1.getAttributeStringValues())
			{
				mapStringValues.put(name, i);
				i++;
			}
			if(!equalTables)
			{
				System.out.println("no equal tables");
				for(String name : table2.getAttributeStringValues())
				{
					if(!mapStringValues.containsKey(name))
					{
						mapStringValues.put(name, i);
						i++;
					}
				}
			}
		}
		
		if(indexToCompare.size() == 0)
			return result;
		
		numValues =  mapStringValues.size();
		
		int[] nCounts = new int[ (int)Math.pow((double)numValues, (double)(indexToCompare.size()+1))];
		int[] nodes ;
			
		
		ncols = Math.min(table1.nColumns(),table2.nColumns());
		if(equalTables)
		{
			if(indexToCompare.size() == 1)
			{
				if(indexToCompare.get(0) == indexBase)
					nodes = new int[indexToCompare.size()];
				else
					nodes = new int[indexToCompare.size()+1];
			}
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
		if(indexToCompare.size() < nodes.length || !equalTables)
			nodes[i] = indexBase;
		
		for(col = 0; col<ncols;col++ )
		{
			count = 0;
			for(i=0;i<(nodes.length-1);i++)
			{
				if(table2.hasValue(nodes[i], col))
					count = numValues*count + mapStringValues.get(table2.stringValue(nodes[i], col));
			}
			if(i<nodes.length)
			{
				if(table1.hasValue(nodes[i], col))
					count = numValues*count + mapStringValues.get(table1.stringValue(nodes[i], col));
			}
			nCounts[count]++;
		}
		
		result = getScoreWithCounts(nodes,nCounts ,ncols);
		
		
		return  result;
	}
	
	private double getScoreWithCounts(int[] nodes,int[] nCounts , int nData)
	{
		double result = 0.0;
		int combinations;
		int i,j;
		int numValues =  mapStringValues.size();
		int numTimes = 0;
		double proba,proba2;
		
		combinations = (int)Math.pow((double)mapStringValues.size(),(double)(nodes.length-1));
		for(i=0;i<combinations;i++)
		{
			if(conditional)
			{
				numTimes = 0;
				for(j=0;j<numValues;j++)
				{
					numTimes += nCounts[i*numValues+j];
				}
			}
			for(j=0;j<numValues;j++)
			{
				if(nCounts[i*numValues+j] > 0)
				{
					proba = (double)nCounts[i*numValues+j]/(double)nData;
					if(conditional)
						proba2 = (double)nCounts[i*numValues+j]/(double)numTimes;
					else
						proba2 = proba;
					result += (proba * log.getLog(proba2));
				}
			}
		}
		
		result = -1.0*result;
		
		return result;
	}
	
	
	public void setParameters(Map<String,Object> params){
		if(params.containsKey("Conditional"))
			conditional = (Boolean)params.get("Conditional");
		
		if(params.containsKey("LogBase"))
		{
			
			String temp = (String)params.get("LogBase");
			if(temp.matches("log2"))
			{
				log = log2;
			}
			if(temp.matches("log10"))
			{
				log = log10;
			}
			if(temp.matches("loge"))
			{
				log = loge;
			}
			
		}
		
	}
	
	private class GeneralLog {
		
		public double getLog(double x)
		{
			return 0.0;
		}
	}
	
	private class LogBaseE extends GeneralLog {
		
		@Override
		public double getLog(double x)
		{
			return Math.log(x);
		}
		
	}
	
	private class LogBase10 extends GeneralLog {
		
		@Override
		public double getLog(double x)
		{
			return Math.log10(x);
		}
		
	}

	private class LogBase2 extends GeneralLog {
		
		@Override
		public double getLog(double x)
		{
			return (double) (Math.log(x) / Math.log(2.0));
		}
		
	}

	
}