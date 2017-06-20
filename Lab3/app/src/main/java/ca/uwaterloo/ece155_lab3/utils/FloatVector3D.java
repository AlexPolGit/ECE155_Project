package ca.uwaterloo.ece155_lab3.utils;

import android.util.Log;

public class FloatVector3D
{
    float fx = 0.0f;
    float fy = 0.0f;
    float fz = 0.0f;
    float magnitude = 0.0f;

    // default constructor for 3D vector
    public FloatVector3D(){}

    // constructor for 3D vector which asks for the values in x, y and z
    public FloatVector3D(float x, float y, float z)
    {
        fx = x;
        fy = y;
        fz = z;
        setMagnitude();
    }

    // constructor for 3D vector that asks for a 3-long array
    public FloatVector3D(float[] f)
    {
        fx = f[0];
        fy = f[1];
        fz = f[2];
        setMagnitude();
    }

    // set the X component to desired value
    public void setX(float f)
    {
        fx = f;
        setMagnitude();
    }

    // set the Y component to desired value
    public void setY(float f)
    {
        fy = f;
        setMagnitude();
    }

    // set the Z component to desired value
    public void setZ(float f)
    {
        fz = f;
        setMagnitude();
    }

    // get the X component
    public float getX()
    {
        return fx;
    }

    // get the Y component
    public float getY()
    {
        return fy;
    }

    // get the Z component
    public float getZ()
    {
        return fz;
    }

    // set the 0->2 component to desired value, 0 = X, 1 = Y, 2 = Z, else = error
    public void set(int dim, float f)
    {
        switch(dim)
        {
            case 0:
            {
                fx = f;
            }
            case 1:
            {
                fy = f;
            }
            case 2:
            {
                fz = f;
            }
            default:
            {
                Log.d("debug1", "Bad Float Set to Vector.");
            }
        }
        setMagnitude();
    }

    // get the 0->2 component, 0 = X, 1 = Y, 2 = Z, else = error (returns 0.0f)
    public float get(int dim)
    {
        switch(dim)
        {
            case 0:
            {
                return fx;
            }
            case 1:
            {
                return fy;
            }
            case 2:
            {
                return fz;
            }
            default:
            {
                Log.d("debug1", "Bad Float Get to Vector.");
                return 0.0f;
            }
        }
    }

    // sets the magnitude of the vector using magnitude of 3D vector equation
    public void setMagnitude()
    {
        magnitude = (float) Math.sqrt(Math.pow(fx, 2) + Math.pow(fy, 2) + Math.pow(fz, 2));
    }

    // gets the magnitude of the vector
    public float getMagnitude()
    {
        return magnitude;
    }

    // "resets" vector by making all value zero
    public void zeroValues()
    {
        fx = 0.0f;
        fy = 0.0f;
        fz = 0.0f;
        magnitude = 0.0f;
    }
}