package ca.uwaterloo.ece155_lab2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import ca.uwaterloo.sensortoy.LineGraphView;

// class that handles the events triggered by the sensors, buttons and line graph
public class GeneralEventListener implements SensorEventListener
{
    // object references
    private LineGraphView output;
    private MainActivity main;

    // stores the current value of the sensor readings
    private float readingLS;
    private FloatVector3D readingACC;
    private FloatVector3D readingMS;
    private FloatVector3D readingRV;
    private FloatVector3D f;

    // list to contain the 100 most recent acc readings
    private FIFOQueue accelerometerReadings;

    // get accessor method to return the list of 100 most recent acc sensor readings
    public FIFOQueue getAccelerometerReadings()
    {
        Log.d("debug1", "GETTING ACCEL READINGS: " + accelerometerReadings.toString());
        return accelerometerReadings;
    }

    // stores the history record of highest sensor values
    private float highestReadingLS;
    private FloatVector3D highestReadingACC;
    private FloatVector3D highestReadingMS;
    private FloatVector3D highestReadingRV;

    // constructor that creates a new data entry for the graph
    public GeneralEventListener(LineGraphView outputView, MainActivity m)
    {
        output = outputView;
        main = m;

        accelerometerReadings = new FIFOQueue(100);

        readingLS = 0.0f;
        readingACC = new FloatVector3D();
        readingMS = new FloatVector3D();
        readingRV = new FloatVector3D();

        highestReadingLS = 0.0f;
        highestReadingACC = new FloatVector3D();
        highestReadingMS = new FloatVector3D();
        highestReadingRV = new FloatVector3D();
    }

    //  clears the history of records
    public void zeroRecords()
    {
        highestReadingLS = 0.0f;
        highestReadingACC.zeroValues();
        highestReadingMS.zeroValues();
        highestReadingRV.zeroValues();
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

    // method is called if a sensor event has been triggered
    public void onSensorChanged(SensorEvent ev)
    {
        // if the sensor event is null then clear the history of record values, the line graph and the current readings
        if (ev == null)
        {
            zeroRecords();
            output.purge();
            return;
        }
        // Add new light reading to light sensor value and check if the record high should be changed
        else if (ev.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            readingLS = ev.values[0];

            if (readingLS > highestReadingLS)
            {
                highestReadingLS = readingLS;
            }
        }
        // Add new acc reading to the acc sensor value and check if the record high should be changed
        else if (ev.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            readingACC.setX(ev.values[0]);
            readingACC.setY(ev.values[1]);
            readingACC.setZ(ev.values[2]);
            addToAccelerometerReadings(ev.values[0], ev.values[1], ev.values[2]);

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
            // output each component of the acc vector to the graph on its own individual line
            output.addPoint(f);
        }
        // Add new MF reading to the MF sensor value and check if the record high should be changed
        else if (ev.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            readingMS.setX(ev.values[0]);
            readingMS.setY(ev.values[1]);
            readingMS.setZ(ev.values[2]);

            if (readingMS.getMagnitude() > highestReadingMS.getMagnitude())
            {
                highestReadingMS.setX(readingMS.getX());
                highestReadingMS.setY(readingMS.getY());
                highestReadingMS.setZ(readingMS.getZ());
            }
        }
        // Add new RT reading to the RT sensor value and check if the record high should be changed
        else if (ev.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            readingRV.setX(ev.values[0]);
            readingRV.setY(ev.values[1]);
            readingRV.setZ(ev.values[2]);

            if (readingRV.getMagnitude() > highestReadingRV.getMagnitude())
            {
                highestReadingRV.setX(readingRV.getX());
                highestReadingRV.setY(readingRV.getY());
                highestReadingRV.setZ(readingRV.getZ());
            }
        }

        // update the sensor readings on the screen
        main.setTextOfDebugTextViews(readingLS, highestReadingLS, readingACC, highestReadingACC, readingMS, highestReadingMS, readingRV, highestReadingRV);
    }
}