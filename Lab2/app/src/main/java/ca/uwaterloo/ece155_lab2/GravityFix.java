package ca.uwaterloo.ece155_lab2;

/**
 * Created by virgil on 2017-05-30.
 */

import android.hardware.SensorEvent;

public class GravityFix{

    public void linearAcceleration(SensorEvent event){

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate
        final float alpha=0.8f;
        float gravity[] = {0f, 0f, 0f};

        gravity[0] = alpha * gravity[0] + (1 - alpha)*event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha)*event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha)*event.values[2];

        float linearAcceleration[] = {0f, 0f, 0f};

        linearAcceleration[0] = event.values[0]-gravity[0];
        linearAcceleration[1] = event.values[1]-gravity[1];
        linearAcceleration[2] = event.values[2]-gravity[2];

        //setAccValues(linearAceleration[0],linearAceleration[1], linearAceleration[3]);
    }
}

