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


class BPCAFillAlgorithm
{

    public BPCAFillAlgorithm(double y[][], MissingValueHandler missing)
    {
        verbose = 5;
        epoch = 0;
        maxEpoch = 500;
        isConverged = false;
        isFinished = false;
        FEATURE_DELETION_THRESHOLD = 1E-08D;
        numData = y.length;
        iDim = y[0].length;
        fDim = iDim - 1;
        yest = new double[numData][iDim];
        missingIndex = new boolean[numData][iDim];
        numberOfMissingIndex = new int[numData];
        missing.isMissing(numData, iDim, y, missingIndex, numberOfMissingIndex);
        u = new BPCAUnit(0, iDim, fDim);
        Rxo = new double[fDim][fDim];
        invRxo = new double[fDim][fDim];
        matT = new double[iDim][fDim];
        comp_mat = new double[fDim][fDim];
        dy = new double[iDim];
        x = new double[fDim];
        ex = new double[fDim];
        MatrixUtils.init(iDim);
        int count = 0;
        double sum = 0.0D;
        for(int i = iDim - 1; i >= 0; i--)
        {
            count = 0;
            sum = 0.0D;
            for(int j = numData - 1; j >= 0; j--)
                if(!missingIndex[j][i])
                {
                    count++;
                    sum += y[j][i];
                }

            if(count == 0)
                u.mu[i] = 0.0D;
            else
                u.mu[i] = sum / (double)count;
        }

        for(int i = iDim - 1; i >= 0; i--)
        {
            for(int j = numData - 1; j >= 0; j--)
                if(missingIndex[j][i])
                {
                    yest[j][i] = u.mu[i];
                }
                else
                    yest[j][i] = y[j][i];

        }

        u.gamma = numData;
        u.galpha0 = 1E-10D;
        u.balpha0 = 1.0D;
        u.gtau0 = 1E-10D;
        u.btau0 = 1.0D;
        u.gmu0 = 0.001D;
        u.min_tau = 1E-10D;
        u.max_tau = 10000000000D;
        u.isVB = true;
        initParameterByPCA();
    }

    public double[][] getMatrix()
    {
        return yest;
    }

    public double getTau()
    {
        return u.tau;
    }

    public BPCAUnit getModel()
    {
        return u;
    }

    private void initParameterByPCA()
    {
        int sort_i[] = new int[iDim];
        double eig[] = new double[iDim];
        double sort[] = new double[iDim];
        double tmp[][] = new double[iDim][iDim];
        double cmf[] = new double[numData];
        for(int i = numData - 1; i >= 0; i--)
            cmf[i] = 1.0D;

        double cov[][] = new double[iDim][iDim];
        MatrixUtils.cov(numData, iDim, yest, u.mu, cmf, cov);
        double tmpsum = 0.0D;
        for(int i = 0; i < iDim; i++)
            tmpsum += cov[i][i];

        if(fDim > 0)
        {
            MatrixUtils.svdcmp(iDim, iDim, cov, eig, tmp);
            sort = MatrixUtils.sortDecend(eig, sort_i);
        }
        for(int j = 0; j < fDim; j++)
        {
            for(int i = 0; i < iDim; i++)
                u.W[i][j] = Math.sqrt(sort[j]) * tmp[i][sort_i[j]];

        }

        u.tau = 0.0D;
        for(int i = fDim - 1; i >= 0; i--)
            tmpsum -= sort[i];

        u.tau = 1.0D / tmpsum;
        calcAlpha();
        for(int j = 0; j < fDim; j++)
            u.invDw[j][j] = (1.0D * (double)numData) / (double)iDim;

        calcInvRx();
    }

    public boolean doStep()
    {
        epoch++;
        preEStep();
        for(int i = 0; i < numData; i++)
            if(numberOfMissingIndex[i] == 0)
                eStepWithoutMiss(yest[i]);
            else
                eStepWithMiss(yest[i], missingIndex[i]);

        postEStep();
        doMStep();
        deleteDeadFeatures();
        return isFinished;
    }

    public void deleteDeadFeatures()
    {
        fDim = u.fDim;
        double min = 10D;
        int minidx = 0;
        for(int j = fDim - 1; j >= 0; j--)
            if(u.diagWTW[j] < min)
            {
                min = u.diagWTW[j];
                minidx = j;
            }

        if(min < FEATURE_DELETION_THRESHOLD)
            u.deleteFeature(minidx);
        fDim = u.fDim;
    }

    private void preEStep()
    {
        MatrixUtils.mulScalar(iDim, fDim, 0.0D, matT);
        trS = 0.0D;
        calcInvRx();
    }

    private void eStepWithoutMiss(double y[])
    {
        for(int i = iDim - 1; i >= 0; i--)
            dy[i] = y[i] - u.mu[i];

        MatrixUtils.mul(iDim, fDim, dy, u.W, ex);
        MatrixUtils.mulScalar(fDim, u.tau, ex);
        MatrixUtils.mul(fDim, fDim, ex, u.invRx, x);
        for(int i = iDim - 1; i >= 0; i--)
        {
            trS += dy[i] * dy[i];
            for(int j = fDim - 1; j >= 0; j--)
                matT[i][j] += dy[i] * x[j];

        }

    }

