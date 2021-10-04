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
        CheckBoxPreference hideNoti;
        ListPreference icon;
        ListPreference iconFanse;
        ListPreference lyricAnim;
        EditTextPreference lyricMaxWidth;
        ListPreference lyricModel;
        CheckBoxPreference lyricOff;
        CheckBoxPreference lyricService;
        EditTextPreference lyricWidth;
        CheckBoxPreference aodLyricService;
        EditTextPreference defSign;
        CheckBoxPreference srcService;
        EditTextPreference lyricColour;



        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            checkPermission();
            init();
            initIcon(getContext());
            this.config = new Config();

            // 歌词总开关
            this.lyricService = findPreference("lyricService");
            assert this.lyricService != null;
            this.lyricService.setChecked(this.config.getLyricService());
            this.lyricService.setOnPreferenceChangeListener((preference, newValue) -> {
                lyricWidth.setDefaultValue(newValue);
                config.setLyricService((Boolean) newValue);
                return true;
            });

            // 歌词宽度
            this.lyricWidth = findPreference("lyricWidth");
            assert this.lyricWidth != null;
            this.lyricWidth.setSummary(this.config.getLyricWidth() + "%");
            if (this.config.getLyricWidth() == -1) {
                this.lyricWidth.setSummary("自适应");
            }
            this.lyricWidth.setDialogMessage("(-1~100，-1为自适应)，当前:" + this.lyricWidth.getSummary());
            this.lyricWidth.setOnPreferenceChangeListener((preference, newValue) -> {
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
            this.lyricMaxWidth = findPreference("lyricMaxWidth");
            assert this.lyricMaxWidth != null;
            this.lyricMaxWidth.setSummary(this.config.getLyricMaxWidth() + "%");
            if (this.config.getLyricMaxWidth() == -1) {
                this.lyricMaxWidth.setSummary("关闭");
            }
            this.lyricMaxWidth.setDialogMessage("(-1~100，-1为关闭，仅在歌词宽度为自适应时生效)，当前:" + this.lyricMaxWidth.getSummary());
            this.lyricMaxWidth.setOnPreferenceChangeListener((preference, newValue) -> {
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
            this.lyricAnim = findPreference("lyricAnim");
            assert this.lyricAnim != null;
            String[] strArr = new String[7];
            strArr[0] = "关闭";
            strArr[1] = "上滑";
            strArr[2] = "下滑";
            strArr[3] = "左滑";
            strArr[4] = "右滑";
            strArr[5] = "随机";
            strArr[6] = "旋转";
            this.lyricAnim.setEntries(strArr);
            this.lyricAnim.setEntryValues(strArr);
            this.lyricAnim.setSummary(this.config.getLyricAnim().equals("") ? "关闭" : this.config.getLyricAnim());
            this.lyricAnim.setOnPreferenceChangeListener((preference, newValue) -> {
                this.config.setLyricAnim(newValue.toString());
                this.lyricAnim.setSummary(newValue.toString());
                return true;
            });

            // 图标
            this.icon = findPreference("icon");
            assert this.icon != null;
            strArr = new String[3];
            strArr[0] = "关闭";
            strArr[1] = "自动";
            strArr[2] = "自定义";
            this.icon.setEntries(strArr);
            this.icon.setEntryValues(strArr);
            this.icon.setSummary(this.config.getIcon().equals("") ? "关闭" : this.config.getIcon());
            this.icon.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setIcon(newValue.toString());
                this.icon.setSummary(newValue.toString());
                return true;
            });

            // 图标反色
            this.iconFanse = findPreference("iconFanse");
            assert this.iconFanse != null;
            strArr = new String[3];
            strArr[0] = "关闭";
            strArr[1] = "模式一";
            strArr[2] = "模式二";
            this.iconFanse.setEntries(strArr);
            this.iconFanse.setEntryValues(strArr);
            if (this.config.getFanse().equals("")) {
                iconFanse.setSummary("关闭");
            } else {
                iconFanse.setSummary(this.config.getFanse());
            }
            this.iconFanse.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setFanse(newValue.toString());
                this.iconFanse.setSummary(newValue.toString());
                return true;
            });

            // 歌词获取模式
            this.lyricModel = findPreference("lyricModel");
            assert this.lyricModel != null;
            strArr = new String[2];
            strArr[0] = "通用模式";
            strArr[1] = "增强模式";
            this.lyricModel.setEntries(strArr);
            this.lyricModel.setEntryValues(strArr);
            this.lyricModel.setEnabled(false);
            this.lyricModel.setSummary(this.config.getLyricModel());
            if (this.lyricModel.getSummary().equals("通用模式")) {
                this.lyricModel.setValueIndex(0);
            } else {
                this.lyricModel.setValueIndex(1);
            }
            this.lyricModel.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setLyricModel(newValue.toString());
                this.lyricModel.setSummary(newValue.toString());
                return true;
            });

            // 隐藏通知图标
            this.hideNoti = findPreference("hideNoti");
            assert this.hideNoti != null;
            this.hideNoti.setChecked(this.config.getHideNoti());
            this.hideNoti.setOnPreferenceChangeListener((preference, newValue) -> {
                config.setHideNoti((Boolean) newValue);
                return true;
            });

            // 暂停关闭歌词
            this.lyricOff = findPreference("lyricOff");
            assert this.lyricOff != null;
            this.lyricOff.setChecked(this.config.getLyricOff());
            this.lyricOff.setOnPreferenceChangeListener((preference, newValue) -> {
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
            this.aodLyricService = findPreference("aodLyricService");
            assert this.aodLyricService != null;
            this.aodLyricService.setChecked(this.config.getAodLyricService());
            this.aodLyricService.setOnPreferenceChangeListener((preference, newValue) -> {
                this.config.setAodLyricService((Boolean) newValue);
                return true;
            });

            // 防烧屏
            this.srcService = findPreference("proSrc");
            assert this.srcService != null;
            this.srcService.setChecked(this.config.getSrcService());
            this.srcService.setOnPreferenceChangeListener((preference, newValue) -> {
                this.config.setSrcService((Boolean) newValue);
                return true;
            });

            // 个性签名
            this.defSign = findPreference("defSign");
            assert this.defSign != null;
            this.defSign.setSummary(this.config.getSign());
            this.defSign.setDialogMessage("");
            this.defSign.setOnPreferenceChangeListener((preference, newValue) -> {
                this.config.setSign(newValue.toString());
                this.defSign.setSummary(newValue.toString());
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

            this.lyricColour = findPreference("lyricColour");
            assert this.lyricColour != null;
            this.lyricColour.setSummary(config.getLyricColor());
            this.lyricColour.setDialogMessage("请输入16进制颜色代码，例如: #C0C0C0，目前：" + config.getLyricColor());
            this.lyricColour.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!newValue.toString().equals("关闭")) {
                    try {
                        Color.parseColor(newValue.toString());
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "颜色代码不正确!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                this.lyricColour.setDialogMessage("请输入16进制颜色代码，例如: #C0C0C0，目前：" + newValue.toString());
                this.lyricColour.setSummary(newValue.toString());
                config.setLyricColor(newValue.toString());
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}