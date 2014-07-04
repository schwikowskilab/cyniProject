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
package fr.systemsbiology.cyni.internal.imputationAlgorithms.RAVGFillAlgorithm;

import java.io.IOException;

import fr.systemsbiology.cyni.CyniAlgorithmContext;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.TunableValidator.ValidationState;
import org.cytoscape.work.util.ListSingleSelection;

public class RAVGImputationContext extends CyniAlgorithmContext implements TunableValidator {
	@Tunable(description="How to define a missing value:",groups="Missing Value Definition", xorChildren=true)
	public ListSingleSelection<String> chooser = new ListSingleSelection<String>("By a single value","By a single Maximum Threshold",
			"By a single Minimum Threshold","By a double Threshold");
	
	@Tunable(description="Missing Value:",groups={"Missing Value Definition","Single Value Selection"},xorKey="By a single value")
	public double missValue = 999;
	
	@Tunable(description="Missing Value if lower than:",groups={"Missing Value Definition","Single Maximum Threshold Selection"},xorKey="By a single Maximum Threshold")
	public double missValueLower = 999;
	
	@Tunable(description="Missing Value if larger than:",groups={"Missing Value Definition","Single Minimum Threshold Selection"},xorKey="By a single Minimum Threshold")
	public double missValueLarger = 999;
	
	@Tunable(description="Missing Value if lower than:",groups={"Missing Value Definition","Double Threshold Missing Value Selection"},xorKey="By a double Threshold")
	public double missValueDown = 999;
	@Tunable(description="Missing Value if larger than:",groups={"Missing Value Definition","Double Threshold Missing Value Selection"},xorKey="By a double Threshold")
	public double missValueUp = 999;


	public RAVGImputationContext() {

		super(true);
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		/*if(chooser.getSelectedValue().matches("By an interval") && missValueDown > missValueUp)
		{
			try {
				errMsg.append("Missing Value Low Threshold has to be smaller than Missing Value High Threshold");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}*/
		
		return ValidationState.OK;
	}
}
