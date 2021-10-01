package cn.fkj233.hook.miuistatusbarlrcy;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigTools2 {
    static String path = Environment.getExternalStorageDirectory() + "/Android/media/cn.fkj233.hook.miuistatusbarlrcy/.msblConfig2";

    public static String getConfig() {
        new File(path);
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

    public static void setConfig(String str) {
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

    public static String formateJson(String str) {
        if (str.charAt(0) == '{' && str.charAt(str.length() - 1) == '}') {
            int i = 0;
            String str2 = "";
            StringBuilder sb = new StringBuilder();
            while (true) {
                int idx = getIdx(str, i);
                if (idx < 0) {
                    sb.append(str.substring(i, str.length()));
                    return sb.toString();
                }
                String str3 = str2;
                if (isOpen(str, i) && isOpen(str, idx)) {
                    String stringBuffer = str2 + "    ";
                    str2 = stringBuffer;
                    str3 = stringBuffer;
                } else if (isOpen(str, i) && isEnd(str, idx)) {
                    str3 = str2;
                    str2 = str2 + "    ";
                } else if (isEnd(str, i) && isOpen(str, idx)) {
                    if (str2.length() >= "    ".length()) {
                        str2 = str2.substring(0, str2.length() - "    ".length());
                    }
                    str3 = str2;
                } else if (isEnd(str, i) && isEnd(str, idx)) {
                    if (str2.length() >= "    ".length()) {
                        str2 = str2.substring(0, str2.length() - "    ".length());
                    }
                    if (str2.length() >= "    ".length()) {
                        str3 = str2.substring(0, str2.length() - "    ".length());
                    }
                }
                if (i + 1 < idx) {
                    String substring = str.substring(i + 1, idx);
                    if (!",".equals(substring)) {
                        substring = substring.replaceAll(",", ",\r\n" + str2);
                    }
                    sb.append(str.substring(i, i + 1) + "\r\n" + str2 + substring + "\r\n" + str3);
                } else {
                    sb.append(str.substring(i, i + 1) + "\r\n" + str3);
                }
                i = idx;
            }
        } else {
            throw new RuntimeException("兄弟别搞事啊，json字符串可是以\"{\"开头且以\"}\"结尾，你传进来的可不是这样！");
        }
    }

    public static int getIdx(String str, int i) {
        char[] cArr = {'{', '}', '[', ']'};
        int i2 = -1;
        for (int i3 = 0; i3 < cArr.length; i3++) {
            int indexOf = str.indexOf(cArr[i3], i + 1);
            if (indexOf > -1 && (i2 == -1 || indexOf < i2)) {
                i2 = indexOf;
            }
        }
        return i2;
    }

    public static boolean isOpen(String str, int i) {
        if (i <= -1 || i >= str.length() || (str.charAt(i) != '{' && str.charAt(i) != '[')) {
            return false;
        }
        return true;
    }

    public static boolean isEnd(String str, int i) {
        if (i <= -1 || i >= str.length() || (str.charAt(i) != '}' && str.charAt(i) != ']')) {
            return false;
        }
        return true;
    }
}