package ca.uwaterloo.ece155_lab3;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

import ca.uwaterloo.ece155_lab2.R;
import ca.uwaterloo.ece155_lab3.utils.FloatVector3D;

public class MainActivity extends AppCompatActivity
{
    public final static String debugFilter1 = "debug1";
    public final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;

    ca.uwaterloo.sensortoy.LineGraphView lineGraph;
    ca.uwaterloo.sensortoy.LineGraphView filteredLineGraph;
    TextView text_graph;
    TextView text_filtered;
    TextView text_ACC;
    TextView text_ACC_filtered;
    TextView text_Direction;
    Button btn_generateCSV;
    static NumberPicker field_filter;

    // sensor values
    SensorManager sensorManager;
    AccelerometerListener listener;
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
        final Toast toastCSV = Toast.makeText(getApplicationContext(), getString(R.string.toast_create_csv), Toast.LENGTH_LONG);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.scrollLayout);

        // set the text for the title of the first (unfiltered) graph
        text_graph = (TextView) findViewById(R.id.tv_title);
        text_graph.setText(R.string.graph_title);
        text_graph.setTextSize(12);

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

        text_filtered = new TextView(getApplicationContext());
        text_filtered.setId(text_filtered.generateViewId());
        Log.d(debugFilter1, "Generated ID for text_filtered: " + Integer.toString(text_filtered.getId()));
        relativeLayout.addView(text_filtered);

        // create another line graph for the filtered values
        filteredLineGraph = new ca.uwaterloo.sensortoy.LineGraphView
                (
                        getApplicationContext(),
                        100,
                        Arrays.asList(
                                getString(R.string.filtered_accelerator_x_label),
                                getString(R.string.filtered_accelerator_y_label),
                                getString(R.string.filtered_accelerator_z_label),
                                "X Threshold",
                                "-X Threshold"
                        )
                );
        // add the line graph to the app view
        filteredLineGraph.setId(filteredLineGraph.generateViewId());
        Log.d(debugFilter1, "Generated ID for lineGraph: " + Integer.toString(filteredLineGraph.getId()));
        relativeLayout.addView(filteredLineGraph);

        // format the linegraph view on the app view
        RelativeLayout.LayoutParams params_filteredLineGraph = (RelativeLayout.LayoutParams) filteredLineGraph.getLayoutParams();
        params_filteredLineGraph.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_filteredLineGraph.setMargins(10, 100, 10, 50);
        filteredLineGraph.setLayoutParams(params_filteredLineGraph);
        filteredLineGraph.setScaleX(1.75f);
        filteredLineGraph.setScaleY(1.5f);
        filteredLineGraph.setVisibility(View.VISIBLE);
        //endregion

        //region FILTER_INTENSITY_PICKER
        // add the number picker slider to customize intensity of low pass filter
        field_filter = new NumberPicker(getApplicationContext());
        field_filter.setId(field_filter.generateViewId());
        Log.d(debugFilter1, "Generated ID for field_filter: " + Integer.toString(field_filter.getId()));
        relativeLayout.addView(field_filter);
        RelativeLayout.LayoutParams params_field_filter = (RelativeLayout.LayoutParams) field_filter.getLayoutParams();
        params_field_filter.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_field_filter.setMargins(10, 10, 10, 10);
        field_filter.setMinValue(1);
        field_filter.setMaxValue(100);
        field_filter.setValue(50);
        field_filter.setLayoutParams(params_field_filter);
        field_filter.setVisibility(View.VISIBLE);
        setNumberPickerTextColor(field_filter, R.color.black);
        setDividerColor(field_filter, R.color.black);
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
        params_btn_generateCSV.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_btn_generateCSV.setMargins(10, 10, 10, 10);
        btn_generateCSV.setLayoutParams(params_btn_generateCSV);
        btn_generateCSV.setVisibility(View.VISIBLE);
        //endregion

        //region DEBUG_TEXTVIEWS
        // format the text outputted to the app view
        text_ACC = new TextView(getApplicationContext());
        text_ACC_filtered = new TextView(getApplicationContext());
        text_Direction = new TextView(getApplicationContext());

        text_ACC.setId(text_ACC.generateViewId());
        text_ACC_filtered.setId(text_ACC.generateViewId());
        text_Direction.setId(text_Direction.generateViewId());

        Log.d(debugFilter1, "Generated ID for text_ACC: " + Integer.toString(text_ACC.getId()));
        Log.d(debugFilter1, "Generated ID for text_ACC_filtered: " + Integer.toString(text_ACC_filtered.getId()));
        Log.d(debugFilter1, "Generated ID for text_Direction: " + Integer.toString(text_Direction.getId()));

        relativeLayout.addView(text_ACC);
        relativeLayout.addView(text_ACC_filtered);
        relativeLayout.addView(text_Direction);

        RelativeLayout.LayoutParams params_ACC = (RelativeLayout.LayoutParams) text_ACC.getLayoutParams();
        RelativeLayout.LayoutParams params_ACC_filtered = (RelativeLayout.LayoutParams) text_ACC_filtered.getLayoutParams();
        RelativeLayout.LayoutParams params_direction = (RelativeLayout.LayoutParams) text_Direction.getLayoutParams();
        RelativeLayout.LayoutParams params_filtered = (RelativeLayout.LayoutParams) text_filtered.getLayoutParams();

        // set the order each element is laid out (top most is first)
        params_filteredLineGraph.addRule(RelativeLayout.BELOW, text_filtered.getId());
        params_filtered.addRule(relativeLayout.BELOW, lineGraph.getId());
        params_direction.addRule(relativeLayout.BELOW, filteredLineGraph.getId());
        params_ACC.addRule(relativeLayout.BELOW, text_Direction.getId());
        params_ACC_filtered.addRule(relativeLayout.BELOW, text_ACC.getId());
        params_field_filter.addRule(RelativeLayout.BELOW, text_ACC_filtered.getId());
        params_btn_generateCSV.addRule(RelativeLayout.BELOW, field_filter.getId());

        // center the elements
        params_ACC.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_ACC_filtered.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_direction.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params_filtered.addRule(RelativeLayout.CENTER_HORIZONTAL);

        params_ACC.setMargins(10, 10, 10, 30);
        params_ACC_filtered.setMargins(10, 10, 10, 30);
        params_direction.setMargins(10, 10, 10, 10);
        params_filtered.setMargins(10, 10, 10, 10);

        text_ACC.setLayoutParams(params_ACC);
        text_ACC_filtered.setLayoutParams(params_ACC_filtered);
        text_Direction.setLayoutParams(params_direction);
        text_filtered.setLayoutParams(params_filtered);

        text_ACC.setGravity(Gravity.CENTER);
        text_ACC_filtered.setGravity(Gravity.CENTER);
        text_Direction.setGravity(Gravity.CENTER);
        text_filtered.setGravity(Gravity.CENTER);

        text_ACC.setVisibility(View.VISIBLE);
        text_ACC_filtered.setVisibility(View.VISIBLE);
        text_Direction.setVisibility(View.VISIBLE);
        text_filtered.setVisibility(View.VISIBLE);

        text_ACC.setTextColor(Color.BLACK);
        text_ACC_filtered.setTextColor(Color.BLACK);
        text_Direction.setTextColor(Color.BLACK);
        text_filtered.setTextColor(Color.BLACK);

        // output the text for the debugging messages
        text_ACC.setText(R.string.accelerometer_reading);
        text_ACC_filtered.setText(R.string.accelerometer_reading);
        text_Direction.setText("No Dir");
        text_Direction.setTextSize(80.0f);
        text_filtered.setText(getString(R.string.filtered_graph_title));
        text_filtered.setTextSize(12);
        //endregion

        // initialize the sensor listener and managers
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        listener = new AccelerometerListener(lineGraph, filteredLineGraph, this);

        // get the sensors from the Android device
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // register the sensors to the sensor listeners
        sensorManager.registerListener(listener, accelerometer, SENSOR_DELAY);

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
    public void setTextOfDebugTextViews(FloatVector3D acc, FloatVector3D facc)
    {
        text_ACC.setText(Html.fromHtml(getString(R.string.accelerometer_reading) + String.format("<br>(<font color=#ff0000>%.3f</font>, <font color=#008000>%.3f</font>, <font color=#0000ff>%.3f</font>)", acc.getX(), acc.getY(), acc.getZ()) + " m/s²"));
        text_ACC_filtered.setText(Html.fromHtml(getString(R.string.filtered_accelerometer_reading) + String.format("<br>(<font color=#ff0000>%.3f</font>, <font color=#008000>%.3f</font>, <font color=#0000ff>%.3f</font>)", facc.getX(), facc.getY(), facc.getZ()) + " m/s²"));

        if (listener.getGesture() == AccelerometerListener.gestures.RIGHT) text_Direction.setText(getString(R.string.orientation_right));
        else if (listener.getGesture() == AccelerometerListener.gestures.LEFT) text_Direction.setText(getString(R.string.orientation_left));
        else if (listener.getGesture() == AccelerometerListener.gestures.UP) text_Direction.setText(getString(R.string.orientation_up));
        else if (listener.getGesture() == AccelerometerListener.gestures.DOWN) text_Direction.setText(getString(R.string.orientation_down));
        else if (listener.getGesture() == AccelerometerListener.gestures.NONE) text_Direction.setText(getString(R.string.orientation_none));
        else text_Direction.setText(getString(R.string.orientation_error));
    }

    // opens the folder of the csv file
    public void openFolder()
    {
        Log.d(debugFilter1, "Opening Folder: " + new File(getExternalFilesDir("Accelerometer Data"), fileName).getAbsolutePath());

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(Uri.parse(new File(getExternalFilesDir("Accelerometer Data"), fileName).getAbsolutePath()), "file/*");
        startActivity(intent);
    }

    //region NUMBER_PICKER_CHANGE PARAMS
    // method to change the color of the number picker for the LPF intensity
    public boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++)
        {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText)
            {
                try
                {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(getResources().getColor(color));
                    numberPicker.invalidate();
                    return true;
                }
                catch (NoSuchFieldException e)
                {
                    Log.d("NumberPickerTextColor", "NoSuchFieldException");
                }
                catch (IllegalAccessException e)
                {
                    Log.d("NumberPickerTextColor", "IllegalAccessException");
                }
                catch (IllegalArgumentException e)
                {
                    Log.d("NumberPickerTextColor", "IllegalArgumentException");
                }
            }
        }
        return false;
    }

    private void setDividerColor(NumberPicker picker, int color)
    {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields)
        {
            if (pf.getName().equals("mSelectionDivider"))
            {
                pf.setAccessible(true);
                try
                {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
                catch (Resources.NotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    //endregion
}