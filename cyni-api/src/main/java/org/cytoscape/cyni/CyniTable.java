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

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;



public class CyniTable {
	private int nRows;
	private int nColumns;
	private Object data[][];
	private CyTable internalTable;
	private double colWeights[];
	private double rowWeights[];
	private Object rowLabels[];
	private Object columnLabels[];
	protected boolean transpose;
	protected boolean anyMissing;
	protected boolean ignoreMissing;
	protected boolean selectedOnly;
	private Class<?>[] indexToTypes;
	private ArrayList<String> stringValues;
	private Boolean colHasMissingValue[];
	private Boolean rowHasMissingValue[];
	private Map<Object,Integer> mapRowLabels;
	private Map<Object,Integer> mapColLabels;
	private Map<Integer,Integer> mapRowOrder;



	/**
	 * Create a Cyni table from the current selected table.  The Cyni Table will contain the columns
	 * of the CyTable that are defined in the attributes variable
	 *
	 * @param attribute name of the columns to store
	 * @param table {@link org.cytoscape.model.CyTable}
	 * @param transpose true if we are transposing this table 
	 * @param ignoreMissing true if the new table will not have any row with missing values
	 * @param selectedOnly true if the new table will only contains rows that correspond to selected nodes
	 */
	public CyniTable(  CyTable table, String[] attributes, boolean transpose, boolean ignoreMissing, boolean selectedOnly) {
		int i,j,index;
		Set tempSet = new HashSet<String>();
		
		mapRowLabels =  new HashMap<Object,Integer>();
		mapColLabels =  new HashMap<Object,Integer>();
		//It maps the external value for the index row with the internal value for the same index
		//so we can change the order of the rows by just changing the values in this map
		mapRowOrder =  new HashMap<Integer,Integer>();
		
		stringValues = new ArrayList<String>();
		this.transpose = transpose;
		this.ignoreMissing = ignoreMissing;
		this.selectedOnly = selectedOnly;
		anyMissing = false;
		nRows = 0;
		nColumns = 0;
		
		internalTable = table;
		
		indexToTypes = new Class[attributes.length];
		
		if (transpose) 
		{
			rowLabels = new Object[attributes.length];
			columnLabels = new Object[internalTable.getAllRows().size()];
		}
		else
		{
			columnLabels = new Object[attributes.length];
			rowLabels = new Object[internalTable.getAllRows().size()];
		}

		if (attributes.length >= 1 ) 
		{
			//Set the labels depending on the transpose parameter
			for ( i = 0; i < attributes.length; i++) 
			{
				if (transpose) {
					rowLabels[i] = attributes[i];
					mapRowLabels.put(attributes[i], i);
					nRows++;
				}
				else
				{
					columnLabels[i] = attributes[i];
					mapColLabels.put(attributes[i], i);
					nColumns++;
				}
			}
			i = 0;
			String primaryKey = internalTable.getPrimaryKey().getName();
			for ( CyRow row : internalTable.getAllRows() ) 
			{
				if (selectedOnly && row.isSet(CyNetwork.SELECTED))
				{
					if(!row.get(CyNetwork.SELECTED, Boolean.class))
					continue;
				}
				if (transpose) {
					columnLabels[i++] = row.getRaw(primaryKey);
					mapColLabels.put(row.getRaw(primaryKey), i-1);
					nColumns++;
				}
				else
				{
					rowLabels[i++] = row.getRaw(primaryKey);
					mapRowLabels.put(row.getRaw(primaryKey), i-1);
					nRows++;
				}
			}
			
			//If columns contain Strings, all possible values found in these columns are store for a future use
			for ( i = 0; i < attributes.length; i++) 
			{
				indexToTypes[i] = internalTable.getColumn(attributes[i]).getType();
				if(indexToTypes[i] == String.class)
				{
					stringValues.addAll(internalTable.getColumn(attributes[i]).getValues(String.class));
				}
			}
			
			if(stringValues.size()>0)
			{
				tempSet.addAll(stringValues);
				stringValues.clear();
				stringValues.addAll(tempSet);
			}
			
			//Initialize missing values variables and weights
			colHasMissingValue = new Boolean[nColumns];
			rowHasMissingValue = new Boolean[nRows];
			Arrays.fill(colHasMissingValue, false);
			Arrays.fill(rowHasMissingValue, false);
			setUniformWeights();
			
			//Fill in the internal table
			this.data = new Object[nRows][nColumns];
			for(i=0;i<nRows;i++)
			{
				mapRowOrder.put(i, i);
				for(j=0;j<nColumns;j++)
				{
					if (transpose) {
						data[i][j] = internalTable.getRow(columnLabels[j]).get((String)rowLabels[i], indexToTypes[i]);
					}
					else
					{
						data[i][j] = internalTable.getRow(rowLabels[i]).get((String)columnLabels[j], indexToTypes[j]);
					}
					if(data[i][j] == null)
					{
						anyMissing = true;
						rowHasMissingValue[i] = true;
						colHasMissingValue[j] = true;
					}
				}
			}
			//If ignoreMissing is true, rows with missing values will be eliminated but only if table is not transposed
			if(ignoreMissing && !transpose)
			{
				anyMissing = false;
				index = 0;
				for(i=0;i<nRows;i++)
				{
					if(!rowHasMissingValue[i])
					{
						 System.arraycopy(data[i], 0, data[index], 0,nColumns);
						 rowLabels[index] = rowLabels[i];
						 index++;
					}
				}
				if(nRows != index)
				{
					Arrays.fill(colHasMissingValue, false);
					Arrays.fill(rowHasMissingValue, false);
					nRows = index;
				}
			}
		} else {
			
			return;
		}
	}

