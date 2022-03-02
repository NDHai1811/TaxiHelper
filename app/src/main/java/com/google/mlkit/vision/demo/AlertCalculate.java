package com.google.mlkit.vision.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.mlkit.vision.demo.java.LivePreviewActivity;

public class AlertCalculate {

    public long start, end;
    public int time;
    public int state = 0;
    Vibrator vibrator;

    Context mContext;
    Activity mActivity;

    public AlertCalculate(Context mContext, Activity activity) {
        this.mContext = mContext;
        this.mActivity = activity;
    }
    public boolean eyeBlink(float left_eye, float right_eye){
        if (left_eye <0.005 && right_eye < 0.005){
            return true;
        }
        return false;
    }

    public void startVibrate() {
        long pattern[] = { 300, 700 };
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 0);
    }

    public void stopVibrate() {
        vibrator.cancel();
    }

    public int checkHead(float x, float y, float z) {
//        -15<EulerX<10
//                -20<EulerY<20
//        if (x<-20 || y<-20 || x>25 || y>20){
//            return true;
//        }
//        else{
//            return false;
//        }

        switch (state) {
            case 0:
                if ((x > -20 && x<25) && (y > -20 && y<20)) {
                    // Both eyes are initially open
                    Log.d("Head", "In range instability");
                    state = 1;
                }
                break;

            case 1:
                if (x<-20 || y<-20 || x>25 || y>20) {
                    // Both eyes become closed
                    Log.d("Head", "Out of range");
                    start = System.nanoTime();
                    state = 2;
                    startVibrate();
                }
                break;

            case 2:
                if ((x > -20 && x<25) && (y > -20 && y<20)) {
                    // Both eyes are open again
                    Log.d("Head", "In range");
                    end = System.nanoTime();
                    time = (int)((end - start) / 1000000);
                    Log.d("Head", "time:"+time);
                    state = 0;
                    stopVibrate();
                }
                break;
        }
        return time;
    }
}
