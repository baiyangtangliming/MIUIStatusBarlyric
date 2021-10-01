package cn.fkj233.hook.miuistatusbarlrcy;

import android.os.Environment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LyricTools {
    static String path = Environment.getExternalStorageDirectory() + "/Android/media/cn.fkj233.hook.miuistatusbarlrcy/.msbl";

    public static String getlyric() {
        String str = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            byte[] bArr = new byte[fileInputStream.available()];
            fileInputStream.read(bArr);
            str = new String(bArr);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return str;
    }

    public static void setlyric(String str) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(str.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}