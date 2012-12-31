/*
  File: CyInductionImpl.java

  Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.cyni.internal;


import java.util.*;

import org.cytoscape.property.CyProperty;
import org.cytoscape.cyni.*;


/**
 * CyInductionsImpl is a singleton class that is used to register all available
 * Induction algorithms.  
 */
public class CyCyniMetricsImpl implements CyCyniMetricsManager {

	private final Map<String, CyCyniMetric> CyniMetricsMap;
	private final Map<String, String> CyniMetricsMapNames;
	private final CyProperty<Properties> cyProps;
	/** The name of the default Induction. */
	private String DEFAULT_Cyni_Metric_NAME ;

	public CyCyniMetricsImpl(final CyProperty<Properties> p) {
		this.cyProps = p;
		DEFAULT_Cyni_Metric_NAME = "Correlation";
		CyniMetricsMap = new HashMap<String,CyCyniMetric>();
		CyniMetricsMapNames = new HashMap<String,String>();
	}

	/**
	 * Add a Induction to the Induction manager's list.  If menu is "null"
	 * it will be assigned to the "none" menu, which is not displayed.
	 * This can be used to register Inductions that are to be used for
	 * specific algorithmic purposes, but not, in general, supposed
	 * to be for direct user use.
	 *
	 * @param Induction The Induction to be added
	 * @param menu The menu that this should appear under
	 */
	public void addCyniMetric(CyCyniMetric InductionMetric, Map props) {
		if ( InductionMetric != null )
		{
			CyniMetricsMap.put(InductionMetric.getName(),InductionMetric);
			CyniMetricsMapNames.put(InductionMetric.toString(),InductionMetric.getName() );
		}
	}

	/**
	 * Remove a Induction from the Induction maanger's list.
	 *
	 * @param Induction The Induction to remove
	 */
	public void removeCyniMetric(CyCyniMetric InductionMetric, Map props) {
		if ( InductionMetric != null )
		{
			CyniMetricsMap.remove(InductionMetric.getName());
			CyniMetricsMapNames.remove(InductionMetric.toString());
		}
	}

	/**
	 * Get the Induction named "name".  If "name" does
	 * not exist, this will return null
	 *
	 * @param name String representing the name of the Induction
	 * @return the Induction of that name or null if it is not reigstered
	 */
	@Override
	public CyCyniMetric getCyniMetric(String name) {
		if (name != null)
		{
			if(CyniMetricsMap.containsKey(name))
				return CyniMetricsMap.get(name);
			else if(CyniMetricsMapNames.containsKey(name))
				return CyniMetricsMap.get(CyniMetricsMapNames.get(name));
		}
		return null;
	}

	/**
	 * Get all of the available Inductions.
	 *
	 * @return a Collection of all the available Inductions
	 */
	@Override
	public ArrayList<CyCyniMetric> getAllCyniMetrics() {
		return  new ArrayList<CyCyniMetric>(CyniMetricsMap.values());
	}
	
	
	/**
	 * Returns the list of names of metrics.
	 * 
	 * @param type
	 * @return the list of names of metrics.
	 */
	@Override
	public ArrayList<CyCyniMetric> getAllCyniMetricsWithType(List<String> types){
		ArrayList<CyCyniMetric> list = new ArrayList<CyCyniMetric>();
		
		for(Map.Entry<String, CyCyniMetric> metric : CyniMetricsMap.entrySet()){
			if(metric.getValue().getTypesList().containsAll(types))
				list.add(metric.getValue());
		}
		
		return list;
	}

	/**
	 * Get the default Induction.  This is either the grid Induction or a Induction
	 * chosen by the user via the setting of the "Induction.default" property.
	 *
	 * @return CyInductionAlgorithm to use as the default Induction algorithm
	 */
	@Override
	public CyCyniMetric getDefaultCyniMetric() {
		// See if the user has set the Induction.default property	
		String defaultInductionMetric = cyProps.getProperties().getProperty(CyCyniMetricsManager.DEFAULT_Cyni_Metric_PROPERTY_NAME);
		if (defaultInductionMetric == null || CyniMetricsMap.containsKey(defaultInductionMetric) == false)
			defaultInductionMetric = DEFAULT_Cyni_Metric_NAME; 

		return getCyniMetric(defaultInductionMetric);
	}
	
	/**
	 * Get the default Induction.  This is either the grid Induction or a Induction
	 * chosen by the user via the setting of the "Induction.default" property.
	 *
	 * @return CyInductionAlgorithm to use as the default Induction algorithm
	 */
	@Override
	public void setDefaultCyniMetric(String name) {
		DEFAULT_Cyni_Metric_NAME = name;
	}
}
