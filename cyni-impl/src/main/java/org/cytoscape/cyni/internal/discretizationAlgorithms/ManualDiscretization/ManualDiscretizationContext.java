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
package org.cytoscape.cyni.internal.discretizationAlgorithms.ManualDiscretization;

import java.io.IOException;
import java.util.*;

import org.cytoscape.cyni.CyniAlgorithmContext;
import org.cytoscape.cyni.CyCyniMetric;
import org.cytoscape.work.util.*;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyColumn;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class ManualDiscretizationContext extends CyniAlgorithmContext implements TunableValidator {
	@Tunable(description="Number of Intervals", groups={"Interval Definition"}, xorChildren=true)
	public ListSingleSelection<String> interval = new ListSingleSelection<String>("2","3","4","5","6","7","8","9","10");
	
	@Tunable(description="Threshold 1", groups={"Interval Definition","Threshold's Coordinate for 2 Intervals"},params="slider=true",  xorKey="2")
	public BoundedDouble th11 ;
	
	@Tunable(description="Threshold 1", groups={"Interval Definition","Threshold's Coordinates for 3 Intervals"}, params="slider=true", xorKey="3")
	public BoundedDouble th21 ;
	
	@Tunable(description="Threshold 2", groups={"Interval Definition","Threshold's Coordinates for 3 Intervals"}, params="slider=true", xorKey="3")
	public BoundedDouble th22  ;
	
	@Tunable(description="Threshold 1", groups={"Interval Definition","Threshold's Coordinates for 4 Intervals"}, params="slider=true", xorKey="4")
	public BoundedDouble th31 ;
	
	@Tunable(description="Threshold 2", groups={"Interval Definition","Threshold's Coordinates for 4 Intervals"}, params="slider=true", xorKey="4")
	public BoundedDouble th32 ;
	
	@Tunable(description="Threshold 3", groups={"Interval Definition","Threshold's Coordinates for 4 Intervals"}, params="slider=true", xorKey="4")
	public BoundedDouble th33 ;
	
	@Tunable(description="Threshold 1", groups={"Interval Definition","Threshold's Coordinates for 5 Intervals"}, params="slider=true", xorKey="5")
	public BoundedDouble th41 ;
	
	@Tunable(description="Threshold 2", groups={"Interval Definition","Threshold's Coordinates for 5 Intervals"}, params="slider=true", xorKey="5")
	public BoundedDouble th42 ;
	
	@Tunable(description="Threshold 3", groups={"Interval Definition","Threshold's Coordinates for 5 Intervals"}, params="slider=true", xorKey="5")
	public BoundedDouble th43 ;
	
	@Tunable(description="Threshold 4", groups={"Interval Definition","Threshold's Coordinates for 5 Intervals"}, params="slider=true", xorKey="5")
	public BoundedDouble th44 ;
	
	@Tunable(description="Threshold 1", groups={"Interval Definition","Threshold's Coordinates for 6 Intervals"}, params="slider=true", xorKey="6")
	public BoundedDouble th51 ;
	
	@Tunable(description="Threshold 2", groups={"Interval Definition","Threshold's Coordinates for 6 Intervals"}, params="slider=true", xorKey="6")
	public BoundedDouble th52 ;
	
	@Tunable(description="Threshold 3", groups={"Interval Definition","Threshold's Coordinates for 6 Intervals"}, params="slider=true", xorKey="6")
	public BoundedDouble th53 ;
	
	@Tunable(description="Threshold 4", groups={"Interval Definition","Threshold's Coordinates for 6 Intervals"}, params="slider=true", xorKey="6")
	public BoundedDouble th54 ;
	
	@Tunable(description="Threshold 5", groups={"Interval Definition","Threshold's Coordinates for 6 Intervals"}, params="slider=true", xorKey="6")
	public BoundedDouble th55 ;
	
	@Tunable(description="Threshold 1", groups={"Interval Definition","Threshold's Coordinates for 7 Intervals"}, params="slider=true", xorKey="7")
	public BoundedDouble th61 ;
	
	@Tunable(description="Threshold 2", groups={"Interval Definition","Threshold's Coordinates for 7 Intervals"}, params="slider=true", xorKey="7")
	public BoundedDouble th62 ;
	
	@Tunable(description="Threshold 3", groups={"Interval Definition","Threshold's Coordinates for 7 Intervals"}, params="slider=true", xorKey="7")
	public BoundedDouble th63 ;
	
	@Tunable(description="Threshold 4", groups={"Interval Definition","Threshold's Coordinates for 7 Intervals"}, params="slider=true", xorKey="7")
	public BoundedDouble th64 ;
	
	@Tunable(description="Threshold 5", groups={"Interval Definition","Threshold's Coordinates for 7 Intervals"}, params="slider=true", xorKey="7")
	public BoundedDouble th65 ;
	
	@Tunable(description="Threshold 6", groups={"Interval Definition","Threshold's Coordinates for 7 Intervals"}, params="slider=true", xorKey="7")
	public BoundedDouble th66 ;
	
	@Tunable(description="Threshold 1", groups={"Interval Definition","Threshold's Coordinates for 8 Intervals"}, params="slider=true", xorKey="8")
	public BoundedDouble th71 ;
	
	@Tunable(description="Threshold 2", groups={"Interval Definition","Threshold's Coordinates for 8 Intervals"}, params="slider=true", xorKey="8")
	public BoundedDouble th72 ;
	
	@Tunable(description="Threshold 3", groups={"Interval Definition","Threshold's Coordinates for 8 Intervals"}, params="slider=true", xorKey="8")
	public BoundedDouble th73 ;
	
	@Tunable(description="Threshold 4", groups={"Interval Definition","Threshold's Coordinates for 8 Intervals"}, params="slider=true", xorKey="8")
	public BoundedDouble th74 ;
	
	@Tunable(description="Threshold 5", groups={"Interval Definition","Threshold's Coordinates for 8 Intervals"}, params="slider=true", xorKey="8")
	public BoundedDouble th75 ;
	
	@Tunable(description="Threshold 6", groups={"Interval Definition","Threshold's Coordinates for 8 Intervals"}, params="slider=true", xorKey="8")
	public BoundedDouble th76 ;
	
	@Tunable(description="Threshold 7", groups={"Interval Definition","Threshold's Coordinates for 8 Intervals"}, params="slider=true", xorKey="8")
	public BoundedDouble th77 ;
	
	@Tunable(description="Threshold 1", groups={"Interval Definition","Threshold's Coordinates for 9 Intervals"}, params="slider=true", xorKey="9")
	public BoundedDouble th81 ;
	
	@Tunable(description="Threshold 2", groups={"Interval Definition","Threshold's Coordinates for 9 Intervals"}, params="slider=true", xorKey="9")
	public BoundedDouble th82 ;
	
	@Tunable(description="Threshold 3", groups={"Interval Definition","Threshold's Coordinates for 9 Intervals"}, params="slider=true", xorKey="9")
	public BoundedDouble th83 ;
	
	@Tunable(description="Threshold 4", groups={"Interval Definition","Threshold's Coordinates for 9 Intervals"}, params="slider=true", xorKey="9")
	public BoundedDouble th84 ;
	
	@Tunable(description="Threshold 5", groups={"Interval Definition","Threshold's Coordinates for 9 Intervals"}, params="slider=true", xorKey="9")
	public BoundedDouble th85 ;
	
	@Tunable(description="Threshold 6", groups={"Interval Definition","Threshold's Coordinates for 9 Intervals"}, params="slider=true", xorKey="9")
	public BoundedDouble th86 ;
	
	@Tunable(description="Threshold 7", groups={"Interval Definition","Threshold's Coordinates for 9 Intervals"}, params="slider=true", xorKey="9")
	public BoundedDouble th87 ;
	
	@Tunable(description="Threshold 8", groups={"Interval Definition","Threshold's Coordinates for 9 Intervals"}, params="slider=true", xorKey="9")
	public BoundedDouble th88 ;
	
	@Tunable(description="Threshold 1", groups={"Interval Definition","Threshold's Coordinates for 10 Intervals"}, params="slider=true", xorKey="10")
	public BoundedDouble th91 ;
	
	@Tunable(description="Threshold 2", groups={"Interval Definition","Threshold's Coordinates for 10 Intervals"}, params="slider=true", xorKey="10")
	public BoundedDouble th92 ;
	
	@Tunable(description="Threshold 3", groups={"Interval Definition","Threshold's Coordinates for 10 Intervals"}, params="slider=true", xorKey="10")
	public BoundedDouble th93 ;
	
	@Tunable(description="Threshold 4", groups={"Interval Definition","Threshold's Coordinates for 10 Intervals"}, params="slider=true", xorKey="10")
	public BoundedDouble th94 ;
	
	@Tunable(description="Threshold 5", groups={"Interval Definition","Threshold's Coordinates for 10 Intervals"}, params="slider=true", xorKey="10")
	public BoundedDouble th95 ;
	
	@Tunable(description="Threshold 6", groups={"Interval Definition","Threshold's Coordinates for 10 Intervals"}, params="slider=true", xorKey="10")
	public BoundedDouble th96 ;
	
	@Tunable(description="Threshold 7", groups={"Interval Definition","Threshold's Coordinates for 10 Intervals"}, params="slider=true", xorKey="10")
	public BoundedDouble th97 ;
	
	@Tunable(description="Threshold 8", groups={"Interval Definition","Threshold's Coordinates for 10 Intervals"}, params="slider=true", xorKey="10")
	public BoundedDouble th98 ;
	
	@Tunable(description="Threshold 9", groups={"Interval Definition","Threshold's Coordinates for 10 Intervals"}, params="slider=true", xorKey="10")
	public BoundedDouble th99 ;
	

	@Tunable(description="Numerical Attributes", groups="Attributes to discretize")
	public ListMultipleSelection<String> attributeList;
	
	private List<String> attributes;
	
	private Double maxValue,minValue,mean;
	public Map<String ,BoundedDouble> mapTh;

	public ManualDiscretizationContext( CyTable table) {
		super(true);
		Double value;
		CyColumn column;
		boolean isInteger;
		maxValue = 0.0;
		minValue = 0.0;
	    attributes = getAllAttributesNumbers(table);
	    if(table != null)
	    {
		    List<CyRow> listRows= table.getAllRows();
		    for(String col : attributes)
	    	{
		    	column = table.getColumn(col);
		    	if(column.getType() == Integer.class)
		    		isInteger = true;
		    	else
		    		isInteger = false;
			    for(CyRow row : listRows)
			    {
			    	if(isInteger)
			    		value = ((Integer)row.get(col, column.getType())).doubleValue();
			    	else
			    		value = (Double)row.get(col, column.getType());
		    		if(value != null)
		    		{
		    			if(value > maxValue)
		    				maxValue = value;
		    			if(value < minValue)
		    				minValue = value;
		    		}
		    	}
		    }
	    }
	    mean = (maxValue - minValue)/2.0;
	    
		if(attributes.size() > 0)
		{
			attributeList = new  ListMultipleSelection<String>(attributes);
		}
		else
		{
			attributeList = new  ListMultipleSelection<String>("No sources available");
		}
		th11 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th21 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th22 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th31 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th32 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th33 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th41 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th42 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th43 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th44 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th51 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th52 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th53 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th54 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th55 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th61 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th62 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th63 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th64 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th65 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th66 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th71 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th72 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th73 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th74 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th75 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th76 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th77 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th81 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th82 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th83 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th84 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th85 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th86 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th87 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th88 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th91 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th92 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th93 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th94 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th95 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th96 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th97 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th98 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		th99 = new BoundedDouble(minValue -1, mean, maxValue +1,false, false);
		mapTh = new HashMap<String ,BoundedDouble>();
		mapTh.put("th11", th11);
		mapTh.put("th21", th21);
		mapTh.put("th22", th22);
		mapTh.put("th31", th31);
		mapTh.put("th32", th32);
		mapTh.put("th33", th33);
		mapTh.put("th41", th41);
		mapTh.put("th42", th42);
		mapTh.put("th43", th43);
		mapTh.put("th44", th44);
		mapTh.put("th51", th51);
		mapTh.put("th52", th52);
		mapTh.put("th53", th53);
		mapTh.put("th54", th54);
		mapTh.put("th55", th55);
		mapTh.put("th61", th61);
		mapTh.put("th62", th62);
		mapTh.put("th63", th63);
		mapTh.put("th64", th64);
		mapTh.put("th65", th65);
		mapTh.put("th66", th66);
		mapTh.put("th71", th71);
		mapTh.put("th72", th72);
		mapTh.put("th73", th73);
		mapTh.put("th74", th74);
		mapTh.put("th75", th75);
		mapTh.put("th76", th76);
		mapTh.put("th77", th77);
		mapTh.put("th81", th81);
		mapTh.put("th82", th82);
		mapTh.put("th83", th83);
		mapTh.put("th84", th84);
		mapTh.put("th85", th85);
		mapTh.put("th86", th86);
		mapTh.put("th87", th87);
		mapTh.put("th88", th88);
		mapTh.put("th91", th91);
		mapTh.put("th92", th92);
		mapTh.put("th93", th93);
		mapTh.put("th94", th94);
		mapTh.put("th95", th95);
		mapTh.put("th96", th96);
		mapTh.put("th97", th97);
		mapTh.put("th98", th98);
		mapTh.put("th99", th99);
		
	}
	
	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		if ( attributeList.getPossibleValues().get(0).matches("No sources available")  || attributeList.getSelectedValues().size() ==0)
		{
			try {
				
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
		return ValidationState.OK;
	}
}
