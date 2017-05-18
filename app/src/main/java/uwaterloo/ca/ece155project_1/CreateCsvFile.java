package uwaterloo.ca.ece155project_1;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.io.FileWriter;
import java.io.IOException;

// class that manages the accelerometer data output into a .csv file
public class CreateCsvFile
{
    public void generateCsvFile(File file)
    {
        FileWriter writer = null;
        PrintWriter pw;

        try
        {
            writer = new FileWriter(file);
            pw = new PrintWriter(writer);

            // test numbers: remove later
            float num1 = 0.0000001f;
            float num2 = -6.8930f;
            float num3 = 3.14159265358979f;

            for (int i = 0; i < 5; i++) pw.println(String.format("%.3f, %.3f, %.3f", num1, num2, num3));
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