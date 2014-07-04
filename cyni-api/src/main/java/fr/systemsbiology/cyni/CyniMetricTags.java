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

/**
 * An enum that captures the tags of the Cyni Metrics available
 * @CyAPI.Enum.Class
 */
public enum CyniMetricTags {
	/** 
	* Cyni Metric accepts numbers as input
	*/ 
	INPUT_NUMBERS, 
	
	/** 
	* Cyni Metric accepts strings as input
	*/ 
	INPUT_STRINGS , 
	
	/** 
	* Cyni Metric decomposed as the sum or product of the score of each individual node
	*/ 
	LOCAL_METRIC_SCORE ,
	
	/** 
	* Cyni Metric implements a information theory related metric
	*/ 
	INFORMATION_THEORY ,
	
	/** 
	* Cyni Metric works with continuous values
	*/ 
	CONTINUOUS_VALUES ,
	
	/** 
	* Cyni Metric works with discrete values
	*/ 
	DISCRETE_VALUES ,
	
	/** 
	* Cyni Metric to  be used on bayesian methods
	*/ 
	BAYESIAN_METRIC ,
	
	/** 
	* Cyni Metric that produces different values depending on the order, so metric(X,Y) might be different of metric(Y,X)
	*/ 
	DIRECTIONAL_METRIC ,
	
	/** 
	* Cyni Metric based on searching for any statistical relationship between two sets of data. All metrics with this tag
	* will be available through Basic Correlation Inference algorithm
	*/ 
	CORRELATION_METRIC ,
	
	/** 
	* All metrics with this tag will be available through K2 Bayesian Inference algorithm
	*/ 
	K2_METRIC ,
	
	/** 
	* All metrics with this tag will be available through Hill Climbing Bayesian Inference algorithm
	*/ 
	HILL_CLIMBING_METRIC ,
	
	/** 
	* Cyni Metric that produces a value that is significant if it is low. By default metrics output values are significant
	* when the value is high. However, metrics containing this tag produce output values that are more significant when the output
	* value is the lowest possible.
	*/ 
	LOW_METRIC ;
	
	
	
}
