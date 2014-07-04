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


package fr.systemsbiology.cyni.internal.imputationAlgorithms.BPCAFillAlgorithm;

import java.io.*;


// Referenced classes of package jp.ac.naist.dynamix.mpca:
//            SpecialFunctions

public class BPCAUnit implements Serializable
{

    public double getGamma()
    {
        return gamma;
    }

    public double getNumAssignedData()
    {
        return gamma;
    }

    public double[] getMu()
    {
        return mu;
    }

    public double[][] getW()
    {
        return W;
    }

    public double getTau()
    {
        return tau;
    }

    public int getHiddenDim()
    {
        return fDim;
    }

    public void setGamma(double d)
    {
        gamma = d;
    }

    public void setTau(double d)
    {
        tau = d;
    }

    public void setID(int i)
    {
        id = i;
    }

    public void setPrior(BPCAUnit b)
    {
        gtau0 = b.gtau0;
        btau0 = b.btau0;
        gmu0 = b.gmu0;
        galpha0 = b.galpha0;
        balpha0 = b.balpha0;
    }

    public void setMu(double newMu[])
    {
        for(int i = 0; i < iDim; i++)
            mu[i] = newMu[i];

    }

    public BPCAUnit getClone()
    {
        BPCAUnit u = new BPCAUnit(id, iDim, fDim);
        u.initMatrices();
        u.tau = tau;
        u.lntau = lntau;
        u.galpha0 = galpha0;
        u.balpha0 = balpha0;
        u.gtau0 = gtau0;
        u.btau0 = btau0;
        u.gmu0 = gmu0;
        u.gamma = gamma;
        u.logdetRx = logdetRx;
        for(int i = fDim - 1; i >= 0; i--)
        {
            u.alpha[i] = alpha[i];
            for(int j = fDim - 1; j >= 0; j--)
            {
                u.Rx[i][j] = Rx[i][j];
                u.invRx[i][j] = invRx[i][j];
                u.invDw[i][j] = invDw[i][j];
            }

            for(int j = iDim - 1; j >= 0; j--)
                u.W[j][i] = W[j][i];

        }

        for(int j = iDim - 1; j >= 0; j--)
            u.mu[j] = mu[j];

        return u;
    }

    public String toString()
    {
        return "";
    }

    public BPCAUnit(int id, int iDim, int fDim)
    {
        DEBUG = false;
        galpha0 = 0.001D;
        balpha0 = 1.0D;
        gtau0 = 0.5D;
        btau0 = 1.0D;
        gmu0 = Math.exp(-1D);
        min_tau = 1E-10D;
        max_tau = 10000000000D;
        ACTIVE_FEATURE_THRESHOLD = 1.0000000000000001E-05D;
        this.iDim = iDim;
        this.fDim = fDim;
        this.id = id;
        initMatrices();
    }

    public void initMatrices()
    {
        W = new double[iDim][fDim];
        diagWTW = new double[fDim];
        mu = new double[iDim];
        alpha = new double[fDim];
        Rx = new double[fDim][fDim];
        invRx = new double[fDim][fDim];
        invDw = new double[fDim][fDim];
    }

    private double HG(double gam, double b, double lnb, double gam0, double b0, double lnb0)
    {
        return (((((gam0 - gam) * SpecialFunctions.digamma(gam) - SpecialFunctions.gammaln(gam0)) + SpecialFunctions.gammaln(gam)) - (gam0 * b) / b0) + gam + gam0 * Math.log(b) + gam0 * Math.log(gam0)) - gam0 * lnb0 - gam0 * Math.log(gam);
    }

