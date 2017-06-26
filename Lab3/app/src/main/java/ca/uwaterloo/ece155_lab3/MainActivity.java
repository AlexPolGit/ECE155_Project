package ca.uwaterloo.ece155_lab3;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

import ca.uwaterloo.ece155_lab2.R;

public class MainActivity extends AppCompatActivity
{
    public final static String debugFilter1 = "debug1";
    public final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;

    TextView text_direction;
    ImageView img_gameboard;
    GameBlock gameBlock;

    public float gameboardWidth;
    public float gameboardHeight;
    public static float gameboardUnitWidth;
    public static float gameboardUnitHeight;

    static final int field_filter = 50;

    // sensor values
    SensorManager sensorManager;
    AccelerometerListener listener;
    Sensor accelerometer;

    // runs on initial creation of app
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(debugFilter1, "APP CREATED.");

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        img_gameboard = (ImageView) findViewById(R.id.gameBoard);
        text_direction = (TextView) findViewById(R.id.text_dir);

        gameboardWidth = img_gameboard.getLayoutParams().width;
        gameboardHeight = img_gameboard.getLayoutParams().height;
        gameboardUnitWidth = gameboardWidth / 4;
        gameboardUnitHeight = gameboardHeight / 4;
        Log.d("debug1", "Gameboard Height: " + gameboardHeight + ", Unit: " + gameboardUnitHeight);
        Log.d("debug1", "Gameboard Width: " + gameboardWidth + ", Unit: " + gameboardUnitWidth);

        text_direction.setText("No Dir");

        Timer gameLoop = new Timer();
        GameLoopTask myGameLoop = new GameLoopTask(this, getApplicationContext(), relativeLayout);
        gameLoop.schedule(myGameLoop, 50, 50);

        // initialize the sensor listener and managers
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        listener = new AccelerometerListener(this, myGameLoop);

        // get the sensors from the Android device
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // register the sensors to the sensor listeners
        sensorManager.registerListener(listener, accelerometer, SENSOR_DELAY);
    }

    // runs when app is paused, disables the listener (for performance)
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(debugFilter1, "APP PAUSED.");
        sensorManager.unregisterListener(listener);
    }

    // runs when app is stopped, disables the listener (for performance)
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d(debugFilter1, "APP STOPPED.");
        sensorManager.unregisterListener(listener);
    }

    // runs when app is resumed, re-enables the listener
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(debugFilter1, "APP RESUMED.");
        sensorManager.registerListener(listener, accelerometer, SENSOR_DELAY);
    }

    // runs when app is restarted, re-enables the listener
    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.d(debugFilter1, "APP RESTARTED.");
        sensorManager.registerListener(listener, accelerometer, SENSOR_DELAY);
    }

    // method that sets the text related to the sensor readings to the app view
    public void setTextOfDebugTextViews()
    {
        if (listener.getGesture() == AccelerometerListener.gestures.RIGHT) text_direction.setText(getString(R.string.orientation_right));
        else if (listener.getGesture() == AccelerometerListener.gestures.LEFT) text_direction.setText(getString(R.string.orientation_left));
        else if (listener.getGesture() == AccelerometerListener.gestures.UP) text_direction.setText(getString(R.string.orientation_up));
        else if (listener.getGesture() == AccelerometerListener.gestures.DOWN) text_direction.setText(getString(R.string.orientation_down));
        else if (listener.getGesture() == AccelerometerListener.gestures.NONE) text_direction.setText(getString(R.string.orientation_none));
        else text_direction.setText(getString(R.string.orientation_error));
    }
}