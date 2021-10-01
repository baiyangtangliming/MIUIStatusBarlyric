package cn.fkj233.hook.miuistatusbarlrcy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
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
    public static String PATH = Environment.getExternalStorageDirectory() + "/Android/media/cn.fkj233.hook.miuistatusbarlrcy/";
    String iconM = "";

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
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        switch (lpparam.packageName) {
            case "com.android.systemui":
                XposedBridge.log("Hook SystemUI");
                try {
                    XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.android.systemui.statusbar.phone.CollapsedStatusBarFragment"), "onViewCreated", Class.forName("android.view.View"), Class.forName("android.os.Bundle"), new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Field clockField;
                            final Application application = AndroidAppHelper.currentApplication();
                            AudioManager audioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
                            int dw = displayMetrics.widthPixels;

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

                            TextView clock = (TextView) clockField.get(param.thisObject);
                            clock.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                            int measuredHeight = clock.getMeasuredHeight();
                            AutoMarqueeTextView autoMarqueeTextView = new AutoMarqueeTextView(application);
                            autoMarqueeTextView.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, (float) 19));
                            autoMarqueeTextView.setWidth((dw * 35) / 100);
                            autoMarqueeTextView.setHeight(clock.getHeight());
                            autoMarqueeTextView.setTypeface(clock.getTypeface());
                            autoMarqueeTextView.setGravity(19);
                            autoMarqueeTextView.setTextSize(0, clock.getTextSize());
                            autoMarqueeTextView.setSingleLine(true);
                            autoMarqueeTextView.setMarqueeRepeatLimit(-1);
                            AutoMarqueeTextView autoMarqueeTextView2 = new AutoMarqueeTextView(application);
                            autoMarqueeTextView2.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, (float) 19));
                            autoMarqueeTextView2.setWidth((dw * 35) / 100);
                            autoMarqueeTextView2.setHeight(clock.getHeight());
                            autoMarqueeTextView2.setTypeface(clock.getTypeface());
                            autoMarqueeTextView2.setGravity(19);
                            autoMarqueeTextView2.setTextSize(0, clock.getTextSize());
                            autoMarqueeTextView2.setSingleLine(true);
                            autoMarqueeTextView2.setMarqueeRepeatLimit(-1);
                            final ViewFlipper viewFlipper = new ViewFlipper(application);
                            viewFlipper.setPadding(5, 0, 0, 0);
                            viewFlipper.addView(autoMarqueeTextView);
                            viewFlipper.addView(autoMarqueeTextView2);
                            viewFlipper.setVisibility(View.GONE);
                            LinearLayout linearLayout = (LinearLayout) clock.getParent();
                            linearLayout.setGravity(19);
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout.addView(viewFlipper, 1);
                            final AutoMarqueeTextView autoMarqueeTextView3 = new AutoMarqueeTextView(application);
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
                            LinearLayout linearLayout2 = new LinearLayout(application);
                            linearLayout2.setGravity(19);
                            linearLayout2.addView(autoMarqueeTextView3);
                            linearLayout.addView(linearLayout2, 1);
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
                            Handler hAMT = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    super.handleMessage(message);
                                    autoMarqueeTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                }
                            };
                            Handler hAMT2 = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    super.handleMessage(message);
                                    autoMarqueeTextView2.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                }
                            };
                            Handler hAMTC = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    super.handleMessage(message);
                                    autoMarqueeTextView3.setVisibility(View.VISIBLE);
                                    cTextView.setVisibility(View.GONE);
                                    cTextView.removeAllViews();
                                    autoMarqueeTextView3.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                }
                            };
                            Handler hT = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    textView2.setCompoundDrawables((Drawable) message.obj, null, null, null);
                                }
                            };
                            Thread thread = new Thread(() -> {
                                try {
                                    Thread.sleep(1500);
                                    hAMTC.sendMessage(hAMTC.obtainMessage());
                                } catch (InterruptedException e) {
                                }
                            });
                            thread.start();

                            final Handler handler1 = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    Config config;
                                    super.handleMessage(message);
                                    if (message.obj == null) {
                                        config = new Config();
                                    } else {
                                        config = (Config) message.obj;
                                    }
                                    Config2 config2 = new Config2();
                                    String string = message.getData().getString(KEY_LYRIC);
                                    XposedBridge.log("GetData: " + string);
                                    if (!string.equals("")) {
                                        if (new File(config2.getIconPath()).exists()) {
                                            gifView.setVisibility(View.GONE);
                                            gifView.setMovieResource("");
                                        } else {
                                            textView2.setCompoundDrawables(null, null, null, null);
                                            if (config2.getIcon().equals("自定义")) {
                                                if (new File(PATH + "icon.gif").exists()) {
                                                    gifView.setVisibility(View.VISIBLE);
                                                    gifView.setMovieResource(PATH + "icon.gif");
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
                                            viewFlipper.setInAnimation(null);
                                            viewFlipper.setOutAnimation(null);
                                        } else {
                                            viewFlipper.setInAnimation(new AnimationTools().translateIn(i));
                                            viewFlipper.setOutAnimation(new AnimationTools().translateOut(i));
                                        }
                                        if (viewFlipper.getDisplayedChild() == 0 && !string.equals(autoMarqueeTextView.getText().toString())) {
                                            // TODO 是否显示图标 不知道MiuiStatusBarManager在哪下 未实现
//                                        if (config.getHideNoti() && MiuiStatusBarManager.isShowNotificationIcon(application)) {
//                                            MiuiStatusBarManager.setShowNotificationIcon(application, false);
//                                        }
                                            viewFlipper.showNext();
                                            if (config.getLyricAnim().equals("旋转")) {
                                                thread.interrupt();
                                                autoMarqueeTextView3.setEllipsize(null);
                                                autoMarqueeTextView3.setText(string);
                                                autoMarqueeTextView3.setVisibility(View.GONE);
                                                cTextView.setVisibility(View.VISIBLE);
                                                cTextView.setText(string, application, measuredHeight, clock.getTextSize(), clock.getTypeface(), clock.getTextColors());
                                                new Thread(() -> {
                                                    try {
                                                        Thread.sleep(1500);
                                                        hAMTC.sendMessage(hAMTC.obtainMessage());
                                                    } catch (InterruptedException e) {
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
                                                }
                                                hAMT2.sendMessage(hAMT2.obtainMessage());
                                            }).start();
                                            if (config.getLyricWidth() == -1) {
                                                TextPaint paint = autoMarqueeTextView2.getPaint();
                                                if (config.getLyricMaxWidth() == -1 || ((int) paint.measureText(string)) + 6 <= (dw * config.getLyricMaxWidth()) / 100) {
                                                    autoMarqueeTextView.setWidth(((int) paint.measureText(string)) + 6);
                                                    autoMarqueeTextView2.setWidth(((int) paint.measureText(string)) + 6);
                                                    autoMarqueeTextView3.setWidth(((int) paint.measureText(string)) + 6);
                                                    cTextView.setLayoutParams(new LinearLayout.LayoutParams(((int) paint.measureText(string)) + 6, measuredHeight, (float) 19));
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
                                            // TODO 是否显示图标 不知道MiuiStatusBarManager在哪下 未实现
//                                        if (config.getHideNoti() && MiuiStatusBarManager.isShowNotificationIcon(application)) {
//                                            MiuiStatusBarManager.setShowNotificationIcon(application, false);
//                                        }
                                            viewFlipper.showNext();
                                            if (config.getLyricAnim().equals("旋转")) {
                                                thread.interrupt();
                                                autoMarqueeTextView3.setEllipsize(null);
                                                autoMarqueeTextView3.setText(string);
                                                autoMarqueeTextView3.setVisibility(View.GONE);
                                                cTextView.setVisibility(View.VISIBLE);
                                                cTextView.setText(string, application, measuredHeight, clock.getTextSize(), clock.getTypeface(), clock.getTextColors());
                                                new Thread(() -> {
                                                    try {
                                                        Thread.sleep(1500);
                                                        hAMTC.sendMessage(hAMTC.obtainMessage());
                                                    } catch (InterruptedException e) {
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
                                        return;
                                    }
                                    textView2.setCompoundDrawables(null, null, null, null);
                                    gifView.setVisibility(View.GONE);
                                    gifView.setMovieResource("");
                                    clock.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, (float) 17));
                                    clock.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                                    viewFlipper.setVisibility(View.GONE);
                                    autoMarqueeTextView3.setVisibility(View.GONE);
                                    cTextView.setVisibility(View.GONE);
                                    // TODO 是否显示图标 不知道MiuiStatusBarManager在哪下 未实现
//                                if (config.getHideNoti().booleanValue() && !MiuiStatusBarManager.isShowNotificationIcon(application)) {
//                                    MiuiStatusBarManager.setShowNotificationIcon(application, true);
//                                }
                                }
                            };

                            new Timer().schedule(
                                    new TimerTask() {
                                        boolean b = false;
                                        ColorStateList color = null;
                                        Config config = new Config();
                                        Config2 config2 = new Config2();
                                        int count = 0;
                                        String fanse = "关闭";
                                        boolean fs = true;
                                        String icon = "关闭";
                                        String iconPath = "";
                                        String lyric = "";
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
                                                        setlyric("");
                                                        XposedBridge.log("播放器关闭，清除歌词");
                                                        Message obtainMessage = handler1.obtainMessage();
                                                        obtainMessage.obj = this.config;
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString(KEY_LYRIC, "");
                                                        obtainMessage.setData(bundle);
                                                        handler1.sendMessage(obtainMessage);
                                                        this.lyric = "";
                                                        this.temp = this.lyric;
                                                        this.b = false;
                                                    }
                                                } else {
                                                    this.b = true;
                                                    this.fs = true;
                                                    this.config = new Config();
                                                    this.config2 = new Config2();
                                                    this.icon = this.config2.getIcon();
                                                    this.iconPath = this.config2.getIconPath();
                                                    this.fanse = this.config.getFanse();
                                                    this.lyricService = this.config.getLyricService();
                                                    this.lyricOff = !this.config.getLyricOff() || audioManager.isMusicActive();
                                                }
                                                this.count = 0;
                                            }
                                            if (this.b && !this.lyric.equals("")) {
                                                if (!(clock.getTextColors() == null || this.color == clock.getTextColors())) {
                                                    autoMarqueeTextView.setTextColor(clock.getTextColors());
                                                    autoMarqueeTextView2.setTextColor(clock.getTextColors());
                                                    cTextView.setTextColor(clock.getTextColors());
                                                    autoMarqueeTextView3.setTextColor(clock.getTextColors());
                                                    this.color = clock.getTextColors();
                                                    this.fs = true;
                                                }
                                                if (this.config2 != null && !this.icon.equals("关闭") && this.fs) {
                                                    if (new File(this.iconPath).exists()) {
                                                        Drawable createFromPath = Drawable.createFromPath(this.iconPath);
                                                        Drawable drawable = createFromPath;
                                                        createFromPath.setBounds(0, 0, (int) clock.getTextSize(), (int) clock.getTextSize());
                                                        if (this.fanse.equals("模式一")) {
                                                            drawable = fanse(drawable, !isBri(clock.getTextColors().getDefaultColor()), true);
                                                        } else if (this.fanse.equals("模式二")) {
                                                            drawable = fanse(drawable, isBri(clock.getTextColors().getDefaultColor()), true);
                                                        }
                                                        Message obtainMessage2 = hT.obtainMessage();
                                                        obtainMessage2.obj = drawable;
                                                        hT.sendMessage(obtainMessage2);
                                                    }
                                                    this.fs = false;
                                                }
                                            }
                                            if (this.b && this.lyricSpeed == 10) {
                                                this.lyric = LyricTools.getlyric();
                                                this.lyricSpeed = 0;
                                                if (!this.lyric.equals("") && this.lyricService && this.lyricOff) {
                                                    this.lyric = LyricTools.getlyric();
                                                    if (!this.temp.equals(this.lyric)) {
                                                        Message obtainMessage3 = handler1.obtainMessage();
                                                        obtainMessage3.obj = this.config;
                                                        Bundle bundle2 = new Bundle();
                                                        bundle2.putString(KEY_LYRIC, this.lyric);
                                                        obtainMessage3.setData(bundle2);
                                                        handler1.sendMessage(obtainMessage3);
                                                        this.temp = this.lyric;
                                                    }
                                                } else if (this.b) {
                                                    if ((viewFlipper.getVisibility() != View.GONE) || (autoMarqueeTextView3.getVisibility() != View.GONE)) {
                                                        setlyric("");
                                                        XposedBridge.log("开关关闭或播放器暂停，清除歌词");
                                                        Message obtainMessage4 = handler1.obtainMessage();
                                                        obtainMessage4.obj = this.config;
                                                        Bundle bundle3 = new Bundle();
                                                        bundle3.putString(KEY_LYRIC, "");
                                                        obtainMessage4.setData(bundle3);
                                                        handler1.sendMessage(obtainMessage4);
                                                        this.lyric = "";
                                                        this.temp = this.lyric;
                                                        this.b = false;
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
                XposedBridge.log("Hook Settings");
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.android.settings.NotchStatusBarSettings"), "onCreate", Class.forName("android.os.Bundle"), new XC_MethodHook() {
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
                        setIconP("netease");
                        LyricTools.setlyric(param.args[0].toString());
                        musicName = param.args[0].toString();
                        XposedBridge.log("网易云： " + param.args[0].toString());
                    }
                });
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.netease.cloudmusic.module.player.t.e"), "F", Class.forName("java.lang.String"), Class.forName("java.lang.String"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        LyricTools.setlyric(param.args[0].toString());
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
                XposedBridge.log("正在hook酷狗音乐");
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
                        if (!new Config().getLyricModel().equals("增强模式") || ((HashMap) param.args[0]).values().toArray()[0].equals(LyricTools.getlyric())) {
                            XposedBridge.log("酷狗音乐:hook到了但没完全hook到");
                            return;
                        }
                        setIconP("kugou");
                        XposedBridge.log("酷狗音乐:" + ((HashMap) param.args[0]).values().toArray()[0]);
                        LyricTools.setlyric("" + ((HashMap) param.args[0]).values().toArray()[0]);
                    }
                });
                break;
            case "cn.kuwo.player":
                XposedHelpers.findAndHookMethod(Class.forName("android.bluetooth.BluetoothAdapter").getName(), lpparam.classLoader, "isEnabled", new XC_MethodHook() {
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
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("cn.kuwo.mod.playcontrol.session.media.MediaSessionDirector"), "updateMetaData", Class.forName("java.lang.String"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (new Config().getLyricModel().equals("增强模式") && param.args[1] != null) {
                            String str = (String) param.args[1];
                            if (!str.equals(LyricTools.getlyric())) {
                                setIconP("kuwo");
                                LyricTools.setlyric(" " + str);
                                XposedBridge.log("酷我音乐:" + str);
                            }
                        }

                    }
                });
                break;
            case "com.tencent.qqmusic":
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.qqmusicplayerprocess.servicenew.mediasession.d$d"), "run", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (new Config().getLyricModel().equals("增强模式")) {
                            Class findClass = XposedHelpers.findClass("com.lyricengine.base.h", lpparam.classLoader);
                            Object obj = XposedHelpers.findField(param.thisObject.getClass(), "b").get(param.thisObject);
                            Field declaredField = findClass.getDeclaredField("a");
                            declaredField.setAccessible(true);
                            String str = (String) declaredField.get(obj);
                            if (!str.equals(LyricTools.getlyric())) {
                                setIconP("qqmusic");
                                LyricTools.setlyric(str);
                                XposedBridge.log("QQ音乐:" + str);
                                return;
                            }
                            XposedBridge.log("hook到了但没完全hook到");
                            return;
                        }
                        XposedBridge.log("hook到了但没完全hook到");

                    }
                });
                break;
            case "com.ximalaya.ting.android":
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.ximalaya.ting.android.main.playpage.fragment.PlayHistoryFragment"), "initUi", Class.forName("android.os.Bundle"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult("测试");
                        XposedBridge.log("执行" + param.thisObject);
                    }
                });
                break;
            case "com.miui.aod":
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.miui.aod.AODView"), "makeNormalPanel", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Config config = new Config();
                        if (config.getAodLyricService() && !LyricTools.getlyric().equals("")) {
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
                            Drawable createFromPath = Drawable.createFromPath(PATH + "icon7.png");
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
                            if (new File(PATH + "icon7.png").exists()) {
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
                            Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    super.handleMessage(message);
                                    textView3.setText(simpleDateFormat.format(new Date()));
                                    if (message.what == 101) {
                                        String str = LyricTools.getlyric();
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
                                }
                            };
                            new Thread(() -> {
                                while (true) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                    }
                                    Message message = new Message();
                                    message.what = 101;
                                    handler.sendMessage(message);
                                }
                            }).start();
                            Handler handler1 = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    super.handleMessage(message);
                                    linearLayout.setY((float) message.arg1);
                                }
                            };
                            Handler handler2 = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    super.handleMessage(message);
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
                                            return;
                                        }
                                        i = (int) (Math.random() * ((double) 10));
                                    }
                                }
                            };
                            new Thread(() -> {
                                while (new Config().getSrcService()) {
                                    try {
                                        Thread.sleep(60000);
                                    } catch (InterruptedException e) {
                                    }
                                    Message message = new Message();
                                    message.what = 101;
                                    handler2.sendMessage(message);
                                }
                            }).start();
                            Handler handler3 = new Handler() {
                                public void handleMessage(Message message) {
                                    super.handleMessage(message);
                                    gifView.setMovieResource(PATH + "icon7.gif");
                                }
                            };
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

    public void setlyric(String str) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Android/media/cn.fkj233.hook.miuistatusbarlrcy/.msbl");
            fileOutputStream.write(str.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Boolean isBri(int i) {
        return ColorUtils.calculateLuminance(i) >= 0.5d;
    }

    public static Drawable fanse(Drawable drawable, Boolean bool, Boolean bool2) {
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
            bufferedReader.close();
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    XposedBridge.log("无法获取UI版本！");
                }
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

    public void setIconP(String str) {
        Config2 config2 = new Config2();
        if (!this.iconM.equals(config2.getIcon())) {
            switch (config2.getIcon()) {
                case "关闭":
                    config2.setIconPath(PATH + "夏.png");
                    break;
                case "自动":
                    config2.setIconPath(PATH + str + ".png");
                    break;
                case "自定义":
                    config2.setIconPath(PATH + "icon.png");
                    break;
            }
        }
        this.iconM = config2.getIcon();
    }


}
