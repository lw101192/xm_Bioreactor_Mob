package com.example.xm.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by liuwei on 2016/7/28.
 */
public class Util {
    /**
     * 保存到本地文件
     * @param context
     * @param buf
     * @param fileName
     */
    public static void SaveToFile(Context context, byte[] buf, String fileName) {
        // TODO Auto-generated method stub
        String filename = fileName;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            //fileOutputStream = new FileOutputStream(filename);
            fileOutputStream.write(buf);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }

    /**
     * 从本地读取
     * @param context
     * @param fileName
     * @return
     */
    public static String ReadFromFile(Context context, String fileName) {
        // TODO Auto-generated method stub
        String filename = fileName;
        FileInputStream fileinputStream = null;
        String line;
        StringBuffer stringBuffer = null;
        try {
            fileinputStream = context.openFileInput(filename);
            //fileOutputStream = new FileOutputStream(filename);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(fileinputStream, "UTF-8"));
            stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\r\n");
            }

            return stringBuffer.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            if (fileinputStream != null)
                try {
                    fileinputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }

        }
    }


}