    private void eStepWithMiss(double y[], boolean missIdx[])
    {
        for(int d = iDim - 1; d >= 0; d--)
            if(missIdx[d])
                dy[d] = 0.0D;
            else
                dy[d] = y[d] - u.mu[d];

        logdetRxo = calcInvRxo(missIdx);
        MatrixUtils.mul(iDim, fDim, dy, u.W, ex);
        MatrixUtils.mulScalar(fDim, u.tau, ex);
        MatrixUtils.mul(fDim, fDim, ex, invRxo, x);
        for(int d = iDim - 1; d >= 0; d--)
            if(missIdx[d])
            {
                y[d] = u.mu[d];
                for(int i = fDim - 1; i >= 0; i--)
                    y[d] += u.W[d][i] * x[i];

                dy[d] = y[d] - u.mu[d];
            }

        double iTau = 1.0D / u.tau;
        double tmpTrS = 0.0D;
        for(int d = iDim - 1; d >= 0; d--)
        {
            tmpTrS += dy[d] * dy[d];
            if(missIdx[d])
                tmpTrS += iTau;
            for(int j = fDim - 1; j >= 0; j--)
            {
                matT[d][j] += dy[d] * x[j];
                if(missIdx[d])
                {
                    for(int i = fDim - 1; i >= 0; i--)
                    {
                        matT[d][i] += u.W[d][j] * invRxo[i][j];
                        tmpTrS += u.W[d][i] * u.W[d][j] * invRxo[i][j];
                    }

                }
            }

        }

        trS += tmpTrS;
    }

    private void postEStep()
    {
        trS = trS / (double)numData;
        MatrixUtils.mulScalar(iDim, fDim, 1.0D / (double)numData, matT);
    }

    private void doMStep()
    {
        double tmp = 0.0D;
        for(int i = fDim - 1; i >= 0; i--)
        {
            for(int j = fDim - 1; j >= 0; j--)
            {
                tmp = 0.0D;
                for(int k = iDim - 1; k >= 0; k--)
                    tmp += matT[k][i] * u.W[k][j];

                tmp *= u.tau;
                u.invDw[i][j] = tmp;
            }

            u.invDw[i][i]++;
        }

        MatrixUtils.mul(fDim, fDim, fDim, u.invDw, u.invRx, comp_mat);
        if(u.isVB)
        {
            for(int j = fDim - 1; j >= 0; j--)
                comp_mat[j][j] += u.alpha[j] / (double)numData;

        }
        MatrixUtils.inverse(fDim, comp_mat, u.invDw);
        MatrixUtils.symmetrize(u.invDw);
        MatrixUtils.mul(iDim, fDim, fDim, matT, u.invDw, u.W);
        if(u.isVB)
        {
            u.tau = ((double)iDim + (2D * u.gtau0) / u.gamma) / ((trS - MatrixUtils.matrixInnerProduct(iDim, fDim, u.W, matT)) + (MatrixUtils.innerProduct(iDim, u.mu, u.mu) * u.gmu0 + (2D * u.gtau0) / u.btau0) / u.gamma);
            double gtau = ((double)iDim * u.gamma) / 2D + u.gtau0;
            u.lntau = SpecialFunctions.digamma(gtau) - Math.log(gtau);
        } else
        {
            u.tau = (double)iDim / (trS - MatrixUtils.matrixInnerProduct(iDim, fDim, u.W, matT));
            u.lntau = 0.0D;
        }
        u.tau = Math.min(Math.max(u.tau, u.min_tau), u.max_tau);
        u.lntau += Math.log(u.tau);
        calcAlpha();
    }

    private void calcInvRx()
    {
        double k1 = (double)iDim / u.gamma;
        for(int i = fDim - 1; i >= 0; i--)
        {
            for(int j = fDim - 1; j >= i; j--)
            {
                double tmp = 0.0D;
                for(int k = iDim - 1; k >= 0; k--)
                    tmp += u.W[k][i] * u.W[k][j];

                if(i == j)
                    u.diagWTW[i] = tmp;
                tmp *= u.tau;
                if(u.isVB)
                    tmp += k1 * u.invDw[i][j];
                u.Rx[i][j] = tmp;
                u.Rx[j][i] = tmp;
                comp_mat[i][j] = tmp;
                comp_mat[j][i] = tmp;
            }

            u.Rx[i][i]++;
            comp_mat[i][i]++;
        }

        u.logdetRx = MatrixUtils.logDetWithInverse(fDim, comp_mat, u.invRx);
    }

    private void calcAlpha()
    {
        double k1 = (double)iDim / u.gamma;
        for(int j = 0; j < fDim; j++)
        {
            double wtw = 0.0D;
            for(int i = 0; i < iDim; i++)
                wtw += u.W[i][j] * u.W[i][j];

            u.alpha[j] = (2D * u.galpha0 + (double)iDim) / (u.tau * wtw + k1 * u.invDw[j][j] + u.galpha0 / u.balpha0);
        }

    }

    private double calcInvRxo(boolean missIdx[])
    {
        for(int i = fDim - 1; i >= 0; i--)
        {
            for(int j = fDim - 1; j >= i; j--)
            {
                double tmp = 0.0D;
                for(int k = iDim - 1; k >= 0; k--)
                    if(missIdx[k])
                        tmp += u.W[k][i] * u.W[k][j];

                tmp *= u.tau;
                tmp = u.Rx[i][j] - tmp;
                Rxo[i][j] = tmp;
                Rxo[j][i] = tmp;
                comp_mat[i][j] = tmp;
                comp_mat[j][i] = tmp;
            }

        }

        return MatrixUtils.logDetWithInverse(fDim, comp_mat, invRxo);
    }

    int verbose;
    BPCAUnit u;
    int numData;
    int iDim;
    int fDim;
    double yest[][];
    private boolean missingIndex[][];
    private int numberOfMissingIndex[];
    private double Rxo[][];
    private double invRxo[][];
    private double comp_mat[][];
    private double matT[][];
    private double dy[];
    private double x[];
    private double ex[];
    private double trS;
    private double logdetRxo;
    int epoch;
    int maxEpoch;
    boolean isConverged;
    boolean isFinished;
    double FEATURE_DELETION_THRESHOLD;
}