	/**
	 * Create a cyni table from an existing one. 
	 *
	 * @param duplicate The cyni table to duplicate
	 */
	public CyniTable(CyniTable duplicate) {
		this.nRows = duplicate.nRows();
		this.nColumns = duplicate.nColumns();
		this.data = new Object[nRows][nColumns];
		this.colWeights = new double[nColumns];
		this.rowWeights = new double[nRows];
		this.columnLabels = new Object[nColumns];
		this.rowLabels = new Object[nRows];
		this.ignoreMissing = duplicate.ignoreMissing;
		this.selectedOnly = duplicate.selectedOnly;
		this.internalTable = duplicate.internalTable;
		this.indexToTypes = new Class[duplicate.indexToTypes.length];
		this.stringValues = new ArrayList<String>();
		this.rowHasMissingValue = new Boolean[nRows];
		this.colHasMissingValue = new Boolean[nColumns];

		
		this.transpose = duplicate.transpose;
		this.anyMissing = duplicate.anyMissing;

		for (int row = 0; row < nRows; row++) {
			rowWeights[row] = duplicate.getRowWeight(row);
			rowLabels[row] = duplicate.getRowLabel(row);
			rowHasMissingValue[row] = duplicate.rowHasMissingValue(row);
			
			for (int col = 0; col < nColumns; col++) {
				if (row == 0) {
					colWeights[col] = duplicate.getColWeight(col);
					columnLabels[col] = duplicate.getColLabel(col);
					colHasMissingValue[col] = duplicate.columnHasMissingValue(col);
					
				}
				if (duplicate.getValue(row, col) != null)
					this.data[row][col] = duplicate.getValue(row, col);
			}
			mapRowOrder.put(row, duplicate.mapRowOrder.get(row));
		}
		for(Object label : duplicate.mapRowLabels.keySet())
			mapRowLabels.put(label,  duplicate.mapRowLabels.get(label));
		for(Object label : duplicate.mapColLabels.keySet())
			mapColLabels.put(label,  duplicate.mapColLabels.get(label));
		for (int i = 0; i < duplicate.indexToTypes.length; i++) {
			indexToTypes[i] = duplicate.indexToTypes[i];
		}
		for(String temp : duplicate.stringValues)
		{
			stringValues.add(temp);
		}
	}

	/**
	 * Create an empty Cyni Table with the specified number of columns and rows.
	 *
	 * @param rows number of rows
	 * @param cols number of cols
	 */
	public CyniTable(int rows, int cols) {
		this.nRows = rows;
		this.nColumns = cols;
		this.data = new Double[rows][cols];
		this.colWeights = new double[cols];
		this.rowWeights = new double[rows];
		this.columnLabels = new Object[cols];
		this.rowLabels = new Object[rows];
		this.transpose = false;
		this.ignoreMissing = false;
		this.anyMissing = false;
		this.selectedOnly = false;
		this.internalTable = null;
		this.indexToTypes = null;
		this.stringValues = new ArrayList<String>();
		this.mapRowLabels =  new HashMap<Object,Integer>();
		this.mapColLabels =  new HashMap<Object,Integer>();
		this.mapRowOrder =  new HashMap<Integer,Integer>();
	}

