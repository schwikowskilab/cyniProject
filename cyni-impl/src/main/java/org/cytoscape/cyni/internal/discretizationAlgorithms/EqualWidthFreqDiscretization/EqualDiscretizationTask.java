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
package org.cytoscape.cyni.internal.discretizationAlgorithms.EqualWidthFreqDiscretization;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.cyni.*;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyTable;




/**
 * The BasicInduction provides a very simple Induction, suitable as
 * the default Induction for Cytoscape data readers.
 */
public class EqualDiscretizationTask extends AbstractCyniTask {
	
	private  final int bins;
	private final CyTable mytable;
	private final List<String> attributeArray;
	private final Boolean freq;
	private final Boolean all;
	private List<String> columnsNames;
	
	
	

	/**
	 * Creates a new BasicInduction object.
	 */
	public EqualDiscretizationTask(final String name, final EqualDiscretizationContext context, CyTable selectedTable)
	{
		super(name, context,null,null,null, null,null,null,null);
		bins = context.bins;
		this.attributeArray = context.attributeList.getSelectedValues();
		
		this.mytable = selectedTable;
		this.freq = context.freq;
		this.all = context.all;
		
	}

	/**
	 *  Perform actual Discretization task.
	 *  This creates the default square Induction.
	 */
	@Override
	final protected void doCyniTask(final TaskMonitor taskMonitor) {
		
		Double progress = 0.0d;
		double  valDouble=0;
		Double step;
		String label;
		Object value;
		int pos = 0;
		CyColumn column;
		String newColName;
		List<Object> values;
		List<Double> thresholds = new ArrayList<Double>();
		columnsNames = new ArrayList<String>();
   
        step = 1.0 /  attributeArray.size();
        
        taskMonitor.setTitle("Cyni - Equal Width/Freq Discretization Algorithm ");
        taskMonitor.setStatusMessage("Discretizating data...");
		taskMonitor.setProgress(progress);
		if(all)
		{
			values = new ArrayList<Object>();
			for (final String  columnName : attributeArray)
			{
				column = mytable.getColumn(columnName);
				values.addAll(column.getValues(column.getType()));
			}
			if(freq)
			{
				getThresholdsFromFreq(thresholds, values);
			}
			else
			{
				getThresholdsFromWidth(thresholds, values);
			}
		}
		
	
		for (final String  columnName : attributeArray)
		 {
			
			column = mytable.getColumn(columnName);
			values =  column.getValues(column.getType());
			newColName = "nominal."+columnName;
			newColName = getUniqueColumnName(mytable,newColName);
			
			columnsNames.add(newColName);
			
			mytable.createColumn(newColName, String.class, false);
			
			if(!all)
			{
				thresholds.clear();
				if(freq)
				{
					getThresholdsFromFreq(thresholds, values);
				}
				else
				{
					getThresholdsFromWidth(thresholds, values);
				}
			}
			for ( CyRow row : mytable.getAllRows() ) 
			{
					 
				value = row.get(columnName, column.getType());
				
				if(value == null)
					continue;
				
				if(column.getType() == Integer.class)
					valDouble = ((Integer)value).doubleValue();
				else
					valDouble = (Double)value;
					
				for(int i = 0 ; i< (thresholds.size() -1) ; i++ )
				{
					//if((valDouble >= (min + width*i)) && (valDouble < (min + width*(i+1))))
					if((valDouble >= thresholds.get(i)) && (valDouble <= thresholds.get(i+1)))
					{
						pos = i;
						break;
					}
				}
				
				label = "(" + String.format("%.5g", thresholds.get(pos)) + "," + String.format("%.5g",thresholds.get(pos+1)) + ")";
				row.set(newColName, label);
			}
			 
			 progress = progress + step;
			 taskMonitor.setProgress(progress);
		 }
		
		if(!columnsNames.isEmpty())
		{
			taskMonitor.setStatusMessage("Discretization successful: " + columnsNames.size() + " new columns created with prefix nominal. in column name");
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, "Discretization successful: " + columnsNames.size() + " new columns created with prefix nominal. in column name", "Results", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}

		
		taskMonitor.setProgress(1.0d);
		
	}
	
	public void getThresholdsFromWidth(List<Double> thr, List<Object> values)
	{
		
		double  valDouble=0;
		Double  max,min, width;
		Boolean first = true;
		
		max = 0.0;
		min = 0.0;
		
		for (Object val : values)
		{
			if(val == null)
				continue;
			valDouble = ((Double)val).doubleValue();
			
			if(first)
			{
				max = valDouble;
				min = valDouble;
				first = false;
			}
			if(valDouble > max)
			{
				max = valDouble;
			}
			if(valDouble < min)
			{
				min = valDouble;
			}
			
		}
		
		width = (max - min) / (double)bins;
		thr.add(min);
		for(int i = 1 ; i< bins ; i++ )
		{
			
			thr.add(min +width*i);
		}
		thr.add(max);
		
	}
	
	public void getThresholdsFromFreq(List<Double> thr, List<Object> values)
	{
		int elemPerThr =0;
		int elemLeft = 0;
		int i,binElements,limit;
		double doubleValues[];
		
		doubleValues = new double[values.size()];
		elemPerThr = values.size()/bins;
		elemLeft = values.size()%bins;
		
		for (i= 0; i < values.size(); i++)
			doubleValues[i]= ((Double)values.get(i)).doubleValue();
		
		Arrays.sort(doubleValues);
		i=0;
		binElements = 1;
		thr.add(doubleValues[0]);
		for (i=0;i<values.size();i++)
		{
			if(elemLeft > 0)
				limit = elemPerThr + 1;
			else
				limit = elemPerThr;
			if(binElements == limit && (i+1) < doubleValues.length)
			{
				
				thr.add((doubleValues[i] + doubleValues[i+1] )/2.0);
				elemLeft--;
				binElements = 0;
			}
			binElements++;
			
		}
		thr.add(doubleValues[i-1]);
		
	}
	
	private String getUniqueColumnName(CyTable table, final String preferredName) {
		if (table.getColumn(preferredName) == null)
			return preferredName;

		String newUniqueName;
		int i = 0;
		do {
			++i;
			newUniqueName = preferredName + "-" + i;
		} while (table.getColumn(newUniqueName) != null);

		return newUniqueName;
	}
	
	@Override
	public Object getResults(Class requestedType) {
		if(columnsNames == null)
			return "Ok";
	
		if(requestedType.equals(List.class))
			return columnsNames;
		else if(requestedType.equals(String.class))
			return columnsNames.toString();
		
		
		return "Ok";
	}
	
}