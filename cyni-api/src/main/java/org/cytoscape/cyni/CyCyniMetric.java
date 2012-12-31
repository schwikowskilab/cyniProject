/*
  File: CyCyniMetric.java

  Copyright (c) 2006, 2010-2012, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.cyni;

import java.util.List;


/**
 * An interface specific to Cyni metrics.
 * 
 * @CyAPI.Spi.Interface
 */
public interface CyCyniMetric {
	/**
	 * The implementation of the metric
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
	 * The list of types of this metric
	 * 
	 * @return the list of types
	 */
	public List<String>  getTypesList();
	
	/**
	 * It resets the metric and leaves the default metric's parameters
	 * 
	 */
	public  void resetParameters();
}
