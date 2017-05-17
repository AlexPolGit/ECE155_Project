package uwaterloo.ca.ece155project_1;

import android.content.Context;
import android.os.Environment;

import java.io.*;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

// class that manages the accelerometer data output into a .csv file
public class CreateCsvFile {
    public void generateCsvFile(String data){
        File f = null;
        FileWriter writer = null;
        PrintWriter pw = null;

        try{
            f = new File(Environment.getDataDirectory(), data);
            writer = new FileWriter(f);
            pw = new PrintWriter(writer);

            // test numbers: remove later
            float num1 = 0.0000001f;
            float num2 = -6.8930f;
            float num3 = 3.14159265358979f;

            pw.println(String.format("" + num1 + ", " + num2 + ", " + num3));
            pw.println(String.format("" + num1 + ", " + num2 + ", " + num3));

        }catch(IOException e){
            // file could not be opened!
            e.printStackTrace();
        }finally{
            try{
                if (writer != null){
                    writer.flush();
                    writer.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
