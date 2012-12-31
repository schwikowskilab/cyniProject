/*
  File: AbstractCyniAlgorithm.java

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
package org.cytoscape.cyni;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.cytoscape.work.TunableSetter;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.TunableValidator.ValidationState;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

/**
 * The AbstractCyniAlgorithm provides a basic implementation of a cyni algorithm
 * TaskFactory.
 * 
 * @CyAPI.Abstract.Class
 */
public abstract class AbstractCyniAlgorithm implements CyCyniAlgorithm {

	private final boolean supportsSelectedOnly;
	private final String humanName;
	private final String computerName;
	private final CyniCategory category;

	/**
	 * The Constructor.
	 * 
	 * @param computerName
	 *            a computer readable name used to construct property strings.
	 * @param humanName
	 *            a user visible name of the cyni algorithm.
	 * @param supportsSelectedOnly
	 *            indicates whether only selected rows/nodes will be used to apply the algorithm.
	 * @param category
	 *            the category is used to set the type of cyni algorithm
	 */
	public AbstractCyniAlgorithm(final String computerName,
			final String humanName, boolean supportsSelectedOnly, CyniCategory category) {
		this.computerName = computerName;
		this.humanName = humanName;
		this.supportsSelectedOnly = supportsSelectedOnly;
		this.category = category;
	}

	/**
	 * A computer readable name used to construct property strings.
	 * 
	 * @return a computer readable name used to construct property strings.
	 */
	public String getName() {
		return computerName;
	}
	
	/**
	 * Returns the category of the Cyni Algorithm.
	 * 
	 * @return The category for the Cyni Algorithm.
	 */
	public CyniCategory getCategory() {
		return category;
	}

	/**
	 * Used to get the user-visible name of the cyni algorithm.
	 * 
	 * @return the user-visible name of the cyni algorithm.
	 */
	public String toString() {
		return humanName;
	}


	/**
	 * Returns a new cyni context object. This method can be used to create
	 * custom configurations for cyni algorithms.
	 * @param table The table where to get the data.
	 * @param metricsManager The cyni metrics manager.
	 * @param tunableSetter The tunable setter to set the parameters if not GUI is used.
	 * @param mparams The map of each one of the parameters with the value of the parameter.
	 * @return a new cyni context object.
	 */
	@Override
	public Object createCyniContext(CyTable table, CyCyniMetricsManager metricsManager, TunableSetter tunableSetter,Map<String, Object> mparams) {
		return new Object();
	}
	
	
	@Override
	public boolean isReady(Object tunableContext) {
		if (tunableContext instanceof TunableValidator) {
			StringBuilder errors = new StringBuilder();
			return ((TunableValidator) tunableContext)
					.getValidationState(errors) == ValidationState.OK;
		}
		return true;
	}
	
	public boolean supportsSelectedOnly() {
		return supportsSelectedOnly;
	}

}
