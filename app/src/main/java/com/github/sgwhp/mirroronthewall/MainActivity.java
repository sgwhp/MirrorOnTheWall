package com.github.sgwhp.mirroronthewall;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.github.sgwhp.mirroronthewall.dao.AlarmDao;
import com.github.sgwhp.mirroronthewall.dao.CityDao;
import com.github.sgwhp.mirroronthewall.dao.DatabaseHelper;
import com.github.sgwhp.mirroronthewall.model.Alarm;
import com.github.sgwhp.mirroronthewall.model.CityInfo;
import com.github.sgwhp.mirroronthewall.model.Constant;
import com.github.sgwhp.mirroronthewall.model.LocationInfo;
import com.github.sgwhp.mirroronthewall.model.VoiceResult;
import com.github.sgwhp.mirroronthewall.sync.LocationService;
import com.github.sgwhp.mirroronthewall.sync.WeatherService;
import com.github.sgwhp.mirroronthewall.util.ConfirmLock;
import com.github.sgwhp.mirroronthewall.util.LogUtil;
import com.github.sgwhp.mirroronthewall.util.Util;
import com.github.sgwhp.mirroronthewall.widget.adapter.MessageAdapter;
import com.github.sgwhp.mirroronthewall.widget.view.VoiceDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.misc.TransactionManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@WindowFeature({Window.FEATURE_NO_TITLE})
@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity implements VoiceDialog.OnVoiceResultListener {
    @ViewById
    TextView weather;
    @ViewById
    TextView dayOfWeek;
    @Bean
    CityDao cityDao;
    @ViewById
    ListView msgBox;
    @Bean
    AlarmDao alarmDao;
    private MessageAdapter adapter;
    private VoiceDialog dialog;
    private ConfirmLock lock;
    private AlarmManager am;
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);

        DatabaseHelper.copyDbFile(this);
        dialog = new VoiceDialog(this);
        lock = new ConfirmLock();
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                    startBDVoice(this, true, "");
                    return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        dialog.destroy();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Constant.DOMAIN_ALARM.equals(intent.getStringExtra(Constant.EXTRA_DOMAIN))
            && Constant.INTENT_INSERT.equals(intent.getStringExtra(Constant.EXTRA_INTENT))) {
            adapter.addMsg(intent.getStringExtra(Constant.EXTRA_MSG));
            adapter.notifyDataSetChanged();
        }
    }

    @AfterViews
    void init(){
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.CHINA);
        dayOfWeek.setText(sdf.format(Calendar.getInstance().getTime()));
        adapter = new MessageAdapter(this);
        msgBox.setAdapter(adapter);
        updateWeatherInfoNow();
    }

    @UiThread
    void setWeather(CityInfo.WeatherInfo weatherInfo, CityInfo.WeatherInfo curWeatherInfo){
        if(weatherInfo != null && curWeatherInfo != null) {
            weather.setText(String.format(getString(R.string.weather_info)
                    , weatherInfo.weather, curWeatherInfo.temp));
        }
    }

    private void updateWeatherInfo(){
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.URL_BAIDU_MAP)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            LocationService locationService = retrofit.create(LocationService.class);
            Call<LocationInfo> locationInfoCall = locationService.getLocationInfo(getString(R.string.appKey));
            LocationInfo locationInfo = locationInfoCall.execute().body();
            if (locationInfo == null || locationInfo.content == null) {
                LogUtil.v("cant not get your location");
                return;
            }
            LogUtil.v(locationInfo.content.address_detail.city);
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.URL_WEATHER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final WeatherService service = retrofit.create(WeatherService.class);
            String cityCode = cityDao.getCityCode(locationInfo.content.address_detail.city);
            Call<CityInfo> call = service.getWeatherInfo(cityCode);
            CityInfo result = call.execute().body();
            if(result == null){
                return;
            }
            call = service.getCurWeatherInfo(cityCode);
            CityInfo curResult = call.execute().body();
            if(curResult != null){
                setWeather(result.weatherinfo, curResult.weatherinfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //每小时更新天气
            updateWeatherInfoDelay();
        }
    }

    @Background
    void updateWeatherInfoNow() {
        updateWeatherInfo();
    }

    @Background(delay = Constant.ONE_HOUR)
    void updateWeatherInfoDelay(){
        updateWeatherInfo();
    }

    private void startBDVoice(VoiceDialog.OnVoiceResultListener listener, boolean nlu, String msg){
        dialog.setOnVoiceResultListener(listener);
        dialog.show(nlu, msg);
    }

    public void onResults(Bundle results) {
        dialog.dismiss();
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        LogUtil.v("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        LogUtil.v(json_res);
        Gson gson = new Gson();
        try{
            VoiceResult result = gson.fromJson(json_res, VoiceResult.class);
            if(result != null && result.content != null
                    && result.content.json_res != null){
                VoiceResult.JsonRes jsonRes = gson.fromJson(result.content.json_res
                        , VoiceResult.JsonRes.class);
                if(jsonRes != null && jsonRes.results != null && jsonRes.results.length > 0){
                    onNLUEvent(jsonRes.results[0]);
                }
            }
        } catch(JsonSyntaxException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed() {
        dialog.dismiss();
    }

    private void onNLUEvent(VoiceResult.NLUResult event){
        if(Constant.DOMAIN_ALARM.equals(event.domain)){
            if(Constant.INTENT_INSERT.equals(event.intent)){
                if(insert(event.object)){
                    AppMsg.makeText(this, "已新建提醒：" + event.object.event, AppMsg.STYLE_INFO).show();
                } else {
                    AppMsg.makeText(this, "新建提醒失败", AppMsg.STYLE_ALERT).show();
                }
            } else if(Constant.INTENT_REMOVE.equals(event.intent)){
                adapter.clear();
                adapter.notifyDataSetChanged();
                try {
                    if(alarmDao.count() > 0){
                        waitForRemoveAllAlarm();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Background
    void waitForRemoveAllAlarm(){
        showAlertDialog("还有未执行的提醒，是否一并删除？");
        if(lock.waitForResponse()){
            remove();
        }
    }

    @UiThread
    void showAlertDialog(String msg) {
        startBDVoice(lock, false, msg);
    }

    /**
     * insert an alarms
     */
    private boolean insert(VoiceResult.NLUObject obj){
        Intent intent = new Intent(Constant.ACTION_REMINDING);
        intent.putExtra(Constant.EXTRA_DOMAIN, Constant.DOMAIN_ALARM);
        intent.putExtra(Constant.EXTRA_INTENT, Constant.INTENT_INSERT);
        intent.putExtra(Constant.EXTRA_MSG, obj.event);
        long time;
        if(Constant.ALARM_TYPE_ABSOLUTE.equals(obj.type)) {
            Date date = Util.parseDateTime(obj.date + " " + obj.time);
            if (date != null) {
                time = date.getTime();
                LogUtil.d("new alarm: " + obj.event);
                return addAlarm(intent, time, obj);
            }
        } else if(Constant.ALARM_TYPE_RELATIVE.equals(obj.type)){
            time = System.currentTimeMillis() + obj.interval * 1000;
            return addAlarm(intent, time, obj);
        } else if(Constant.ALARM_TYPE_REPEAT.equals(obj.type)){
            LogUtil.d("new alarm: " + obj.event);
            addRepeatAlarm(intent, obj);
            return true;
        }
        return false;
    }

    /**
     * remove all alarm
     */
    private boolean remove(){
        Intent intent = new Intent(Constant.ACTION_REMINDING);
        intent.putExtra(Constant.EXTRA_DOMAIN, Constant.DOMAIN_ALARM);
        intent.putExtra(Constant.EXTRA_INTENT, Constant.INTENT_INSERT);
        try {
            alarmDao.clearOutOfDateAlarm();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ArrayList<Alarm> list = alarmDao.getAll();
            Alarm alarm;
            PendingIntent pi;
            for(int i = list.size() - 1; i >= 0; i--){
                alarm = list.get(i);
                intent.putExtra(Constant.EXTRA_MSG, alarm.event);
                pi = PendingIntent.getBroadcast(this, alarm.requestCode
                        , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.cancel(pi);
            }
            alarmDao.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private PendingIntent createPendingIntent(Intent intent, long time, VoiceResult.NLUObject obj){
        try {
            Alarm alarm = new Alarm(obj.event, time ,0);
            alarm = alarmDao.getDao().createIfNotExists(alarm);
            if(alarm != null){
                return PendingIntent.getBroadcast(this, alarm.requestCode
                        , intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean addAlarm(Intent intent, long time, VoiceResult.NLUObject obj){
        PendingIntent pi = createPendingIntent(intent, time, obj);
        if(pi != null){
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
            return true;
        }
        return false;
    }

    private boolean addRepeatAlarm(final Intent intent, final VoiceResult.NLUObject obj){
        if(!Constant.EMPTY_MONTH_AND_DAY.equals(obj.repeat.substring(0, 4))){
//                try{
//                    long repeat;
//                    int month = Integer.parseInt(obj.repeat.substring(0, 2));
//                    int day = Integer.parseInt(obj.repeat.substring(2, 4));
//                    if(month != 0){
//                        calendar.set(Calendar.MONTH, month);
//                    }
//                    if(day != 0){
//                        calendar.set(Calendar.DAY_OF_MONTH, day);
//                    }
//                } catch (NumberFormatException e){
//                    e.printStackTrace();
//                }
            try {
                TransactionManager.callInTransaction(alarmDao.getDao().getConnectionSource(), new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        int n = obj.repeat.length();
                        ArrayList<Alarm> list = new ArrayList<Alarm>();
                        Alarm alarm;
                        for (int i = 4; i < n; i++) {
                            Calendar calendar = Calendar.getInstance();
                            int[] times = Util.parseTime(obj.time);
                            calendar.set(Calendar.HOUR, times[0]);
                            calendar.set(Calendar.MINUTE, times[1]);
                            calendar.set(Calendar.SECOND, times[2]);
                            // 前4位是月和日
                            long time;

                            if ('1' == obj.repeat.charAt(i)) {
                                calendar.set(Calendar.DAY_OF_WEEK, i - 4);
                                time = calendar.getTimeInMillis();
                                alarm = new Alarm(obj.event, time, 0);
                                alarm = alarmDao.getDao().createIfNotExists(alarm);
                                list.add(alarm);
                            }
                        }
                        n = list.size();
                        PendingIntent pi;
                        for (int i = 0; i < n; i++) {
                            alarm = list.get(i);
                            pi = PendingIntent.getBroadcast(MainActivity.this, alarm.requestCode
                                    , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            am.setRepeating(AlarmManager.RTC_WAKEUP, alarm.time, Constant.ONE_WEAK, pi);
                        }
                        return null;
                    }
                });
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
