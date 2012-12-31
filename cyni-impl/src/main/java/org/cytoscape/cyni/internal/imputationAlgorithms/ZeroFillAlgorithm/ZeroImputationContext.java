package org.cytoscape.cyni.internal.imputationAlgorithms.ZeroFillAlgorithm;

import java.io.IOException;
import java.util.List;

import org.cytoscape.cyni.AbstractCyniAlgorithmContext;
import org.cytoscape.work.util.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class ZeroImputationContext extends AbstractCyniAlgorithmContext implements TunableValidator {
	
	@Tunable(description="Use an interval to define missing value",groups="Missing Value Definition")
	public boolean interval = false;
	@Tunable(description="Missing Value",dependsOn="interval=false",groups="Missing Value Definition",params="displayState=hidden")
	public double missValue = 999;
	
	@Tunable(description="Missing Value Down",dependsOn="interval=true",groups="Missing Value Definition",params="displayState=hidden")
	public double missValueDown = 999;
	@Tunable(description="Missing Value Up",dependsOn="interval=true",groups="Missing Value Definition",params="displayState=hidden")
	public double missValueUp = 999;


	public ZeroImputationContext() {

		super(true);
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		System.out.println("validation...");
		if(interval && missValueDown > missValueUp)
		{
			try {
				errMsg.append("Threshold needs to be greater than 0.0!!!!");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
		if (!interval && missValue < 0.0 )
		{
			try {
				errMsg.append("Threshold needs to be greater than 0.0!!!!");
			} catch (IOException e) {
				e.printStackTrace();
				return ValidationState.INVALID;
			}
			return ValidationState.INVALID;
		}
		return ValidationState.OK;
	}
}