	/**
	 * Returns if the table is transposed 
	 * @return True if the table is transposed
	 */
	public boolean isTransposed() { return this.transpose; }
	
	/**
	 * Returns if the table has any missing value
	 * @return True if the table has any missing value
	 */
	public boolean hasAnyMissingValue() { return this.anyMissing; }
	
	/**
	 * Returns the number of rows in the table.
	 * @return The number of rows in the table.
	 */
	public int nRows() { return this.nRows; }

	/**
	 * Returns the number of columns in the table.
	 * @return The number of columns in the table
	 */
	public int nColumns() { return this.nColumns; }

	/**
	 * Returns the value for the specified row and column 
	 * @param row  The number of the row.
	 * @param column  The number of the column.
	 * @return The value for the column and row
	 */
	public Object getValue(int row, int column) {
		if(row >= nRows || column >= nColumns)
			return null;
		return data[mapRowOrder.get(row)][column];
	}
	
	/**
	 * Returns the list of all possible strings values in the table
	 * @return The list of all possible strings values in the table
	 */
	public ArrayList<String> getAttributeStringValues() {
		return stringValues;
	}

	/**
	 * Returns the double value for the specified row and column combination
	 * @param row  The number of the row.
	 * @param column  The number of the column.
	 * @return The double value, if the content is not a number, it returns a not a number value
	 */
	public double doubleValue(int row, int column) {
		double result;
		int typeIndex;
		result = Double.NaN;
		
		if (transpose) {
			typeIndex = mapRowOrder.get(row);
		}
		else
		{
			typeIndex = column;
		}
		
		if(row >= nRows || column >= nColumns)
			return result;
		
		if(indexToTypes[typeIndex] == Double.class || indexToTypes[typeIndex] == Float.class)
		{
			result = (Double) data[mapRowOrder.get(row)][column];
		}
		else
		{
			if(indexToTypes[typeIndex] == Integer.class)
			{
				Integer intVal = (Integer) data[mapRowOrder.get(row)][column];
				result = Double.valueOf(intVal.doubleValue());
			}
				
		}
			
		return result;
	}
	
	/**
	 * Returns the integer value for the specified row and column combination.
	 * @param row  The number of the row.
	 * @param column  The number of the column.
	 * @return The integer value, if the content is not a number, it returns a zero value.
	 */
	public int integerValue(int row, int column) {
		int result;
		int typeIndex;
		result = 0;
		
		if (transpose) {
			typeIndex = mapRowOrder.get(row);
		}
		else
		{
			typeIndex = column;
		}
		
		if(row >= nRows || column >= nColumns)
			return result;
		
		if(indexToTypes[typeIndex] == Double.class || indexToTypes[typeIndex] == Float.class)
		{
			Double val = (Double) data[mapRowOrder.get(row)][column];
			result = val.intValue();
		}
		else
		{
			if(indexToTypes[typeIndex] == Integer.class)
			{
				result = (Integer) data[mapRowOrder.get(row)][column];
			}
				
		}
			
		return result;
	}
	
	/**
	 * Returns the string value for the specified row and column combination.
	 * @param row  The number of the row.
	 * @param column  The number of the column.
	 * @return The string value, if the content is not a string, it returns an empty string.
	 */
	public String stringValue(int row, int column) {
		String result;
		int typeIndex;
		result = "";
		
		if(row >= nRows || column >= nColumns)
			return result;
		
		if (transpose) {
			typeIndex = mapRowOrder.get(row);
		}
		else
		{
			typeIndex = column;
		}
		
		if(indexToTypes[typeIndex] == String.class )
		{
			result = (String) data[mapRowOrder.get(row)][column];
		}
			
		return result;
	}
	

	/**
	 * Returns if the content of the table for the specified row and column combination has a real value.
	 * @param row  The number of the row.
	 * @param column  The number of the column.
	 * @return False if the content of the table for that cell is null or it doesn't exist 
	 */
	public boolean hasValue(int row, int column) {
		boolean result;
		result = true;
		if(row >= nRows || column >= nColumns || data[mapRowOrder.get(row)][column]==null)
			result = false;
		
		return result;
	}
	
