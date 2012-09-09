// Author: François Ingelrest
// Date:   08/08/2011

package com.sensorscope.client;

public class Operation
{
    public static final int OP_NOP = 0;
    public static final int OP_ADD = 1;
    public static final int OP_SUB = 2;
    public static final int OP_MUL = 3;
    public static final int OP_DIV = 4;

    private int mA;
    private int mB;
    private int mOp;

    public Operation()
    {
    }

    public Operation(int a, int b, int op)
    {
        set(a, b, op);
    }

    public Operation(int a)
    {
        set(a);
    }

    public Operation(Operation op)
    {
        set(op);
    }

    public void set(int a, int b, int op)
    {
        mA  = a;
        mB  = b;
        mOp = op;
    }

    public void set(int a)
    {
        set(a, 0, OP_NOP);
    }

    public void set(Operation op)
    {
        set(op.mA, op.mB, op.mOp);
    }

    public void setOp(int op)
    {
        mOp = op;
    }

    public String toString()
    {
        switch(mOp)
        {
            case OP_NOP: return Integer.toString(mA);
            case OP_ADD: return mA + " + " + mB + " = " + (mA + mB);
            case OP_SUB: return mA + " - " + mB + " = " + (mA - mB);
            case OP_MUL: return mA + " * " + mB + " = " + (mA * mB);
            case OP_DIV: return mA + " / " + mB + " = " + (mA / mB);
        }

        return "Unknown operation (" + mOp + ")";
    }
}
