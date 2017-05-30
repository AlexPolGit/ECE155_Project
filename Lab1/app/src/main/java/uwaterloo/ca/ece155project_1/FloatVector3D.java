package uwaterloo.ca.ece155project_1;

import android.util.Log;

public class FloatVector3D
{
    float fx = 0.0f;
    float fy = 0.0f;
    float fz = 0.0f;
    float magnitude = 0.0f;

    public FloatVector3D(){}

    public FloatVector3D(float x, float y, float z)
    {
        fx = x;
        fy = y;
        fz = z;
        setMagnitude();
    }
    public FloatVector3D(float[] f)
    {
        fx = f[0];
        fy = f[1];
        fz = f[2];
        setMagnitude();
    }

    public void setX(float f)
    {
        fx = f;
        setMagnitude();
    }

    public void setY(float f)
    {
        fy = f;
        setMagnitude();
    }

    public void setZ(float f)
    {
        fz = f;
        setMagnitude();
    }

    public float getX()
    {
        return fx;
    }

    public float getY()
    {
        return fy;
    }

    public float getZ()
    {
        return fz;
    }

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

    public void setMagnitude()
    {
        magnitude = (float) Math.sqrt(Math.pow(fx, 2) + Math.pow(fy, 2) + Math.pow(fz, 2));
    }

    public float getMagnitude()
    {
        return magnitude;
    }

    public void zeroValues()
    {
        fx = 0.0f;
        fy = 0.0f;
        fz = 0.0f;
        magnitude = 0.0f;
    }
}