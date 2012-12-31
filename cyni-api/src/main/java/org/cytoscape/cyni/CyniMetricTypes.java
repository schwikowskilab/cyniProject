/*
  File: CyniMetricTypes.java

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
