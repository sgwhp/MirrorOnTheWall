package com.github.sgwhp.mirroronthewall.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by robust on 2015/9/28.
 */
@DatabaseTable(tableName = "alarm")
public class Alarm {
    @DatabaseField(generatedId = true)
    public int requestCode;
    @DatabaseField
    public String event;//提醒内容
    @DatabaseField
    public long time;//开始时间
    @DatabaseField
    public long repeat;//重复间隔

    public Alarm(){}

    public Alarm(String event, long time, long repeat) {
        this.event = event;
        this.time = time;
        this.repeat = repeat;
    }
}
