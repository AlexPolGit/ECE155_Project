package uwaterloo.ca.ece155project_1;

import android.util.Log;

import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;
import java.lang.Float;

// class that manages the accelerometer data output into a .csv file
public class CreateCsvFile
{
    private MainActivity m;

    // constructor that, when called, sets a reference to the MainActivity
    public CreateCsvFile(MainActivity _m) {
        m = _m;
    }

    // method that outputs the most recent 100 acc readings to a .csv file
    public void generateCsvFile(String fileName)
    {
        File file = new File(m.getExternalFilesDir("Accelerometer Data"), fileName);
        FileWriter writer = null;
        PrintWriter pw;

        try
        {
            writer = new FileWriter(file);
            pw = new PrintWriter(writer);
            // get the accelerometer reading history
            LinkedList<Vector<Float>> accelerometerReadings = m.listener.getAccelerometerReadings();

            // output the 100 values of the accelerometer reading history to the csv file
            {
            }

        }
        // file could not be opened!
        catch (IOException e)
        {
            Log.d("IOException","CSV file could not be found or opened");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.flush();
                    writer.close();
                }
                else
                {
                    Log.d(MainActivity.debugFilter1, "WRITER WAS NULL!");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            Log.d(MainActivity.debugFilter1, "Done with file, closing.");
        }
    }
}