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

/**
 * An enum that captures the types of the default Cyni Metrics available
 * @CyAPI.Enum.Class
 */
public enum CyniMetricTypes {
	/** 
	* Cyni Metric accepts numbers as input
	*/ 
	INPUT_NUMBERS( 0x01), 
	
	/** 
	* Cyni Metric accepts strings as input
	*/ 
	INPUT_STRINGS (0x02), 
	
	/** 
	* Cyni Metric decomposed as the sum or product of the score of each individual node
	*/ 
	LOCAL_METRIC_SCORE (0x04),
	
	/** 
	* Cyni Metric based on searching for any statistical relationship between two sets of data
	*/ 
	CORRELATION_METRIC (0x08);
	
	
	private int type;
	
	CyniMetricTypes(int type)
	{
		this.type = type;
	}
	public int getType()
	{
		return this.type;
	}
	
}
