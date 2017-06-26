package ca.uwaterloo.ece155_lab2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import ca.uwaterloo.ece155_lab2.utils.FIFOQueue;
import ca.uwaterloo.ece155_lab2.utils.FloatVector3D;
import ca.uwaterloo.sensortoy.LineGraphView;

public class AccelerometerListener implements SensorEventListener
{
    // object references
    private LineGraphView output;
    private LineGraphView filteredOutput;
    private MainActivity main;

    // stores the values for the acc readings
    private FloatVector3D readingACC;
    private FloatVector3D prevReadingACC;
    private FloatVector3D filtReadingACC;
    private FloatVector3D f;

    // queue object to contain the 100 most recent acc readings
    private FIFOQueue accelerometerReadings;
    private FIFOQueue accelerometerReadingsFiltered;

    // FSM class declaration
    SignalProcessor signalProcessor = new SignalProcessor();

    // number of readings since last FSM update
    private int accCount = 0;

    // the possible hand gestures and the current gesture
    public enum gestures
    {
        NONE, ERR, RIGHT, LEFT, UP, DOWN
    }
    public static gestures gesture = gestures.NONE;

    // the possible FSM states and the current state
    public enum states
    {
        UNKNOWN, WAIT, X_INCR, Z_INCR, X_DECR, Z_DECR
    }
    public static states currentState = states.WAIT;

    // X and Z thresholds
    public static final float Xthreshhold = 1f;
    public static final float Zthreshhold = 1f;

    // is it safe to update the FSM?
    public static boolean isSafe = true;

    // time-out after FSM update is declared unsafe
    private final int safeTime = 30;
    private int s = safeTime;

    // predefined number of readings before FSM is updated
    private final int updateCount = 10;

    // get accessor method to return the list of 100 most recent acc sensor readings
    public FIFOQueue getAccelerometerReadings()
    {
        //Log.d("debug1", "GETTING ACCEL READINGS: " + accelerometerReadings.toString());
        return accelerometerReadings;
    }

    // return the list of 100 filtered acc sensor readings
    public FIFOQueue getAccelerometerReadingsFiltered()
    {
        //Log.d("debug1", "GETTING ACCEL READINGS: " + accelerometerReadingsFiltered.toString());
        return accelerometerReadingsFiltered;
    }

    // stores the history record of accelerometer
    private FloatVector3D highestReadingACC;

    // constructor that creates a new data entry for the graph
    public AccelerometerListener(LineGraphView outputView, LineGraphView outputViewFiltered, MainActivity m)
    {
        output = outputView;
        filteredOutput = outputViewFiltered;
        main = m;
        accelerometerReadings = new FIFOQueue(100);
        accelerometerReadingsFiltered = new FIFOQueue(100);
        readingACC = new FloatVector3D();
        filtReadingACC = new FloatVector3D();
        highestReadingACC = new FloatVector3D();
    }

    //  clears the history of records
    public void zeroRecords()
    {
        highestReadingACC.zeroValues();
    }

    // detect if the accuracy of the sensor has changed
    public void onAccuracyChanged(Sensor s, int i) {}

    // everytime an acc sensor is read, add it to the queue of 100 most recent acc readings
    // if however there are more than 100 elements, push the oldest reading out of the queue
    private void addToAccelerometerReadings(float x, float y, float z)
    {
        f = new FloatVector3D(x, y, z);
        accelerometerReadings.push(f);
    }

    // same as above except for filtered acc readings
    private void addToFilteredAccelerometerReadings(float x, float y, float z)
    {
        f = new FloatVector3D(x, y, z);
        accelerometerReadingsFiltered.push(f);
    }

    // change the current gesture to the specified gesture
    public static void changeGesture(gestures changeTo)
    {
        gesture = changeTo;
    }

    // get the current gesture (for text view)
    public static gestures getGesture()
    {
        return gesture;
    }

