/*
  File:  BPCAImputationContext.java

  Copyright (c) 2006, 2010-2012, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.cyni.internal.imputationAlgorithms.BPCAFillAlgorithm;

import java.io.IOException;
import java.util.List;

import org.cytoscape.cyni.AbstractCyniAlgorithmContext;
import org.cytoscape.work.TunableValidator.ValidationState;
import org.cytoscape.work.util.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class BPCAImputationContext extends AbstractCyniAlgorithmContext implements TunableValidator {
	@Tunable(description="Use an interval to define missing value",groups="Missing Value Definition")
	public boolean interval = false;
	@Tunable(description="Missing Value",dependsOn="interval=false",groups="Missing Value Definition",params="displayState=hidden")
	public double missValue = 999;
	
	@Tunable(description="Missing Value Down",dependsOn="interval=true",groups="Missing Value Definition",params="displayState=hidden")
	public double missValueDown = 999;
	@Tunable(description="Missing Value Up",dependsOn="interval=true",groups="Missing Value Definition",params="displayState=hidden")
	public double missValueUp = 999;


	public BPCAImputationContext() {

		super(true);
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		if(interval && missValueDown > missValueUp)
		{
			try {
				errMsg.append("Missing Value Down has to be smaller than Missing Value Up");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
		return ValidationState.OK;
	}
}