    public double modelComplexity()
    {
        double tmp = 0.0D;
        hmu = (((double)iDim * (Math.log(gmu0) - Math.log(gmu0 + gamma)) - MatrixUtils.innerProduct(iDim, mu, mu) * gmu0 * tau) + (double)iDim) - gmu0 / (gmu0 + gamma);
        hmu *= 0.5D;
        double h1 = (double)iDim * (1.0D - Math.log(gamma));
        hw = 0.0D;
        for(int i = fDim - 1; i >= 0; i--)
        {
            for(int j = iDim - 1; j >= 0; j--)
                tmp += W[j][i] * W[j][i];

            double h0 = (h1 + (double)iDim * Math.log(alpha[i] * invDw[i][i])) - (tau * tmp + ((double)iDim / gamma) * invDw[i][i]) * alpha[i];
            hw += h0;
        }

        hw *= 0.5D;
        htau = HG(gtau0 + ((double)iDim * gamma) / 2D, tau, lntau, gtau0, btau0, Math.log(btau0));
        if(DEBUG)
        {
            MatrixUtils.disp("hmu", hmu);
            MatrixUtils.disp("hw", hw);
            MatrixUtils.disp("htau", htau);
        }
        return hmu + hw + htau;
    }

    public void deleteFeature(int fidx)
    {
        int newfDim = fDim - 1;
        double newW[][] = new double[iDim][newfDim];
        double newdiagWTW[] = new double[newfDim];
        double newalpha[] = new double[newfDim];
        double newRx[][] = new double[newfDim][newfDim];
        double newinvRx[][] = new double[newfDim][newfDim];
        double newinvDw[][] = new double[newfDim][newfDim];
        int j = 0;
        for(int j0 = 0; j < newfDim; j0++)
        {
            if(j == fidx)
                j0++;
            newdiagWTW[j] = diagWTW[j0];
            newalpha[j] = alpha[j0];
            for(int i = 0; i < iDim; i++)
                newW[i][j] = W[i][j0];

            int k = 0;
            for(int k0 = 0; k < newfDim; k0++)
            {
                if(k == fidx)
                    k0++;
                newRx[j][k] = Rx[j0][k0];
                newinvRx[j][k] = invRx[j0][k0];
                newinvDw[j][k] = invDw[j0][k0];
                k++;
            }

            j++;
        }

        W = newW;
        diagWTW = newdiagWTW;
        alpha = newalpha;
        Rx = newRx;
        invRx = newinvRx;
        invDw = newinvDw;
        fDim = newfDim;
    }

    public void addFeature()
    {
        int newfDim = fDim + 1;
        double newW[][] = new double[iDim][newfDim];
        double newdiagWTW[] = new double[newfDim];
        double newalpha[] = new double[newfDim];
        double newRx[][] = new double[newfDim][newfDim];
        double newinvRx[][] = new double[newfDim][newfDim];
        double newinvDw[][] = new double[newfDim][newfDim];
        for(int j = 0; j < fDim; j++)
        {
            newdiagWTW[j] = diagWTW[j];
            newalpha[j] = alpha[j];
            for(int i = 0; i < iDim; i++)
                newW[i][j] = W[i][j];

            for(int k = 0; k < fDim; k++)
            {
                newRx[j][k] = Rx[j][k];
                newinvRx[j][k] = invRx[j][k];
                newinvDw[j][k] = invDw[j][k];
            }

        }

        W = newW;
        diagWTW = newdiagWTW;
        alpha = newalpha;
        Rx = newRx;
        invRx = newinvRx;
        invDw = newinvDw;
        fDim = newfDim;
        alpha[fDim - 1] = 1.0D;
        Rx[fDim - 1][fDim - 1] = 1.0D;
        invRx[fDim - 1][fDim - 1] = 1.0D;
        invDw[fDim - 1][fDim - 1] = 1.0D;
    }

    public int numOfActiveFeatures()
    {
        int count = 0;
        for(int i = 0; i < fDim; i++)
            if(diagWTW[i] * tau > ACTIVE_FEATURE_THRESHOLD)
                count++;

        return count;
    }

    public boolean DEBUG;
    public int id;
    public boolean isVB;
    public int iDim;
    public int fDim;
    public double gamma;
    public double mu[];
    public double tau;
    public double lntau;
    public double W[][];
    public double alpha[];
    public double diagWTW[];
    public double Rx[][];
    public double invRx[][];
    public double invDw[][];
    public double logdetRx;
    public double galpha0;
    public double balpha0;
    public double gtau0;
    public double btau0;
    public double gmu0;
    public double min_tau;
    public double max_tau;
    public double hw;
    public double hmu;
    public double htau;
    private double ACTIVE_FEATURE_THRESHOLD;
}
