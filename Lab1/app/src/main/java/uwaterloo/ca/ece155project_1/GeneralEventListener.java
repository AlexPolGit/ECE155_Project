package uwaterloo.ca.ece155project_1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.LinkedList;
import java.util.Vector;

import ca.uwaterloo.sensortoy.LineGraphView;

// class that handles the events triggered by the sensors, buttons and line graph
public class GeneralEventListener implements SensorEventListener
{
    // object references
    private LineGraphView output;
    private MainActivity main;

    // stores the current value of the sensor readings
    private float readingLS;
    private Vector<Float> readingACC;
    private Vector<Float> readingMS;
    private Vector<Float> readingRV;

    // list to contain the 100 most recent acc readings
    private LinkedList<Vector<Float>> accelerometerReadings;
    // get accessor method to return the list of 100 most recent acc sensor readings
    public LinkedList<Vector<Float>> getAccelerometerReadings() {
        return accelerometerReadings;
    }

    // stores the history record of highest sensor values
    private float highestReadingLS;
    private float[] highestReadingACC;
    private float[] highestReadingMS;
    private float[] highestReadingRV;

    // constructor that creates a new data entry for the graph
    public GeneralEventListener(LineGraphView outputView, MainActivity m)
    {
        output = outputView;
        main = m;

        accelerometerReadings = new LinkedList<>();

        readingLS = 0.0f;
        readingACC = new Vector<>();
        readingACC.setSize(3);
        readingMS = new Vector<>();
        readingMS.setSize(3);
        readingRV = new Vector<>();
        readingRV.setSize(3);

        for (int i = 0; i < 3; i++)
        {
            readingACC.setElementAt(0.0f, 0);
            readingMS.setElementAt(0.0f, 0);
            readingRV.setElementAt(0.0f, 0);
        }

        highestReadingLS = 0.0f;
        highestReadingACC = new float[3];
        highestReadingMS = new float[3];
        highestReadingRV = new float[3];
    }

    //  clears the history of records
    public void zeroRecords()
    {
        highestReadingLS = 0.0f;
        for (int i = 0; i < 3; i++)
        {
            highestReadingACC[i] = 0.0f;
            highestReadingMS[i] = 0.0f;
            highestReadingRV[i] = 0.0f;
        }

    }

    // detect if the accuracy of the sensor has changed
    public void onAccuracyChanged(Sensor s, int i) {}

    // everytime an acc sensor is read, add it to the queue of 100 most recent acc readings
    // if however there are more than 100 elements, push the oldest reading out of the queue
    private void addToAccelerometerReadings()
    {
        if (accelerometerReadings.size() == 100)
        {
            accelerometerReadings.pop();
        }
        accelerometerReadings.add(readingACC);
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
        if (ev.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            readingLS = ev.values[0];

            if (readingLS > highestReadingLS)
            {
                highestReadingLS = readingLS;
            }
        }
        // Add new acc reading to the acc sensor value and check if the record high should be changed
        if (ev.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            readingACC.setElementAt(ev.values[0], 0);
            readingACC.setElementAt(ev.values[1], 1);
            readingACC.setElementAt(ev.values[2], 2);
            addToAccelerometerReadings();

            if (get3DVectorMagnitude(readingACC) > get3DArrayMagnitude(highestReadingACC))
            {
                highestReadingACC[0] = readingACC.get(0);
                highestReadingACC[1] = readingACC.get(1);
                highestReadingACC[2] = readingACC.get(2);
            }
        }
        // Add new MF reading to the MF sensor value and check if the record high should be changed
        if (ev.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            readingMS.setElementAt(ev.values[0], 0);
            readingMS.setElementAt(ev.values[1], 1);
            readingMS.setElementAt(ev.values[2], 2);

            if (get3DVectorMagnitude(readingMS) > get3DArrayMagnitude(highestReadingMS))
            {
                highestReadingMS[0] = readingMS.get(0);
                highestReadingMS[1] = readingMS.get(1);
                highestReadingMS[2] = readingMS.get(2);
            }
        }
        // Add new RT reading to the RT sensor value and check if the record high should be changed
        if (ev.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            readingRV.setElementAt(ev.values[0], 0);
            readingRV.setElementAt(ev.values[1], 1);
            readingRV.setElementAt(ev.values[2], 2);

            if (get3DVectorMagnitude(readingRV) > get3DArrayMagnitude(highestReadingRV))
            {
                highestReadingRV[0] = readingRV.get(0);
                highestReadingRV[1] = readingRV.get(1);
                highestReadingRV[2] = readingRV.get(2);
            }
        }

        // get the acc vector from the newest acc sensor readings
        float[] f = {
                readingACC.get(0),
                readingACC.get(1),
                readingACC.get(2)
        };
        // output each component of the acc vector to the graph on its own individual line
        output.addPoint(f);

        // update the sensor readings on the screen
        main.setTextOfDebugTextViews(readingLS, highestReadingLS, readingACC, highestReadingACC, readingMS, highestReadingMS, readingRV, highestReadingRV);
    }

    // returns the vector average of a vector's coordinates
    private float get3DVectorAverage(Vector<Float> vect)
    {
        return (vect.get(0) + vect.get(1) + vect.get(2)) / 3;
    }

    // returns the vector magnitude
    private double get3DVectorMagnitude(Vector<Float> vect)
    {
        return Math.sqrt(Math.pow(vect.get(0), 2) + Math.pow(vect.get(1), 2) + Math.pow(vect.get(2), 2));
    }

    // returns the average of an array of 3 elements
    private float get3ArrayAverage(float[] arr)
    {
        return (arr[0] + arr[1] + arr[2]) / 3;
    }

    // returns the magnitude of an array of 3 elements
    private double get3DArrayMagnitude(float[] arr)
    {
        return Math.sqrt(Math.pow(arr[0], 2) + Math.pow(arr[1], 2) + Math.pow(arr[2], 2));
    }
}