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
	
	
	@Tunable(description="Data Attributes", groups="Sources for Network Induction")
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
