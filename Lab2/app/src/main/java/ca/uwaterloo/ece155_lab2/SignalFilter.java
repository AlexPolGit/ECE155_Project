package ca.uwaterloo.ece155_lab2;

import android.util.Log;

public class SignalFilter
{
    public static float FILTER_CONSTANT = 2.0f;

    public static FIFOQueue getSmoothQueue(FIFOQueue toFilter, int startIndex, int endIndex)
    {
        FIFOQueue newQueue = toFilter;
        for (int i = startIndex; i < endIndex + 1; i++)
        {
            if (i == toFilter.getSize() - 1)
            {
                break;
            }
            else if (toFilter.elementAt(i) instanceof FloatVector3D && toFilter.elementAt(i + 1) != null)
            {
                //TODO: Apply the filter "properly"

                ((FloatVector3D)newQueue.elementAt(i)).setX((((FloatVector3D)toFilter.elementAt(i)).getX() + ((FloatVector3D)toFilter.elementAt(i + 1)).getX()) / FILTER_CONSTANT);
                ((FloatVector3D)newQueue.elementAt(i)).setY((((FloatVector3D)toFilter.elementAt(i)).getY() + ((FloatVector3D)toFilter.elementAt(i + 1)).getY()) / FILTER_CONSTANT);
                ((FloatVector3D)newQueue.elementAt(i)).setZ((((FloatVector3D)toFilter.elementAt(i)).getZ() + ((FloatVector3D)toFilter.elementAt(i + 1)).getZ()) / FILTER_CONSTANT);
            }
            else if (toFilter.elementAt(i + 1) == null)
            {
                break;
            }
            else
            {
                Log.d("debug1", "ERROR: NOT A VECTOR IN QUEUE");
                return null;
            }
        }
        return newQueue;
    }
}