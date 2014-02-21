/*
 * #%L
 * Cyni API (cyni-api)
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
package org.cytoscape.cyni;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyTable;



/**
 * The AbstractCyniMetrics provides a basic implementation of a cyni metric
 * TaskFactory.
 * 
 * @CyAPI.Abstract.Class
 */
public abstract class AbstractCyniMetric implements CyCyniMetric {

	private final String humanName;
	private final String computerName;
	private final ArrayList<String> tags;
	

	/**
	 * The Constructor.
	 * 
	 * @param computerName
	 *            a computer readable name used to differentiate the metrics.
	 * @param humanName
	 *            a user visible name of the metric.
	 *
	 */
	public AbstractCyniMetric(final String computerName,final String humanName) {
		this.humanName = humanName;
		this.computerName = computerName;
		tags = new ArrayList<String>();

	}
	
	/**
	 * A computer readable name used to differentiate the metrics.
	 * 
	 * @return a computer readable name used to differentiate the metrics.
	 */
	public String getName() {
		return computerName;
	}
	
	/**
	 * Used to get the user-visible name of the cyni Metric.
	 * 
	 * @return the user-visible name of the cyni Metric.
	 */
	public String toString() {
		return humanName;
	}
	
	/**
	 * It adds a tags to the list of tags that define that metric
	 * 
	 */
	protected void addTag(String tag){
		tags.add(tag);
	}
	
	/**
	 * It produces a CyniTable with methods specific for this metric
	 * 
	 * @return the new extended CyniTable
	 */
	public  CyniTable getCyniTable( CyTable table, String[] attributes, boolean transpose, boolean ignoreMissing, boolean selectedOnly)
	{
		return new CyniTable(table, attributes, transpose,ignoreMissing,selectedOnly);
	}
	
	/**
	 * The list of tags of this metric
	 * 
	 * @return the list of types
	 */
	public List<String>  getTagsList() {
		return tags;
	}

	/**
	 * It initializes the metric and after this method the metric is ready to start calculating measurements.
	 * If there are parameters to be set, they should be set before calling this method.
	 * 
	 */
	public void initMetric(){
		
	}
	
	/**
	 * It sets the  metric's parameters
	 * @param params The map that maps the name of the parameter with the actual variable
	 * 
	 */
	public void setParameters(Map<String,Object> params)
	{
		
	}

}
