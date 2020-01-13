package com.DingTu.CoordinateSystem;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class LKMatrix {
    private double[][] data;
    public double[][] GetData() { return this.data; }

    public LKMatrix(double[][] da)
    {
        int Rows = da.length;
        int Cols = da[0].length;
        this.data = new double[Rows][Cols];
        for (int i = 0; i < Rows; i++)
        {
            for (int j = 0; j < Cols; j++)
            {
                data[i][j] = da[i][j];
            }
        }
    }

    //转置
    public LKMatrix Transpose()
    {
        int Rows = this.data.length;
        int Cols = this.data[0].length;
        double[][] da = new double[Cols][Rows];
        for (int i = 0; i < Rows; i++)
        {
            for (int j = 0; j < Cols; j++)
            {
                da[j][i] = this.data[i][j];
            }
        }
        return new LKMatrix(da);

    }

    //相乘
    public LKMatrix Multiply(LKMatrix ma)
    {
        int Rows = this.data.length;
        int Cols1 = this.data[0].length;
        int Cols = ma.GetData()[0].length;

        double[][] mtemp = new double[Rows][Cols];

        for (int i = 0; i < Rows; i++)
        {
            for (int j = 0; j < Cols; j++)
            {
                mtemp[i][j] = 0;
                for (int k = 0; k < Cols1; k++)
                    mtemp[i][j] += this.data[i][k] * ma.GetData()[k][j];
            }
        }
        return new LKMatrix(mtemp);
    }

    //逆矩阵
    public void Inv()
    {

        double temp = 0;
        int i, j, k, N = this.data.length;

        //debug
        for (i = 1; i < N; i++)
            for (j = 0; j < i; j++)
                this.data[i][j] = 0;
        for (i = 0; i < N; i++)
        {
            for (j = i; j < N; j++)
            {
                temp = this.data[i][j];
                for (k = 0; k < i; k++)
                    temp = temp - this.data[k][i] * this.data[k][j] / this.data[k][k];
                if (j == i)
                    this.data[i][j] = 1 / temp;
                else
                    this.data[i][j] = temp * this.data[i][i];
            }
        }

        for (i = 0; i < N - 1; i++)
        {
            for (j = i + 1; j < N; j++)
            {
                temp = -this.data[i][j];
                for (k = i + 1; k < j; k++)
                {
                    temp = temp - this.data[i][k] * this.data[k][j];
                }
                this.data[i][j] = temp;
            }
        }

        for (i = 0; i < N - 1; i++)
        {
            for (j = i; j < N; j++)
            {
                if (j == i)
                    temp = this.data[i][j];
                else
                    temp = this.data[i][j] * this.data[j][j];
                for (k = j + 1; k < N; k++)
                    temp = temp + this.data[i][k] * this.data[j][k] * this.data[k][k];
                this.data[i][j] = temp;
            }
        }
        for (i = 1; i < N; i++)
            for (j = 0; j < i; j++)
                this.data[i][j] = this.data[j][i];
    }
}
