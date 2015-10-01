package com.github.sgwhp.mirroronthewall.sync;

import com.github.sgwhp.mirroronthewall.model.CityInfo;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * 获取天气信息接口
 * Created by robust on 2015/9/22.
 */
public interface WeatherService {
    /**
     * 获取当天的天气信息
     * @param cityid 城市编号
     * @return 天气信息，包含天气情况、温度范围等
     */
    @GET("/adat/cityinfo/{cityid}.html")
    public Call<CityInfo> getWeatherInfo(@Path("cityid") String cityid);

    /**
     * 获取当前的天气信息
     * @param cityid 城市编号
     * @return 天气信息，包含当前温度、风力等
     */
    @GET("/adat/sk/{cityid}.html")
    public Call<CityInfo> getCurWeatherInfo(@Path("cityid") String cityid);
}
