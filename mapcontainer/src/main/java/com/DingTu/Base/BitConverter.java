package com.DingTu.Base;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class BitConverter {

    //调换字节顺序，原因是C#与java的存储方式不同
    public static byte[] Reverse(byte[] b,int StartIndex,int Count)
    {
        byte[] temp = new byte[Count];
        for (int i = 0; i < Count; i++)
        {
            temp[i] = b[StartIndex+Count - 1 - i];
        }
        return temp;
    }
    public static byte[] Reverse(byte[] b)
    {
        return Reverse(b,0,b.length);
    }
    public static int ToInt(byte[] b)
    {
        return ToInt(b,0);
    }
    public static int ToInt(byte[] b, int offset)
    {
        int value= 0;
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    //浮点到字节转换
    public static byte[] GetBytes(double d){
        byte[] b=new byte[8];
        long l=Double.doubleToLongBits(d);
        for(int i=0;i<b.length;i++){
            b[i]=new Long(l).byteValue();
            l=l>>8;
        }
        return b;
    }

    //浮点到字节转换
    public static byte[] GetBytes(int i)
    {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public static byte[] GetBytes(short s) {
        byte[] intByte = new byte[2];
        intByte[1] = (byte) (s >> 8);
        intByte[0] = (byte) (s >> 0);
        return intByte;
    }

    //大端整数转小端整数
    public static short BigToLittleShort(short bigShort)
    {
        byte[] intByte = GetBytes(bigShort);
        short resultInt = ToShort(Reverse(intByte));
        return resultInt;
    }

    //大端整数转小端整数
    public static int BigToLittleInt(int bigInt)
    {
        byte[] intByte = GetBytes(bigInt);
        int resultInt = ToInt(Reverse(intByte));
        return resultInt;
    }
    //大端Double转小端Double
    public static double BigToLittleDouble(double bigDouble)
    {
        byte[] dblByte = GetBytes(bigDouble);
        double resultInt = ToDouble(Reverse(dblByte));
        return resultInt;
    }


    //字节到浮点转换
    public static double ToDouble(byte[] b)
    {
        return ToDouble(b,0);
    }

    public static double ToDouble(byte[] b,int offset)
    {
        long lval = 0;
        for (int i = 0; i < 8; i++)
        {
            lval = lval << 8;
            lval += (b[(7 - i+offset)] & 0xff);
        }
        return Double.longBitsToDouble(lval);
    }

    public static short ToShort(byte[] b) {
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }
}
