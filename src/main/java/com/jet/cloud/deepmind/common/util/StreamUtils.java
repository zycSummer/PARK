package com.jet.cloud.deepmind.common.util;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class StreamUtils {

    public static void closeInput(InputStream in){
        if(in != null){
            try {
                in.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void closeReader(Reader reader){
        if (reader != null) {
            try {
                reader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void closeWorkbook(Workbook workbook){
        if(workbook != null){
            try{
                workbook.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
