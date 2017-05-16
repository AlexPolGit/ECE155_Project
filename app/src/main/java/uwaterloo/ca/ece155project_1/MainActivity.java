package uwaterloo.ca.ece155project_1;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{
    public final static String debugFilter1 = "debug1";

    ca.uwaterloo.sensortoy.LineGraphView lineGraph;
    TextView tv_title;
    TextView[] debugTextViews;
    Button btn_clear;
    Button btn_generateCSV;


    SensorManager sensorManager;
    SensorEventListener listener;
    Sensor lightSensor;
    Sensor accelerometer;
    Sensor magneticSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.scrollLayout);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.graph_title);

        ///// LINE GRAPH /////
        lineGraph = new ca.uwaterloo.sensortoy.LineGraphView
        (
            getApplicationContext(),
            100,
            Arrays.asList(
                    getString(R.string.light_sensor_label),
                    getString(R.string.accelerator_x_label),
                    getString(R.string.accelerator_y_label),
                    getString(R.string.accelerator_z_label),
                    getString(R.string.magnet_sensor_x_label),
                    getString(R.string.magnet_sensor_y_label),
                    getString(R.string.magnet_sensor_z_label))
        );
        lineGraph.setId(lineGraph.generateViewId());
        Log.d(debugFilter1, "Generated ID for lineGraph: " + Integer.toString(lineGraph.getId()));
        relativeLayout.addView(lineGraph);
        RelativeLayout.LayoutParams params_lineGraph = (RelativeLayout.LayoutParams) lineGraph.getLayoutParams();
        params_lineGraph.addRule(RelativeLayout.BELOW, R.id.tv_title);
        params_lineGraph.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_lineGraph.setMargins(10, 100, 10, 100);
        lineGraph.setLayoutParams(params_lineGraph);
        lineGraph.setScaleX(1.75f);
        lineGraph.setScaleY(1.5f);
        lineGraph.setVisibility(View.VISIBLE);

        ///// CLEAR BUTTON /////
        btn_clear = new Button(getApplicationContext());
        btn_clear.setText(R.string.clear_record_data);
        btn_clear.setAllCaps(false);
        btn_clear.setId(btn_clear.generateViewId());
        Log.d(debugFilter1, "Generated ID for btn_clear: " + Integer.toString(btn_clear.getId()));
        relativeLayout.addView(btn_clear);
        RelativeLayout.LayoutParams params_btn_clear = (RelativeLayout.LayoutParams) btn_clear.getLayoutParams();
        params_btn_clear.addRule(RelativeLayout.BELOW, lineGraph.getId());
        params_btn_clear.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_btn_clear.setMargins(10, 10, 10, 10);
        btn_clear.setLayoutParams(params_btn_clear);
        btn_clear.setVisibility(View.VISIBLE);

        ///// CSV BUTTON /////
        btn_generateCSV = new Button(getApplicationContext());
        btn_generateCSV.setText(R.string.generate_csv);
        btn_generateCSV.setAllCaps(false);
        btn_generateCSV.setId(btn_generateCSV.generateViewId());
        Log.d(debugFilter1, "Generated ID for btn_generateCSV: " + Integer.toString(btn_generateCSV.getId()));
        relativeLayout.addView(btn_generateCSV);
        RelativeLayout.LayoutParams params_btn_generateCSV = (RelativeLayout.LayoutParams) btn_generateCSV.getLayoutParams();
        params_btn_generateCSV.addRule(RelativeLayout.BELOW, btn_clear.getId());
        params_btn_generateCSV.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_btn_generateCSV.setMargins(10, 10, 10, 10);
        btn_generateCSV.setLayoutParams(params_btn_generateCSV);
        btn_generateCSV.setVisibility(View.VISIBLE);

        ///// DEBUG TEXT VIEWS /////
        debugTextViews = new TextView[6];
        RelativeLayout.LayoutParams[] params_debugTextViews = new RelativeLayout.LayoutParams[6];
        for (int i = 0; i < 6; i++)
        {
            debugTextViews[i] = new TextView(getApplicationContext());
            debugTextViews[i].setId(debugTextViews[i].generateViewId());
            Log.d(debugFilter1, "Generated ID for debugTextViews " + i + ": " + Integer.toString(debugTextViews[i].getId()));
            relativeLayout.addView(debugTextViews[i]);
            params_debugTextViews[i] = (RelativeLayout.LayoutParams) debugTextViews[i].getLayoutParams();
            if (i == 0)
            {
                params_debugTextViews[i].addRule(RelativeLayout.BELOW, btn_generateCSV.getId());
            }
            else
            {
                params_debugTextViews[i].addRule(RelativeLayout.BELOW, debugTextViews[i - 1].getId());
            }
            params_debugTextViews[i].addRule(RelativeLayout.CENTER_HORIZONTAL);
            params_debugTextViews[i].setMargins(10, 10, 10, 10);
            debugTextViews[i].setLayoutParams(params_debugTextViews[i]);
            debugTextViews[i].setGravity(Gravity.CENTER);
            debugTextViews[i].setVisibility(View.VISIBLE);
        }

        // set the text color for the debugging messages
        for (int i = 0; i < debugTextViews.length; i++) {
            debugTextViews[i].setTextColor(Color.BLACK);
        }

        // output the text for the debugging messages
        debugTextViews[1].setText(R.string.light_sensor_reading_record);
        debugTextViews[2].setText(R.string.accelerometer_reading);
        debugTextViews[3].setText(R.string.accelerometer_reading_record);
        debugTextViews[4].setText(R.string.magnetic_sensor_reading);
        debugTextViews[5].setText(R.string.magnetic_sensor_reading_record);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        listener = new GeneralEventListener(lineGraph, this);

        // get the sensors from the Android device
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // register the sensors to the sensor listeners
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // output the record high sensor reading
        btn_clear.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                debugTextViews[1].setText(getString(R.string.light_sensor_reading_record));
                debugTextViews[3].setText(getString(R.string.accelerometer_reading_record));
                debugTextViews[5].setText(getString(R.string.magnetic_sensor_reading_record));
                listener.onSensorChanged(null);
            }
        });
    }

    public void setTextOfDebugTextViews(float ls, float[] acc, float[] ms, float hls, float[] hacc, float[] hms)
    {
        debugTextViews[0].setText(getString(R.string.light_sensor_reading) + String.valueOf(ls));
        debugTextViews[1].setText(getString(R.string.light_sensor_reading_record) + String.valueOf(hls));
        debugTextViews[2].setText(getString(R.string.accelerometer_reading) + Arrays.toString(acc));
        debugTextViews[3].setText(getString(R.string.accelerometer_reading_record) + Arrays.toString(hacc));
        debugTextViews[4].setText(getString(R.string.magnetic_sensor_reading) + Arrays.toString(ms));
        debugTextViews[5].setText(getString(R.string.magnetic_sensor_reading_record) + Arrays.toString(hms));
    }

    public void clearRecords()
    {

    }
}