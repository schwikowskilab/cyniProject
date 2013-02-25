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
package org.cytoscape.cyni.internal.imputationAlgorithms.BPCAFillAlgorithm;



import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.cyni.*;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyTable;




/**
 * The BasicInduction provides a very simple Induction, suitable as
 * the default Induction for Cytoscape data readers.
 */
public class BPCAImputationTask extends AbstractCyniTask {
	private static  double missValue;
	private static  double missValueDown;
	private static  double missValueUp;
	private final CyTable mytable;
	private String chooser;
	private boolean interval;
	
	private static ArrayList<ArrayList<Integer>> listPositions; 
	private static String[] indexToNames;
	private static Object[] rowIndexToPrimaryKey;
	
    private int epoch;
    private int maxEpoch;
    private BPCAFillAlgorithm bpa;
    private boolean finishFlag;
    private double previousTau;
    private double convergenceThreshold;
	

	/**
	 * Creates a new BasicInduction object.
	 */
	public BPCAImputationTask(final String name, final BPCAImputationContext context, CyTable selectedTable)
	{
		super(name,context,null,null,null,null,null,null,null);
		missValue = context.missValue;
		missValueDown = context.missValueDown;
		missValueUp = context.missValueUp;
		chooser = context.chooser.getSelectedValue();
		int i = 0;
		this.mytable = selectedTable;
		rowIndexToPrimaryKey = new Object[selectedTable.getAllRows().size()]; 
		
		indexToNames = new String[selectedTable.getColumns().size()];
		for (final CyColumn column : selectedTable.getColumns())
		{
			indexToNames[i] = column.getName();
			i++;
		}
		if(chooser.matches("By a single value"))
			interval = false;
		else
			interval = true;
		String primaryKey = selectedTable.getPrimaryKey().getName();
		i = 0;
		for ( CyRow row : selectedTable.getAllRows() ) 
			rowIndexToPrimaryKey[i++] = row.getRaw(primaryKey);
		
		listPositions = new ArrayList<ArrayList<Integer>>();
		
	}

	/**
	 *  Perform actual Induction task.
	 *  This creates the default square Induction.
	 */
	@Override
	final protected void doCyniTask(final TaskMonitor taskMonitor) {
		
		Double progress = 0.0d;
		double data[][];
		Double step;
		MissingValueHandler missing = new MissingValueHandler();
		
        maxEpoch = 200;
        finishFlag = false;
        convergenceThreshold = 1.0000000000000001E-05D;
        epoch = 0;
        finishFlag = false;
        previousTau = 0.0D;
        
        step = 1.0 / maxEpoch;
        
        taskMonitor.setStatusMessage("Estimating missing data...");
		taskMonitor.setProgress(progress);
		
		if(interval)
		{
			missing.setMissingValue(missValueUp, missValueDown, interval);
			missValue = (missValueUp + missValueDown)/2;
		}
		else
			missing.setMissingValue(missValue, missValue, interval);
		
		data = loadData(mytable);
	
		ExpressionMatrix em = new ExpressionMatrix(data);
		
		
		bpa = new BPCAFillAlgorithm(em.getMatrix(), missing);
		
		while(finishFlag == false)
		{
			doStep();
			progress = progress + step;
			taskMonitor.setProgress(progress);
		}
		
		saveData(mytable,getMatrixResult());
		taskMonitor.setProgress(1.0d);
		taskMonitor.setProgress(1.0d);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "Number of missing entries: " + listPositions.size() + 
						"\nNumber of estimated missing entries: " + listPositions.size(), "Results", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	public String doStep()
	{
	    epoch++;
	    bpa.doStep();
	    double tau = bpa.getTau();
	    double dtau = Math.abs(tau - previousTau);
	    if(dtau < convergenceThreshold)
	        finishFlag = true;
	    if(epoch >= maxEpoch)
	        finishFlag = true;
	    previousTau = tau;
	    return "epoch=" + epoch + "/" + maxEpoch + ", dtau=" + dtau;
	}
	
	public double[][] getMatrixResult()
    {
        return bpa.yest;
    }

	
	 public static double[][] loadData( CyTable table)
	 {
		 double dataMatrix[][];
		 double valDouble;
		 Object value;
		 int nCols = 0;
		 int nRows;
		 int rows = 0;
		 int cols = 0;
		 int colsMatrix = 0;
		 ArrayList<Integer> colPos ;
		 
		 nRows = table.getRowCount();
		 
		 for (final CyColumn column : table.getColumns())
		 {
			 if (column.getType() == Double.class || column.getType() == Float.class || column.getType() == Integer.class) {
				 nCols++;
				}
			 
		 }
		 
		 dataMatrix = new double [nRows][nCols];
		 
		 for (final CyColumn column : table.getColumns())
		 {
			 
			 if (column.getType() == Double.class || column.getType() == Float.class || column.getType() == Integer.class) 
			 {
				 
				 //values = column.getValues(column.getType());
				
				 rows = 0;
				 //for (rows = 0;rows < nRows; rows++ )
				 for ( CyRow row : table.getAllRows() ) 
				 {
					 valDouble = 0;
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
					 if (Math.abs(valDouble - missValue) < 1 || value == null)
					 {
						 colPos = new ArrayList<Integer>();
						 colPos.add(rows);
						 colPos.add(cols);
						 colPos.add(colsMatrix);
						 listPositions.add(colPos);
						 valDouble = missValue;
						 //colPos.clear();
					 }
					 dataMatrix[rows][colsMatrix] = valDouble;
					 rows++;
				 }
				 colsMatrix++;
			 }
			 cols++;
			 
		 }
		 
		 System.out.println("positions: " + listPositions.size() );
		 
		 return dataMatrix;
		 
	 }
	 
	 public static void saveData( CyTable table, double result[][])
	 {
		 int row,col, colMatrix,temp;
		 CyRow tableRow;
		 		 
		 for(ArrayList<Integer> list : listPositions)
		 {
			 row = list.get(0);
			 col = list.get(1);
			 colMatrix = list.get(2);
			 
			 System.out.println("positions, row: " + row + " col: " + col + " colMatrix: " + colMatrix );
			 System.out.println("Column Name: " + indexToNames[col]);
			 tableRow = table.getRow(rowIndexToPrimaryKey[row]);
			 System.out.println("row value first : " + tableRow.get(indexToNames[col], Double.class));
			 if(table.getColumn(indexToNames[col]).getType() == Integer.class)
			 {
				 temp = (int) result[row][colMatrix];
				 tableRow.set(indexToNames[col],temp );
			 }
			 else
			 {
				 tableRow.set(indexToNames[col], result[row][colMatrix]);
				 System.out.println("row value: " + tableRow.get(indexToNames[col], Double.class));
			 }
			 System.out.println("value: " + result[row][colMatrix]);
		 }
		 
	 }
	

}
