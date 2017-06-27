package ca.uwaterloo.ece155_lab4.utils;

import java.util.ArrayList;
import java.util.List;

public class FIFOQueue
{
    int size = 1;
    boolean isFull = false;
    Object[] queue;

    // Constructor for First-In-First-Out Queue
    public FIFOQueue(int s)
    {
        size = s;
        queue = new Object[s];
        for (int i = 0 ; i < s; i++)
        {
            queue[i] = null;
        }
    }

    // returns the maximum capacity of the queue
    public int getSize()
    {
        return size;
    }

    // returns the array used as a queue itself
    public Object[] getArray()
    {
        return queue;
    }

    // pushes an element into the front of the queue, if this makes the number of values over
    // the maximum capacity, it will also pop the last element (FIFO style)
    public boolean push(Object obj)
    {
        if (!isFull)
        {
            for (int i = 0 ; i < size; i++)
            {
                if (queue[i] == null)
                {
                    queue[i] = obj;
                    if (i == size - 1)
                    {
                        //System.out.println("QUEUE FULL");
                        isFull = true;
                    }
                    return true;
                }
            }
        }
        else
        {
            pop();
            queue[0] = obj;
            return true;
        }
        
        return false;
    }

    // removes the last (or "first in") element of the queue
    public boolean pop()
    {
        try
        {
            Object[] temp = new Object[size - 1];
            System.arraycopy(queue, 0, temp, 0, size - 1);
            queue[0] = null;
            System.arraycopy(temp, 0, queue, 1, size - 1);

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    // returns the element at a specified index
    public Object elementAt(int index)
    {
        return queue[index];
    }

    // creates a list object and copies the values of the queue to it
    public List<FloatVector3D> toList()
    {
        List<FloatVector3D> list = new ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            list.add((FloatVector3D)queue[i]);
        }
        return list;
    }

    // custom toString() method
    @Override
    public String toString()
    {
        String s = "";
        for (int i = 0; i < size; i++)
        {
            if (queue[i] != null)
            {
                if (queue[i] instanceof FloatVector3D)
                {
                    s += "(" + ((FloatVector3D)queue[i]).getX() + ", " + ((FloatVector3D)queue[i]).getY() + ", " + ((FloatVector3D)queue[i]).getZ() + ")";
                }
                else
                {
                    s += queue[i].toString();
                }
            }
            else
            {
                break;
            }
            if (i != size)
            {
                s += ", ";
            }
            else
            {
                s += ".";
            }
        }
        return s;
    }
}