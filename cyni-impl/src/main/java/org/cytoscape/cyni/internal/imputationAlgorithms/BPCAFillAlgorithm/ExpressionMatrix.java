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


public class ExpressionMatrix
{

    public ExpressionMatrix(double mat[][])
    {
        this(mat, null);
    }

    public ExpressionMatrix(double mat[][], String dlm)
    {
       
        setData(mat);
        this.dlm = dlm;
    }

    public void setData(double mat[][])
    {
        matrix = mat;
        numG = mat.length;
        numS = mat[0].length;
    }

    public int getNumG()
    {
        return numG;
    }

    public int getNumS()
    {
        return numS;
    }

    public String getDlm()
    {
        return dlm;
    }

    public double[] getGVector(int idx)
    {
        return matrix[idx];
    }

    public double[] getSVector(int idx)
    {
        double v[] = new double[numG];
        for(int i = 0; i < numG; i++)
            v[i] = matrix[i][idx];

        return v;
    }

    public double[][] getMatrix()
    {
        return matrix;
    }

    public double[][] getSubMatrix(int gIdx[], int sIdx[])
    {
        double out[][] = new double[gIdx.length][sIdx.length];
        for(int i = gIdx.length - 1; i >= 0; i--)
        {
            for(int j = sIdx.length - 1; j >= 0; j--)
                out[i][j] = matrix[gIdx[i]][sIdx[j]];

        }

        return out;
    }

    public double[][] setSubMatrix(double mat[][], int gIdx[], int sIdx[])
    {
        double out[][] = new double[gIdx.length][sIdx.length];
        for(int i = gIdx.length - 1; i >= 0; i--)
        {
            for(int j = sIdx.length - 1; j >= 0; j--)
                matrix[gIdx[i]][sIdx[j]] = mat[i][j];

        }

        return out;
    }

    private int numG;
    private int numS;
    private double matrix[][];
    private String dlm;
   
}
