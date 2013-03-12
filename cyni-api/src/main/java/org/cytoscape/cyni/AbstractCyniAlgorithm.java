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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
			final ValidationState validationState = ((TunableValidator) tunableContext).getValidationState(errors);
			if(validationState != ValidationState.OK)
			{
				JOptionPane.showMessageDialog(new JFrame(), errors.toString(),
					      "Input Validation Problem",
					      JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}
	
	public boolean supportsSelectedOnly() {
		return supportsSelectedOnly;
	}

}