	/**
	 * Tells if the specified row has a missing value.
	 * @param row  The number of the row.
	 * @return False if the content of that row is null for one of its values or the specified row doesn't exist
	 */
	public boolean rowHasMissingValue(int row) {
	
		if(row >= nRows )
			return true;
		
		return rowHasMissingValue[mapRowOrder.get(row)];
	}
	
	/**
	 * Tells if the specified column has a missing value.
	 * @param column  The number of the column.
	 * @return False if the content of that column is null for one of its values or the specified column doesn't exist
	 */
	public boolean columnHasMissingValue( int column) {
		
		if( column >= nColumns)
			return true;
		
		return colHasMissingValue[column];
	}
	
	/**
	 * Returns the type of for the specified row and column combination.
	 * @param row  The number of the row.
	 * @param column  The number of the column.
	 * @return The type of that table content
	 */
	public Class getType(int row, int column) {
		int typeIndex;
		
		if (transpose) {
			typeIndex = mapRowOrder.get(row);
		}
		else
		{
			typeIndex = column;
		}
		
		return indexToTypes[typeIndex];
	}

	/**
	 * Set the weights to a constant value.
	 */
	public void setUniformWeights() {
		if (colWeights == null || rowWeights == null) {
			colWeights = new double[nColumns];
			rowWeights = new double[nRows];
		}
		Arrays.fill(this.colWeights,1.0);
		Arrays.fill(this.rowWeights,1.0);
	}

	/**
	 * Returns the array of weights for the rows. 
	 * @return The array of weights for the rows.
	 */
	public double[] getRowWeights() {
		return this.rowWeights;
	}

	/**
	 * Returns the weight for the specified row. 
	 * @param row  The number of the row.
	 * @return The weight for the specified row.
	 */
	public double getRowWeight(int row) {
		return this.rowWeights[mapRowOrder.get(row)];
	}

	/**
	 * Returns the array of weights for the columns. 
	 * @return The array of weights for the columns.
	 */
	public double[] getColWeights() {
		return this.colWeights;
	}

	/**
	 * Returns the weight for the specified column. 
	 * @param col  The number of the column.
	 * @return The weight for the specified column.
	 */
	public double getColWeight(int col) {
		return this.colWeights[col];
	}

	/**
	 * Set the weight for the specified row.
	 * @param row  The number of the row.
	 * @param value  The weight
	 */
	public void setRowWeight(int row, double value) {
		if (rowWeights == null) {
			rowWeights = new double[nRows];
		}
		rowWeights[mapRowOrder.get(row)] = value;
	}

	/**
	 * Set the weight for the specified column.
	 * @param col  The number of the column.
	 * @param value  The weight
	 */
	public void setColWeight(int col, double value) {
		if (colWeights == null) {
			colWeights = new double[nColumns];
		}
		colWeights[col] = value;
	}

	/**
	 * Returns the array with all column labels.
	 * @return The array with all column labels.
	 */
	public Object[] getColLabels() {
		return this.columnLabels;
	}

	/**
	 * Returns the label for the specified column.
	 * @param col  The number of the column.
	 * @return The label for the specified column.
	 */
	public Object getColLabel(int col) {
		return this.columnLabels[col];
	}

	/**
	 * Set the label for the specified column. 
	 * @param col  The number of the column.
	 * @param label  The label.
	 */
	public void setColLabel(int col, Object label) {
		if(col<nColumns)
		{
			this.columnLabels[col] = label;
			mapColLabels.put(label, col);
		}
	}
	
	/**
	 * Returns the index column for the specified label.
	 * @param label  The label object.
	 * @return The index column for the specified label or 0 if the label does not exist.
	 */
	public Integer getColIndex(Object label) {
		if(mapColLabels.containsKey(label))
			return this.mapColLabels.get(label);
		else
			return 0;
	}

	/**
	 * Returns the array with all row labels.
	 * @return The array with all row labels.
	 */
	public Object[] getRowLabels() {
		return this.rowLabels;
	}

	/**
	 * Returns the label for the specified row.
	 * @param row  The number of the row.
	 * @return The label for the specified row.
	 */
	public Object getRowLabel(int row) {
		return this.rowLabels[mapRowOrder.get(row)];
	}
	
