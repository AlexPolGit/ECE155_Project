package ca.uwaterloo.ece155_lab3.unused;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import ca.uwaterloo.ece155_lab3.MainActivity;
import ca.uwaterloo.sensortoy.LineGraphView;

public class LightSensorListener implements SensorEventListener
{
    private LineGraphView output;
    private MainActivity main;
    private float readingLS;
    private float highestReadingLS;

    public LightSensorListener(LineGraphView outputView, MainActivity m)
    {
        output = outputView;
        main = m;
        readingLS = 0.0f;
        highestReadingLS = 0.0f;
    }

    public void zeroRecords()
    {
        highestReadingLS = 0.0f;
    }

    public void onAccuracyChanged(Sensor s, int i) {}

    public void onSensorChanged(SensorEvent ev)
    {
        if (ev == null)
        {
            zeroRecords();
            output.purge();
            return;
        }
        else if (ev.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            readingLS = ev.values[0];

            if (readingLS > highestReadingLS)
            {
                highestReadingLS = readingLS;
            }
        }

        // update the sensor readings on the screen
        //main.setTextOfDebugTextViews(readingLS, highestReadingLS, readingACC, highestReadingACC, readingMS, highestReadingMS, readingRV, highestReadingRV);
    }
}