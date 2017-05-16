package uwaterloo.ca.ece155project_1;

/**
 * Created by virgil on 2017-05-16.
 */
import java.io.*;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class CreateCsvFile {
    //Create File



    void generateCsvFile(String data){
        FileWriter writer = null;
        PrintWriter pw = null;

        try{
            writer = new FileWriter(data);
            pw = new PrintWriter(writer);
            float num1 = 1.098442f;
            float num2 = 2.677238123f;
            float num3 = -3.123123123123445f;
            //System.out.println("11111");
            pw.println(String.format("" + num1 + ", " + num2 + ", " + num3));
            //pw.println(String.format("" + num4));
        }catch(IOException e){
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
