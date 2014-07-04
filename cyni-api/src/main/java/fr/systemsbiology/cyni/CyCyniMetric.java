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
package fr.systemsbiology.cyni;

import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyTable;


/**
 * An interface specific to Cyni metrics.
 * 
 * @CyAPI.Spi.Interface
 */
public interface CyCyniMetric {
	/**
	 * The implementation of the metric. This method takes two sets of data and calculate a measurement.
	 * @param table1 The cyniTable where there is the first element to compare.
	 * @param table2 The cyniTable where there is the second element to compare.
	 * @param indexBase The index of the element in the first CyniTable that will be compared.
	 * @param indexToCompare The list of indexes of the elements in the second CyniTable that will compared 
	 * @return the result of the measurement
	 */
	public Double getMetric(CyniTable table1, CyniTable table2, int indexBase,List<Integer> indexToCompare);
	/**
	 * Returns the computer-readable name of the Cyni Metric. To get a human
	 * readable name, use toString().
	 * 
	 * @return The computer-readable name of the metric.
	 */
	public String getName();
	
	/**
	 * The list of tags  that define this metric
	 * 
	 * @return the list of tags
	 */
	public List<String>  getTagsList();
	
	/**
	 * It produces a CyniTable with methods specific for this metric
	 * 
	 * @return the new extended CyniTable
	 */
	public  CyniTable getCyniTable( CyTable table, String[] attributes, boolean transpose, boolean ignoreMissing, boolean selectedOnly);
	
	/**
	 * It initializes the metric and after this method the metric is ready to start calculating measurements.
	 * If there are parameters to be set, they should be set before calling this method.
	 * 
	 */
	public  void initMetric();
	
	/**
	 * It sets the  metric's parameters
	 * @param params The map that maps the name of the parameter with the actual variable
	 * 
	 */
	public void setParameters(Map<String,Object> params);
}
