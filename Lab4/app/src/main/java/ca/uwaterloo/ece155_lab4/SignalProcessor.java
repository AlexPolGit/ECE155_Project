package ca.uwaterloo.ece155_lab4;

import android.util.Log;

import ca.uwaterloo.ece155_lab4.utils.Direction;

public class SignalProcessor
{
    GameLoopTask gameLoopTask;

    public SignalProcessor(GameLoopTask loop)
    {
        gameLoopTask = loop;
    }

    // time-out variables for FSM
    private int timeOutX = 30;
    private int cX = timeOutX;
    private int timeOutZ = 30;
    private int cZ = timeOutZ;

    // FSM for X axis (right-left)
    public void fsmX(float reading)
    {
        boolean doFSM = true;

            switch (AccelerometerListener.currentState)
            {
                // Base X state, waiting for acceleration to surpass threshold
                case WAIT:
                {
                    if (reading > AccelerometerListener.Xthreshhold)
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.X_INCR;
                    }
                    else if (reading < -AccelerometerListener.Xthreshhold)
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.X_DECR;
                    }
                    else
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                    }
                    break;
                }
                // X has a positive motion, this indicates the start of a "RIGHT" gesture.
                // check if the gesture really is right (if it dips back below zero)
                case X_INCR:
                {
                    if (reading < 0)
                    {
                        AccelerometerListener.changeGesture(AccelerometerListener.gestures.RIGHT);
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                        cX = timeOutX;
                        AccelerometerListener.isSafe = false;
                        gameLoopTask.doMove(Direction.RIGHT);

                        Log.d("debug1", "RIGHT");
                    }
                    else if (cX == 0)
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                        cX = timeOutX;
                    }
                    else
                    {
                        cX--;
                    }
                    break;
                }
                // X has a negative motion, this indicates the start of a "LEFT" gesture.
                // check if the gesture really is left (if it dips back above zero)
                case X_DECR:
                {
                    if (reading > 0)
                    {
                        AccelerometerListener.changeGesture(AccelerometerListener.gestures.LEFT);
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                        cX = timeOutX;
                        AccelerometerListener.isSafe = false;
                        gameLoopTask.doMove(Direction.LEFT);

                        Log.d("debug1", "LEFT");
                    }
                    else if (cX == 0)
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                        cX = timeOutX;
                    }
                    else
                    {
                        cX--;
                    }
                    break;
                }
                // default error state
                default:
                {
                    AccelerometerListener.changeGesture(AccelerometerListener.gestures.ERR);
                    AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                    break;
                }
            }

    }

    // FSM for Z axis (up-down)
    public void fsmZ(float reading)
    {
            switch (AccelerometerListener.currentState)
            {
                // Base Z state, waiting for acceleration to surpass threshold
                case WAIT:
                {
                    if (reading > AccelerometerListener.Zthreshhold)
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.Z_INCR;
                    }
                    else if (reading < -AccelerometerListener.Zthreshhold)
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.Z_DECR;
                    }
                    else
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                    }
                    break;
                }
                // Z has a positive motion, this indicates the start of a "UP" gesture.
                // check if the gesture really is up (if it dips back below zero)
                case Z_INCR:
                {
                    if (reading < 0)
                    {
                        AccelerometerListener.changeGesture(AccelerometerListener.gestures.UP);
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                        cZ = timeOutZ;
                        AccelerometerListener.isSafe = false;
                        gameLoopTask.doMove(Direction.UP);

                        Log.d("debug1", "UP");
                    }
                    else if (cZ == 0)
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                        cZ = timeOutZ;
                    }
                    else
                    {
                        cZ--;
                    }
                    break;
                }
                // Z has a negative motion, this indicates the start of a "DOWN" gesture.
                // check if the gesture really is down (if it dips back above zero)
                case Z_DECR:
                {
                    if (reading > 0)
                    {
                        AccelerometerListener.changeGesture(AccelerometerListener.gestures.DOWN);
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                        cZ = timeOutZ;
                        AccelerometerListener.isSafe = false;
                        gameLoopTask.doMove(Direction.DOWN);

                        Log.d("debug1", "DOWN");
                    }
                    else if (cZ == 0)
                    {
                        AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                        cZ = timeOutZ;
                    }
                    else
                    {
                        cZ--;
                    }
                    break;
                }
                // default error state
                default:
                {
                    AccelerometerListener.changeGesture(AccelerometerListener.gestures.ERR);
                    AccelerometerListener.currentState = AccelerometerListener.states.WAIT;
                    break;
                }
            }
    }
}