/*
  File:  MatrixUtils.java

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


public class MatrixUtils
{

    public MatrixUtils()
    {
    }

    public static void init(int size)
    {
        dMatBuf = new double[size][size];
        dVecBuf = new double[size];
        iVecBuf = new int[size];
        bufferSize = size;
    }

    public static int getBufferSize()
    {
        return bufferSize;
    }

    public static double pythag(double a, double b)
    {
        double absa = Math.abs(a);
        double absb = Math.abs(b);
        if(absa > absb)
        {
            double tmp = absb / absa;
            return absa * Math.sqrt(1.0D + tmp * tmp);
        }
        if(absb == 0.0D)
        {
            return 0.0D;
        } else
        {
            double tmp = absa / absb;
            return absb * Math.sqrt(1.0D + tmp * tmp);
        }
    }

    public static void svdcmp(int m, int n, double a[][], double w[], double v[][])
    {
        int l = 0;
        int nm = 0;
        double rv1[] = new double[n];
        double anorm;
        double scale;
        double g = scale = anorm = 0.0D;
        for(int i = 0; i < n; i++)
        {
            l = i + 1;
            rv1[i] = scale * g;
            double s;
            g = s = scale = 0.0D;
            if(i < m)
            {
                for(int k = i; k < m; k++)
                    scale += Math.abs(a[k][i]);

                if(scale != 0.0D)
                {
                    for(int k = i; k < m; k++)
                    {
                        a[k][i] /= scale;
                        s += a[k][i] * a[k][i];
                    }

                    double f = a[i][i];
                    if(f >= 0.0D)
                        g = -Math.abs(Math.sqrt(s));
                    else
                        g = Math.abs(Math.sqrt(s));
                    double h = f * g - s;
                    a[i][i] = f - g;
                    for(int j = l; j < n; j++)
                    {
                        s = 0.0D;
                        for(int k = i; k < m; k++)
                            s += a[k][i] * a[k][j];

                        f = s / h;
                        for(int k = i; k < m; k++)
                            a[k][j] += f * a[k][i];

                    }

                    for(int k = i; k < m; k++)
                        a[k][i] *= scale;

                }
            }
            w[i] = scale * g;
            g = s = scale = 0.0D;
            if(i < m && i != n - 1)
            {
                for(int k = l; k < n; k++)
                    scale += Math.abs(a[i][k]);

                if(scale != 0.0D)
                {
                    for(int k = l; k < n; k++)
                    {
                        a[i][k] /= scale;
                        s += a[i][k] * a[i][k];
                    }

                    double f = a[i][l];
                    if(f >= 0.0D)
                        g = -Math.abs(Math.sqrt(s));
                    else
                        g = Math.abs(Math.sqrt(s));
                    double h = f * g - s;
                    a[i][l] = f - g;
                    for(int k = l; k < n; k++)
                        rv1[k] = a[i][k] / h;

                    for(int j = l; j < m; j++)
                    {
                        s = 0.0D;
                        for(int k = l; k < n; k++)
                            s += a[j][k] * a[i][k];

                        for(int k = l; k < n; k++)
                            a[j][k] += s * rv1[k];

                    }

                    for(int k = l; k < n; k++)
                        a[i][k] *= scale;

                }
            }
            anorm = Math.max(anorm, Math.abs(w[i]) + Math.abs(rv1[i]));
        }

        for(int i = n - 1; i >= 0; i--)
        {
            if(i < n - 1)
            {
                if(g != 0.0D)
                {
                    for(int j = l; j < n; j++)
                        v[j][i] = a[i][j] / a[i][l] / g;

                    for(int j = l; j < n; j++)
                    {
                        double s = 0.0D;
                        for(int k = l; k < n; k++)
                            s += a[i][k] * v[k][j];

                        for(int k = l; k < n; k++)
                            v[k][j] += s * v[k][i];

                    }

                }
                for(int j = l; j < n; j++)
                    v[i][j] = v[j][i] = 0.0D;

            }
            v[i][i] = 1.0D;
            g = rv1[i];
            l = i;
        }

        for(int i = Math.min(m, n) - 1; i >= 0; i--)
        {
            l = i + 1;
            g = w[i];
            for(int j = l; j < n; j++)
                a[i][j] = 0.0D;

            if(g != 0.0D)
            {
                g = 1.0D / g;
                for(int j = l; j < n; j++)
                {
                    double s = 0.0D;
                    for(int k = l; k < m; k++)
                        s += a[k][i] * a[k][j];

                    double f = (s / a[i][i]) * g;
                    for(int k = i; k < m; k++)
                        a[k][j] += f * a[k][i];

                }

                for(int j = i; j < m; j++)
                    a[j][i] *= g;

            } else
            {
                for(int j = i; j < m; j++)
                    a[j][i] = 0.0D;

            }
            a[i][i]++;
        }

        for(int k = n - 1; k >= 0; k--)
        {
            for(int its = 1; its <= 30; its++)
            {
                int flag = 1;
                for(l = k; l >= 0; l--)
                {
                    nm = l - 1;
                    if(Math.abs(rv1[l]) + anorm == anorm)
                    {
                        flag = 0;
                        break;
                    }
                    if(Math.abs(w[nm]) + anorm == anorm)
                        break;
                }

                double c;
                double f;
                double h;
                double s;
                double y;
                double z;
                if(flag != 0)
                {
                    c = 0.0D;
                    s = 1.0D;
                    for(int i = l; i < k; i++)
                    {
                        f = s * rv1[i];
                        rv1[i] = c * rv1[i];
                        if(Math.abs(f) + anorm == anorm)
                            break;
                        g = w[i];
                        h = pythag(f, g);
                        w[i] = h;
                        h = 1.0D / h;
                        c = g * h;
                        s = -f * h;
                        for(int j = 0; j < m; j++)
                        {
                            y = a[j][nm];
                            z = a[j][i];
                            a[j][nm] = y * c + z * s;
                            a[j][i] = z * c - y * s;
                        }

                    }

                }
                z = w[k];
                if(l == k)
                {
                    if(z < 0.0D)
                    {
                        w[k] = -z;
                        for(int j = 0; j < n; j++)
                            v[j][k] = -v[j][k];

                    }
                    break;
                }
                if(its == 30)
                {
                    System.out.println("no convergence in 30 svdcmp iterations");
                    return;
                }
                double x = w[l];
                nm = k - 1;
                y = w[nm];
                g = rv1[nm];
                h = rv1[k];
                f = ((y - z) * (y + z) + (g - h) * (g + h)) / (2D * h * y);
                g = pythag(f, 1.0D);
                if(f >= 0.0D)
                    f = Math.abs(g);
                else
                    f = -Math.abs(g);
                f = ((x - z) * (x + z) + h * (y / f - h)) / x;
                c = s = 1.0D;
                for(int j = l; j <= nm; j++)
                {
                    int i = j + 1;
                    g = rv1[i];
                    y = w[i];
                    h = s * g;
                    g = c * g;
                    z = pythag(f, h);
                    rv1[j] = z;
                    c = f / z;
                    s = h / z;
                    f = x * c + g * s;
                    g = g * c - x * s;
                    h = y * s;
                    y *= c;
                    for(int jj = 0; jj < n; jj++)
                    {
                        x = v[jj][j];
                        z = v[jj][i];
                        v[jj][j] = x * c + z * s;
                        v[jj][i] = z * c - x * s;
                    }

                    z = pythag(f, h);
                    w[j] = z;
                    if(z != 0.0D)
                    {
                        z = 1.0D / z;
                        c = f * z;
                        s = h * z;
                    }
                    f = c * g + s * y;
                    x = c * y - s * g;
                    for(int jj = 0; jj < m; jj++)
                    {
                        y = a[jj][j];
                        z = a[jj][i];
                        a[jj][j] = y * c + z * s;
                        a[jj][i] = z * c - y * s;
                    }

                }

                rv1[l] = 0.0D;
                rv1[k] = f;
                w[k] = x;
            }

        }

    }

    public static void svdcmpold(int m, int n, double a[][], double w[], double v[][])
    {
        int l = 0;
        int nm = 0;
        double rv1[] = new double[n];
        double anorm;
        double scale;
        double g = scale = anorm = 0.0D;
        for(int i = 0; i < n; i++)
        {
            l = i + 1;
            rv1[i] = scale * g;
            double s;
            g = s = scale = 0.0D;
            if(i < m)
            {
                for(int k = i; k < m; k++)
                    scale += Math.abs(a[k][i]);

                if(scale != 0.0D)
                {
                    for(int k = i; k < m; k++)
                    {
                        a[k][i] /= scale;
                        s += a[k][i] * a[k][i];
                    }

                    double f = a[i][i];
                    if(f >= 0.0D)
                        g = -Math.abs(Math.sqrt(s));
                    else
                        g = Math.abs(Math.sqrt(s));
                    double h = f * g - s;
                    a[i][i] = f - g;
                    for(int j = l; j < n; j++)
                    {
                        s = 0.0D;
                        for(int k = i; k < m; k++)
                            s += a[k][i] * a[k][j];

                        f = s / h;
                        for(int k = i; k < m; k++)
                            a[k][j] += f * a[k][i];

                    }

                    for(int k = i; k < m; k++)
                        a[k][i] *= scale;

                }
            }
            w[i] = scale * g;
            g = s = scale = 0.0D;
            if(i < m && i != n - 1)
            {
                for(int k = l; k < n; k++)
                    scale += Math.abs(a[i][k]);

                if(scale != 0.0D)
                {
                    for(int k = l; k < n; k++)
                    {
                        a[i][k] /= scale;
                        s += a[i][k] * a[i][k];
                    }

                    double f = a[i][l];
                    if(f >= 0.0D)
                        g = -Math.abs(Math.sqrt(s));
                    else
                        g = Math.abs(Math.sqrt(s));
                    double h = f * g - s;
                    a[i][l] = f - g;
                    for(int k = l; k < n; k++)
                        rv1[k] = a[i][k] / h;

                    for(int j = l; j < m; j++)
                    {
                        s = 0.0D;
                        for(int k = l; k < n; k++)
                            s += a[j][k] * a[i][k];

                        for(int k = l; k < n; k++)
                            a[j][k] += s * rv1[k];

                    }

                    for(int k = l; k < n; k++)
                        a[i][k] *= scale;

                }
            }
            anorm = Math.max(anorm, Math.abs(w[i]) + Math.abs(rv1[i]));
        }

        for(int i = n - 1; i >= 0; i--)
        {
            if(i < n - 1)
            {
                if(g != 0.0D)
                {
                    for(int j = l; j < n; j++)
                        v[j][i] = a[i][j] / a[i][l] / g;

                    for(int j = l; j < n; j++)
                    {
                        double s = 0.0D;
                        for(int k = l; k < n; k++)
                            s += a[i][k] * v[k][j];

                        for(int k = l; k < n; k++)
                            v[k][j] += s * v[k][i];

                    }

                }
                for(int j = l; j < n; j++)
                    v[i][j] = v[j][i] = 0.0D;

            }
            v[i][i] = 1.0D;
            g = rv1[i];
            l = i;
        }

        for(int i = Math.min(m, n) - 1; i >= 0; i--)
        {
            l = i + 1;
            g = w[i];
            for(int j = l; j < n; j++)
                a[i][j] = 0.0D;

            if(g != 0.0D)
            {
                g = 1.0D / g;
                for(int j = l; j < n; j++)
                {
                    double s = 0.0D;
                    for(int k = l; k < m; k++)
                        s += a[k][i] * a[k][j];

                    double f = (s / a[i][i]) * g;
                    for(int k = i; k < m; k++)
                        a[k][j] += f * a[k][i];

                }

                for(int j = i; j < m; j++)
                    a[j][i] *= g;

            } else
            {
                for(int j = i; j < m; j++)
                    a[j][i] = 0.0D;

            }
            a[i][i]++;
        }

        for(int k = n - 1; k >= 0; k--)
        {
            for(int its = 1; its <= 100; its++)
            {
                int flag = 1;
                for(l = k; l >= 0; l--)
                {
                    nm = l - 1;
                    if(Math.abs(rv1[l]) + anorm == anorm)
                    {
                        flag = 0;
                        break;
                    }
                    if(Math.abs(w[nm]) + anorm == anorm)
                        break;
                }

                double c;
                double f;
                double h;
                double s;
                double y;
                double z;
                if(flag != 0)
                {
                    c = 0.0D;
                    s = 1.0D;
                    for(int i = l; i < k; i++)
                    {
                        f = s * rv1[i];
                        rv1[i] = c * rv1[i];
                        if(Math.abs(f) + anorm == anorm)
                            break;
                        g = w[i];
                        h = pythag(f, g);
                        w[i] = h;
                        h = 1.0D / h;
                        c = g * h;
                        s = -f * h;
                        for(int j = 0; j < m; j++)
                        {
                            y = a[j][nm];
                            z = a[j][i];
                            a[j][nm] = y * c + z * s;
                            a[j][i] = z * c - y * s;
                        }

                    }

                }
                z = w[k];
                if(l == k)
                {
                    if(z < 0.0D)
                    {
                        w[k] = -z;
                        for(int j = 0; j < n; j++)
                            v[j][k] = -v[j][k];

                    }
                    break;
                }
                if(its == 100)
                {
                    System.out.println("no convergence in 100 svdcmp iterations");
                    System.exit(1);
                }
                double x = w[l];
                nm = k - 1;
                y = w[nm];
                g = rv1[nm];
                h = rv1[k];
                f = ((y - z) * (y + z) + (g - h) * (g + h)) / (2D * h * y);
                g = pythag(f, 1.0D);
                if(f >= 0.0D)
                    f = Math.abs(g);
                else
                    f = -Math.abs(g);
                f = ((x - z) * (x + z) + h * (y / f - h)) / x;
                c = s = 1.0D;
                for(int j = l; j <= nm; j++)
                {
                    int i = j + 1;
                    g = rv1[i];
                    y = w[i];
                    h = s * g;
                    g = c * g;
                    z = pythag(f, h);
                    rv1[j] = z;
                    c = f / z;
                    s = h / z;
                    f = x * c + g * s;
                    g = g * c - x * s;
                    h = y * s;
                    y *= c;
                    for(int jj = 0; jj < n; jj++)
                    {
                        x = v[jj][j];
                        z = v[jj][i];
                        v[jj][j] = x * c + z * s;
                        v[jj][i] = z * c - x * s;
                    }

                    z = pythag(f, h);
                    w[j] = z;
                    if(z != 0.0D)
                    {
                        z = 1.0D / z;
                        c = f * z;
                        s = h * z;
                    }
                    f = c * g + s * y;
                    x = c * y - s * g;
                    for(int jj = 0; jj < m; jj++)
                    {
                        y = a[jj][j];
                        z = a[jj][i];
                        a[jj][j] = y * c + z * s;
                        a[jj][i] = z * c - y * s;
                    }

                }

                rv1[l] = 0.0D;
                rv1[k] = f;
                w[k] = x;
            }

        }

    }

    public static void pseudoInverse(int m, int n, double a[][], double p[][])
    {
        double w[] = new double[n];
        double v[][] = new double[n][n];
        svdcmp(m, n, a, w, v);
        for(int i = 0; i < n; i++)
            if(Math.abs(w[i]) > 0.0D)
            {
                double tmp = 1.0D / w[i];
                for(int j = 0; j < n; j++)
                    v[j][i] *= tmp;

            } else
            {
                for(int j = 0; j < n; j++)
                    v[j][i] = 0.0D;

            }

        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < m; j++)
            {
                double tmp = 0.0D;
                for(int k = 0; k < n; k++)
                    tmp += v[i][k] * a[j][k];

                p[i][j] = tmp;
            }

        }

    }

    public static void inverse(int M, double a[][], double y[][], int index[], double col[])
    {
        ludcmp(a, M, index);
        for(int j = 0; j < M; j++)
        {
            for(int i = 0; i < M; i++)
                col[i] = 0.0D;

            col[j] = 1.0D;
            lubksb(a, M, index, col);
            for(int i = 0; i < M; i++)
                y[i][j] = col[i];

        }

    }

    public static void inverse(int M, double a[][], double y[][])
    {
        inverse(M, a, y, iVecBuf, dVecBuf);
    }

    public static double det(int n, double a[][], int index[])
    {
        double s = ludcmp(a, n, index);
        double v = 0.0D;
        for(int i = n - 1; i >= 0; i--)
        {
            if(a[i][i] == 0.0D)
                return 0.0D;
            if(a[i][i] < 0.0D)
                s *= -1D;
            v += Math.log(Math.abs(a[i][i]));
        }

        return s * Math.exp(v);
    }

    public static double logDetWithInverse(int M, double a[][], double y[][])
    {
        double s = ludcmp(a, M, iVecBuf);
        double v = 0.0D;
        for(int j = 0; j < M; j++)
        {
            for(int i = 0; i < M; i++)
                dVecBuf[i] = 0.0D;

            dVecBuf[j] = 1.0D;
            lubksb(a, M, iVecBuf, dVecBuf);
            for(int i = 0; i < M; i++)
                y[i][j] = dVecBuf[i];

        }

        for(int i = M - 1; i >= 0; i--)
        {
            if(a[i][i] == 0.0D)
                return (-1.0D / 0.0D);
            v += Math.log(Math.abs(a[i][i]));
        }

        return v;
    }

    public static double logDet(int M, double a[][])
    {
        for(int j = 0; j < M; j++)
        {
            for(int i = 0; i < M; i++)
                dMatBuf[i][j] = a[i][j];

        }

        double s = ludcmp(a, M, iVecBuf);
        double v = 0.0D;
        for(int i = M - 1; i >= 0; i--)
        {
            if(a[i][i] == 0.0D)
                return (-1.0D / 0.0D);
            v += Math.log(Math.abs(a[i][i]));
        }

        return v;
    }

    public static double ludcmp(double a[][], int n, int index[])
    {
        int imax = 0;
        double vv[] = new double[n];
        double d = 1.0D;
        for(int i = 0; i < n; i++)
        {
            double big = 0.0D;
            for(int j = 0; j < n; j++)
            {
                double temp;
                if((temp = Math.abs(a[i][j])) > big)
                    big = temp;
            }

            if(big == 0.0D)
            {
                System.out.println("Singular matrix in routine ludcmp");
                big = 1 / 0;
            }
            vv[i] = 1.0D / big;
        }

        for(int j = 0; j < n; j++)
        {
            for(int i = 0; i < j; i++)
            {
                double sum = a[i][j];
                for(int k = 0; k < i; k++)
                    sum -= a[i][k] * a[k][j];

                a[i][j] = sum;
            }

            double big = 0.0D;
            for(int i = j; i < n; i++)
            {
                double sum = a[i][j];
                for(int k = 0; k < j; k++)
                    sum -= a[i][k] * a[k][j];

                a[i][j] = sum;
                double dum;
                if((dum = vv[i] * Math.abs(sum)) >= big)
                {
                    big = dum;
                    imax = i;
                }
            }

            if(j != imax)
            {
                for(int k = 0; k < n; k++)
                {
                    double dum = a[imax][k];
                    a[imax][k] = a[j][k];
                    a[j][k] = dum;
                }

                d = -d;
                vv[imax] = vv[j];
            }
            index[j] = imax;
            if(a[j][j] == 0.0D)
                a[j][j] = 9.9999999999999995E-21D;
            if(j != n - 1)
            {
                double dum = 1.0D / a[j][j];
                for(int i = j + 1; i < n; i++)
                    a[i][j] *= dum;

            }
        }

        return d;
    }

    public static void lubksb(double a[][], int n, int index[], double b[])
    {
        int ii = -1;
        for(int i = 0; i < n; i++)
        {
            int ip = index[i];
            double sum = b[ip];
            b[ip] = b[i];
            if(ii != -1)
            {
                for(int j = ii; j <= i - 1; j++)
                    sum -= a[i][j] * b[j];

            } else
            if(sum != 0.0D)
                ii = i;
            b[i] = sum;
        }

        for(int i = n - 1; i >= 0; i--)
        {
            double sum = b[i];
            for(int j = i + 1; j < n; j++)
                sum -= a[i][j] * b[j];

            b[i] = sum / a[i][i];
        }

    }

    public static void mul(int N1, int N2, int N3, double a[][], double b[][], double c[][])
    {
        for(int i = N1 - 1; i >= 0; i--)
        {
            for(int j = N3 - 1; j >= 0; j--)
            {
                c[i][j] = 0.0D;
                for(int k = N2 - 1; k >= 0; k--)
                    c[i][j] += a[i][k] * b[k][j];

            }

        }

    }

    public static void mul(int M, int N, double a[], double b[][], double c[])
    {
        for(int i = N - 1; i >= 0; i--)
        {
            c[i] = 0.0D;
            for(int j = M - 1; j >= 0; j--)
                c[i] += a[j] * b[j][i];

        }

    }

    public static void mul(int M, int N, double a[][], double b[], double c[])
    {
        for(int i = M - 1; i >= 0; i--)
        {
            c[i] = 0.0D;
            for(int j = N - 1; j >= 0; j--)
                c[i] += a[i][j] * b[j];

        }

    }

    public static double norm(int D, double vec[])
    {
        double norm = 0.0D;
        for(int i = D - 1; i >= 0; i--)
            norm += vec[i] * vec[i];

        return Math.sqrt(norm);
    }

    public static double norm(int D, double vec[][])
    {
        double norm = 0.0D;
        for(int i = D - 1; i >= 0; i--)
            norm += vec[i][0] * vec[i][0];

        return Math.sqrt(norm);
    }

    public static void mulScalar(int D, double d, double vec[])
    {
        for(int i = D - 1; i >= 0; i--)
            vec[i] *= d;

    }

    public static void mulScalar(int M, int N, double d, double mat[][])
    {
        for(int i = M - 1; i >= 0; i--)
        {
            for(int j = N - 1; j >= 0; j--)
                mat[i][j] *= d;

        }

    }

    public static double innerProduct(int D, double v1[], double v2[])
    {
        double sum = 0.0D;
        for(int i = D - 1; i >= 0; i--)
            sum += v1[i] * v2[i];

        return sum;
    }

    public static double trace(int M, double m[][])
    {
        double sum = 0.0D;
        for(int i = M - 1; i >= 0; i--)
            sum += m[i][i];

        return sum;
    }

    public static void transpose(int M, int N, double src[][], double dst[][])
    {
        for(int i = M - 1; i >= 0; i--)
        {
            for(int j = N - 1; j >= 0; j--)
                dst[j][i] = src[i][j];

        }

    }

    public static double[][] transpose(double src[][])
    {
        int M = src.length;
        int N = src[0].length;
        double dst[][] = new double[N][M];
        for(int i = M - 1; i >= 0; i--)
        {
            for(int j = N - 1; j >= 0; j--)
                dst[j][i] = src[i][j];

        }

        return dst;
    }

    public static void transpose(int N, double src[][])
    {
        for(int i = N - 1; i >= 0; i--)
        {
            for(int j = i - 1; j >= 0; j--)
            {
                double tmp = src[i][j];
                src[i][j] = src[j][i];
                src[j][i] = tmp;
            }

        }

    }

    public static void disp(int N, int M, double a[][])
    {
        System.out.print("[");
        for(int i = 0; i < N; i++)
        {
            for(int j = 0; j < M; j++)
                System.out.print(a[i][j] + " ");

            if(i == N - 1)
                System.out.println("]");
            else
                System.out.println(";...");
        }

    }

    public static void disp(double a[][])
    {
        int N = a.length;
        int M = a[0].length;
        disp(N, M, a);
    }

    public static void disp(String s, double a[][])
    {
        int N = a.length;
        int M = a[0].length;
        System.out.println(s + "=...");
        disp(N, M, a);
    }

    public static void disp(String s, double a[])
    {
        int N = a.length;
        System.out.print(s + "=");
        disp(a);
    }

    public static void disp(double a[])
    {
        int N = a.length;
        System.out.print("[");
        for(int i = 0; i < N; i++)
            System.out.print(a[i] + " ");

        System.out.println("]");
    }

    public static void disp(String s, double a)
    {
        System.out.println(s + "=" + a);
    }

    public static void subMatrix(double src[][], double dst[][], int row, int col, int M, int N)
    {
        for(int i = M - 1; i >= 0; i--)
        {
            for(int j = N - 1; j >= 0; j--)
                dst[i][j] = src[row + i][col + j];

        }

    }

    public static double matrixInnerProduct(int M, int N, double m1[][], double m2[][])
    {
        double sum = 0.0D;
        for(int i = M - 1; i >= 0; i--)
        {
            for(int j = N - 1; j >= 0; j--)
                sum += m1[i][j] * m2[i][j];

        }

        return sum;
    }

    public static void diagonalMatrix(int M, double src[][], double dst[][])
    {
        for(int i = M - 1; i >= 0; i--)
        {
            for(int j = M - 1; j >= 0; j--)
                dst[i][j] = 0.0D;

            dst[i][i] = src[i][i];
        }

    }

    public static void diagonalMatrix(int M, double mat[][], double val)
    {
        for(int i = M - 1; i >= 0; i--)
        {
            mat[i][i] = val;
            for(int j = i - 1; j >= 0; j--)
                mat[i][j] = mat[j][i] = 0.0D;

        }

    }

    public static void substitution(int M, int N, double src[][], double dst[][])
    {
        for(int i = M - 1; i >= 0; i--)
        {
            for(int j = N - 1; j >= 0; j--)
                dst[i][j] = src[i][j];

        }

    }

    public static void identityMatrix(int M, double mat[][])
    {
        for(int i = M - 1; i >= 0; i--)
        {
            mat[i][i] = 1.0D;
            for(int j = i - 1; j >= 0; j--)
                mat[i][j] = mat[j][i] = 0.0D;

        }

    }

    public static void add(int M, int N, double a[][], double b[][])
    {
        for(int i = M - 1; i >= 0; i--)
        {
            for(int j = N - 1; j >= 0; j--)
                a[i][j] += b[i][j];

        }

    }

    public static void printMatrix(int M, int N, double mat[][])
    {
        for(int i = 0; i < M; i++)
        {
            for(int j = 0; j < N; j++)
                System.out.print(mat[i][j] + " ");

            System.out.println();
        }

    }

    public static void zeroMatrix(int M, int N, double mat[][])
    {
        for(int i = M - 1; i >= 0; i--)
        {
            for(int j = N - 1; j >= 0; j--)
                mat[i][j] = 0.0D;

        }

    }

    public static void fillZero(double mat[][])
    {
        zeroMatrix(mat.length, mat[0].length, mat);
    }

    public static void fillZero(double vec[])
    {
        for(int i = vec.length - 1; i >= 0; i--)
            vec[i] = 0.0D;

    }

    public static double calcInvRx(int D, int q, double Rx[][], double invRx[][], double W[][], double tau)
    {
        for(int i = q - 1; i >= 0; i--)
        {
            for(int j = q - 1; j >= i; j--)
            {
                double tmp = 0.0D;
                for(int k = D - 1; k >= 0; k--)
                    tmp += W[k][i] * W[k][j];

                tmp *= tau;
                Rx[i][j] = tmp;
                Rx[j][i] = tmp;
                dMatBuf[i][j] = tmp;
                dMatBuf[j][i] = tmp;
            }

            Rx[i][i]++;
            dMatBuf[i][i]++;
        }

        return logDetWithInverse(q, dMatBuf, invRx);
    }

    public static void cov(int N, int D, double dat[][], double mu[], double cov[][])
    {
        zeroMatrix(D, D, cov);
        for(int n = N - 1; n >= 0; n--)
        {
            for(int i = D - 1; i >= 0; i--)
            {
                for(int j = D - 1; j >= 0; j--)
                    cov[i][j] += (dat[n][i] - mu[i]) * (dat[n][j] - mu[j]);

            }

        }

        for(int i = D - 1; i >= 0; i--)
        {
            for(int j = D - 1; j >= 0; j--)
                cov[i][j] /= N;

        }

    }

    public static void cov(int N, int D, double dat[][], double mu[], double cmf[], double cov[][])
    {
        zeroMatrix(D, D, cov);
        double e = 0.0D;
        for(int n = N - 1; n >= 0; n--)
        {
            e += cmf[n];
            for(int i = D - 1; i >= 0; i--)
            {
                for(int j = D - 1; j >= 0; j--)
                    cov[i][j] += cmf[n] * (dat[n][i] - mu[i]) * (dat[n][j] - mu[j]);

            }

        }

        for(int i = D - 1; i >= 0; i--)
        {
            for(int j = D - 1; j >= 0; j--)
                cov[i][j] /= e;

        }

    }

    public static void mean(int N, int D, double dat[][], double mu[])
    {
        for(int j = D - 1; j >= 0; j--)
            mu[j] = 0.0D;

        for(int n = N - 1; n >= 0; n--)
        {
            for(int j = D - 1; j >= 0; j--)
                mu[j] += dat[n][j];

        }

        for(int j = D - 1; j >= 0; j--)
            mu[j] /= N;

    }

    public static double mean(double vec[])
    {
        double sum = 0.0D;
        for(int i = vec.length - 1; i >= 0; i--)
            sum += vec[i];

        return sum / (double)vec.length;
    }

    public static double calcInvRx(int D, int q, double Rx[][], double invRx[][], double W[][], double tau, boolean missIdx[])
    {
        for(int i = q - 1; i >= 0; i--)
        {
            for(int j = q - 1; j >= i; j--)
            {
                double tmp = 0.0D;
                for(int k = D - 1; k >= 0; k--)
                    if(!missIdx[k])
                        tmp += W[k][i] * W[k][j];

                tmp *= tau;
                Rx[i][j] = tmp;
                Rx[j][i] = tmp;
                dMatBuf[i][j] = tmp;
                dMatBuf[j][i] = tmp;
            }

            Rx[i][i]++;
            dMatBuf[i][i]++;
        }

        return logDetWithInverse(q, dMatBuf, invRx);
    }

    public static double[] sort(double s[], int idx[])
    {
        int D = s.length;
        double a[] = new double[D];
        for(int i = 0; i < D; i++)
        {
            a[i] = s[i];
            idx[i] = i;
        }

        for(int i = 0; i < D - 1; i++)
        {
            for(int j = D - 1; j > i; j--)
                if(a[j] < a[j - 1])
                {
                    double t = a[j];
                    a[j] = a[j - 1];
                    a[j - 1] = t;
                    int k = idx[j];
                    idx[j] = idx[j - 1];
                    idx[j - 1] = k;
                }

        }

        return a;
    }

    public static double[] sortDecend(double s[], int idx[])
    {
        int D = s.length;
        double a[] = new double[D];
        for(int i = 0; i < D; i++)
        {
            a[i] = s[i];
            idx[i] = i;
        }

        for(int i = 0; i < D - 1; i++)
        {
            for(int j = D - 1; j > i; j--)
                if(a[j] > a[j - 1])
                {
                    double t = a[j];
                    a[j] = a[j - 1];
                    a[j - 1] = t;
                    int k = idx[j];
                    idx[j] = idx[j - 1];
                    idx[j - 1] = k;
                }

        }

        return a;
    }

    public static void symmetrize(double mat[][])
    {
        int n = mat.length;
        for(int i = n - 1; i > 0; i--)
        {
            for(int j = i - 1; j >= 0; j--)
            {
                double tmp = (mat[i][j] + mat[j][i]) / 2D;
                mat[i][j] = tmp;
                mat[j][i] = tmp;
            }

        }

    }

    public static void orthogonalize(double W[][])
    {
        int m = W.length;
        int l = W[0].length;
        double newW[][] = dMatBuf;
        double lambda2[] = dVecBuf;
        lambda2[0] = 0.0D;
        for(int i = m - 1; i >= 0; i--)
        {
            double w = W[i][0];
            newW[i][0] = w;
            lambda2[0] += w * w;
        }

        for(int j = 1; j < l; j++)
        {
            for(int i = m - 1; i >= 0; i--)
                newW[i][j] = W[i][j];

            for(int k = 0; k < j; k++)
                if(lambda2[k] > 1E-08D)
                {
                    double ip = 0.0D;
                    for(int i = m - 1; i >= 0; i--)
                        ip += W[i][j] * newW[i][k];

                    ip /= lambda2[k];
                    for(int i = m - 1; i >= 0; i--)
                        newW[i][j] -= ip * newW[i][k];

                }

            lambda2[j] = 0.0D;
            for(int i = m - 1; i >= 0; i--)
                lambda2[j] += newW[i][j] * newW[i][j];

        }

        for(int j = 0; j < l; j++)
        {
            for(int i = 0; i < m; i++)
                W[i][j] = newW[i][j];

        }

    }

    public static void main(String args[])
    {
        double a[] = {
            3D, 2D, 5D, 4D
        };
        int idx[] = {
            1, 1, 1, 1, 1
        };
        double B[][] = new double[2][2];
        double A[][] = {
            {
                2D, 1.0D
            }, {
                0.0D, 0.0D
            }
        };
        init(5);
        try
        {
            inverse(2, A, B);
            disp("B", B);
        }
        catch(ArithmeticException e)
        {
            e.printStackTrace();
            System.out.println("catched!!");
        }
    }

    private static int bufferSize;
    private static double dMatBuf[][];
    private static double dVecBuf[];
    private static int iVecBuf[];
}
