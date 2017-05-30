package ca.uwaterloo.ece155_lab2;

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
    ca.uwaterloo.sensortoy.LineGraphView filteredLineGraph;
    TextView tv_title;
    TextView text_ACC;
    TextView text_Direction;
    Button btn_generateCSV;

    // sensor values
    SensorManager sensorManager;
    GeneralEventListener listener;
    Sensor accelerometer;

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
        params_lineGraph.setMargins(10, 100, 10, 125);
        lineGraph.setLayoutParams(params_lineGraph);
        lineGraph.setScaleX(1.75f);
        lineGraph.setScaleY(1.5f);
        lineGraph.setVisibility(View.VISIBLE);

        // create another line graph for the filtered values
        filteredLineGraph = new ca.uwaterloo.sensortoy.LineGraphView
        (
            getApplicationContext(),
            100,
            Arrays.asList(
                    getString(R.string.filtered_accelerator_x_label),
                    getString(R.string.filtered_accelerator_y_label),
                    getString(R.string.filtered_accelerator_z_label)
                    )
        );
        // add the line graph to the app view
        filteredLineGraph.setId(filteredLineGraph.generateViewId());
        Log.d(debugFilter1, "Generated ID for lineGraph: " + Integer.toString(filteredLineGraph.getId()));
        relativeLayout.addView(filteredLineGraph);

        // format the linegraph view on the app view
        RelativeLayout.LayoutParams params_filteredLineGraph = (RelativeLayout.LayoutParams) filteredLineGraph.getLayoutParams();
        params_filteredLineGraph.addRule(RelativeLayout.BELOW, lineGraph.getId());
        params_filteredLineGraph.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_filteredLineGraph.setMargins(10, 100, 10, 100);
        filteredLineGraph.setLayoutParams(params_filteredLineGraph);
        filteredLineGraph.setScaleX(1.75f);
        filteredLineGraph.setScaleY(1.5f);
        filteredLineGraph.setVisibility(View.VISIBLE);
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
        params_btn_generateCSV.addRule(RelativeLayout.BELOW, filteredLineGraph.getId());
        params_btn_generateCSV.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_btn_generateCSV.setMargins(10, 10, 10, 10);
        btn_generateCSV.setLayoutParams(params_btn_generateCSV);
        btn_generateCSV.setVisibility(View.VISIBLE);
        //endregion

        //region DEBUG_TEXTVIEWS
        // format the text outputted to the app view
        text_ACC = new TextView(getApplicationContext());
        text_Direction = new TextView(getApplicationContext());
        text_ACC.setId(text_ACC.generateViewId());
        text_Direction.setId(text_Direction.generateViewId());
        Log.d(debugFilter1, "Generated ID for accelerometer: " + Integer.toString(text_ACC.getId()));
        Log.d(debugFilter1, "Generated ID for direction: " + Integer.toString(text_Direction.getId()));

        relativeLayout.addView(text_ACC);
        relativeLayout.addView(text_Direction);

        RelativeLayout.LayoutParams params_ACC = (RelativeLayout.LayoutParams) text_ACC.getLayoutParams();
        RelativeLayout.LayoutParams params_direction = (RelativeLayout.LayoutParams) text_Direction.getLayoutParams();

        params_ACC.addRule(relativeLayout.BELOW, btn_generateCSV.getId());
        params_direction.addRule(relativeLayout.BELOW, text_ACC.getId());

        params_ACC.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_direction.addRule(RelativeLayout.CENTER_HORIZONTAL);

        params_ACC.setMargins(10, 10, 10, 30);
        params_direction.setMargins(10, 10, 10, 10);

        text_ACC.setLayoutParams(params_ACC);
        text_Direction.setLayoutParams(params_direction);

        text_ACC.setGravity(Gravity.CENTER);
        text_Direction.setGravity(Gravity.CENTER);

        text_ACC.setVisibility(View.VISIBLE);
        text_Direction.setVisibility(View.VISIBLE);

        text_ACC.setTextColor(Color.BLACK);
        text_Direction.setTextColor(Color.BLACK);

        // output the text for the debugging messages
        text_ACC.setText(R.string.accelerometer_reading);
        text_Direction.setText("left");
        text_Direction.setTextSize(80.0f);
        //endregion

        // initialize the sensor listener and managers
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        listener = new GeneralEventListener(lineGraph, this);

        // get the sensors from the Android device
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // register the sensors to the sensor listeners
        sensorManager.registerListener(listener, accelerometer, sensorDelay);

        //region BUTTON_EVENT_HANDLERS
        // generate and read into a .csv file the past 100 readings for the accelerometer
        btn_generateCSV.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            cFile.generateCsvFile(fileName);
            toastCSV.show();
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
        sensorManager.registerListener(listener, accelerometer, sensorDelay);
    }

    // runs when app is restarted, re-enables the listener
    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.d(debugFilter1, "APP RESTARTED.");
        sensorManager.registerListener(listener, accelerometer, sensorDelay);
    }

    // method that sets the text related to the sensor readings to the app view
    public void setTextOfDebugTextViews(FloatVector3D acc) {
        text_ACC.setText(Html.fromHtml(getString(R.string.accelerometer_reading) + String.format("<br>(<font color=#ff0000>%.3f</font>, <font color=#008000>%.3f</font>, <font color=#0000ff>%.3f</font>)", acc.getX(), acc.getY(), acc.getZ()) + " m/s²"));
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