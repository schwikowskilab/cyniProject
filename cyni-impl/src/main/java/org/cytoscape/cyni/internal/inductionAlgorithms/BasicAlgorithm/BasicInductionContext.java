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
package org.cytoscape.cyni.internal.inductionAlgorithms.BasicAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cytoscape.work.util.*;
import org.cytoscape.model.CyTable;

import org.cytoscape.cyni.*;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class BasicInductionContext extends CyniAlgorithmContext implements TunableValidator {
	@Tunable(description="Threshold to add new edge", gravity=1.0)
	public double thresholdAddEdge = 0.5;
	
	@Tunable(description="Type of correlation", gravity=2.0)
	public ListSingleSelection<String> type = new ListSingleSelection<String>(POSITIVE,NEGATIVE,NEGATIVE_AND_POSITIVE);
	
	@Tunable(description="Use selected nodes only", groups="Parameters if a network associated to table data", gravity=3.0)
	public boolean selectedOnly = false;
	
	public ListSingleSelection<CyCyniMetric> measures;
	@Tunable(description="Metric", gravity=4.0)
	public ListSingleSelection<CyCyniMetric> getMeasures()
	{
		return measures;
	}
	public void setMeasures(ListSingleSelection<CyCyniMetric> mes)
	{
		measures = mes;
	}
	
	public ListMultipleSelection<String> attributeList;
	@Tunable(description="Data Attributes", groups="Sources for Network Inference",listenForChange={"Measures"}, gravity=5.0)
	public ListMultipleSelection<String> getAttributeList()
	{
		List<String>  tagList ;
		if(measures.getPossibleValues().size()==0)
		{
			attributeList = new  ListMultipleSelection<String>("No sources available");
		}
		else
		{
			tagList = measures.getSelectedValue().getTagsList();
			if(tagList.contains(CyniMetricTags.INPUT_NUMBERS.toString())  &&  !currentType.matches(CyniMetricTags.INPUT_NUMBERS.toString()))
			{
				attributes = getAllAttributesNumbers(selectedTable);
				if(!Arrays.equals(attributes.toArray(),attributeList.getPossibleValues().toArray()))
				{
					if(attributes.size() > 0)
					{
						attributeList = new  ListMultipleSelection<String>(attributes);
						attributeList.setSelectedValues(attributeList.getPossibleValues());
					}
					else
					{
						attributeList = new  ListMultipleSelection<String>("No sources available");
					}
				}
				currentType = CyniMetricTags.INPUT_NUMBERS.toString();
			}
			else
			{
				if(tagList.contains(CyniMetricTags.INPUT_STRINGS.toString())  &&  !currentType.matches(CyniMetricTags.INPUT_STRINGS.toString()))
				{
					attributes = getAllAttributesStrings(selectedTable);
					if(!Arrays.equals(attributes.toArray(),attributeList.getPossibleValues().toArray()))
					{
						attributeList = new  ListMultipleSelection<String>(attributes);
						List<String> temp = new ArrayList<String>( attributes);
						temp.remove(selectedTable.getPrimaryKey().getName());
						if(!temp.isEmpty())
							attributeList.setSelectedValues(temp);
						currentType =  CyniMetricTags.INPUT_STRINGS.toString();
					}
				}
				else
				{
					if(currentType.isEmpty())
						attributeList = new  ListMultipleSelection<String>("No sources available");
				}
			}
		}
		
		return attributeList;
	}
	public void setAttributeList(ListMultipleSelection<String> input)
	{
		attributeList = input;
	}

	
	private List<String> attributes;
	private String currentType ;
	private CyTable selectedTable;
	public static String NEGATIVE = "Negative";
	public static String POSITIVE = "Positive";
	public static String NEGATIVE_AND_POSITIVE = "Absolute value";
	
	public BasicInductionContext(boolean supportsSelectedOnly, CyTable table,  List<CyCyniMetric> metrics) {
		super(supportsSelectedOnly);
		selectedTable = table;
		currentType = "";
		if(metrics.size() > 0)
		{
			measures = new  ListSingleSelection<CyCyniMetric>(metrics);
		}
		else
		{
			measures = new  ListSingleSelection<CyCyniMetric>();//("No metrics available");
			attributes = new ArrayList<String>();
		}
		if(!measures.getPossibleValues().isEmpty())
		{
			if(measures.getSelectedValue().getTagsList().contains(CyniMetricTags.INPUT_NUMBERS.toString()) )
			{
				attributes = getAllAttributesNumbers(table);
				currentType = CyniMetricTags.INPUT_NUMBERS.toString();
			}
			else
			{
				attributes = getAllAttributesStrings(table);
				currentType =  CyniMetricTags.INPUT_STRINGS.toString();
			}
		}
		if(attributes.size() > 0)
		{
			attributeList = new  ListMultipleSelection<String>(attributes);
			attributeList.setSelectedValues(attributeList.getPossibleValues());
		}
		else
		{
			attributeList = new  ListMultipleSelection<String>("No sources available");
		}
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		setSelectedOnly(selectedOnly);
		if (thresholdAddEdge < 0.0 && type.getSelectedValue().matches(POSITIVE))
		{
			try {
				errMsg.append("Threshold needs to be greater than 0.0!!!!");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
		
		if (thresholdAddEdge > 0.0 && type.getSelectedValue().matches(NEGATIVE))
		{
			try {
				errMsg.append("Threshold needs to be lower or equal than 0.0!!!!");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
			
		if(measures.getPossibleValues().size()<=0) {
			try {
				errMsg.append("No metrics available to apply the algorithm!!!!");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
			
		}
		
		if(attributeList.getPossibleValues().get(0).matches("No sources available") || attributeList.getSelectedValues().size() == 0) {
			try {
				errMsg.append("No sources selected to apply the algorithm or there are no available. Please, select sources from the list if available.");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
			
		}
		return ValidationState.OK;
	}
}
