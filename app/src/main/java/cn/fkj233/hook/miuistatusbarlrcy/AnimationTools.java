package cn.fkj233.hook.miuistatusbarlrcy;


import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationTools {
    public Animation translateIn(int i) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = null;
        if (i == 1) {
            translateAnimation = new TranslateAnimation((float) 0, (float) 0, (float) 100, (float) 0);
        } else if (i == 2) {
            translateAnimation = new TranslateAnimation((float) 0, (float) 0, (float) -100, (float) 0);
        } else if (i == 3) {
            translateAnimation = new TranslateAnimation((float) 100, (float) 0, (float) 0, (float) 0);
        } else if (i == 4) {
            translateAnimation = new TranslateAnimation((float) -100, (float) 0, (float) 0, (float) 0);
        }
        translateAnimation.setDuration(300);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0, (float) 1);
        alphaAnimation.setDuration(300);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }

    public Animation translateOut(int i) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = null;
        if (i == 1) {
            translateAnimation = new TranslateAnimation((float) 0, (float) 0, (float) 0, (float) -100);
        } else if (i == 2) {
            translateAnimation = new TranslateAnimation((float) 0, (float) 0, (float) 0, (float) 100);
        } else if (i == 3) {
            translateAnimation = new TranslateAnimation((float) 0, (float) -100, (float) 0, (float) 0);
        } else if (i == 4) {
            translateAnimation = new TranslateAnimation((float) 0, (float) 100, (float) 0, (float) 0);
        }
        translateAnimation.setDuration(300);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 1, (float) 0);
        alphaAnimation.setDuration(300);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }

    public Animation animRotate(int i) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setDuration(300);
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360.0f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setDuration(300);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setStartOffset((i * 30));
        animationSet.setDuration(300);
        return animationSet;
    }
}