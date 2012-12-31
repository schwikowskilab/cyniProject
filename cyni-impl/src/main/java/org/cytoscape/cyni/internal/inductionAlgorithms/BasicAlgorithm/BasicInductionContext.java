package org.cytoscape.cyni.internal.inductionAlgorithms.BasicAlgorithm;

import java.io.IOException;
import java.util.List;
import org.cytoscape.work.util.*;
import org.cytoscape.model.CyTable;

import org.cytoscape.cyni.*;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class BasicInductionContext extends AbstractCyniAlgorithmContext implements TunableValidator {
	@Tunable(description="Threshold to add new edge")
	public double thresholdAddEdge = 0.5;

	@Tunable(description="Output Only Nodes with Edges")
	public boolean removeNodes = false;
	
	@Tunable(description="Use selected nodes only", groups="Parameters if a network associated to table data")
	public boolean selectedOnly = false;
	
	@Tunable(description="Metric")
	public ListSingleSelection<CyCyniMetric> measures;
	
	@Tunable(description="Data Attributes", groups="Sources for Network Induction")
	public ListMultipleSelection<String> attributeList;

	
	private List<String> attributes;
	
	public BasicInductionContext(boolean supportsSelectedOnly, CyTable table,  List<CyCyniMetric> metrics) {
		super(supportsSelectedOnly);
		attributes = getAllAttributesNumbers(table);
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
			measures = new  ListSingleSelection<CyCyniMetric>();//("No metrics available");
		}
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		System.out.println("validation...");
		setSelectedOnly(selectedOnly);
		if (thresholdAddEdge > 0.0 && measures.getPossibleValues().size()>0)
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
