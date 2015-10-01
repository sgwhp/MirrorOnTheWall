package com.github.sgwhp.mirroronthewall.util;

import android.os.Bundle;
import android.speech.SpeechRecognizer;

import com.github.sgwhp.mirroronthewall.widget.view.VoiceDialog;

import java.util.ArrayList;

/**
 * Created by robust on 2015/9/28.
 */
public class ConfirmLock implements VoiceDialog.OnVoiceResultListener {
    private static final String[] POSITIVE_STR = {"确定", "是", "是的"};
    //    private static final String[] NEGATIVE_STR = {"不是", "否", "取消", "不"};
    private boolean positive;
    private final Object lock = new Object();

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    private boolean isPositive(String str){
        for(int i = POSITIVE_STR.length - 1; i >= 0; i--){
            if(POSITIVE_STR[i].equals(str)){
                return true;
            }
        }
        return false;
    }

    public boolean waitForResponse() {
        synchronized (lock){
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return positive;
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if(nbest != null && nbest.size() > 0){
            if(isPositive(nbest.get(0))){
                setPositive(true);
                synchronized (lock){
                    lock.notify();
                }
                return;
            }
        }
        setPositive(false);
        synchronized (lock){
            lock.notify();
        }
    }

    @Override
    public void onFailed() {

    }
}
