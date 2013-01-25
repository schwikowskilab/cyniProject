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
package org.cytoscape.cyni.internal.inductionAlgorithms.K2Algorithm;

import java.io.IOException;
import java.util.*;

import org.cytoscape.work.util.*;
import org.cytoscape.model.CyTable;
import org.cytoscape.cyni.*;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class K2InductionContext extends AbstractCyniAlgorithmContext implements TunableValidator {
	
	@Tunable(description="Maximmun number of parents")
	public int maxNumParents = 5;
	@Tunable(description="Output Only Nodes with Edges")
	public boolean removeNodes = false;
	
	@Tunable(description="Row order", groups="Row order options")
	public ListSingleSelection<String> ordering = new ListSingleSelection<String>("Default Cytoscape Order", "Random Order", "Use Column");
	@Tunable(description="Use this column to order rows",dependsOn="ordering=Use Column", groups="Row order options")
	public ListSingleSelection<String> selectedColumn;
	
	@Tunable(description="Use selected nodes only", groups="Parameters if a network associated to table data")
	public boolean selectedOnly = false;
	
	public ListSingleSelection<CyCyniMetric> measures;
	@Tunable(description="Metric")
	public ListSingleSelection<CyCyniMetric> getMeasures()
	{
		return measures;
	}
	public void setMeasures(ListSingleSelection<CyCyniMetric> mes)
	{
	}
	
	public ListMultipleSelection<String> attributeList;
	@Tunable(description="Data Attributes", groups="Sources for Network Induction",listenForChange={"Measures"})
	public ListMultipleSelection<String> getA()
	{
		List<String>  typeList ;
		if(measures.getPossibleValues().size()==0)
		{
			attributeList = new  ListMultipleSelection<String>("No sources available");
		}
		else
		{
			typeList = measures.getSelectedValue().getTypesList();
			if(typeList.contains(CyniMetricTypes.INPUT_NUMBERS.toString())  &&  !currentType.matches(CyniMetricTypes.INPUT_NUMBERS.toString()))
			{
				attributes = getAllAttributesNumbers(selectedTable);
				attributeList = new  ListMultipleSelection<String>(attributes);
				currentType = CyniMetricTypes.INPUT_NUMBERS.toString();
			}
			else
			{
				if(typeList.contains(CyniMetricTypes.INPUT_STRINGS.toString())  &&  !currentType.matches(CyniMetricTypes.INPUT_STRINGS.toString()))
				{
					attributes = getAllAttributesStrings(selectedTable);
					attributeList = new  ListMultipleSelection<String>(attributes);
					currentType =  CyniMetricTypes.INPUT_STRINGS.toString();
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
	public void setA(ListMultipleSelection<String> input)
	{
	}
	
	private List<String> attributes;
	private CyTable selectedTable;
	private String currentType ;

	public K2InductionContext(boolean supportsSelectedOnly, CyTable table,  ArrayList<CyCyniMetric> metrics, CyCyniMetricsManager metricsManager) {
		super(supportsSelectedOnly);
		selectedTable = table;
		currentType = "";
		ArrayList<String> tempList = new ArrayList<String>(getAllAttributesNumbers(selectedTable));
		tempList.addAll(getAllAttributesStrings(selectedTable));
		
		if(metrics.size() > 0)
		{
			measures = new  ListSingleSelection<CyCyniMetric>(metrics);
			measures.setSelectedValue(metricsManager.getDefaultCyniMetric());
		}
		else
		{
			measures = new  ListSingleSelection<CyCyniMetric>();
		}
		selectedColumn = new  ListSingleSelection<String>(tempList);
		
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		setSelectedOnly(selectedOnly);
		if (maxNumParents > 0 && measures.getPossibleValues().size()>0)
			return ValidationState.OK;
		else {
			try {
				errMsg.append("Threshold needs to be greater than 0.0!!!!");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
	}
}
