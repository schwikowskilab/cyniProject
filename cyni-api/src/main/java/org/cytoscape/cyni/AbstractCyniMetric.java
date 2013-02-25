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



/**
 * The AbstractCyniMetrics provides a basic implementation of a cyni metric
 * TaskFactory.
 * 
 * @CyAPI.Abstract.Class
 */
public abstract class AbstractCyniMetric implements CyCyniMetric {

	private final String humanName;
	private final String computerName;
	private final ArrayList<String> types;
	

	/**
	 * The Constructor.
	 * 
	 * @param computerName
	 *            a computer readable name used to differentiate the metrics.
	 * @param humanName
	 *            a user visible name of the metric.
	 * @param types
	 *            list of types that this metric supports.
	 */
	public AbstractCyniMetric(final String computerName,final String humanName) {
		this.humanName = humanName;
		this.computerName = computerName;
		types = new ArrayList<String>();

	}
	
	/**
	 * A computer readable name used to to differentiate the metrics.
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
	 * It adds a type to the list of supported types for that metric
	 * 
	 */
	protected void addType(String type){
		types.add(type);
	}
	/**
	 * The list of types of this metric
	 * 
	 * @return the list of types
	 */
	public List<String>  getTypesList() {
		return types;
	}

	/**
	 * It resets the metric and leaves the default metric's parameters
	 * 
	 */
	public void resetParameters(){
		
	}
	

}
