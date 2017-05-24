package uwaterloo.ca.ece155project_1;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

// class that manages the accelerometer data output into a .csv file
public class CreateCsvFile
{
    private MainActivity m;

    public CreateCsvFile(MainActivity _m) {
        m = _m;
    }

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
            for(int i = 0; i < 100; i++) {
                pw.print(accelerometerReadings.get(i).get(0) + ",");
                pw.print(accelerometerReadings.get(i).get(1) + ",");
                pw.println(accelerometerReadings.get(i).get(2));
            }

        }
        catch (IOException e)
        {
            // file could not be opened!
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