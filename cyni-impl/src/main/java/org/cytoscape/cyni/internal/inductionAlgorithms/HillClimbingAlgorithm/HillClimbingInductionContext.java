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

public class HillClimbingInductionContext extends AbstractCyniAlgorithmContext implements TunableValidator {
	@Tunable(description="Maximmun number of parents")
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
		System.out.println("validation...");
		setSelectedOnly(selectedOnly);
		if (maxNumParents > 0 &&  measures.getPossibleValues().size()>0)
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
