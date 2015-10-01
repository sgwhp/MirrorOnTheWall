package com.github.sgwhp.mirroronthewall.model;

/**
 * Created by robust on 2015/9/21.
 */
public class CityInfo {
    public WeatherInfo weatherinfo;

    public CityInfo(WeatherInfo weatherinfo) {
        this.weatherinfo = weatherinfo;
    }

    public static class WeatherInfo {
        public String city;
        public String cityid;
        public String temp;
        public String temp1;
        public String temp2;
        public String weather;
        public String img1;
        public String img2;
        public String ptime;
        public String WD;
        public String WS;
        public String SD;
        public String WSE;
        public String time;
        public String isRadar;
        public String Radar;
        public String njd;
        public String qy;
    }
}
