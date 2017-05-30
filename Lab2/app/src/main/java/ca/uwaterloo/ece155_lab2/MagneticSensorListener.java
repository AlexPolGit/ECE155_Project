package ca.uwaterloo.ece155_lab2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import ca.uwaterloo.sensortoy.LineGraphView;

public class MagneticSensorListener implements SensorEventListener
{
    private LineGraphView output;
    private MainActivity main;
    private FloatVector3D readingMS;
    private FloatVector3D highestReadingMS;

    public MagneticSensorListener(LineGraphView outputView, MainActivity m)
    {
        output = outputView;
        main = m;
        readingMS = new FloatVector3D();
        highestReadingMS = new FloatVector3D();
    }

    public void zeroRecords()
    {
        highestReadingMS.zeroValues();
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

        // update the sensor readings on the screen
        //main.setTextOfDebugTextViews(readingLS, highestReadingLS, readingACC, highestReadingACC, readingMS, highestReadingMS, readingRV, highestReadingRV);
    }
}