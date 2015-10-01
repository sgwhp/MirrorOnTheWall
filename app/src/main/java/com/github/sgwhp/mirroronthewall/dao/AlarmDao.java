package com.github.sgwhp.mirroronthewall.dao;

import android.content.Context;

import com.github.sgwhp.mirroronthewall.model.Alarm;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by robust on 2015/9/28.
 */
@EBean
public class AlarmDao {
    @RootContext
    Context context;
    private Dao<Alarm, Object> dao;

    @AfterInject
    void init(){
        try {
            dao = DatabaseHelper.getInstance(context).getDao(Alarm.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Alarm, Object> getDao(){
        return dao;
    }

    public ArrayList<Alarm> getAll() throws SQLException {
        return (ArrayList<Alarm>) dao.queryForAll();
    }

    public void clearOutOfDateAlarm() throws SQLException {
        DeleteBuilder builder = dao.deleteBuilder();
        builder.where().lt("time", System.currentTimeMillis()).and().ne("repeat", 0);
        builder.delete();
    }

    public void clear() throws SQLException {
        DeleteBuilder builder = dao.deleteBuilder();
        builder.delete();
    }

    public long count() throws SQLException {
        return dao.countOf();
    }
}
