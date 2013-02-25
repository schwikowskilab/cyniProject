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


public class SpecialFunctions
{

    public SpecialFunctions()
    {
    }

    public static double sinc(double x[])
    {
        double sum = 0.0D;
        for(int i = x.length - 1; i >= 0; i--)
            sum += x[i] * x[i];

        sum = Math.abs(Math.sqrt(sum));
        if(sum > 0.0D)
            return Math.sin(sum) / sum;
        else
            return 1.0D;
    }

    public static double tanh(double x)
    {
        double e1 = Math.exp(x);
        double e2 = Math.exp(-x);
        return (e1 - e2) / (e1 + e2);
    }

    public static double gammaln(double xx)
    {
        double cof[] = {
            76.180091729471457D, -86.505320329416776D, 24.014098240830911D, -1.231739572450155D, 0.001208650973866179D, -5.3952393849530003E-06D
        };
        double x;
        double y = x = xx;
        double tmp = x + 5.5D;
        tmp -= (x + 0.5D) * Math.log(tmp);
        double ser = 1.0000000001900149D;
        for(int j = 0; j <= 5; j++)
            ser += cof[j] / ++y;

        return -tmp + Math.log((2.5066282746310007D * ser) / x);
    }

    public static double digamma(double xx)
    {
        return (gammaln(xx + 1.0000000000000001E-05D) - gammaln(xx)) * 100000D;
    }

    public static double betainc(double x, double a, double b)
    {
        if(x < 0.0D || x > 1.0D)
            System.out.println("Bad x in routine betainc()");
        double bt;
        if(x == 0.0D || x == 1.0D)
            bt = 0.0D;
        else
            bt = Math.exp((gammaln(a + b) - gammaln(a) - gammaln(b)) + a * Math.log(x) + b * Math.log(1.0D - x));
        if(x < (a + 1.0D) / (a + b + 2D))
            return (bt * betacf(x, a, b)) / a;
        else
            return 1.0D - (bt * betacf(1.0D - x, b, a)) / b;
    }

    public static double betacf(double x, double a, double b)
    {
        double qab = a + b;
        double qap = a + 1.0D;
        double qam = a - 1.0D;
        double c = 1.0D;
        double d = 1.0D - (qab * x) / qap;
        if(Math.abs(d) < 1.0000000000000001E-30D)
            d = 1.0000000000000001E-30D;
        d = 1.0D / d;
        double h = d;
        int m;
        for(m = 1; m <= 100; m++)
        {
            int m2 = 2 * m;
            double aa = ((double)m * (b - (double)m) * x) / ((qam + (double)m2) * (a + (double)m2));
            d = 1.0D + aa * d;
            if(Math.abs(d) < 1.0000000000000001E-30D)
                d = 1.0000000000000001E-30D;
            c = 1.0D + aa / c;
            if(Math.abs(c) < 1.0000000000000001E-30D)
                c = 1.0000000000000001E-30D;
            d = 1.0D / d;
            h *= d * c;
            aa = (-(a + (double)m) * (qab + (double)m) * x) / ((a + (double)m2) * (qap + (double)m2));
            d = 1.0D + aa * d;
            if(Math.abs(d) < 1.0000000000000001E-30D)
                d = 1.0000000000000001E-30D;
            c = 1.0D + aa / c;
            if(Math.abs(c) < 1.0000000000000001E-30D)
                c = 1.0000000000000001E-30D;
            d = 1.0D / d;
            double del = d * c;
            h *= del;
            if(Math.abs(del - 1.0D) < 2.9999999999999999E-07D)
                break;
        }

        if(m > 100)
            System.out.println("a or b too big, or MAXIT too small in betacf");
        return h;
    }

    private static final int MAXIT = 100;
    private static final double EPS = 2.9999999999999999E-07D;
    private static final double FPMIN = 1.0000000000000001E-30D;
}
