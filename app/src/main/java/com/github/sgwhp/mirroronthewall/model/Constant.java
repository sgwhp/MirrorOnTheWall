package com.github.sgwhp.mirroronthewall.model;

/**
 * Created by robust on 2015/9/23.
 */
public interface Constant {
    public static final boolean DEBUG = true;
    public static final String URL_BAIDU_MAP = "http://api.map.baidu.com";
    public static final String URL_WEATHER = "http://www.weather.com.cn";

    public static final int ONE_HOUR = 60 * 60 * 1000;
    public static final long ONE_WEAK = 7 * 24 * ONE_HOUR;

    public static final String EXTRA_APP_ID = "appid";
    public static final String EXTRA_KEY = "key";
    public static final String EXTRA_SECRET = "secret";

    public static final String EXTRA_MSG = "msg";
    public static final String EXTRA_DOMAIN = "domain";
    public static final String EXTRA_INTENT = "intent";


    public static final String EXTRA_NLU = "nlu";

    public static final String DOMAIN_ALARM = "alarm";
    public static final String INTENT_INSERT = "insert";
    public static final String INTENT_REMOVE = "remove";

    public static final String ACTION_REMINDING = "com.github.sgwhp.mirroronthewall.REMINDING";

    public static final int MAX_MSG_NUM = 3;


    public static final String ALARM_TYPE_ABSOLUTE = "absolute";
    public static final String ALARM_TYPE_RELATIVE = "relative";
    public static final String ALARM_TYPE_REPEAT = "repeat";
    public static final String EMPTY_MONTH_AND_DAY = "0000";
}
