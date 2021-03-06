package uwaterloo.ca.ece155project_1;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{
    public final static String debugFilter1 = "debug1";
    public final int sensorDelay = SensorManager.SENSOR_DELAY_GAME;

    ca.uwaterloo.sensortoy.LineGraphView lineGraph;
    TextView tv_title;
    TextView[] debugTextViews;
    Button btn_clear;
    Button btn_generateCSV;
    Button btn_openFolder;

    // sensor values
    SensorManager sensorManager;
    GeneralEventListener listener;
    Sensor lightSensor;
    Sensor accelerometer;
    Sensor magneticSensor;
    Sensor rotationSensor;

    // object to reference the class that manages file output to the csv file
    private CreateCsvFile cFile = new CreateCsvFile(this);
    // the file name of the file to output the accelerometer readings to
    private String fileName = "accelerometer_data.csv";

    // runs on initial creation of app
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(debugFilter1, "APP CREATED.");

        // set the toast text and interaction for the buttons
        final Toast toastClear = Toast.makeText(getApplicationContext(), getString(R.string.toast_clear), Toast.LENGTH_LONG);
        final Toast toastCSV = Toast.makeText(getApplicationContext(), getString(R.string.toast_create_csv), Toast.LENGTH_LONG);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.scrollLayout);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.graph_title);

        //region LINE_GRAPH
        // create a new linegraph using acc values
        lineGraph = new ca.uwaterloo.sensortoy.LineGraphView
        (
            getApplicationContext(),
            100,
            Arrays.asList(
                    getString(R.string.accelerator_x_label),
                    getString(R.string.accelerator_y_label),
                    getString(R.string.accelerator_z_label)
                    )
        );
        // add the line graph to the app view
        lineGraph.setId(lineGraph.generateViewId());
        Log.d(debugFilter1, "Generated ID for lineGraph: " + Integer.toString(lineGraph.getId()));
        relativeLayout.addView(lineGraph);

        // format the linegraph view on the app view
        RelativeLayout.LayoutParams params_lineGraph = (RelativeLayout.LayoutParams) lineGraph.getLayoutParams();
        params_lineGraph.addRule(RelativeLayout.BELOW, R.id.tv_title);
        params_lineGraph.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_lineGraph.setMargins(10, 100, 10, 100);
        lineGraph.setLayoutParams(params_lineGraph);
        lineGraph.setScaleX(1.75f);
        lineGraph.setScaleY(1.5f);
        lineGraph.setVisibility(View.VISIBLE);
        //endregion

        //region CLEAR_BUTTON
        // generate the clear record high history data button
        btn_clear = new Button(getApplicationContext());
        btn_clear.setText(R.string.clear_record_data);
        btn_clear.setAllCaps(false);
        btn_clear.setId(btn_clear.generateViewId());
        Log.d(debugFilter1, "Generated ID for btn_clear: " + Integer.toString(btn_clear.getId()));

        // format the clear button view on the app view
        relativeLayout.addView(btn_clear);
        RelativeLayout.LayoutParams params_btn_clear = (RelativeLayout.LayoutParams) btn_clear.getLayoutParams();
        params_btn_clear.addRule(RelativeLayout.BELOW, lineGraph.getId());
        params_btn_clear.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_btn_clear.setMargins(10, 10, 10, 10);
        btn_clear.setLayoutParams(params_btn_clear);
        btn_clear.setVisibility(View.VISIBLE);
        //endregion

        //region CSV_BUTTON
        // create the 100 acc output to csv button
        btn_generateCSV = new Button(getApplicationContext());
        btn_generateCSV.setText(R.string.generate_csv);
        btn_generateCSV.setAllCaps(false);
        btn_generateCSV.setId(btn_generateCSV.generateViewId());
        Log.d(debugFilter1, "Generated ID for btn_generateCSV: " + Integer.toString(btn_generateCSV.getId()));

        // format the button view on the app view
        relativeLayout.addView(btn_generateCSV);
        RelativeLayout.LayoutParams params_btn_generateCSV = (RelativeLayout.LayoutParams) btn_generateCSV.getLayoutParams();
        params_btn_generateCSV.addRule(RelativeLayout.BELOW, btn_clear.getId());
        params_btn_generateCSV.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_btn_generateCSV.setMargins(10, 10, 10, 10);
        btn_generateCSV.setLayoutParams(params_btn_generateCSV);
        btn_generateCSV.setVisibility(View.VISIBLE);
        //endregion

        //region OPEN_FOLDER_BUTTON
        // create the 100 acc output to csv button
        btn_openFolder = new Button(getApplicationContext());
        btn_openFolder.setText(R.string.open_folder);
        btn_openFolder.setAllCaps(false);
        btn_openFolder.setId(btn_openFolder.generateViewId());
        Log.d(debugFilter1, "Generated ID for btn_openFolder: " + Integer.toString(btn_openFolder.getId()));

        // format the button view on the app view
        relativeLayout.addView(btn_openFolder);
        RelativeLayout.LayoutParams params_btn_openFolder = (RelativeLayout.LayoutParams) btn_openFolder.getLayoutParams();
        params_btn_openFolder.addRule(RelativeLayout.BELOW, btn_generateCSV.getId());
        params_btn_openFolder.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_btn_openFolder.setMargins(10, 10, 10, 10);
        btn_openFolder.setLayoutParams(params_btn_openFolder);
        btn_openFolder.setVisibility(View.VISIBLE);
        //endregion


        //region DEBUG_TEXTVIEWS
        // format the text outputted to the app view
        final int NUMBER_OF_TEXT_VIEWS = 8;
        debugTextViews = new TextView[NUMBER_OF_TEXT_VIEWS];
        RelativeLayout.LayoutParams[] params_debugTextViews = new RelativeLayout.LayoutParams[NUMBER_OF_TEXT_VIEWS];
        for (int i = 0; i < NUMBER_OF_TEXT_VIEWS; i++)
        {
            debugTextViews[i] = new TextView(getApplicationContext());
            debugTextViews[i].setId(debugTextViews[i].generateViewId());
            Log.d(debugFilter1, "Generated ID for debugTextViews " + i + ": " + Integer.toString(debugTextViews[i].getId()));
            relativeLayout.addView(debugTextViews[i]);
            params_debugTextViews[i] = (RelativeLayout.LayoutParams) debugTextViews[i].getLayoutParams();
            if (i == 0)
            {
                params_debugTextViews[i].addRule(RelativeLayout.BELOW, btn_openFolder.getId());
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
        //endregion

        // initialize the sensor listener and managers
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        listener = new GeneralEventListener(lineGraph, this);

        // get the sensors from the Android device
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // register the sensors to the sensor listeners
        sensorManager.registerListener(listener, lightSensor, sensorDelay);
        sensorManager.registerListener(listener, accelerometer, sensorDelay);
        sensorManager.registerListener(listener, magneticSensor, sensorDelay);
        sensorManager.registerListener(listener, rotationSensor, sensorDelay);

        //region BUTTON_EVENT_HANDLERS
        // clear the record high sensor measurements when the clear button is pressed
        btn_clear.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                debugTextViews[1].setText(getString(R.string.light_sensor_reading_record));
                debugTextViews[3].setText(getString(R.string.accelerometer_reading_record));
                debugTextViews[5].setText(getString(R.string.magnetic_sensor_reading_record));
                debugTextViews[7].setText(getString(R.string.rotation_sensor_reading_record));
                listener.onSensorChanged(null);
                toastClear.show();
            }
        });

        // generate and read into a .csv file the past 100 readings for the accelerometer
        btn_generateCSV.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            cFile.generateCsvFile(fileName);
            toastCSV.show();
            }
        });

        // open the folder if the .csv file
        btn_openFolder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openFolder();
            }
        });
        //endregion
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
        sensorManager.registerListener(listener, lightSensor, sensorDelay);
        sensorManager.registerListener(listener, accelerometer, sensorDelay);
        sensorManager.registerListener(listener, magneticSensor, sensorDelay);
        sensorManager.registerListener(listener, rotationSensor, sensorDelay);
    }

    // runs when app is restarted, re-enables the listener
    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.d(debugFilter1, "APP RESTARTED.");
        sensorManager.registerListener(listener, lightSensor, sensorDelay);
        sensorManager.registerListener(listener, accelerometer, sensorDelay);
        sensorManager.registerListener(listener, magneticSensor, sensorDelay);
        sensorManager.registerListener(listener, rotationSensor, sensorDelay);
    }

    // method that sets the text related to the sensor readings to the app view
    public void setTextOfDebugTextViews(float ls, float hls, FloatVector3D acc, FloatVector3D hacc, FloatVector3D ms, FloatVector3D hms, FloatVector3D rv, FloatVector3D hrv)
    {
        debugTextViews[0].setText(Html.fromHtml(getString(R.string.light_sensor_reading) + String.format("<br> %.0f", ls) + " lx"));
        debugTextViews[1].setText(Html.fromHtml(getString(R.string.light_sensor_reading_record) + String.format("<br> %.0f", hls) + " lx"));
        debugTextViews[2].setText(Html.fromHtml(getString(R.string.accelerometer_reading) + String.format("<br>(<font color=#ff0000>%.3f</font>, <font color=#008000>%.3f</font>, <font color=#0000ff>%.3f</font>)", acc.getX(), acc.getY(), acc.getZ()) + " m/s²"));
        debugTextViews[3].setText(Html.fromHtml(getString(R.string.accelerometer_reading_record) + String.format("<br>(<font color=#ff0000>%.3f</font>, <font color=#008000>%.3f</font>, <font color=#0000ff>%.3f</font>)", hacc.getX(), hacc.getY(), hacc.getZ()) + " m/s²"));
        debugTextViews[4].setText(Html.fromHtml(getString(R.string.magnetic_sensor_reading) + String.format("<br>(%.3f, %.3f, %.3f)", ms.getX(), ms.getY(), ms.getZ()) + " μT"));
        debugTextViews[5].setText(Html.fromHtml(getString(R.string.magnetic_sensor_reading_record) + String.format("<br>(%.3f, %.3f, %.3f)", hms.getX(), hms.getY(), hms.getZ()) + " μT"));
        debugTextViews[6].setText(Html.fromHtml(getString(R.string.rotation_sensor_reading) + String.format("<br>(%.3f, %.3f, %.3f)", rv.getX(), rv.getY(), rv.getZ())));
        debugTextViews[7].setText(Html.fromHtml(getString(R.string.rotation_sensor_reading_record) + String.format("<br>(%.3f, %.3f, %.3f)", hrv.getX(), hrv.getY(), hrv.getZ())));
    }

    // opens the folder of the csv file
    public void openFolder()
    {
        Log.d(debugFilter1, "Opening Folder: " + new File(getExternalFilesDir("Accelerometer Data"), fileName).getAbsolutePath());

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(Uri.parse(new File(getExternalFilesDir("Accelerometer Data"), fileName).getAbsolutePath()), "file/*");
        startActivity(intent);
    }
}