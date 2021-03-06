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
package fr.systemsbiology.cyni.internal.discretizationAlgorithms.EqualWidthFreqDiscretization;

import java.io.IOException;
import java.util.List;

import fr.systemsbiology.cyni.CyniAlgorithmContext;
import org.cytoscape.work.util.*;
import org.cytoscape.model.CyTable;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class EqualDiscretizationContext extends CyniAlgorithmContext implements TunableValidator {
	@Tunable(description="Intervals")
	public int bins = 5;
	
	@Tunable(description="Use Equal Frequency")
	public Boolean freq = false;
	
	@Tunable(description="Apply same discretization thresholds for all selected attributes")
	public Boolean all = true;

	@Tunable(description="Numerical Attributes", groups="Attributes to discretize")
	public ListMultipleSelection<String> attributeList;
	
	private List<String> attributes;

	public EqualDiscretizationContext( CyTable table) {
		super(true);
	    attributes = getAllAttributesNumbers(table);
		if(attributes.size() > 0)
		{
			attributeList = new  ListMultipleSelection<String>(attributes);
		}
		else
		{
			attributeList = new  ListMultipleSelection<String>("No sources available");
		}
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		if (bins > 0 && !attributeList.getPossibleValues().get(0).matches("No sources available")  && attributeList.getSelectedValues().size() >0)
			return ValidationState.OK;
		else {
			try {
				if (bins <= 0)
					errMsg.append("Intervals parameter needs to be greater than 0.\n");
				if(attributeList.getSelectedValues().size() == 0)
					errMsg.append("There are no numerical attributes selected to apply the Discretization Algorithm. Please, select them.\n");
				else if(attributeList.getSelectedValues().get(0).matches("No sources available"))
					errMsg.append("There are no numerical attributes available to apply the Discretization Algorithm.\n");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
	}
}
