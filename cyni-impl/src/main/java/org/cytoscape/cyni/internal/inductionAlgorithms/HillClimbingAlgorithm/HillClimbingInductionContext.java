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
package org.cytoscape.cyni.internal.inductionAlgorithms.HillClimbingAlgorithm;

import java.io.IOException;
import java.util.*;
import org.cytoscape.work.util.*;
import org.cytoscape.model.CyTable;

import org.cytoscape.cyni.*;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class HillClimbingInductionContext extends CyniAlgorithmContext implements TunableValidator {
	@Tunable(description="Maximum number of parents")
	public int maxNumParents = 5;
	
	@Tunable(description="Check reverse edges")
	public boolean reversalOption = false;
	
	@Tunable(description="Output Only Nodes with Edges")
	public boolean removeNodes = false;
	
	@Tunable(description="Use network associated as initial search", groups="Parameters if a network associated to table data")
	public boolean useNetworkAsInitialSearch = false;
	
	@Tunable(description="Use selected nodes only", groups="Parameters if a network associated to table data")
	public boolean selectedOnly = false;
	
	@Tunable(description="Keep selected edges", groups="Parameters if a network associated to table data")
	public boolean edgesBlocked = false;
	
	@Tunable(description="Metric")
	public ListSingleSelection<CyCyniMetric> measures;
	
	
	@Tunable(description="Data Attributes", groups="Sources for Network Inference")
	public ListMultipleSelection<String> attributeList;
	
	private List<String> attributes;

	public HillClimbingInductionContext(boolean supportsSelectedOnly, CyTable table,  ArrayList<CyCyniMetric> metrics) {
		super(supportsSelectedOnly);
		attributes = getAllAttributesStrings(table);
		if(attributes.size() > 0)
		{
			attributeList = new  ListMultipleSelection<String>(attributes);
			List<String> temp = new ArrayList<String>( attributes);
			temp.remove(table.getPrimaryKey().getName());
			if(!temp.isEmpty())
				attributeList.setSelectedValues(temp);
		}
		else
		{
			attributeList = new  ListMultipleSelection<String>("No sources available");
		}
		if(metrics.size() > 0)
		{
			measures = new  ListSingleSelection<CyCyniMetric>(metrics);
		}
		else
		{
			measures = new  ListSingleSelection<CyCyniMetric>();
		}
		
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		setSelectedOnly(selectedOnly);
		if (maxNumParents > 0 &&  !attributeList.getPossibleValues().get(0).matches("No sources available") && measures.getPossibleValues().size()>0 && attributeList.getSelectedValues().size() >0)
			return ValidationState.OK;
		else {
			try {
				if (maxNumParents <= 0)
					errMsg.append("Number of parents needs to be greater than 0!!!!\n");
				if( attributeList.getSelectedValues().size() == 0)
					errMsg.append("There are no source attributes selected to apply the Inference Algorithm. Please, select them.\n");
				else if(attributeList.getSelectedValues().get(0).matches("No sources available"))
					errMsg.append("There are no source attributes available to apply the Inference Algorithm.\n");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
	}
}
