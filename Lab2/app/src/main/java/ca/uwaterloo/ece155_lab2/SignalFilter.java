package ca.uwaterloo.ece155_lab2;

public class SignalFilter
{
    public static FloatVector3D getSmoothVector(FloatVector3D prev, FloatVector3D curr)
    {
        FloatVector3D vec = new FloatVector3D();
        vec.setX(   prev.getX() + (curr.getX() - prev.getX()) / MainActivity.field_filter.getValue()   );
        vec.setY(   prev.getY() + (curr.getY() - prev.getY()) / MainActivity.field_filter.getValue()   );
        vec.setZ(   prev.getZ() + (curr.getZ() - prev.getZ()) / MainActivity.field_filter.getValue()   );
        return vec;
    }

    /*
    public static FIFOQueue getSmoothQueue(FIFOQueue toFilter, int startIndex, int endIndex)
    {
        FIFOQueue newQueue = toFilter;
        FloatVector3D prev = new FloatVector3D();
        for (int i = startIndex + 1; i < endIndex + 1; i++)
        {
            if (i == toFilter.getSize() - 1)
            {
                break;
            }
            else if (toFilter.elementAt(i) instanceof FloatVector3D && toFilter.elementAt(i + 1) != null)
            {
                //TODO: Apply the filter "properly"

                ((FloatVector3D)newQueue.elementAt(i)).setX(   ((FloatVector3D)newQueue.elementAt(i - 1)).getX() + (((FloatVector3D)toFilter.elementAt(i)).getX() - ((FloatVector3D)toFilter.elementAt(i - 1)).getX()) / FILTER_CONSTANT   );
                ((FloatVector3D)newQueue.elementAt(i)).setY(   ((FloatVector3D)newQueue.elementAt(i - 1)).getY() + (((FloatVector3D)toFilter.elementAt(i)).getY() - ((FloatVector3D)toFilter.elementAt(i - 1)).getY()) / FILTER_CONSTANT   );
                ((FloatVector3D)newQueue.elementAt(i)).setZ(   ((FloatVector3D)newQueue.elementAt(i - 1)).getZ() + (((FloatVector3D)toFilter.elementAt(i)).getZ() - ((FloatVector3D)toFilter.elementAt(i - 1)).getZ()) / FILTER_CONSTANT   );
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
    */
}