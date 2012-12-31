package org.cytoscape.cyni.internal.discretizationAlgorithms.EqualWidthFreqDiscretization;

import java.io.IOException;
import java.util.List;

import org.cytoscape.cyni.AbstractCyniAlgorithmContext;
import org.cytoscape.work.util.*;
import org.cytoscape.model.CyTable;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class EqualDiscretizationContext extends AbstractCyniAlgorithmContext implements TunableValidator {
	@Tunable(description="Bins")
	public int bins = 5;
	
	@Tunable(description="Use Equal Frequency")
	public Boolean freq = false;
	
	@Tunable(description="Apply same discretization thresholds for all selected attributes")
	public Boolean all = false;

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
		System.out.println("validation...");
		if (bins > 0 )
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
