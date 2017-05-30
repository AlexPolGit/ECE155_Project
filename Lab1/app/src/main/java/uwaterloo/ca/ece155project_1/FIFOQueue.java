package uwaterloo.ca.ece155project_1;

public class FIFOQueue
{
    int size = 1;
    boolean isFull = false;
    Object[] queue;
    
    public FIFOQueue(int s)
    {
        size = s;
        queue = new Object[s];
        for (int i = 0 ; i < s; i++)
        {
            queue[i] = null;
        }
    }

    public int getSize()
    {
        return size;
    }

    public Object[] getArray()
    {
        return queue;
    }
    
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
        }
        
        return false;
    }
    
    public boolean pop()
    {
        Object[] temp = new Object[size - 1];
        System.arraycopy(queue, 0, temp, 0, size - 1);
        queue[0] = null;
        System.arraycopy(temp, 0, queue, 1, size - 1);
        
        return false;
    }

    public Object elementAt(int index)
    {
        return queue[index];
    }
    
    @Override
    public String toString()
    {
        String s = "";
        for (int i = 0; i < size; i++)
        {
            if (queue[i] != null)
            {
                s += queue[i].toString();
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