	/**
	 * Returns the index row for the specified label.
	 * @param label  The label object.
	 * @return The index row for the specified label or 0 if the label does not exist.
	 */
	public Integer getRowIndex(Object label) {
		if(mapRowLabels.containsKey(label))
			return this.mapRowLabels.get(label);
		else
			return 0;
	}

	/**
	 * Set the label for the specified row. 
	 * @param row  The number of the row.
	 * @param label  The label.
	 */
	public void setRowLabel(int row, Object label) {
		if(row<nRows)
		{
			this.rowLabels[mapRowOrder.get(row)] = label;
			mapRowLabels.put(label, row);
		}
	}
	
	/**
	 * Changes the order of the rows in the table randomly
	 */
	public void changeOrderRowsToRandom() {
		
		Random random = new Random();
		int temp,i,pos;
		for (i = 0; i < nRows; i++) {
			pos = Math.abs(random.nextInt()) % nRows;
			temp = mapRowOrder.get(i);
			mapRowOrder.put(i, mapRowOrder.get(pos));
			mapRowOrder.put(pos, temp);	
			mapRowLabels.put(getRowLabel(i),i);
		}
	}
	
	/**
	 * Changes the order to the initial internal state
	 */
	public void resetOrderRows() {
		int i;
		for (i = 0; i < nRows; i++) {
			mapRowOrder.put(i, i);
			mapRowLabels.put(getRowLabel(i),i);
		}
	}
	
	/**
	 * Changes the order of the rows according to the values of a selected column
	 * The method supposes that the first value of the input list corresponds to the first 
	 * element of the column and so on.
	 * If the list of column values has different size than the number of rows of the table, 
	 * the change of order will not proceed.
	 * @param colValues  The list of values for the selected column
	 */
	public void changeOrderRowsByColumnValuesOrder(List<Object> colValues) {
		int i;
		 Map<Object,Integer> mySortedMap = new TreeMap<Object,Integer>();
		 if(colValues.size() != nRows)
			 return;
		 
		 for(i=0;i<nRows;i++)
		 {
			 mySortedMap.put(colValues.get(i), i);
		 }
		 i=0;
		 for(Integer pos : mySortedMap.values())
		 {
			 mapRowOrder.put(i, pos);
			 mapRowLabels.put(getRowLabel(i),i);
			 i++;
		 }
	}

	/**
	 * Returns an array with a number for each position representing the rank of the value in the whole row
	 * @param row  The number of the row.
	 * @return The array with the ranks
	 */
	public double[] getRank(int row) {
		// Get the masked row
		double[] tData = new double[nColumns];
		int nVals = 0;
		for (int column = 0; column < nColumns; column++) {
			if (hasValue(row,column))
			{
				if(getType(row,column) == Integer.class)
				{
					tData[nVals++] = ((Integer) data[mapRowOrder.get(row)][column]).doubleValue();
				}
				else
				{
					tData[nVals++] = (Double) data[mapRowOrder.get(row)][column];
				}
			}
		}

		if (nVals == 0)
			return null;

		// Sort the data
		Integer index[] = indexSort(tData,nVals);

		// Build a rank table
		double[] rank = new double[nVals];
		for (int i = 0; i < nVals; i++) rank[index[i].intValue()] = i;

		// Fix for equal ranks
		int i = 0;
		while (i < nVals) {
			int m = 0;
			double value = tData[index[i].intValue()];
			int j = i+1;
			while (j < nVals && tData[index[j].intValue()] == value) j++;
			m = j - i; // Number of equal ranks found
			value = rank[index[i].intValue()] + (m-1)/2.0;
			for (j = i; j < i+m; j++) rank[index[j].intValue()] = value;
			i += m;
		}
		return rank;
	}


	private Integer[] indexSort(double[] tData, int nVals) {
		Integer[] index = new Integer[nVals];
		for (int i = 0; i < nVals; i++) index[i] = new Integer(i);
		IndexComparator iCompare = new IndexComparator(tData);
		Arrays.sort(index, iCompare);
		return index;
	}
	
	private class IndexComparator implements Comparator<Integer> {
		double[] data = null;


		public IndexComparator(double[] data) { this.data = data; }

		public int compare(Integer o1, Integer o2) {
			if (data != null) {
				if (data[o1.intValue()] < data[o2.intValue()]) return -1;
				if (data[o1.intValue()] > data[o2.intValue()]) return 1;
				return 0;
			} 
			return 0;
		}

		boolean equals() { return false; };
	}

	
}
