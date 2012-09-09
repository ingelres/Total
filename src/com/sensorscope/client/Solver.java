// Author: François Ingelrest
// Date:   08/08/2011

package com.sensorscope.client;

public class Solver
{
    private int       mTarget;         // The target we seek (slowly comes into sight...)
    private int       mArrays[][];     // Storage arrays used during recursion
    private int       mBestDiff;       // The best result we could get (smallest difference)
    private int       mNbBestOps;      // How many operations to achieve the best result
    private Operation mBestOps[];      // Operations leading to the best result
    private Operation mCurrOps[];      // Operations composing the current trial

    public Solver(int input[], int target)
    {
        mTarget  = target;
        mArrays  = new int[input.length + 1][];
        mCurrOps = new Operation[input.length - 1];
        mBestOps = new Operation[input.length - 1];

        // Pre-create the lists of operations
        for(int i=0; i<mCurrOps.length; ++i)
        {
            mCurrOps[i] = new Operation();
            mBestOps[i] = new Operation();
        }

        // Pre-create one array per recursion level
        for(int i=2; i<=input.length; ++i)
            mArrays[i] = new int[i];

        // First recursion level is filled with the input numbers
        for(int i=0; i<input.length; ++i)
            mArrays[input.length][i] = input[i];
    }

    public String getOps()
    {
        String ops = "";

        for(int i=0; i<mNbBestOps; ++i)
            ops += "<pre>" + mBestOps[i] + "</pre><br />";

        return ops;
    }

    public boolean hasExactSolution()
    {
        return mBestDiff == 0;
    }

    private void examineOp(int a, int b, int c, int op, int nbOps)
    {
        int diff = Math.abs(mTarget - c);

        // Did we achieve a smaller difference?
        // And if not, did we achieve the same difference with less operations?
        if(diff < mBestDiff || (diff == mBestDiff && (nbOps + 1) < mNbBestOps))
        {
            mBestDiff  = diff;
            mNbBestOps = nbOps + 1;

            for(int i=0; i<nbOps; ++i)
                mBestOps[i].set(mCurrOps[i]);

            mBestOps[nbOps].set(a, b, op);
        }
    }

    public void solve()
    {
        mBestDiff  = Integer.MAX_VALUE;
        mNbBestOps = 1;

        // For now, the best result is the input number
        // that is the closest to the target
        for(int value: mArrays[mArrays.length - 1])
        {
            int diff = Math.abs(mTarget - value);

            if(diff < mBestDiff)
            {
                mBestDiff = diff;
                mBestOps[0].set(value);
            }
        }

        solve(mArrays.length - 1);
    }

    private void solve(int lvl)
    {
        int currOpIdx   = mArrays.length - lvl - 1;
        int currArray[] = mArrays[lvl];

        // Start with the first two values of the array
        // We'll go through all combinations with the two for-loops below
        int a = currArray[0];
        int b = currArray[1];

        // Stop recursion when only two numbers are left
        // This saves a lot of useless leaf calls
        if(lvl == 2)
        {
            examineOp(a, b, a+b, Operation.OP_ADD, currOpIdx);
            examineOp(a, b, a*b, Operation.OP_MUL, currOpIdx);

            if(a > b)
            {
                examineOp(a, b, a-b, Operation.OP_SUB, currOpIdx);

                if(a%b == 0)
                    examineOp(a, b, a/b, Operation.OP_DIV, currOpIdx);
            }
            else
            {
                examineOp(b, a, b-a, Operation.OP_SUB, currOpIdx);

                if(b%a == 0)
                    examineOp(b, a, b/a, Operation.OP_DIV, currOpIdx);
            }
        }
        else
        {
            int nextLvl     = lvl - 1;
            int nextArray[] = mArrays[nextLvl];

            for(int i=1; i<nextArray.length; ++i)
                nextArray[i] = currArray[i+1];

            for(int i=1; i<lvl; ++i)
            {
                for(int j=i; j<lvl; ++j)
                {
                    // Ignore cases where b > a:
                    //   - Subtraction and division have no sense when b > a
                    //   - Addition and multiplication are commutative so (a,b) and (b,a) give the same result
                    //
                    // Note that when a == b, only one couple could be tested
                    if(a >= b)
                    {
                        // Addition
                        nextArray[0] = a+b;
                        examineOp(a, b, nextArray[0], Operation.OP_ADD, currOpIdx);
                        mCurrOps[currOpIdx].set(a, b, Operation.OP_ADD);
                        solve(nextLvl);

                        // Subtraction
                        // We care only for stricly positive numbers
                        if(a != b)
                        {
                            nextArray[0] = a-b;
                            examineOp(a, b, nextArray[0], Operation.OP_SUB, currOpIdx);
                            mCurrOps[currOpIdx].setOp(Operation.OP_SUB);
                            solve(nextLvl);
                        }

                        // Multiplication and division
                        // Multiplying or dividing by 1 is useless
                        if(a>1 && b>1)
                        {
                            // Multiplication
                            nextArray[0] = a*b;
                            examineOp(a, b, nextArray[0], Operation.OP_MUL, currOpIdx);
                            mCurrOps[currOpIdx].setOp(Operation.OP_MUL);
                            solve(nextLvl);

                            // Division
                            if(a%b == 0)
                            {
                                nextArray[0] = a/b;
                                examineOp(a, b, nextArray[0], Operation.OP_DIV, currOpIdx);
                                mCurrOps[currOpIdx].setOp(Operation.OP_DIV);
                                solve(nextLvl);
                            }
                        }
                    }

                    // Next value for b
                    if(j<nextLvl)
                    {
                        int foo = b;

                        b = nextArray[j];
                        nextArray[j] = foo;
                    }
                }

                // Next value for a
                if(i<nextLvl)
                {
                    int foo = a;

                    a = nextArray[i];
                    nextArray[i] = foo;
                }
            }
        }
    }
}
