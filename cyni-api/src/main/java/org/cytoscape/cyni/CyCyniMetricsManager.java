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

import java.util.*;

/**
 * This class provides access to the available Cyni Metrics.
 * 
 * @CyAPI.Api.Interface
 */
public interface CyCyniMetricsManager {

	/** The name of the default Cyni Metric property. */
	String DEFAULT_Cyni_Metric_PROPERTY_NAME = "CyniMetrics.default";

	/**
	 * Returns a Cyni Metric of the specified name and null if no
	 * metric exists with that name.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @return a Cyni metric of the specified name and null if no
	 *         metric exists with that name.
	 */
	CyCyniMetric getCyniMetric(String name);

	/**
	 * Returns a list of all available Cyni Metrics.
	 * 
	 * @return a list of all available Cyni Metrics.
	 */
	ArrayList<CyCyniMetric> getAllCyniMetrics();
	
	/**
	 * Returns the list of metrics that contains at least the same types that are passed as input parameter.
	 * 
	 * @param types The list of types that the metrics need to support
	 * @return the list of metrics.
	 */
	ArrayList<CyCyniMetric> getAllCyniMetricsWithType(List<String> types);

	/**
	 * Returns the default Cyni Metric. The default metric name can be
	 * specified using the DEFAULT_Cyni_Metric_PROPERTY_NAME property.
	 * 
	 * @return the default metric.
	 */
	CyCyniMetric getDefaultCyniMetric();
	
	/**
	 * Sets the default Cyni Metric. The default metric name can be
	 * specified using the DEFAULT_Cyni_Metric_PROPERTY_NAME property.
	 * 
	 * @param the name of the metric that will be the default one.
	 */
	void setDefaultCyniMetric(String name);

}
