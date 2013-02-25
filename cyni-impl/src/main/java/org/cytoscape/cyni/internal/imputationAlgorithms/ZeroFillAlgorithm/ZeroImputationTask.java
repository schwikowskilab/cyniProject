/*
 * #%L
 * Cyni Implementation (cyni-impl)
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
package org.cytoscape.cyni.internal.imputationAlgorithms.ZeroFillAlgorithm;



import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.cyni.*;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyTable;




/**
 * The BasicInduction provides a very simple Induction, suitable as
 * the default Induction for Cytoscape data readers.
 */
public class ZeroImputationTask extends AbstractCyniTask {
	private static  double missValue;
	private static  double missValueDown;
	private static  double missValueUp;
	private int founds = 0;
	private final CyTable mytable;
	private boolean interval;
	
	
	

	/**
	 * Creates a new BasicInduction object.
	 */
	public ZeroImputationTask(final String name, final ZeroImputationContext context, CyTable selectedTable)
	{
		super(name, context, null, null,null,null,null,null,null);
		missValue = context.missValue;
		missValueDown = context.missValueDown;
		missValueUp = context.missValueUp;
		if(context.chooser.getSelectedValue().matches("By a single value"))
			interval = false;
		else
			interval = true;
		
		this.mytable = selectedTable;
		
		
	}

	/**
	 *  Perform actual Induction task.
	 *  This creates the default square Induction.
	 */
	@Override
	final protected void doCyniTask(final TaskMonitor taskMonitor) {
		
		Double progress = 0.0d;
		double  valDouble=0;
		Double step;
		Object value;
		founds = 0;
   
        step = 1.0 /  mytable.getColumns().size();
        
        taskMonitor.setStatusMessage("Estimating missing data...");
		taskMonitor.setProgress(progress);
		
	
		for (final CyColumn column : mytable.getColumns())
		 {
			 
			 if (column.getType() == Double.class || column.getType() == Float.class || column.getType() == Integer.class) 
			 {
				 
				 
				 for ( CyRow row : mytable.getAllRows() ) 
				 {
					 
					 value = row.get(column.getName(), column.getType());
					 if(value != null)
					 {
						 if (column.getType() == Integer.class)
						 {
							 Integer temp = (Integer) value;//values.get(rows);
							 valDouble = Double.valueOf(temp.doubleValue());
						 }
						 else
						 {
							 valDouble =  (Double) value;//(Double) values.get(rows);
						 }
					 }
					 if(interval)
					 {
						 if ((valDouble >= missValueDown &&  valDouble <= missValueUp) || value == null)
						 {
							 if (column.getType() == Double.class || column.getType() == Float.class)
							 {
								 row.set(column.getName(), 0.0);
							 }
							 else
							 {
								 row.set(column.getName(), 0);
							 }
							 founds++;
						 }
						 
					 }
					 else
					 {
						 if (Math.abs(valDouble - missValue) < 1 || value == null)
						 {
							 if (column.getType() == Double.class || column.getType() == Float.class)
							 {
								 row.set(column.getName(), 0.0);
							 }
							 else
							 {
								 row.set(column.getName(), 0);
							 }
							 founds++;
						 }
					 }
					 
				 }
				
			 }
			
			 progress = progress + step;
			 taskMonitor.setProgress(progress);
		 }
		
		
		
		taskMonitor.setProgress(1.0d);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "Number of missing entries: " + founds + 
						"\nNumber of estimated missing entries: " + founds, "Results", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	
}