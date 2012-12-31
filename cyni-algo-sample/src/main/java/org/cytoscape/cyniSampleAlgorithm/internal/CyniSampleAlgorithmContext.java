package org.cytoscape.cyniSampleAlgorithm.internal;

import java.io.IOException;
import java.util.List;

import org.cytoscape.cyni.AbstractCyniAlgorithmContext;
import org.cytoscape.work.util.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class CyniSampleAlgorithmContext extends AbstractCyniAlgorithmContext implements TunableValidator {
	@Tunable(description="Parameter 1")
	public int param1 = 10;
	
	@Tunable(description="Paramter 2")
	public Boolean param2 = false;
	

	public CyniSampleAlgorithmContext( ) {
		super(true);
		
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		if (param1 > 0 )
			return ValidationState.OK;
		else {
			try {
				errMsg.append("parameter 1 is not good");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
	}
}
