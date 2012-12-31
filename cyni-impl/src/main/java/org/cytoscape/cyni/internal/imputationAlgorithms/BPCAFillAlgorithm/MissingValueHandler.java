/*
  File:  MissingValueHandler.java

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

package org.cytoscape.cyni.internal.imputationAlgorithms.BPCAFillAlgorithm;


public class MissingValueHandler
{

    public MissingValueHandler()
    {
    }

    public void setMissingValue(double up, double down, boolean interval)
    {
        missingValue = up;
        missingValueUp = up;
        missingValueDown = down;
        this.interval = interval;
    }

    public static double getMissingValue()
    {
        return missingValue;
    }

    public  boolean isMissing(double data)
    {
    	boolean result = false;
   
    	if(interval)
		{
			 if ((data >= missingValueDown &&  data <= missingValueUp) )
				 result = true;
		}
    	else
    	{
    		 if(Math.abs(data - missingValue) < threshold)
    			 result = true;
    	}
        return result;
    }

    public  boolean[][] isMissing(int N, int M, double data[][])
    {
        boolean miss[][] = new boolean[N][M];
        for(int i = N - 1; i >= 0; i--)
        {
            for(int j = M - 1; j >= 0; j--)
                if(isMissing(data[i][j]))
                    miss[i][j] = true;
                else
                    miss[i][j] = false;

        }

        return miss;
    }

    public  boolean[][] isMissing(double data[][])
    {
        int N = data.length;
        int M = data[0].length;
        return isMissing(N, M, data);
    }

    public void isMissing(int N, int M, double data[][], boolean miss[][])
    {
        for(int i = N - 1; i >= 0; i--)
        {
            for(int j = M - 1; j >= 0; j--)
                if(isMissing(data[i][j]))
                    miss[i][j] = true;
                else
                    miss[i][j] = false;

        }

    }

    public  void isMissing(double data[][], boolean miss[][])
    {
        int N = data.length;
        int M = data[0].length;
        isMissing(N, M, data, miss);
    }

    public  void isMissing(int N, int M, double data[][], boolean miss[][], int nmiss[])
    {
        for(int i = N - 1; i >= 0; i--)
        {
            nmiss[i] = 0;
            for(int j = M - 1; j >= 0; j--)
                if(isMissing(data[i][j]))
                {
                    miss[i][j] = true;
                    nmiss[i]++;
                } else
                {
                    miss[i][j] = false;
                }

        }

    }

    public  boolean[] isMissing(int N, double data[])
    {
        boolean miss[] = new boolean[N];
        for(int i = N - 1; i >= 0; i--)
            if(isMissing(data[i]))
                miss[i] = true;
            else
                miss[i] = false;

        return miss;
    }

    public  boolean[] isMissing(double data[])
    {
        int N = data.length;
        return isMissing(N, data);
    }

    public  void isMissing(int N, double data[], boolean miss[])
    {
        for(int i = N - 1; i >= 0; i--)
            if(isMissing(data[i]))
                miss[i] = true;
            else
                miss[i] = false;

    }

    public  void isMissing(double data[], boolean miss[])
    {
        int N = data.length;
        isMissing(N, data, miss);
    }

    public  int numMiss(double data[], boolean miss[])
    {
        int N = data.length;
        int nmiss = 0;
        for(int i = N - 1; i >= 0; i--)
            if(isMissing(data[i]))
            {
                miss[i] = true;
                nmiss++;
            } else
            {
                miss[i] = false;
            }

        return nmiss;
    }

    public  int[] numMiss(double data[][])
    {
        int N = data.length;
        int M = data[0].length;
        int nm[] = new int[N];
        for(int i = N - 1; i >= 0; i--)
        {
            nm[i] = 0;
            for(int j = M - 1; j >= 0; j--)
                if(isMissing(data[i][j]))
                    nm[i]++;

        }

        return nm;
    }

    public  double[][] removeMissing(double data[][])
    {
        int N = data.length;
        int M = data[0].length;
        int nm[] = numMiss(data);
        int nmd = 0;
        for(int i = N - 1; i >= 0; i--)
            if(nm[i] == 0)
                nmd++;

        double out[][] = new double[nmd][];
        nmd = 0;
        for(int i = N - 1; i >= 0; i--)
            if(nm[i] == 0)
            {
                out[nmd] = data[i];
                nmd++;
            }

        return out;
    }

    public static double mse(double target[][], double guess[][], boolean isMissing[][])
    {
        double mse = 0.0D;
        int N = target.length;
        int M = target[0].length;
        int nMiss = 0;
        for(int i = N - 1; i >= 0; i--)
        {
            for(int j = M - 1; j >= 0; j--)
                if(isMissing[i][j])
                {
                    double err = target[i][j] - guess[i][j];
                    mse += err * err;
                    nMiss++;
                }

        }

        if(nMiss == 0)
            return 0.0D;
        else
            return mse / (double)nMiss;
    }

    public static double nrmse(double target[][], double guess[][], boolean isMissing[][])
    {
        double mse = 0.0D;
        int N = target.length;
        int M = target[0].length;
        int nMiss = 0;
        double t1 = 0.0D;
        double t2 = 0.0D;
        for(int i = N - 1; i >= 0; i--)
        {
            for(int j = M - 1; j >= 0; j--)
                if(isMissing[i][j])
                {
                    double t = target[i][j];
                    t1 += t;
                    t2 += t * t;
                    double err = t - guess[i][j];
                    mse += err * err;
                    nMiss++;
                }

        }

        if(nMiss == 0)
        {
            return 0.0D;
        } else
        {
            double std = Math.sqrt(t2 / (double)nMiss - (t1 / (double)nMiss) * (t1 / (double)nMiss));
            return Math.sqrt(mse / (double)nMiss) / std;
        }
    }

    private static double missingValue = 999D;
    private static double missingValueUp = 999D;
    private static double missingValueDown = 999D;
    private boolean interval = false;
    private static double threshold = 1.0D;

}
