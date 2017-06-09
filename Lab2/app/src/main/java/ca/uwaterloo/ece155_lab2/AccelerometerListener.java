package ca.uwaterloo.ece155_lab2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.ArrayList;

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

    // threshold constants for computing the direction
    // TODO: find the best threshold values after testing
    private final float THRESHOLD_X_POSITIVE = 2.0f;
    private final float THRESHOLD_X_NEGATIVE = -2.0f;
    private final float THRESHOLD_Y_POSITIVE = 2.0f;
    private final float THRESHOLD_Y_NEGATIVE = -2.0f;

    // TODO:
    // enum gesture using the direction in strings.xml

    // get accessor method to return the list of 100 most recent acc sensor readings
    public FIFOQueue getAccelerometerReadings()
    {
        Log.d("debug1", "GETTING ACCEL READINGS: " + accelerometerReadings.toString());
        return accelerometerReadings;
    }

    // return the list of 100 filtered acc sensor readings
    public FIFOQueue getAccelerometerReadingsFiltered()
    {
        Log.d("debug1", "GETTING ACCEL READINGS: " + accelerometerReadingsFiltered.toString());
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

    // method is called if a sensor event has been triggered
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
            filtReadingACC = SignalFilter.getSmoothVector(prevReadingACC, readingACC);
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
                    filtReadingACC.getZ()
            };

            // output each component of the acc (filt and non-filt) vector to the graph on its own individual line
            output.addPoint(f);
            filteredOutput.addPoint(f2);


            //TODO: IMPLEMENT FINITE STATE MACHINE HERE
            // ***note this implementation does not care about the third direction (Z), just ignore it(?)

            // check if the X or Y accelerations have exceeded the threshold range, if yes, begin computing direction
            // then the next (100?) filt values must contain a point that exceeds the negative (or positive)
            // threshold or else the user has not gone back to the original position
            // ***also make sure that only one direction of acceleration has exceeded the threshold NOT BOTH X and Y

            // if X exceeds the threshold range but Y does not change (much)
            if ((filtReadingACC.getX() >= THRESHOLD_X_POSITIVE || filtReadingACC.getX() <= THRESHOLD_X_NEGATIVE)
                    && filtReadingACC.getY() < THRESHOLD_Y_POSITIVE && filtReadingACC.getY() > THRESHOLD_Y_NEGATIVE) {
                if (filtReadingACC.getX() >= THRESHOLD_X_POSITIVE) {
                    // get the next values and check for a drop in acc_X
                    // if there is a value <= threshhold_X_negative then direction = RIGHT
                } else if (filtReadingACC.getX() <= THRESHOLD_X_NEGATIVE) {
                    // get the next values and check for an increase in acc_X
                    // if there is a value >= threshhold_X_positive then direction = LEFT
                }
            // if Y exceeds the threshold range but X does not change (much)
            } else if ((filtReadingACC.getY() >= THRESHOLD_Y_POSITIVE || filtReadingACC.getY() <= THRESHOLD_Y_NEGATIVE)
                    && filtReadingACC.getX() < THRESHOLD_X_POSITIVE && filtReadingACC.getX() > THRESHOLD_X_NEGATIVE) {
                if (filtReadingACC.getY() >= THRESHOLD_Y_POSITIVE) {
                    // get the next values and check for a drop in acc_Y
                    // if there is a value <= threshhold_Y_negative then direction = UP
                } else if (filtReadingACC.getY() <= THRESHOLD_Y_NEGATIVE) {
                    // get the next values and check for a increase in acc_Y
                    // if there is a value >= threshhold_Y_positive then direction = DOWN
                }
            }
        }

        // update the sensor readings on the screen
        main.setTextOfDebugTextViews(readingACC, filtReadingACC);
    }
}