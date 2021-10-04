package cn.fkj233.hook.miuistatusbarlyric;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SettingsActivity extends AppCompatActivity {

    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private Config config;



        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            checkPermission();
            init();
            initIcon(getContext());
            config = new Config();

            // 歌词总开关
            CheckBoxPreference lyricService = findPreference("lyricService");
            assert lyricService != null;
            lyricService.setChecked(config.getLyricService());
            lyricService.setOnPreferenceChangeListener((preference, newValue) -> {
                lyricService.setDefaultValue(newValue);
                config.setLyricService((Boolean) newValue);
                return true;
            });

            // 歌词宽度
            EditTextPreference lyricWidth = findPreference("lyricWidth");
            assert lyricWidth != null;
            lyricWidth.setSummary(config.getLyricWidth() + "%");
            if (config.getLyricWidth() == -1) {
                lyricWidth.setSummary("自适应");
            }
            lyricWidth.setDialogMessage("(-1~100，-1为自适应)，当前:" + lyricWidth.getSummary());
            lyricWidth.setOnPreferenceChangeListener((preference, newValue) -> {
                lyricWidth.setSummary(newValue.toString());
                lyricWidth.setDefaultValue(newValue);
                if (newValue.toString().equals("-1")) {
                    lyricWidth.setDialogMessage("(-1~100，-1为自适应)，当前:自适应");
                } else {
                    lyricWidth.setDialogMessage("(-1~100，-1为自适应)，当前:" + newValue.toString());
                }
                config.setLyricWidth(Integer.parseInt(newValue.toString()));
            return true;
            });

            // 歌词最大自适应宽度
            EditTextPreference lyricMaxWidth = findPreference("lyricMaxWidth");
            assert lyricMaxWidth != null;
            lyricMaxWidth.setSummary(config.getLyricMaxWidth() + "%");
            if (config.getLyricMaxWidth() == -1) {
                lyricMaxWidth.setSummary("关闭");
            }
            lyricMaxWidth.setDialogMessage("(-1~100，-1为关闭，仅在歌词宽度为自适应时生效)，当前:" + lyricMaxWidth.getSummary());
            lyricMaxWidth.setOnPreferenceChangeListener((preference, newValue) -> {
                lyricMaxWidth.setSummary(newValue.toString());
                lyricMaxWidth.setDefaultValue(newValue);
                if (newValue.toString().equals("-1")) {
                    lyricMaxWidth.setDialogMessage("(-1~100，-1为关闭，仅在歌词宽度为自适应时生效)，当前:关闭");
                    lyricMaxWidth.setSummary("关闭");
                    lyricMaxWidth.setDefaultValue("关闭");
                } else {
                    lyricMaxWidth.setDialogMessage("(-1~100，-1为关闭，仅在歌词宽度为自适应时生效)，当前:" + newValue.toString());
                }
                config.setLyricMaxWidth(Integer.parseInt(newValue.toString()));
                return true;
            });

            // 歌词动效
            ListPreference lyricAnim = findPreference("lyricAnim");
            assert lyricAnim != null;
            String[] strArr = new String[7];
            strArr[0] = "关闭";
            strArr[1] = "上滑";
            strArr[2] = "下滑";
            strArr[3] = "左滑";
            strArr[4] = "右滑";
            strArr[5] = "随机";
            strArr[6] = "旋转";
            lyricAnim.setEntries(strArr);
            lyricAnim.setEntryValues(strArr);
            lyricAnim.setSummary(config.getLyricAnim().equals("") ? "关闭" : config.getLyricAnim());
            lyricAnim.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setLyricAnim(newValue.toString());
                lyricAnim.setSummary(newValue.toString());
                return true;
            });

            // 图标
            ListPreference icon = findPreference("icon");
            assert icon != null;
            strArr = new String[3];
            strArr[0] = "关闭";
            strArr[1] = "自动";
            strArr[2] = "自定义";
            icon.setEntries(strArr);
            icon.setEntryValues(strArr);
            icon.setSummary(config.getIcon().equals("") ? "关闭" : config.getIcon());
            icon.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setIcon(newValue.toString());
                icon.setSummary(newValue.toString());
                return true;
            });

            // 图标反色
            ListPreference iconFanse = findPreference("iconFanse");
            assert iconFanse != null;
            strArr = new String[3];
            strArr[0] = "关闭";
            strArr[1] = "模式一";
            strArr[2] = "模式二";
            iconFanse.setEntries(strArr);
            iconFanse.setEntryValues(strArr);
            if (config.getFanse().equals("")) {
                iconFanse.setSummary("关闭");
            } else {
                iconFanse.setSummary(config.getFanse());
            }
            iconFanse.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setFanse(newValue.toString());
                iconFanse.setSummary(newValue.toString());
                return true;
            });

            // 歌词获取模式
            ListPreference lyricModel = findPreference("lyricModel");
            assert lyricModel != null;
            strArr = new String[2];
            strArr[0] = "通用模式";
            strArr[1] = "增强模式";
            lyricModel.setEntries(strArr);
            lyricModel.setEntryValues(strArr);
            lyricModel.setEnabled(false);
            lyricModel.setSummary(config.getLyricModel());
            if (lyricModel.getSummary().equals("通用模式")) {
                lyricModel.setValueIndex(0);
            } else {
                lyricModel.setValueIndex(1);
            }
            lyricModel.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setLyricModel(newValue.toString());
                lyricModel.setSummary(newValue.toString());
                return true;
            });

            // 隐藏通知图标
            CheckBoxPreference hideNoti = findPreference("hideNoti");
            assert hideNoti != null;
            hideNoti.setChecked(config.getHideNoti());
            hideNoti.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setHideNoti((Boolean) newValue);
                return true;
            });

            // 暂停关闭歌词
            CheckBoxPreference lyricOff = findPreference("lyricOff");
            assert lyricOff != null;
            lyricOff.setChecked(config.getLyricOff());
            lyricOff.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setLyricOff((Boolean) newValue);
                return true;
            });

            // 重启SystemUI
            Preference reSystemUI = findPreference("restartUI");
            assert reSystemUI != null;
            reSystemUI.setOnPreferenceClickListener(((preference) -> {
                 new AlertDialog.Builder(requireActivity())
                         .setTitle("确定重启系统界面吗？")
                         .setMessage("若使用中突然发现不能使用，可尝试重启系统界面。")
                         .setPositiveButton("确定", (dialog, which) -> killProcess("systemui"))
                         .create()
                         .show();
                return true;
            }));

            // 息屏显示开关
            CheckBoxPreference aodLyricService = findPreference("aodLyricService");
            assert aodLyricService != null;
            aodLyricService.setChecked(config.getAodLyricService());
            aodLyricService.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setAodLyricService((Boolean) newValue);
                return true;
            });

            // 防烧屏
            CheckBoxPreference srcService = findPreference("proSrc");
            assert srcService != null;
            srcService.setChecked(config.getSrcService());
            srcService.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setSrcService((Boolean) newValue);
                return true;
            });

            // 个性签名
            EditTextPreference defSign = findPreference("defSign");
            assert defSign != null;
            defSign.setSummary(config.getSign());
            defSign.setDialogMessage("");
            defSign.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setSign(newValue.toString());
                defSign.setSummary(newValue.toString());
                return true;
            });

            // 项目地址
            Preference sourcecode = findPreference("Sourcecode");
            assert sourcecode != null;
            sourcecode.setOnPreferenceClickListener((preference) -> {
                Uri uri = Uri.parse("https://github.com/577fkj/MIUIStatusBarlyric");
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(uri);
                startActivity(intent);
                return true;
            });

            //版本介绍
            Preference verExplain = findPreference("ver_explain");
            assert verExplain != null;
            verExplain.setSummary("当前版本: " + Utlis.getLocalVersionName(requireContext()));
            verExplain.setOnPreferenceClickListener((preference) -> {
                new AlertDialog.Builder(requireActivity())
                        .setTitle("当前版本[" + Utlis.getLocalVersionName(requireContext()) + "]适用于")
                        .setMessage("酷狗音乐:v10.8.4\n酷我音乐:v9.4.6.2\n网易云音乐:v8.5.30\nQQ音乐:v10.17.0.11")
                        .setPositiveButton("确定", (dialog, which) -> {})
                        .create()
                        .show();
                return true;
            });

            // 重置插件
            Preference reset = findPreference("reset");
            assert reset != null;
            reset.setOnPreferenceClickListener((preference) -> {
                new AlertDialog.Builder(requireActivity())
                    .setTitle("是否要重置模块")
                    .setMessage("模块没问题请不要随意重置")
                    .setPositiveButton("确定", (dialog, which) -> {
                        delete(new File(Utlis.PATH));
                        delete(new File("/data/data/cn.fkj233.hook.miuistatusbarlyric/shared_prefs/"));
                        Toast.makeText(requireActivity(), "重置成功", Toast.LENGTH_SHORT).show();
                        System.exit(0);
                    })
                    .create()
                    .show();
                return true;
            });

            // 歌词颜色
            EditTextPreference lyricColour = findPreference("lyricColour");
            assert lyricColour != null;
            lyricColour.setSummary(config.getLyricColor());
            lyricColour.setDialogMessage("请输入16进制颜色代码，例如: #C0C0C0，目前：" + config.getLyricColor());
            lyricColour.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!newValue.toString().equals("关闭")) {
                    try {
                        Color.parseColor(newValue.toString());
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "颜色代码不正确!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                lyricColour.setDialogMessage("请输入16进制颜色代码，例如: #C0C0C0，目前：" + newValue.toString());
                lyricColour.setSummary(newValue.toString());
                config.setLyricColor(newValue.toString());
                return true;
            });
            
            // 隐藏实时网速
            CheckBoxPreference hideNetWork = findPreference("hideNetWork");
            assert hideNetWork != null;
            hideNetWork.setChecked(config.getHideNetWork());
            hideNetWork.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setHideNetWork((Boolean) newValue);
                return true;
            });

            // 隐藏运营商名称
            CheckBoxPreference hideCUK = findPreference("hideCUK");
            assert hideCUK != null;
            hideCUK.setChecked(config.getHideCUK());
            hideCUK.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setHideCUK((Boolean) newValue);
                return true;
            });
        }

        public void initIcon(Context context) {
            if (!new File(Utlis.PATH, "icon.png").exists() && !new File(Utlis.PATH, "icon.gif").exists()) {
                copyAssets(context, "icon/icon.png", Utlis.PATH + "icon.png");
            }
            if (!new File(Utlis.PATH, "icon7.png").exists() && !new File(Utlis.PATH, "icon7.gif").exists())
                copyAssets(context, "icon/icon7.png", Utlis.PATH + "icon7.png");
            if (!new File(Utlis.PATH, "kugou.png").exists()) {
                copyAssets(context, "icon/kugou.png", Utlis.PATH + "kugou.png");
            }
            if (!new File(Utlis.PATH, "netease.png").exists()) {
                copyAssets(context, "icon/netease.png", Utlis.PATH + "netease.png");
            }
            if (!new File(Utlis.PATH, "qqmusic.png").exists()) {
                copyAssets(context, "icon/qqmusic.png", Utlis.PATH + "qqmusic.png");
            }
            if (!new File(Utlis.PATH, "kuwo.png").exists()) {
                copyAssets(context, "icon/kuwo.png", Utlis.PATH + "kuwo.png");
            }
            if (!new File(Utlis.PATH, ".nomedia").exists()) {
                try {
                    new File(Utlis.PATH, ".nomedia").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void copyAssets(Context context, String str, String str2) {
            try {
                File file = new File(str2);
                InputStream open = context.getAssets().open(str);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = open.read(bArr);
                    if (read == -1) {
                        fileOutputStream.flush();
                        open.close();
                        fileOutputStream.close();
                        return;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void checkPermission() {
            if (ContextCompat.checkSelfPermission(requireActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), "android.permission.WRITE_EXTERNAL_STORAGE")) {
                    Toast.makeText(getActivity(), "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
                }
                String[] strArr = new String[1];
                strArr[0] = "android.permission.WRITE_EXTERNAL_STORAGE";
                ActivityCompat.requestPermissions(requireActivity(), strArr, 1);
                return;
            }
            Log.e("MIUIStatBar", "checkPermission: 已经授权！");
        }

        public void killProcess(String str) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("su");
                DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                dataOutputStream.write(("pgrep -l " + str + "\n").getBytes());
                dataOutputStream.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String stringBuffer = "kill -9 " + bufferedReader.readLine() + "\n";
                bufferedReader.close();
                dataOutputStream.writeBytes(stringBuffer);
                dataOutputStream.flush();
                dataOutputStream.close();
                process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                process.waitFor();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }


        public void init() {
            File file = new File(Utlis.PATH);
            File file2 = new File(Utlis.PATH + ".msblConfig");
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!file2.exists()) {
                try {
                    Config config = new Config();
                    file2.createNewFile();
                    config.setLyricService(true);
                    config.setLyricWidth(-1);
                    config.setLyricMaxWidth(-1);
                    config.setTimeWidth(-1);
                    config.setLyricAnim("关闭");
                    config.setFanse("关闭");
                    config.setAodLyricService(false);
                    config.setSrcService(true);
                    config.setSign("");
                    config.setLyricOff(false);
                    config.setLyricModel("增强模式");
                    config.setHideNoti(false);
                    config.setIcon("关闭");
                    config.setLyricColor("关闭");
                    config.setHideCUK(false);
                    config.setHideNetWork(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}