package com.github.sgwhp.mirroronthewall.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.sgwhp.mirroronthewall.dao.AlarmDao;
import com.github.sgwhp.mirroronthewall.dao.AlarmDao_;
import com.github.sgwhp.mirroronthewall.model.Alarm;
import com.github.sgwhp.mirroronthewall.model.Constant;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by robust on 2015/9/28.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmDao dao = AlarmDao_.getInstance_(context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent newIntent = new Intent(Constant.ACTION_REMINDING);
        newIntent.putExtra(Constant.EXTRA_DOMAIN, Constant.DOMAIN_ALARM);
        newIntent.putExtra(Constant.EXTRA_INTENT, Constant.INTENT_INSERT);
        try {
            //先删除已经过期的提醒
            dao.clearOutOfDateAlarm();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try{
            ArrayList<Alarm> list = dao.getAll();
            Alarm alarm;
            PendingIntent pi;
            for(int i = list.size() - 1; i >= 0; i--){
                alarm = list.get(i);
                newIntent.putExtra(Constant.EXTRA_MSG, alarm.event);
                pi = PendingIntent.getBroadcast(context, alarm.requestCode
                        , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if(alarm.repeat > 0){
                    am.setRepeating(AlarmManager.RTC_WAKEUP, alarm.time, alarm.repeat, pi);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, alarm.time, pi);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
