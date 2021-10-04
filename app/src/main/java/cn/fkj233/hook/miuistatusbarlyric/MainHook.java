package cn.fkj233.hook.miuistatusbarlyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.app.MiuiStatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.core.graphics.ColorUtils;

public class MainHook implements IXposedHookLoadPackage {
    private static final String KEY_LYRIC = "lyric";
    private static final float[] NEGATIVE;
    private static String musicName = "";
    private Context context = null;
    private static String lyric = "";
    private static String iconPath = "";

    static {
        float[] fArr = new float[20];
        fArr[0] = -1.0f;
        fArr[1] = (float) 0;
        fArr[2] = (float) 0;
        fArr[3] = (float) 0;
        fArr[4] = (float) 255;
        fArr[5] = (float) 0;
        fArr[6] = -1.0f;
        fArr[7] = (float) 0;
        fArr[8] = (float) 0;
        fArr[9] = (float) 255;
        fArr[10] = (float) 0;
        fArr[11] = (float) 0;
        fArr[12] = -1.0f;
        fArr[13] = (float) 0;
        fArr[14] = (float) 255;
        fArr[15] = (float) 0;
        fArr[16] = (float) 0;
        fArr[17] = (float) 0;
        fArr[18] = 1.0f;
        fArr[19] = (float) 0;
        NEGATIVE = fArr;
    }

