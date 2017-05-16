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
    private float[] readingACC;
    private float[] readingMS;

    private LinkedList<Vector<Float>> accelerometerReadings;
    private Vector<Float> currentAccelerometerReading;

    private float highestReadingLS;
    private float[] highestReadingACC;
    private float[] highestReadingMS;

    public GeneralEventListener(LineGraphView outputView, MainActivity m)
    {
        output = outputView;
        main = m;

        accelerometerReadings = new LinkedList<>();
        currentAccelerometerReading = new Vector<Float>(3);

        readingLS = 0.0f;
        readingACC = new float[3];
        readingMS = new float[3];
        highestReadingLS = 0.0f;
        highestReadingACC = new float[3];
        highestReadingMS = new float[3];
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
    }

    public void onAccuracyChanged(Sensor s, int i) {}

    private void addToAccelerometerReadings(Vector<Float> vec)
    {
        if (accelerometerReadings.size() == 100)
        {
            accelerometerReadings.pop();
        }
        accelerometerReadings.add(vec);
    }

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
        }
        if (ev.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            readingACC[0] = ev.values[0];
            readingACC[1] = ev.values[1];
            readingACC[2] = ev.values[2];

            //for (int i = 0; i < 3; i++) currentAccelerometerReading.set(i, ev.values[i]);

            //addToAccelerometerReadings(new Vector<Float>(3));
        }
        if (ev.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            readingMS[0] = ev.values[0];
            readingMS[1] = ev.values[1];
            readingMS[2] = ev.values[2];
        }

        if (readingLS > highestReadingLS)
        {
            highestReadingLS = readingLS;
        }
        if ((readingACC[0] + readingACC[1] + readingACC[2]) / 3 > (highestReadingACC[0] + highestReadingACC[1] + highestReadingACC[2]) / 3)
        {
            highestReadingACC[0] = readingACC[0];
            highestReadingACC[1] = readingACC[1];
            highestReadingACC[2] = readingACC[2];
        }
        if ((readingMS[0] + readingMS[1] + readingMS[2]) / 3 > (highestReadingMS[0] + highestReadingMS[1] + highestReadingMS[2]) / 3)
        {
            highestReadingMS[0] = readingMS[0];
            highestReadingMS[1] = readingMS[1];
            highestReadingMS[2] = readingMS[2];
        }

        float[] f = {
                readingLS,
                readingACC[0],
                readingACC[1],
                readingACC[2],
                readingMS[0],
                readingMS[1],
                readingMS[2]
        };
        output.addPoint(f);

        main.setTextOfDebugTextViews(readingLS, readingACC, readingMS, highestReadingLS, highestReadingACC, highestReadingMS);
    }
}