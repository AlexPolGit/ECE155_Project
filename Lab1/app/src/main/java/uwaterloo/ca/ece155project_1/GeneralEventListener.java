package uwaterloo.ca.ece155project_1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.LinkedList;
import java.util.Vector;

import ca.uwaterloo.sensortoy.LineGraphView;

public class GeneralEventListener implements SensorEventListener
{
    private LineGraphView output;
    private MainActivity main;

    private float readingLS;
    private Vector<Float> readingACC;
    private Vector<Float> readingMS;
    private Vector<Float> readingRV;

    private LinkedList<Vector<Float>> accelerometerReadings;

    private float highestReadingLS;
    private float[] highestReadingACC;
    private float[] highestReadingMS;
    private float[] highestReadingRV;

    public LinkedList<Vector<Float>> getAccelerometerReadings() {
        return accelerometerReadings;
    }

    public GeneralEventListener(LineGraphView outputView, MainActivity m)
    {
        output = outputView;
        main = m;

        accelerometerReadings = new LinkedList<>();

        readingLS = 0.0f;
        readingACC = new Vector<Float>();
        readingACC.setSize(3);
        readingMS = new Vector<Float>();
        readingMS.setSize(3);
        readingRV = new Vector<Float>();
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

    public void zeroRecords()
    {
        highestReadingLS = 0.0f;
        highestReadingACC[0] = 0.0f;
        highestReadingACC[1] = 0.0f;
        highestReadingACC[2] = 0.0f;
        highestReadingMS[0] = 0.0f;
        highestReadingMS[1] = 0.0f;
        highestReadingMS[2] = 0.0f;
        highestReadingRV[0] = 0.0f;
        highestReadingRV[1] = 0.0f;
        highestReadingRV[2] = 0.0f;
    }

    public void onAccuracyChanged(Sensor s, int i) {}

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
        if (ev == null)
        {
            zeroRecords();
            output.purge();
            return;
        }
        if (ev.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            readingLS = ev.values[0];

            if (readingLS > highestReadingLS)
            {
                highestReadingLS = readingLS;
            }
        }
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

        float[] f = {
                readingACC.get(0),
                readingACC.get(1),
                readingACC.get(2)
        };
        output.addPoint(f);

        main.setTextOfDebugTextViews(readingLS, highestReadingLS, readingACC, highestReadingACC, readingMS, highestReadingMS, readingRV, highestReadingRV);
    }

    private float get3DVectorAverage(Vector<Float> vect)
    {
        return (vect.get(0) + vect.get(1) + vect.get(2)) / 3;
    }

    private double get3DVectorMagnitude(Vector<Float> vect)
    {
        return Math.sqrt(Math.pow(vect.get(0), 2) + Math.pow(vect.get(1), 2) + Math.pow(vect.get(2), 2));
    }

    private float get3ArrayAverage(float[] arr)
    {
        return (arr[0] + arr[1] + arr[2]) / 3;
    }

    private double get3DArrayMagnitude(float[] arr)
    {
        return Math.sqrt(Math.pow(arr[0], 2) + Math.pow(arr[1], 2) + Math.pow(arr[2], 2));
    }
}