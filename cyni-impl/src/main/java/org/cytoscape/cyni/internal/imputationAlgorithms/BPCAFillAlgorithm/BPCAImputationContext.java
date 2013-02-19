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

package org.cytoscape.cyni.internal.imputationAlgorithms.BPCAFillAlgorithm;

import java.io.IOException;
import java.util.List;

import org.cytoscape.cyni.AbstractCyniAlgorithmContext;
import org.cytoscape.work.TunableValidator.ValidationState;
import org.cytoscape.work.util.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class BPCAImputationContext extends AbstractCyniAlgorithmContext implements TunableValidator {
	@Tunable(description="How to define a missing value",groups="Missing Value Definition", xorChildren=true)
	public ListSingleSelection<String> chooser = new ListSingleSelection<String>("By a single value","By an interval");
	
	@Tunable(description="Missing Value",groups={"Missing Value Definition","Single Value Selection"},xorKey="By a single value")
	public double missValue = 999;
	
	@Tunable(description="Missing Value Low Threshold",groups={"Missing Value Definition","Interval Missing Value Selection"},xorKey="By an interval")
	public double missValueDown = 999;
	@Tunable(description="Missing Value High Threshold",groups={"Missing Value Definition","Interval Missing Value Selection"},xorKey="By an interval")
	public double missValueUp = 999;


	public BPCAImputationContext() {

		super(true);
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		if(chooser.getSelectedValue().matches("By an interval") && missValueDown > missValueUp)
		{
			try {
				errMsg.append("Missing Value Low Threshold has to be smaller than Missing Value High Threshold");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
		return ValidationState.OK;
	}
}