    public static class LyricReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Lyric_Server") || intent.getAction().equals("Lyric_Server_Aod")) {
                lyric = intent.getStringExtra("Lyric_Data");
                Config config = new Config();
                switch (config.getIcon()) {
                    case "自动":
                        iconPath = Utlis.PATH + intent.getStringExtra("Lyric_Icon") + ".png";
                        break;
                    case "自定义":
                        iconPath = Utlis.PATH + "icon.png";
                        break;
                    default:
                        iconPath = Utlis.PATH + "夏.png";
                        break;
                }
            }
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 获取Context
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                context = (Context) param.args[0];
                // 注册广播
                IntentFilter filter;
                switch (lpparam.packageName) {
                    case "com.android.systemui":
                        filter = new IntentFilter();
                        filter.addAction("Lyric_Server");
                        context.registerReceiver(new LyricReceiver(), filter);
                        break;
                    case "com.miui.aod":
                        if (!new Config().getAodLyricService()) break;
                        filter = new IntentFilter();
                        filter.addAction("Lyric_Server_Aod");
                        context.registerReceiver(new LyricReceiver(), filter);
                        break;
                }
            }
        });

        switch (lpparam.packageName) {
            case "com.android.systemui":
                XposedBridge.log("正在hook 系统界面");
                try {
                    // 状态栏歌词
                    XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.CollapsedStatusBarFragment", lpparam.classLoader, "onViewCreated", Class.forName("android.view.View"), Class.forName("android.os.Bundle"), new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Field clockField;
                            Application application = AndroidAppHelper.currentApplication();
                            AudioManager audioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
                            int dw = displayMetrics.widthPixels;

                            // 获取系统版本
                            String miuiVer = getSystemProperty();
                            XposedBridge.log("MIUI Ver: " + miuiVer);
                            if (miuiVer.equals("V12")) {
                                clockField = XposedHelpers.findField(param.thisObject.getClass(), "mStatusClock");
                            } else if (miuiVer.equals("V125")) {
                                clockField = XposedHelpers.findField(param.thisObject.getClass(), "mClockView");
                            } else {
                                XposedBridge.log("Unknown version");
                                clockField = XposedHelpers.findField(param.thisObject.getClass(), "mClockView");
                            }

                            // 反射获取时钟
                            TextView clock = (TextView) clockField.get(param.thisObject);
                            clock.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                            int measuredHeight = clock.getMeasuredHeight();

                            // 创建跑马灯文字
                            AutoMarqueeTextView autoMarqueeTextView = new AutoMarqueeTextView(application);
                            autoMarqueeTextView.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, (float) 19));
                            autoMarqueeTextView.setWidth((dw * 35) / 100);
                            autoMarqueeTextView.setHeight(clock.getHeight());
                            autoMarqueeTextView.setTypeface(clock.getTypeface());
                            autoMarqueeTextView.setGravity(19);
                            autoMarqueeTextView.setTextSize(0, clock.getTextSize());
                            autoMarqueeTextView.setSingleLine(true);
                            autoMarqueeTextView.setMarqueeRepeatLimit(-1);

                            // 创建跑马灯文字2
                            AutoMarqueeTextView autoMarqueeTextView2 = new AutoMarqueeTextView(application);
                            autoMarqueeTextView2.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, (float) 19));
                            autoMarqueeTextView2.setWidth((dw * 35) / 100);
                            autoMarqueeTextView2.setHeight(clock.getHeight());
                            autoMarqueeTextView2.setTypeface(clock.getTypeface());
                            autoMarqueeTextView2.setGravity(19);
                            autoMarqueeTextView2.setTextSize(0, clock.getTextSize());
                            autoMarqueeTextView2.setSingleLine(true);
                            autoMarqueeTextView2.setMarqueeRepeatLimit(-1);

                            // 创建动画控件
                            ViewFlipper viewFlipper = new ViewFlipper(application);
                            viewFlipper.setPadding(5, 0, 0, 0);
                            viewFlipper.addView(autoMarqueeTextView);
                            viewFlipper.addView(autoMarqueeTextView2);
                            viewFlipper.setVisibility(View.GONE);

                            // 获取时钟线性布局
                            LinearLayout linearLayout = (LinearLayout) clock.getParent();
                            linearLayout.setGravity(19);
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout.addView(viewFlipper, 1);

                            // 创建跑马灯文字3
                            AutoMarqueeTextView autoMarqueeTextView3 = new AutoMarqueeTextView(application);
                            autoMarqueeTextView3.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, (float) 19));
                            autoMarqueeTextView3.setWidth(0);
                            autoMarqueeTextView3.setHeight(clock.getHeight());
                            autoMarqueeTextView3.setTypeface(clock.getTypeface());
                            autoMarqueeTextView3.setGravity(19);
                            autoMarqueeTextView3.setPadding(5, 0, 0, 0);
                            autoMarqueeTextView3.setTextSize(0, clock.getTextSize());
                            autoMarqueeTextView3.setSingleLine(true);
                            autoMarqueeTextView3.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            autoMarqueeTextView3.setMarqueeRepeatLimit(-1);
                            autoMarqueeTextView3.setVisibility(View.GONE);

                            // 创建新的线性布局
                            LinearLayout linearLayout2 = new LinearLayout(application);
                            linearLayout2.setGravity(19);
                            linearLayout2.addView(autoMarqueeTextView3);

                            linearLayout.addView(linearLayout2, 1);

                            // 创建逐字动画
                            CTextView cTextView = new CTextView(application);
                            cTextView.setLayoutParams(new LinearLayout.LayoutParams(0, -2, (float) 19));

                            linearLayout.addView(cTextView, 1);

                            cTextView.setVisibility(View.GONE);

                            TextView textView2 = new TextView(application);
                            textView2.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, (float) 17));

                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView2.getLayoutParams();
                            layoutParams.setMargins(0, 2, 0, 0);

                            textView2.setLayoutParams(layoutParams);

                            GifView gifView = new GifView(application);

                            TextPaint paint = autoMarqueeTextView.getPaint();

                            gifView.setLayoutParams(new LinearLayout.LayoutParams((int) paint.measureText("夏"), (int) paint.measureText("夏"), (float) 17));

                            LinearLayout linearLayout3 = new LinearLayout(application);

                            linearLayout3.addView(textView2);
                            linearLayout3.addView(gifView);

                            linearLayout.addView(linearLayout3, 1);

                            final Handler hAMT = new Handler(message -> {
                                autoMarqueeTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                return true;
                            });

                            final Handler hAMT2 = new Handler(message -> {
                                autoMarqueeTextView2.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                return true;
                            });

                            final Handler hAMTC = new Handler(message -> {
                                autoMarqueeTextView3.setVisibility(View.VISIBLE);
                                cTextView.setVisibility(View.GONE);
                                cTextView.removeAllViews();
                                autoMarqueeTextView3.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                return true;
                            });

                            final Handler iconFanse = new Handler(message -> {
                                textView2.setCompoundDrawables((Drawable) message.obj, null, null, null);
                                return true;
                            });

                            Thread thread = new Thread(() -> {
                                try {
                                    Thread.sleep(1500);
                                    hAMTC.sendMessage(hAMTC.obtainMessage());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                            thread.start();

                            Handler lryciUpdate = new Handler(message -> {
                                Config config;
                                if (message.obj == null) {
                                    config = new Config();
                                } else {
                                    config = (Config) message.obj;
                                }
                                String string = message.getData().getString(KEY_LYRIC);
                                if (!string.equals("")) {
                                    if (new File(iconPath).exists()) {
                                        gifView.setVisibility(View.GONE);
                                        gifView.setMovieResource("");
                                    } else {
                                        textView2.setCompoundDrawables(null, null, null, null);
                                        if (config.getIcon().equals("自定义")) {
                                            if (new File(Utlis.PATH + "icon.gif").exists()) {
                                                gifView.setVisibility(View.VISIBLE);
                                                gifView.setMovieResource(Utlis.PATH + "icon.gif");
                                            } else {
                                                gifView.setVisibility(View.GONE);
                                                gifView.setMovieResource("");
                                            }
                                        }
                                    }
                                    int i = 0;
                                    switch (config.getLyricAnim()) {
                                        case "上滑":
                                            i = 1;
                                            break;
                                        case "下滑":
                                            i = 2;
                                            break;
                                        case "左滑":
                                            i = 3;
                                            break;
                                        case "右滑":
                                            i = 4;
                                            break;
                                        case "随机":
                                            i = ((int) (Math.random() * ((double) 4))) + 1;
                                            break;
                                    }
                                    if (i == 0) {
                                        // 关闭动画
                                        viewFlipper.setInAnimation(null);
                                        viewFlipper.setOutAnimation(null);
                                    } else {
                                        // 开启动画
                                        viewFlipper.setInAnimation(new AnimationTools().translateIn(i));
                                        viewFlipper.setOutAnimation(new AnimationTools().translateOut(i));
                                    }
                                    if (viewFlipper.getDisplayedChild() == 0 && !string.equals(autoMarqueeTextView.getText().toString())) {
                                        if (config.getHideNoti() && MiuiStatusBarManager.isShowNotificationIcon(application)) {
                                            MiuiStatusBarManager.setShowNotificationIcon(application, false);
                                        }
                                        if (config.getHideNetWork() && MiuiStatusBarManager.isShowNetworkSpeed(application)) {
                                            MiuiStatusBarManager.setShowNetworkSpeed(application, false);
                                        }
                                        viewFlipper.showNext();
                                        if (config.getLyricAnim().equals("旋转")) {
                                            thread.interrupt();
                                            autoMarqueeTextView3.setEllipsize(null);
                                            autoMarqueeTextView3.setText(string);
                                            autoMarqueeTextView3.setVisibility(View.GONE);
                                            cTextView.setVisibility(View.VISIBLE);
                                            if (config.getLyricColor().equals("关闭")) {
                                                cTextView.setText(string, application, measuredHeight, clock.getTextSize(), clock.getTypeface(), clock.getTextColors());
                                            } else {
                                                cTextView.setText(string, application, measuredHeight, clock.getTextSize(), clock.getTypeface(), ColorStateList.valueOf(Color.parseColor(config.getLyricColor())));
                                            }
                                            new Thread(() -> {
                                                try {
                                                    Thread.sleep(1500);
                                                    hAMTC.sendMessage(hAMTC.obtainMessage());
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }).start();
                                            viewFlipper.setVisibility(View.GONE);
                                        } else {
                                            cTextView.setVisibility(View.GONE);
                                            viewFlipper.setVisibility(View.VISIBLE);
                                        }
                                        autoMarqueeTextView2.setText(string);
                                        autoMarqueeTextView2.setEllipsize(null);
                                        new Thread(() -> {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            hAMT2.sendMessage(hAMT2.obtainMessage());
                                        }).start();
                                        if (config.getLyricWidth() == -1) {
                                            TextPaint paint1 = autoMarqueeTextView2.getPaint();
                                            if (config.getLyricMaxWidth() == -1 || ((int) paint1.measureText(string)) + 6 <= (dw * config.getLyricMaxWidth()) / 100) {
                                                autoMarqueeTextView.setWidth(((int) paint1.measureText(string)) + 6);
                                                autoMarqueeTextView2.setWidth(((int) paint1.measureText(string)) + 6);
                                                autoMarqueeTextView3.setWidth(((int) paint1.measureText(string)) + 6);
                                                cTextView.setLayoutParams(new LinearLayout.LayoutParams(((int) paint1.measureText(string)) + 6, measuredHeight, (float) 19));
                                            } else {
                                                autoMarqueeTextView.setWidth((dw * config.getLyricMaxWidth()) / 100);
                                                autoMarqueeTextView2.setWidth((dw * config.getLyricMaxWidth()) / 100);
                                                autoMarqueeTextView3.setWidth((dw * config.getLyricMaxWidth()) / 100);
                                                cTextView.setLayoutParams(new LinearLayout.LayoutParams((dw * config.getLyricMaxWidth()) / 100, measuredHeight, (float) 19));
                                            }
                                        } else {
                                            autoMarqueeTextView.setWidth((dw * config.getLyricWidth()) / 100);
                                            autoMarqueeTextView2.setWidth((dw * config.getLyricWidth()) / 100);
                                            autoMarqueeTextView3.setWidth((dw * config.getLyricWidth()) / 100);
                                            cTextView.setLayoutParams(new LinearLayout.LayoutParams((dw * config.getLyricWidth()) / 100, measuredHeight, (float) 19));
                                        }
                                    } else if (viewFlipper.getDisplayedChild() == 1 && !string.equals(autoMarqueeTextView2.getText().toString())) {
                                        if (config.getHideNoti() && MiuiStatusBarManager.isShowNotificationIcon(application)) {
                                            MiuiStatusBarManager.setShowNotificationIcon(application, false);
                                        }
                                        if (config.getHideNetWork() && MiuiStatusBarManager.isShowNetworkSpeed(application)) {
                                            MiuiStatusBarManager.setShowNetworkSpeed(application, false);
                                        }
                                        viewFlipper.showNext();
                                        if (config.getLyricAnim().equals("旋转")) {
                                            thread.interrupt();
                                            autoMarqueeTextView3.setEllipsize(null);
                                            autoMarqueeTextView3.setText(string);
                                            autoMarqueeTextView3.setVisibility(View.GONE);
                                            cTextView.setVisibility(View.VISIBLE);
                                            if (config.getLyricColor().equals("关闭")) {
                                                cTextView.setText(string, application, measuredHeight, clock.getTextSize(), clock.getTypeface(), clock.getTextColors());
                                            } else {
                                                cTextView.setText(string, application, measuredHeight, clock.getTextSize(), clock.getTypeface(), ColorStateList.valueOf(Color.parseColor(config.getLyricColor())));
                                            }
                                            new Thread(() -> {
                                                try {
                                                    Thread.sleep(1500);
                                                    hAMTC.sendMessage(hAMTC.obtainMessage());
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }).start();
                                            viewFlipper.setVisibility(View.GONE);
                                        } else {
                                            cTextView.setVisibility(View.GONE);
                                            viewFlipper.setVisibility(View.VISIBLE);
                                        }
                                        autoMarqueeTextView.setText(string);
                                        autoMarqueeTextView.setEllipsize(null);
                                        new Thread(() -> {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            hAMT.sendMessage(hAMT.obtainMessage());
                                        }).start();
                                        if (config.getLyricWidth() == -1) {
                                            TextPaint paint2 = autoMarqueeTextView.getPaint();
                                            if (config.getLyricMaxWidth() == -1 || ((int) paint2.measureText(string)) + 6 <= (dw * config.getLyricMaxWidth()) / 100) {
                                                autoMarqueeTextView.setWidth(((int) paint2.measureText(string)) + 6);
                                                autoMarqueeTextView2.setWidth(((int) paint2.measureText(string)) + 6);
                                                autoMarqueeTextView3.setWidth(((int) paint2.measureText(string)) + 6);
                                                cTextView.setLayoutParams(new LinearLayout.LayoutParams(((int) paint2.measureText(string)) + 6, measuredHeight, (float) 19));
                                            } else {
                                                autoMarqueeTextView.setWidth((dw * config.getLyricMaxWidth()) / 100);
                                                autoMarqueeTextView2.setWidth((dw * config.getLyricMaxWidth()) / 100);
                                                autoMarqueeTextView3.setWidth((dw * config.getLyricMaxWidth()) / 100);
                                                cTextView.setLayoutParams(new LinearLayout.LayoutParams((dw * config.getLyricMaxWidth()) / 100, measuredHeight, (float) 19));
                                            }
                                        } else {
                                            autoMarqueeTextView.setWidth((dw * config.getLyricWidth()) / 100);
                                            autoMarqueeTextView2.setWidth((dw * config.getLyricWidth()) / 100);
                                            autoMarqueeTextView3.setWidth((dw * config.getLyricWidth()) / 100);
                                            cTextView.setLayoutParams(new LinearLayout.LayoutParams((dw * config.getLyricWidth()) / 100, measuredHeight, (float) 19));
                                        }
                                    }
                                    if (!config.getLyricAnim().equals("旋转")) {
                                        viewFlipper.setVisibility(View.VISIBLE);
                                        autoMarqueeTextView3.setVisibility(View.GONE);
                                        cTextView.setVisibility(View.GONE);
                                    }
                                    clock.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                                    return false;
                                }
                                textView2.setCompoundDrawables(null, null, null, null);
                                gifView.setVisibility(View.GONE);
                                gifView.setMovieResource("");
                                clock.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, (float) 17));
                                clock.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                                viewFlipper.setVisibility(View.GONE);
                                autoMarqueeTextView3.setVisibility(View.GONE);
                                cTextView.setVisibility(View.GONE);
                                if (config.getHideNoti() && !MiuiStatusBarManager.isShowNotificationIcon(application)) {
                                    MiuiStatusBarManager.setShowNotificationIcon(application, true);
                                }
                                if (config.getHideNetWork() && MiuiStatusBarManager.isShowNetworkSpeed(application)) {
                                    MiuiStatusBarManager.setShowNetworkSpeed(application, false);
                                }
                                return true;
                            });

                            new Timer().schedule(
                                    new TimerTask() {
                                        boolean b = false;
                                        ColorStateList color = null;
                                        Config config = new Config();
                                        int count = 0;
                                        String fanse = "关闭";
                                        boolean fs = true;
                                        String icon = "关闭";
                                        boolean lyricOff = false;
                                        boolean lyricService = false;
                                        int lyricSpeed = 0;
                                        String temp = "";

                                        @Override
                                        public void run() {
                                            boolean z;
                                            if (this.count == 100) {
                                                if (!(isServiceRunning(application, "com.kugou") | isServiceRunning(application, "com.netease.cloudmusic") | isServiceRunning(application, "com.tencent.qqmusic.service") | isServiceRunning(application, "cn.kuwo") | isServiceRunning(application, "com.maxmpz.audioplayer")) && !isServiceRunning(application, "remix.myplayer")) {
                                                    boolean z2 = this.b | (viewFlipper.getVisibility() != View.GONE);
                                                    z = autoMarqueeTextView3.getVisibility() != View.GONE;
                                                    if (z2 || z) {
                                                        XposedBridge.log("播放器关闭，清除歌词");
                                                        Message obtainMessage = lryciUpdate.obtainMessage();
                                                        obtainMessage.obj = this.config;
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString(KEY_LYRIC, "");
                                                        obtainMessage.setData(bundle);
                                                        lryciUpdate.sendMessage(obtainMessage);
                                                        lyric = "";
                                                        this.temp = lyric;
                                                        this.b = false;
                                                        if (config.getHideNoti() && !MiuiStatusBarManager.isShowNotificationIcon(application)) {
                                                            MiuiStatusBarManager.setShowNotificationIcon(application, true);
                                                        }
                                                        if (config.getHideNetWork() && !MiuiStatusBarManager.isShowNetworkSpeed(application)) {
                                                            MiuiStatusBarManager.setShowNetworkSpeed(application, true);
                                                        }
                                                    }
                                                } else {
                                                    this.b = true;
                                                    this.fs = true;
                                                    this.config = new Config();
                                                    this.icon = this.config.getIcon();
                                                    this.fanse = this.config.getFanse();
                                                    this.lyricService = this.config.getLyricService();
                                                    this.lyricOff = !this.config.getLyricOff() || audioManager.isMusicActive();
                                                }
                                                this.count = 0;
                                            }
                                            if (this.b && !lyric.equals("")) {
                                                if (!config.getLyricColor().equals("关闭")) {
                                                    autoMarqueeTextView.setTextColor(ColorStateList.valueOf(Color.parseColor(config.getLyricColor())));
                                                    autoMarqueeTextView2.setTextColor(ColorStateList.valueOf(Color.parseColor(config.getLyricColor())));
                                                    cTextView.setTextColor(ColorStateList.valueOf(Color.parseColor(config.getLyricColor())));
                                                    autoMarqueeTextView3.setTextColor(ColorStateList.valueOf(Color.parseColor(config.getLyricColor())));
                                                    this.color = ColorStateList.valueOf(Color.parseColor(config.getLyricColor()));
                                                } else if (!(clock.getTextColors() == null || this.color == clock.getTextColors())) {
                                                    autoMarqueeTextView.setTextColor(clock.getTextColors());
                                                    autoMarqueeTextView2.setTextColor(clock.getTextColors());
                                                    cTextView.setTextColor(clock.getTextColors());
                                                    autoMarqueeTextView3.setTextColor(clock.getTextColors());
                                                    this.color = clock.getTextColors();
                                                    this.fs = true;
                                                }
                                                if (this.config != null && !this.icon.equals("关闭") && this.fs) {
                                                    if (new File(iconPath).exists()) {
                                                        Drawable createFromPath = null;
                                                        try {
                                                            createFromPath = Drawable.createFromPath(iconPath);
                                                        } catch (OutOfMemoryError e) {
                                                            XposedBridge.log("内存溢出!!!!");
                                                            XposedBridge.log(iconPath);
                                                            XposedBridge.log(e);
                                                        }
                                                        if (createFromPath != null) {
                                                            createFromPath.setBounds(0, 0, (int) clock.getTextSize(), (int) clock.getTextSize());
                                                            if (this.fanse.equals("模式一")) {
                                                                createFromPath = fanse(createFromPath, !isBri(clock.getTextColors().getDefaultColor()));
                                                            } else if (this.fanse.equals("模式二")) {
                                                                createFromPath = fanse(createFromPath, isBri(clock.getTextColors().getDefaultColor()));
                                                            }
                                                            Message obtainMessage2 = iconFanse.obtainMessage();
                                                            obtainMessage2.obj = createFromPath;
                                                            iconFanse.sendMessage(obtainMessage2);
                                                        }
                                                    }
                                                    this.fs = false;
                                                }
                                            }
                                            if (this.b && this.lyricSpeed == 10) {
                                                this.lyricSpeed = 0;
                                                if (!lyric.equals("") && this.lyricService && this.lyricOff) {
                                                    if (!this.temp.equals(lyric)) {
                                                        Message obtainMessage3 = lryciUpdate.obtainMessage();
                                                        obtainMessage3.obj = this.config;
                                                        Bundle bundle2 = new Bundle();
                                                        bundle2.putString(KEY_LYRIC, lyric);
                                                        obtainMessage3.setData(bundle2);
                                                        lryciUpdate.sendMessage(obtainMessage3);
                                                        this.temp = lyric;
                                                    }
                                                } else if (this.b) {
                                                    if ((viewFlipper.getVisibility() != View.GONE) || (autoMarqueeTextView3.getVisibility() != View.GONE)) {
                                                        XposedBridge.log("开关关闭或播放器暂停，清除歌词");
                                                        Message obtainMessage4 = lryciUpdate.obtainMessage();
                                                        obtainMessage4.obj = this.config;
                                                        Bundle bundle3 = new Bundle();
                                                        bundle3.putString(KEY_LYRIC, "");
                                                        obtainMessage4.setData(bundle3);
                                                        lryciUpdate.sendMessage(obtainMessage4);
                                                        lyric = "";
                                                        this.temp = lyric;
                                                        this.b = false;
                                                        if (config.getHideNoti() && !MiuiStatusBarManager.isShowNotificationIcon(application)) {
                                                            MiuiStatusBarManager.setShowNotificationIcon(application, true);
                                                        }
                                                        if (config.getHideNetWork() && !MiuiStatusBarManager.isShowNetworkSpeed(application)) {
                                                            MiuiStatusBarManager.setShowNetworkSpeed(application, true);
                                                        }
                                                    }
                                                }
                                            }
                                            if (this.lyricSpeed < 10) {
                                                this.lyricSpeed++;
                                            }
                                            this.count++;
                                        }
                                    }, 0, 10);
                        }
                    });
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
                break;
            case "com.android.settings":
                XposedBridge.log("正在hook 设置");
                XposedHelpers.findAndHookMethod("com.android.settings.NotchStatusBarSettings", lpparam.classLoader, "onCreate", Class.forName("android.os.Bundle"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Application currentApplication = AndroidAppHelper.currentApplication();
                        Object objectField = XposedHelpers.getObjectField(param.thisObject, "mCustomCarrier");
                        XposedHelpers.setObjectField(objectField, "mTitle", "状态栏歌词");
                        XposedHelpers.setObjectField(objectField, "mText", "状态栏歌词");
                        XposedHelpers.setObjectField(objectField, "mClickListener", (View.OnClickListener) view -> {
                            Intent intent = new Intent("msbl.statusbarlyric");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            currentApplication.startActivity(intent);
                        });
                    }
                });
                break;
            case "com.netease.cloudmusic":
                XposedBridge.log("正在hook 网易云音乐");
                XposedHelpers.findAndHookMethod("com.netease.cloudmusic.module.player.t.e", lpparam.classLoader, "o", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(true);
                    }
                });
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.netease.cloudmusic.module.player.t.e"), "B", Class.forName("java.lang.String"), Class.forName("java.lang.String"), Class.forName("java.lang.String"), Long.TYPE, Class.forName("java.lang.Boolean"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        sendLyric(context, param.args[0].toString(), "netease");
                        musicName = param.args[0].toString();
                        XposedBridge.log("网易云： " + param.args[0].toString());
                    }
                });
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.netease.cloudmusic.module.player.t.e"), "F", Class.forName("java.lang.String"), Class.forName("java.lang.String"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        sendLyric(context, param.args[0].toString(), "netease");
                        XposedBridge.log("网易云： " + param.args[0].toString());
                        param.args[0] = musicName;
                        param.setResult(param.args);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
                break;
            case "com.kugou.android":
                XposedBridge.log("正在hook 酷狗音乐");
                XposedHelpers.findAndHookMethod(Class.forName("android.media.AudioManager").getName(), lpparam.classLoader, "isBluetoothA2dpOn", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(true);
                    }
                });
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.kugou.framework.player.c"), "a", HashMap.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("酷狗音乐:" + ((HashMap) param.args[0]).values().toArray()[0]);
                        sendLyric(context, "" + ((HashMap) param.args[0]).values().toArray()[0], "kugou");
                    }
                });
                break;
            case "cn.kuwo.player":
                XposedBridge.log("正在hook 酷我音乐");
                XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isEnabled", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(true);
                    }
                });
                XposedHelpers.findAndHookMethod("cn.kuwo.mod.playcontrol.RemoteControlLyricMgr", lpparam.classLoader, "updateLyricText", Class.forName("java.lang.String"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        String str = (String) param.args[0];
                        XposedBridge.log("酷我音乐:" + str);
                        if (param.args[0] != null && !str.equals("") && !str.equals("好音质 用酷我") && !str.equals("正在搜索歌词...") && !str.contains(" - ")) {
                            sendLyric(context, " " + str, "kuwo");
                        }
                        param.setResult(replaceHookedMethod());
                    }

                    Object replaceHookedMethod() {
                        return null;
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
                break;
            case "com.tencent.qqmusic":
                XposedBridge.log("正在hook QQ音乐");
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.qqmusicplayerprocess.servicenew.mediasession.d$d"), "run", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        Class<?> findClass = XposedHelpers.findClass("com.lyricengine.base.h", lpparam.classLoader);
                        Field declaredField = findClass.getDeclaredField("a");
                        declaredField.setAccessible(true);

                        Object obj = XposedHelpers.findField(param.thisObject.getClass(), "b").get(param.thisObject);
                        String str = (String) declaredField.get(obj);

                        XposedBridge.log("qq音乐: " + str);

                        sendLyric(context, str, "qqmusic");
                    }
                });
                break;
            // TODO 未测试，暂不启用
            // case "com.ximalaya.ting.android":
            //     XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.ximalaya.ting.android.main.playpage.fragment.PlayHistoryFragment"), "initUi", Class.forName("android.os.Bundle"), new XC_MethodHook() {
            //         @Override
            //         protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            //             super.beforeHookedMethod(param);
            //         }

            //         @Override
            //         protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            //             super.afterHookedMethod(param);
            //             param.setResult("测试");
            //             XposedBridge.log("执行" + param.thisObject);
            //         }
            //     });
            //     break;
            case "com.miui.aod":
                XposedBridge.log("正在hook 万象息屏");
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.miui.aod.AODView"), "makeNormalPanel", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Config config = new Config();
                        if (config.getAodLyricService() && !lyric.equals("")) {
                            Application currentApplication = AndroidAppHelper.currentApplication();
                            XposedBridge.log("息屏显示" + param.thisObject);
                            FrameLayout frameLayout = (FrameLayout) ((View) XposedHelpers.findField(param.thisObject.getClass(), "mTableModeContainer").get(param.thisObject)).getParent();
                            TextView textView = new TextView(currentApplication);
                            textView.setWidth(1500);
                            textView.setHeight(1200);
                            textView.setGravity(17);
                            textView.setVisibility(View.VISIBLE);
                            textView.setTextColor(-7829368);
                            textView.setBackgroundColor(-1);
                            textView.setText("");
                            frameLayout.removeViewAt(2);
                            AbsoluteLayout absoluteLayout = new AbsoluteLayout(currentApplication);
                            absoluteLayout.setLayoutParams(new AbsoluteLayout.LayoutParams(-1, -1, 0, 0));
                            absoluteLayout.setBackgroundColor(-16777216);
                            LinearLayout linearLayout = new LinearLayout(currentApplication);
                            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                            linearLayout.setBackgroundColor(-16777216);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.setGravity(17);
                            linearLayout.setPadding(150, 150, 150, 150);
                            Drawable createFromPath = Drawable.createFromPath(Utlis.PATH + "icon7.png");
                            ImageView imageView = new ImageView(currentApplication);
                            imageView.setBackgroundDrawable(createFromPath);
                            GifView gifView = new GifView(currentApplication);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(800, 600);
                            imageView.setLayoutParams(layoutParams);
                            gifView.setLayoutParams(layoutParams);
                            ScrollTextView scrollTextView = new ScrollTextView(currentApplication);
                            scrollTextView.setText("假装这里是歌词");
                            scrollTextView.setTextSize((float) 23);
                            scrollTextView.setTextColor(-1);
                            scrollTextView.setLayoutParams(new LinearLayout.LayoutParams(-2, 200));
                            TextView textView2 = new TextView(currentApplication);
                            textView2.setText("");
                            textView2.setTextSize((float) 15);
                            textView2.setTextColor(-1);
                            textView2.setGravity(17);
                            textView2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                            TextView textView3 = new TextView(currentApplication);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("a hh:mm");
                            textView3.setText("05:30");
                            textView3.setTextSize((float) 19);
                            textView3.setTextColor(-1);
                            textView3.setGravity(17);
                            textView3.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                            LinearLayout linearLayout2 = new LinearLayout(currentApplication);
                            linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, 500));
                            if (new File(Utlis.PATH + "icon7.png").exists()) {
                                linearLayout.addView(imageView);
                            } else {
                                linearLayout.addView(gifView);
                            }
                            linearLayout.addView(scrollTextView);
                            linearLayout.addView(textView2);
                            linearLayout.addView(textView3);
                            linearLayout.addView(linearLayout2);
                            absoluteLayout.addView(linearLayout);
                            linearLayout.setX((float) 0);
                            linearLayout.setY((float) 0);
                            frameLayout.addView(absoluteLayout);
                            WindowManager windowManager = (WindowManager) currentApplication.getSystemService(Context.WINDOW_SERVICE);
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                            int dh = displayMetrics.heightPixels;
                            linearLayout.setY(((float) dh) * 0.2f);
                            Handler handler = new Handler(message -> {
                                textView3.setText(simpleDateFormat.format(new Date()));
                                if (message.what == 101) {
                                    String str = lyric;
                                    if (str.equals("") || (!config.getAodLyricService())) {
                                        Iterator<ActivityManager.RunningAppProcessInfo> it = ((ActivityManager) currentApplication.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses().iterator();
                                        while (true) {
                                            if (it.hasNext()) {
                                                ActivityManager.RunningAppProcessInfo next = it.next();
                                                if (next.processName.equals("com.miui.aod")) {
                                                    android.os.Process.killProcess(next.pid);
                                                    break;
                                                }
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                    textView2.setText(config.getSign());
                                    TextPaint paint = scrollTextView.getPaint();
                                    new LinearGradient((float) 0, (float) 0, (float) scrollTextView.getWidth(), (float) scrollTextView.getHeight(), new int[]{-65536, -16711936, -16776961}, null, Shader.TileMode.CLAMP);
                                    scrollTextView.setWidth(((int) paint.measureText(str)) + 6);
                                    if (!str.equals(scrollTextView.getText())) {
                                        scrollTextView.setText(str);
                                    }
                                }
                                return true;
                            });
                            new Thread(() -> {
                                while (true) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Message message = new Message();
                                    message.what = 101;
                                    handler.sendMessage(message);
                                }
                            }).start();
                            Handler handler1 = new Handler(message -> {
                                linearLayout.setY((float) message.arg1);
                                return true;
                            });
                            Handler handler2 = new Handler(message -> {
                                int i = 0;
                                while (true) {
                                    if (!(i < 2) && !(i > 5)) {
                                        int i2 = (dh * i) / 10;
                                        Timer timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                if (((int) linearLayout.getY()) < i2) {
                                                    Message message = new Message();
                                                    message.what = 101;
                                                    message.arg1 = ((int) linearLayout.getY()) + 1;
                                                    handler1.sendMessage(message);
                                                } else if (((int) linearLayout.getY()) > i2) {
                                                    Message message2 = new Message();
                                                    message2.what = 101;
                                                    message2.arg1 = ((int) linearLayout.getY()) - 1;
                                                    handler1.sendMessage(message2);
                                                } else {
                                                    timer.cancel();
                                                }
                                            }
                                        }, 0, 1);
                                        return true;
                                    }
                                    i = (int) (Math.random() * ((double) 10));
                                }
                            });
                            new Thread(() -> {
                                while (new Config().getSrcService()) {
                                    try {
                                        Thread.sleep(60000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Message message = new Message();
                                    message.what = 101;
                                    handler2.sendMessage(message);
                                }
                            }).start();
                            Handler handler3 = new Handler(message -> {
                                gifView.setMovieResource(Utlis.PATH + "icon7.gif");
                                return true;
                            });
                            new Thread(() -> {
                                Message message = new Message();
                                message.what = 101;
                                handler3.sendMessage(message);
                            }).start();
                        }
                    }
                });
                break;
        }
    }

    public static boolean isServiceRunning(Context context, String str) {
        List<ActivityManager.RunningServiceInfo> runningServices = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(200);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.service.getClassName().contains(str)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isBri(int i) {
        return ColorUtils.calculateLuminance(i) >= 0.5d;
    }

    public static Drawable fanse(Drawable drawable, Boolean bool) {
        ColorMatrix colorMatrix = new ColorMatrix();
        if (bool) {
            colorMatrix.set(NEGATIVE);
        }
        drawable.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        return drawable;
    }

    public String getSystemProperty() {
        BufferedReader bufferedReader = null;
        try {
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop ro.miui.ui.version.name").getInputStream()), 1024);
            bufferedReader = bufferedReader2;
            String readLine = bufferedReader2.readLine();
            try {
                bufferedReader.close();
            } catch (IOException e) {
                XposedBridge.log("无法获取UI版本！");
            }
            return readLine;
        } catch (IOException e2) {
            XposedBridge.log("无法获取UI版本！");
            String str = null;
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e3) {
                    XposedBridge.log("无法获取UI版本！");
                }
            }
            return str;
        } catch (Throwable th) {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e4) {
                    XposedBridge.log("无法获取UI版本！");
                }
            }
            throw th;
        }
    }

    public void sendLyric(Context context, String lyric, String icon) {
        context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Data", lyric).putExtra("Lyric_Icon", icon));
        if (!new Config().getAodLyricService()) return;
        context.sendBroadcast(new Intent().setAction("Lyric_Server_Aod").putExtra("Lyric_Data", lyric).putExtra("Lyric_Icon", icon));
    }


}
