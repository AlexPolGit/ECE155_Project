package ca.uwaterloo.ece155_lab3.unused;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import ca.uwaterloo.ece155_lab3.utils.FloatVector3D;
import ca.uwaterloo.ece155_lab3.MainActivity;
import ca.uwaterloo.sensortoy.LineGraphView;

public class RotationVectorListener implements SensorEventListener
{
    private LineGraphView output;
    private MainActivity main;
    private FloatVector3D readingRV;
    private FloatVector3D highestReadingRV;

    public RotationVectorListener(LineGraphView outputView, MainActivity m)
    {
        output = outputView;
        main = m;
        readingRV = new FloatVector3D();
        highestReadingRV = new FloatVector3D();
    }

    public void zeroRecords()
    {
        highestReadingRV.zeroValues();
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
        //main.setTextOfDebugTextViews(readingLS, highestReadingLS, readingACC, highestReadingACC, readingMS, highestReadingMS, readingRV, highestReadingRV);
    }
}