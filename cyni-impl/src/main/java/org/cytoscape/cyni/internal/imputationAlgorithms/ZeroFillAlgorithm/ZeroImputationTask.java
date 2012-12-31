/*
  File: ZeroImputationTask.java

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
		interval = context.interval;
		
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