    // applies LPF to the accelerometer readings
    private FloatVector3D getSmoothVector()
    {
        FloatVector3D vec = new FloatVector3D();
        vec.setX(   prevReadingACC.getX() + (readingACC.getX() - prevReadingACC.getX()) / MainActivity.field_filter.getValue()   );
        vec.setY(   prevReadingACC.getY() + (readingACC.getY() - prevReadingACC.getY()) / MainActivity.field_filter.getValue()   );
        vec.setZ(   prevReadingACC.getZ() + (readingACC.getZ() - prevReadingACC.getZ()) / MainActivity.field_filter.getValue()   );
        return vec;
    }

    // method is called if a sensor event has been triggered, also updates the FSM every defined number of readings
    public void onSensorChanged(SensorEvent ev)
    {
        // Add new acc reading to the acc sensor value and check if the record high should be changed
        if (ev.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            if (prevReadingACC == null)
            {
                prevReadingACC = new FloatVector3D();
            }
            else
            {
                prevReadingACC.setX(filtReadingACC.getX());
                prevReadingACC.setY(filtReadingACC.getY());
                prevReadingACC.setZ(filtReadingACC.getZ());
            }
            readingACC.setX(ev.values[0]);
            readingACC.setY(ev.values[1]);
            readingACC.setZ(ev.values[2]);
            // apply
            filtReadingACC = getSmoothVector();
            addToAccelerometerReadings(ev.values[0], ev.values[1], ev.values[2]);
            addToFilteredAccelerometerReadings(filtReadingACC.getX(), filtReadingACC.getY(), filtReadingACC.getZ());

            if (readingACC.getMagnitude() > highestReadingACC.getMagnitude())
            {
                highestReadingACC.setX(readingACC.getX());
                highestReadingACC.setY(readingACC.getY());
                highestReadingACC.setZ(readingACC.getZ());
            }

            // get the acc vector from the newest acc sensor readings
            float[] f = {
                    readingACC.getX(),
                    readingACC.getY(),
                    readingACC.getZ()
            };

            // get the filtered acc vector
            float[] f2 = {
                    filtReadingACC.getX(),
                    filtReadingACC.getY(),
                    filtReadingACC.getZ(),
                    Xthreshhold,
                    -Xthreshhold
            };

            // output each component of the acc (filt and non-filt) vector to the graph on its own individual line
            output.addPoint(f);
            filteredOutput.addPoint(f2);
        }

        // update the sensor readings on the screen
        main.setTextOfDebugTextViews(readingACC, filtReadingACC);

        // updates the FSM every [updateCount] readings of the accelerometer
        if (accCount < updateCount)
        {
            accCount++;
        }
        // doesn't allow the FSM to update if a gesture change has recently occurred
        else if (!isSafe)
        {
            s--;
            // if s = 0, then it is now "safe" to update the FSM again
            if (s == 0)
            {
                isSafe = true;
                s = safeTime;
                Log.d("debug1", "now safe");
            }
        }
        // FSM is permitted to update
        else if (isSafe)
        {
            // If in WAIT state and |x| > |z|, give priority to the X motion
            if (Math.abs(filtReadingACC.getX()) >= Math.abs(filtReadingACC.getZ()) && currentState == states.WAIT)
            {
                signalProcessor.fsmX(filtReadingACC.getX());
            }
            // If in WAIT state and |x| < |z|, give priority to the Z motion
            else if (Math.abs(filtReadingACC.getX()) < Math.abs(filtReadingACC.getZ()) && currentState == states.WAIT)
            {
                signalProcessor.fsmZ(filtReadingACC.getZ());
            }
            // If in X-based state
            else if (currentState == states.X_INCR || currentState == states.X_DECR )
            {
                signalProcessor.fsmX(filtReadingACC.getX());
            }
            // If in Z-based state
            else if (currentState == states.Z_INCR || currentState == states.Z_DECR )
            {
                signalProcessor.fsmZ(filtReadingACC.getZ());
            }

            // start counting up again
            accCount = 0;
        }
    }
}